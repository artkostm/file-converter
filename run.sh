#!/bin/sh

HADOOP_CLASSPATH=./file-converter.jar

export HADOOP_CLASSPATH

echo "HADOOP_CLASSPATH=$HADOOP_CLASSPATH"

hadoop com.epam.bigdata101.hdfstask.FileConversionJob avro -i /user/root/destinations.csv -o /tmp/test/dir/at02 -s ./destinations.avsc