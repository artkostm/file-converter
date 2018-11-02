package com.epam.bigdata101.hdfstask.mapper

import org.apache.avro.generic.GenericRecord
import org.apache.hadoop.io.{LongWritable, NullWritable, Text}

class ParquetMapper
    extends HeaderSkippableMapper[LongWritable, Text, NullWritable, GenericRecord]({ (key, value, context) =>
      })
