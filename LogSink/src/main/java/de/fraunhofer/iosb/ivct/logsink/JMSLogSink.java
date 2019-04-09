/* Copyright 2017, Reinhard Herzog (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package de.fraunhofer.iosb.ivct.logsink;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.net.JMSTopicSink;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import nato.ivct.commander.CmdLogMsgListener.LogMsg;
import nato.ivct.commander.CmdLogMsgListener.OnLogMsgListener;
import nato.ivct.commander.CmdQuitListener.OnQuitListener;
import nato.ivct.commander.CmdStartTcListener.OnStartTestCaseListener;
import nato.ivct.commander.CmdStartTcListener.TcInfo;
import nato.ivct.commander.CmdStartTestResultListener.OnResultListener;
import nato.ivct.commander.CmdStartTestResultListener.TcResult;
import nato.ivct.commander.Factory;
import nato.ivct.commander.SutPathsFiles;

public class JMSLogSink implements MessageListener, TcChangedListener, OnResultListener, OnQuitListener, OnStartTestCaseListener, OnLogMsgListener {

	private Logger logger = (Logger) LoggerFactory.getLogger(JMSTopicSink.class);
	// private Logger log;
	private Map<String, FileAppender<ILoggingEvent>> appenderMap = new HashMap<String, FileAppender<ILoggingEvent>>();
	private Map<String, String> tcLogMap = new HashMap<String, String>();
	private ReportEngine reportEngine;
	public TcChangedListener tcListener = null;

	public JMSLogSink(String tcfBindingName, String topicBindingName, String username, String password, ReportEngine reportEngine) {

		this.reportEngine = reportEngine;
		try {
			Properties env = new Properties();
			env.put(Context.INITIAL_CONTEXT_FACTORY, Factory.props.getProperty(Factory.JAVA_NAMING_FACTORY_ID));
            String host = Factory.props.getProperty(Factory.MESSAGING_HOST_ID);
            if (host == null) {
                host = "localhost";
                logger.warn("Environment variable {} not found: using default {}", Factory.MESSAGING_HOST_ID, host);
            }
            String portString;
            portString = Factory.props.getProperty(Factory.MESSAGING_PORT_ID);
            if (portString == null) {
                portString = "61616";
                logger.warn("Environment variable {} not found: using default {}", Factory.MESSAGING_PORT_ID, portString);
            }
            int port = Integer.parseInt(portString);
            String hostPort = new String("tcp://" + host + ":" + port);
			env.put(Context.PROVIDER_URL, hostPort);
			Context ctx = new InitialContext(env);
			TopicConnectionFactory topicConnectionFactory;
			topicConnectionFactory = (TopicConnectionFactory) lookup(ctx, tcfBindingName);
			logger.info("Topic Cnx Factory found");
			Topic topic = (Topic) ctx.lookup(topicBindingName);
			logger.info("Topic found: " + topic.getTopicName());

			TopicConnection topicConnection = topicConnectionFactory.createTopicConnection(username, password);
			logger.info("Topic Connection created");

			TopicSession topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			TopicSubscriber topicSubscriber = topicSession.createSubscriber(topic);
			topicSubscriber.setMessageListener(this);
			topicConnection.start();
			logger.info("Topic Connection started");

		} catch (Exception e) {
			logger.error("Could not read JMS message.", e);
		}
	}

	protected Object lookup(Context ctx, String name) throws NamingException {
		try {
			return ctx.lookup(name);
		} catch (NameNotFoundException e) {
			logger.error("Could not find name [" + name + "].");
			throw e;
		}
	}

	/**
	 * receives messages from the JMS bus and forward them to test case logger  
	 * if the logging events are marked with an testcase name
	 */
	@Override
	public void onMessage(Message message) {
		ILoggingEvent event;
		try {
			if (message.getJMSRedelivered()) {
				logger.warn("ReportEngine:onMessage: MESSAGE REDELIVERED");
			} else if (message instanceof ObjectMessage) {
				ObjectMessage objectMessage = (ObjectMessage) message;
				event = (ILoggingEvent) objectMessage.getObject();

				String tc = event.getMDCPropertyMap().get("testcase");
				if (tc != null) {
					String sutName = event.getMDCPropertyMap().get("sutName");
					SutPathsFiles sutPathsFiles = Factory.getSutPathsFiles();
					String badge = event.getMDCPropertyMap().get("badge");
					String tcLogDir = sutPathsFiles.getSutLogPathName(sutName, badge);
					Logger log = getTestCaseLogger(tc, sutName, tcLogDir);
					log.callAppenders(event);
				}
				String tcStatus = event.getMDCPropertyMap().get("tcStatus");
				if (tcStatus != null) {
					if (tcStatus.contentEquals("ended")) {
						FileAppender<ILoggingEvent> fileAppender = appenderMap.get(tc);
						if (fileAppender != null) {
							fileAppender.stop();
						}
						appenderMap.remove(tc);
					}
				}
			} else {
				logger.warn("Received message is of type " + message.getJMSType() + ", was expecting ObjectMessage.");
			}
		} catch (JMSException jmse) {
			logger.error("Exception thrown while processing incoming message.", jmse);
		}
	}

	/**
	 * Finds or creates a logger for the test case called tcName with an appender writing 
	 * to file named <tcName>.log
	 * 
	 * @param tcName
	 * @return
	 */
	private Logger getTestCaseLogger(String tcName, String sutName, String tcLogDir) {
		FileAppender<ILoggingEvent> fileAppender = appenderMap.get(tcName);
		if (fileAppender == null) {
			LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
			PatternLayoutEncoder ple = new PatternLayoutEncoder();

			ple.setPattern("%date %level [%logger{36}] [%file:%line] %X{testcase}: %msg%n");
			ple.setContext(lc);
			ple.start();
			LocalDateTime ldt = LocalDateTime.now();
			String formattedMM = String.format("%02d", ldt.getMonthValue());
			String formatteddd = String.format("%02d", ldt.getDayOfMonth());
			String formattedhh = String.format("%02d", ldt.getHour());
			String formattedmm = String.format("%02d", ldt.getMinute());
			String formattedss = String.format("%02d", ldt.getSecond());
			fileAppender = new FileAppender<ILoggingEvent>();
			Date date = new Date();
			SimpleDateFormat sdf;
			sdf = new SimpleDateFormat("ZZZ");
			String tcLogName = tcName + "-" + ldt.getYear() + "-" + formattedMM + "-" + formatteddd + "T" + formattedhh + formattedmm + formattedss + sdf.format(date) + ".log";
			fileAppender.setFile(tcLogDir + '/' + tcLogName);
			fileAppender.setEncoder(ple);
			fileAppender.setContext(lc);
			fileAppender.start();
			appenderMap.put(tcName, fileAppender);
			tcLogMap.put(tcName, tcLogName);
		}

		Logger logger = (Logger) LoggerFactory.getLogger(tcName);
		logger.addAppender(fileAppender);
		return logger;
	}

	@Override
	public void onQuit() {
		reportEngine.onQuit();
		System.exit(0);
	}

	/** {@inheritDoc} */
	@Override
	public void onResult(TcResult result) {
		String tcLogName = tcLogMap.get(result.testcase);
		if (tcLogName == null) {
			tcLogName = "test case log file not found";
			return;
		}
		tcLogMap.remove(result.testcase);
		reportEngine.onResult(result, tcLogName);
    }

	@Override
	public void tcChanged(String newTcName) {
		logger.info("Test Case changed to :" + newTcName);
	}

    public void onStartTestCase(TcInfo info) {
        if (tcListener != null) {
            tcListener.tcChanged(info.testCaseId);
        }
    }

    @Override
    public void onLogMsg(LogMsg msg) {
        // TODO Auto-generated method stub
        System.out.println(msg);
        
        if (msg.tc != null) {
            SutPathsFiles sutPathsFiles = Factory.getSutPathsFiles();
            String tcLogDir = sutPathsFiles.getSutLogPathName(msg.sut, msg.badge);
            Logger log = getTestCaseLogger(msg.tc, msg.sut, tcLogDir);
            msg.txt = "  :-)  " + msg.txt;
            switch (msg.level) {
            case "INFO":
                log.info(msg.txt);
                break;
            case "WARN":
                log.warn(msg.txt);
                break;
            case "ERROR":
                log.error(msg.txt);
                break;
            }
        }
        
    }

}
