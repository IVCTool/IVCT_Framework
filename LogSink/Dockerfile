FROM openjdk:8-jre-alpine

ARG version

ADD ./build/distributions/LogSink-${version}.zip /root/application/

RUN ln -s /root/application/LogSink-${version} /root/application/LogSink

RUN chmod +x /root/application/LogSink/bin/LogSink

CMD ["/root/application/LogSink/bin/LogSink"]