/* Copyright 2020, Michael Theis, Felix Schöppenthau (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package nato.ivct.gui.shared.sut;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TcLogMsgNotification implements Serializable {

    private static final long serialVersionUID = 1L;

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");

    private String sutId;
    private String badgeId;
    private String tcId;
    private String logMsg;
    private String logLevel;
    private String timeStamp;

    public String getSut() {
        return sutId;
    }


    public void setSutId(final String sutId) {
        this.sutId = sutId;
    }


    public String getBadgeId() {
        return badgeId;
    }


    public void setBadgeId(final String badgeId) {
        this.badgeId = badgeId;
    }


    public String getTcId() {
        return tcId;
    }


    public void setTcId(final String tcId) {
        this.tcId = tcId;
    }


    public String getLogMsg() {
        return logMsg;
    }


    public void setLogMsg(final String msg) {
        this.logMsg = msg;
    }


    public String getLogLevel() {
        return logLevel;
    }


    public void setLogLevel(final String logLevel) {
        this.logLevel = logLevel;
    }


    public String getTimeStamp() {
        return timeStamp;
    }


    public void setTimeStamp(final long timeStamp) {
        this.timeStamp = dateFormatter.format(new Date(timeStamp));
    }
}
