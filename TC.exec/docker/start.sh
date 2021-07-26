#!/bin/sh

# Initialise the PID of the application
pid=0

# define the SIGTERM-handler
term_handler() {
  echo 'Handler called'
  if [ $pid -ne 0 ]; then
    kill -SIGTERM "$pid"
    wait "$pid"
  fi
  exit 143; # 128 + 15 -- SIGTERM
}

# on signal execute the specified handler
trap 'term_handler' SIGTERM

# run application in the background and set the PID
echo "Starting the Dockerized IVCT Test Case Engine"
exec java -cp $( cat /app/jib-classpath-file ):"$LRC_CLASSPATH" $( cat /app/jib-main-class-file )

pid="$!"

wait "$pid"