#!/bin/sh

HADOOP_CLASSPATH="$HADOOP_CLASSPATH:/tmp/fileConverter/file-converter.jar"

export HADOOP_CLASSPATH

echo "HADOOP_CLASSPATH=$HADOOP_CLASSPATH"

yarn jar /tmp/fileConverter/file-converter.jar avro -i /user/root/destinations.csv -o /tmp/test/dir/at7 -s /tmp/fileConverter/destinations.avsc