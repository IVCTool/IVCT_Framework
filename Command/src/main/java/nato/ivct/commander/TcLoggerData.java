/* Copyright 2020, Reinhard Herzog, Johannes Mulder (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package nato.ivct.commander;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import ch.qos.logback.classic.Level;

public class TcLoggerData {
	private static Map<String, LoggerData> loggerDataMap = new HashMap<>();
	private static String levelStr = "INFO";

	public static LoggerData getLoggerData(final String loggerName) {
		LoggerData loggerData = loggerDataMap.get(loggerName);
		return loggerData;
	}

	public static void addLoggerData(final Logger logger, final String loggerName, final String sutName, final String badgeName, final String tcName) {
		ch.qos.logback.classic.Logger lo = (ch.qos.logback.classic.Logger) logger;
		setLogLevel(lo, levelStr);
		LoggerData loggerData = new LoggerData(logger, sutName, badgeName, tcName);
		loggerDataMap.put(loggerName, loggerData);
	}
	
	public static Level getLogLevel() {
		Level level = null;
		switch (levelStr) {
		case "ERROR":
			level = Level.ERROR;
			break;
		case "WARNING":
			level = Level.WARN;
			break;
		case "INFO":
			level = Level.INFO;
			break;
		case "DEBUG":
			level = Level.DEBUG;
			break;
		case "TRACE":
			level = Level.TRACE;
			break;
		}
		return level;
	}

	public static Set<Logger> getLoggers() {
		Set<Logger> loggers = new HashSet<>();
		for (Map.Entry<String, LoggerData> entry : loggerDataMap.entrySet()) {
			loggers.add(entry.getValue().logger);
		}
		return loggers;
	}

	public static void removeLogger(final String loggerName) {
		loggerDataMap.remove(loggerName);
	}

	public static void setLogLevel(final String level) {
		levelStr = level;
		for (Map.Entry<String, LoggerData> entry : loggerDataMap.entrySet()) {
			ch.qos.logback.classic.Logger lo = (ch.qos.logback.classic.Logger) entry.getValue().logger;
			setLogLevel(lo, level);
			entry.getValue().logger.warn("setLogLevel {}", level);
		}
	}

	public static void setLogLevel(final ch.qos.logback.classic.Logger lo, final String level) {
		switch (level) {
		case "ERROR":
			lo.setLevel(Level.ERROR);
			break;
		case "WARNING":
			lo.setLevel(Level.WARN);
			break;
		case "INFO":
			lo.setLevel(Level.INFO);
			break;
		case "DEBUG":
			lo.setLevel(Level.DEBUG);
			break;
		case "TRACE":
			lo.setLevel(Level.TRACE);
			break;
		}
	}
}
