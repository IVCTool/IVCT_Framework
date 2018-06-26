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

# call the TC exec
chmod a+x /root/application/TC.exec/bin/TC.exec

# Hack the LRC_CLASSPATH into CLASSPATH of TC_Runner launch script
sed -i -e 's#CLASSPATH=#CLASSPATH=$LRC_CLASSPATH:#g' /root/application/TC.exec/bin/TC.exec

sh /root/application/TC.exec/bin/TC.exec
	
pid="$!"

wait "$pid"