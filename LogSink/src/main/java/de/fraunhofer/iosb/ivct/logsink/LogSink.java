package de.fraunhofer.iosb.ivct.logsink;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.CmdHeartbeatSend;
import nato.ivct.commander.CmdLogMsgListener;
import nato.ivct.commander.CmdQuitListener;
import nato.ivct.commander.CmdStartTcListener;
import nato.ivct.commander.CmdStartTestResultListener;
import nato.ivct.commander.CmdAbortTcListener;
import nato.ivct.commander.Factory;


/**
 * Log sink for logging events collected via JMS or AMQP. For JMS use the
 * already existing JMSTopicSink from logback as first attempt.
 *
 * @author Manfred Schenk (Fraunhofer IOSB)
 */
public class LogSink implements CmdHeartbeatSend.OnCmdHeartbeatSend {

    private static Logger log     = LoggerFactory.getLogger(LogSink.class);
    //private JMSTopicSink  jmsTopicSink;0
    private static JMSLogSink  jmsLogSink;
    private static ReportEngine reportEngine = new ReportEngine();
        
    // for CmdHeartbeatSend
    private boolean health;
    private String myClassName = "LogSink";


    /**
     * Main Method of the class.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
		Factory.initialize();
        final LogSink instance = new LogSink();
        instance.init();
        
     // for CmdHeartbeatSend
        try {
             instance.sendHeartbeat(log);
             } catch (Exception ex) {
                 log.error("could not start sendHeartbeat", ex);
             }        
        
        instance.execute();
        System.exit(0);;
    }


    /**
     * initialize the LogSink.
     */
    protected void init() {
        LogSink.jmsLogSink = new JMSLogSink(reportEngine);
		(new CmdStartTestResultListener(jmsLogSink)).execute();
        (new CmdQuitListener(jmsLogSink)).execute();
        (new CmdStartTcListener(jmsLogSink)).execute();
        (new CmdLogMsgListener(jmsLogSink)).execute();
        (new CmdAbortTcListener(jmsLogSink)).execute();
    }

	public String getTestCaseResults() {
		return reportEngine.status.toString();
	}


    /**
     * execute = do wait for termination.
     */
    protected void execute() {
        log.debug("successfully initialized for topic {} .", Factory.props.getProperty("java.naming.provider.url"));
        final BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        // Loop until "exit", "quit" or "q" is typed
        log.info("Type \"quit\" to exit JMSTopicSink.");
        while (true) {
            String s;
            try {
                s = stdin.readLine();
                if(s == null) {
                	// ignore - probably running in a container so we don't have system in
                	// LogSink will be killed when the container is killed.

                    // now lets wait forever to avoid the JVM terminating immediately
                    Object lock = new Object();
                    synchronized (lock) {
                        try {
							lock.wait();
						} catch (InterruptedException e) {
							log.error("got interrupted",e);
						}
                    }
                }
                else if (s.equalsIgnoreCase("exit") || s.equalsIgnoreCase("q") || s.equalsIgnoreCase("quit")) {
                	log.info("Exiting. Kill the application if it does not exit " + "due to daemon threads.");
                    return;
                }
            }
            catch (final IOException ex) {
                log.error("Error in input.", ex);
            }
        }
    }

    // for CmdHeartbeatSend
    public void sendHeartbeat(Logger _logger) throws Exception {
        this.health = true;
        //_logger.info("LogSink create his instance of CmdHeartbeatSend: " + this); // Debug
        CmdHeartbeatSend heartbeatSend = new CmdHeartbeatSend(this);
        heartbeatSend.execute();
    }

  public String getMyClassName() {
    return myClassName;
  }

  public boolean getMyHealth() {
    return health;
  }

}
