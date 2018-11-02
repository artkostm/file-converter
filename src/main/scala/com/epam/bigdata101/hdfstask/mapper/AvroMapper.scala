package com.epam.bigdata101.hdfstask.mapper

import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.avro.mapred.AvroValue
import org.apache.hadoop.io._
import org.apache.hadoop.mapreduce.Mapper

import scala.collection.JavaConverters._
import scala.collection.mutable

class AvroMapper extends HeaderSkippableMapper[LongWritable, Text, NullWritable, AvroValue[GenericRecord]] {
  private var schema: Schema                = _
  private var names: mutable.Buffer[String] = _

  override def setup(context: Mapper[LongWritable, Text, NullWritable, AvroValue[GenericRecord]]#Context): Unit = {
    val schemaString = context.getConfiguration.get("avro.serialization.value.writer.schema")
    schema = new Schema.Parser().parse(schemaString)
    names = schema.getFields.asScala.map(_.name())
  }

  override def process(key: LongWritable,
                       value: Text,
                       context: Mapper[LongWritable, Text, NullWritable, AvroValue[GenericRecord]]#Context): Unit = {
    val record = new GenericData.Record(schema)
    val values = value.toString.split(",")
    values.zipWithIndex.foreach {
      case (v, i) => record.put(names(i), v)
    }

    context.write(NullWritable.get(), new AvroValue(record))
  }
}
