#!/bin/sh

HADOOP_CLASSPATH=../target/scala-2.12/file-converter.jar

export HADOOP_CLASSPATH

echo "HADOOP_CLASSPATH=$HADOOP_CLASSPATH"

hadoop by.artsiom.bigdata101.hdfstask.FileConversionJob avro -i /tmp/destinations.csv -o /tmp/test/ -s ./destinations.avsc
