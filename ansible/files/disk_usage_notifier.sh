#!/bin/bash

# Manual version: 1
# (In order to try be able to find differences after this script has been
# copied.)

# The purpose of this script is to check the disk space of mounted
# volumes (only). Notify hubot _once_ if the available space is less
# than 90%. Will notify again after restart of server.

TIMER_FILE=/tmp/disc_space_timer_file.lock
NOTIFY_LOCK=/tmp/has_notified_about_disk.lock

curl -V > /dev/null || touch /tmp/use_wget

function check_mounted_disk_volume {
  test -f $NOTIFY_LOCK && exit 0
  for PERCENTAGE in `df -h 2> /dev/null | grep "dev.s.. " | tr -d '%' | awk '{ print $5 }'` ; do
    if (( $PERCENTAGE > 90 )); then
      touch $NOTIFY_LOCK
      echo "Disk space usage: ${PERCENTAGE}% on `hostname`"
      echo '{"source":"' >> $NOTIFY_LOCK
      hostname >> $NOTIFY_LOCK
      echo '", "icon":":floppy_disk:", "message": "Volume space ' >> $NOTIFY_LOCK
      echo ${PERCENTAGE} >> $NOTIFY_LOCK
      echo '% used"}' >> $NOTIFY_LOCK
      if [[ -e /tmp/use_wget ]] ; then
        wget -q --post-file=${NOTIFY_LOCK} http://hubot/hubot/say/
      else
        curl -s -X POST -H "Content-Type: application/json"  --data @${NOTIFY_LOCK} http://hubot/hubot/say/
      fi
    fi
  done
}

if [[ -e $TIMER_FILE ]] ; then
  find $TIMER_FILE -cmin +1 -delete
  if [[ ! -e $TIMER_FILE ]] ; then
    check_mounted_disk_volume
  fi
else
  touch $TIMER_FILE
fi
