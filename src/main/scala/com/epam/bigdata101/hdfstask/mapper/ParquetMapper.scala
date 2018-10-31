package com.epam.bigdata101.hdfstask.mapper

import org.apache.avro.mapred.AvroKey
import org.apache.hadoop.io.{LongWritable, NullWritable, Text}
import org.apache.hadoop.mapreduce.Mapper

class ParquetMapper extends Mapper[LongWritable, Text, AvroKey[String], NullWritable] {

}
