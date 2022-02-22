FROM busybox:latest

COPY IVCT.properties /root/conf/IVCT.properties
COPY IVCTsut /root/conf/IVCTsut
COPY Badges /root/conf/Badges
COPY TestSuites /root/conf/TestSuites

ENV VOLUMEDIR /root/conf/
ENV VOLUME ${VOLUMEDIR}/.
VOLUME ${VOLUMEDIR}

ENTRYPOINT [ "sh", "-c", "if [ -n \"$1\" ]; then cp -r /root/conf/* $1; echo \"Copied data to $1\"; else /bin/true; fi", "--" ]
