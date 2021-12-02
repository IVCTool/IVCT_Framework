#!/bin/sh

#
# Script options (exit script on command fail).
#
set -e

CURL_OPTIONS_DEFAULT=
SIGNAL_DEFAULT="SIGHUP"
INOTIFY_EVENTS='create,delete,modify,move'
INOTIFY_OPTONS='-r'

#
# Display settings on standard out.
#
echo "Runtime Mirror settings"
echo "================"
echo
echo "  Runtime:          ${RUNTIME:=/root/conf/}"
echo "  Mirror:           ${MIRROR:=/mirror}"
echo "  Inotify_Events:   ${INOTIFY_EVENTS}"
echo "  Inotify_Options:  ${INOTIFY_OPTONS}"
echo

#
# Inotify part.
#
echo "create initial mirror image"
rsync -avz ${RUNTIME} ${MIRROR}
echo "start listening for change events"
echo ${VOLUMES}
while inotifywait ${INOTIFY_OPTONS} -e ${INOTIFY_EVENTS} "${RUNTIME}"; do
    rsync -avz ${RUNTIME} ${MIRROR}
done