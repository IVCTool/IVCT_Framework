FROM app-docker136.hex.tno.nl/pitch/lrc:5.4.0.0-alpine

ARG version

ADD ./build/distributions/TC.exec-${version}.zip /root/application/

ADD ./docker/start.sh /root/application/start.sh

RUN unzip /root/application/TC.exec-${version}.zip -d /root/application/ && rm /root/application/TC.exec-${version}.zip

RUN ln -s /root/application/TC.exec-${version} /root/application/TC.exec

RUN chmod a+x /root/application/start.sh

ENV IVCT_CONF /root/application

ENV LRC_ENTRYPOINT=/root/application/start.sh
