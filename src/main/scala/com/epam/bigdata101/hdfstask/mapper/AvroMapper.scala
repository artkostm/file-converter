package com.epam.bigdata101.hdfstask.mapper

import java.io.File

import com.epam.bigdata101.hdfstask.AppConfig
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.avro.mapred.AvroValue
import org.apache.hadoop.io._
import org.apache.hadoop.mapreduce.Mapper

import scala.collection.JavaConverters._

class AvroMapper extends Mapper[LongWritable, Text, NullWritable, AvroValue[GenericRecord]]{
  override def map(key: LongWritable, value: Text,
                   context: Mapper[LongWritable, Text, NullWritable, AvroValue[GenericRecord]]#Context): Unit =
    if (context.getConfiguration.getBoolean(AppConfig.SkipHeaderKey, true) && key.get() == 0L) {
      return
    } else {
      val schemaString = context.getConfiguration.get("avro.serialization.value.writer.schema")
      val schema = new Schema.Parser().parse(schemaString)
      val record = new GenericData.Record(schema)
      val names = schema.getFields.asScala.map(_.name())
      val values = value.toString.split(",")
      values.zipWithIndex.foreach {
        case (v, i) => record.put(names(i), v)
      }

      context.write(NullWritable.get(), new AvroValue(record))
    }
}
