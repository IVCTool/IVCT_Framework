FROM alpine:3.3

ADD IVCT.properties /root/conf/IVCT.properties
ADD IVCTsut /root/conf/IVCTsut
ADD Badges /root/conf/Badges

ENTRYPOINT [ "sh", "-c", "if [ -n \"$1\" ]; then cp -r /root/conf/* $1; echo \"Copied data to $1\"; else /bin/true; fi", "--" ]