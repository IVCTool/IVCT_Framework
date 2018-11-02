FROM busybox
ENTRYPOINT [ "sh", "-c", " \
   if [ -n \"$VOLUME\" -a -n \"$1\" ]; then \
      if [ -n \"$OPTS\" ]; then \
         cp $OPTS $VOLUME $1; \
      else \
         cp -a $VOLUME $1; \
      fi; \
      echo \"Copied $VOLUME to $1\"; \
   fi" , \
"--" ]

ADD IVCT.properties /root/conf/IVCT.properties
ADD IVCTsut /root/conf/IVCTsut
ADD Badges /root/conf/Badges

ENV VOLUMEDIR /root/conf/
ENV VOLUME ${VOLUMEDIR}/.
VOLUME ${VOLUMEDIR}

ENTRYPOINT [ "sh", "-c", "if [ -n \"$1\" ]; then cp -r /root/conf/* $1; echo \"Copied data to $1\"; else /bin/true; fi", "--" ]