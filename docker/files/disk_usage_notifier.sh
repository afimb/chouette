#!/bin/bash

# Manual version: 5
# (In order to try be able to find differences after this script has been
# copied.)

# The purpose of this script is to check the disk space of mounted
# volumes (only). Notify hubot _once_ if the available space is less
# than 85%. Will notify every 15 minutes

TIMER_FILE=/tmp/disc_space_timer_file.lock
NOTIFY_LOCK=/tmp/has_notified_about_disk.lock

curl -V > /dev/null || touch /tmp/use_wget

function check_mounted_disk_volume {
  test -f $NOTIFY_LOCK && exit 0
  for PERCENTAGE in `df -h 2> /dev/null | grep "dev.s.. " | tr -d '%' | awk '{ print $5 }'` ; do
    if (( $PERCENTAGE > 85 )); then
      touch $NOTIFY_LOCK
      echo "Disk space usage: ${PERCENTAGE}% on `hostname`"
      echo -n '{"source":"' >> $NOTIFY_LOCK
      hostname | tr -d '\n' >> $NOTIFY_LOCK
      echo -n '", "icon":":floppy_disk:", "message": "Volume space ' >> $NOTIFY_LOCK
      echo -n ${PERCENTAGE} >> $NOTIFY_LOCK
      echo -n '% used"}' >> $NOTIFY_LOCK
      if [[ -e /tmp/use_wget ]] ; then
        wget -q --header 'Content-Type: application/json' --post-file=${NOTIFY_LOCK} http://hubot/hubot/say/
      else
        curl -s -X POST -H "Content-Type: application/json"  --data @${NOTIFY_LOCK} http://hubot/hubot/say/
      fi
    fi
    # If nc exists, report disk usage statistics to graphite
    if [ -x "$(command -v nc)" ]; then
      if [ -z ${ACTIVEMQ_NAME+x} ]; then
        STRIPPED_NAME=`echo $HOSTNAME | cut -d '-' -f 1`
      else
        STRIPPED_NAME="${ACTIVEMQ_NAME}"
      fi
      # TODO if gnu version, use -c as argument instead of -q0
      echo "app.$STRIPPED_NAME.diskusage $PERCENTAGE `date +%s`" | nc -q0 graphite 2003
    fi
  done
}

# Complain every 50 minutes if disk is still full
if [[ -e $NOTIFY_LOCK ]] ; then
  find $NOTIFY_LOCK -cmin +15 -delete
fi

if [[ ! -z $1 ]] ; then
  echo "Performing disk check immediately as a command line parameter was present"
  check_mounted_disk_volume
elif [[ -e $TIMER_FILE ]] ; then
  find $TIMER_FILE -cmin +1 -delete
  if [[ ! -e $TIMER_FILE ]] ; then
    check_mounted_disk_volume
  fi
else
  touch $TIMER_FILE
fi
