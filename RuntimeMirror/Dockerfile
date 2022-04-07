FROM alpine:latest

ARG https_proxy

LABEL maintainer="Reinhard.Herzog@iosb.fraunhofer.de"

#
# Install all required dependencies.
#
RUN apk --update upgrade
RUN apk add rsync
RUN apk add --update inotify-tools
RUN rm -rf /var/cache/apk/*
#RUN apk --update upgrade && \
#    apk add --update inotify-tools && \
#    rm -rf /var/cache/apk/*

#
# Add named init script.
#
ADD init.sh /init.sh
RUN chmod 750 /init.sh

#
# Start named.
#
CMD ["/init.sh"]