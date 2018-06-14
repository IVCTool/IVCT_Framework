FROM openjdk:8-jre-alpine

ARG version

#Install application
ADD ./build/distributions/LogSink-${version}.zip /root/application/

RUN unzip /root/application/LogSink-${version}.zip -d /root/application/ && rm /root/application/LogSink-${version}.zip

RUN ls /root/application/

RUN mv /root/application/LogSink-${version} /root/application/LogSink

RUN chmod +x /root/application/LogSink/bin/LogSink

CMD ["/root/application/LogSink/bin/LogSink"]