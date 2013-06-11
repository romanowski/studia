#!/bin/bash

# check whether TDBROOT environment variable is set

if [ ! -z $1 ]; then
	export TDBROOT=$1;
fi;

if [ -z $TDBROOT ]; then
	echo "Please set \$TDBROOT variable to the root of TDB installation!";
	echo "Alternatively you can pass root of TDB installation as argument.";
	exit 1;
fi;

export PATH=$TDBROOT/bin:$PATH
export LOG_FILE=/tmp/tdb_import.log

echo "TDBROOT=$TDBROOT";
echo "LOG_FILE=$LOG_FILE"

echo "Removing old data (if any)..."
rm -rf /tmp/b1 /tmp/b2

echo "Importing data for dataset b1...";
tdbloader --desc b1_config.ttl b1_data.ttl > $LOG_FILE 2>>$LOG_FILE
echo "Importing data for dataset b2...";
tdbloader --desc b2_config.ttl b2_data.ttl >> $LOG_FILE 2>>$LOG_FILE
echo "Done!"

exit 0;