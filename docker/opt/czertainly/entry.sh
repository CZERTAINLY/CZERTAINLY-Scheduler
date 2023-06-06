#!/bin/sh

czertainlyHome="/opt/czertainly"
source ${czertainlyHome}/static-functions

log "INFO" "Launching the Scheduler"
java $JAVA_OPTS -jar ./app.jar

#exec "$@"