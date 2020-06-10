FROM tomcat:jre8-slim

ARG version

RUN rm -r /usr/local/tomcat/webapps/*
RUN rm /usr/local/tomcat/conf/server.xml
COPY ./docker/tomcat-users.xml /usr/local/tomcat/conf/
COPY ./docker/server.xml /usr/local/tomcat/conf/
COPY ./nato.ivct.gui.server.app.war/build/libs/ivct.gui.server##${version}.war /usr/local/tomcat/webapps/
COPY ./nato.ivct.gui.ui.html.app.war/build/libs/ivct.gui.ui.html##${version}.war /usr/local/tomcat/webapps/
