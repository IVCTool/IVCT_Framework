package de.fraunhofer.iosb.testrunner;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import nato.ivct.commander.*;


public class ReportAppender extends AppenderBase<ILoggingEvent> {

    private String prefix;
    private CmdSendLogMsg logReporter;

    public ReportAppender() {
      logReporter = new CmdSendLogMsg("TC", "SUT", "BADGE");   // <--- get the real values here!!!!
}
    
    @Override
    protected void append(ILoggingEvent eventObject) {
        logReporter.send(eventObject.toString());
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
