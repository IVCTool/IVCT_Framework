FROM tomcat:jre8-slim

RUN rm -r /usr/local/tomcat/webapps/*
RUN rm /usr/local/tomcat/conf/server.xml
ADD ./docker/tomcat-users.xml /usr/local/tomcat/conf/
ADD ./docker/server.xml /usr/local/tomcat/conf/
ADD ./nato.ivct.gui.server.app.war/build/libs/ivct.gui.server##2.1.1.war /usr/local/tomcat/webapps/
ADD ./nato.ivct.gui.ui.html.app.war/build/libs/ivct.gui.ui.html##2.1.1.war /usr/local/tomcat/webapps/
