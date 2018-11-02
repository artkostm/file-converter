package com.epam.bigdata101.hdfstask.mapper

import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.avro.mapred.AvroValue
import org.apache.hadoop.io._

import scala.collection.JavaConverters._

class AvroMapper
    extends HeaderSkippableMapper[LongWritable, Text, NullWritable, AvroValue[GenericRecord]]({ (_, value, context) =>
      val schemaString = context.getConfiguration.get("avro.serialization.value.writer.schema")
      val schema       = new Schema.Parser().parse(schemaString)
      val record       = new GenericData.Record(schema)
      val names        = schema.getFields.asScala.map(_.name())
      val values       = value.toString.split(",")
      values.zipWithIndex.foreach {
        case (v, i) => record.put(names(i), v)
      }

      context.write(NullWritable.get(), new AvroValue(record))
    })
