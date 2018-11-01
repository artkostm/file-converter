package com.epam.bigdata101.hdfstask.mapper

import java.io.File

import com.epam.bigdata101.hdfstask.AppConfig
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.hadoop.io._
import org.apache.hadoop.mapreduce.Mapper

import scala.collection.JavaConverters._

class AvroMapper extends Mapper[LongWritable, Text, Void, GenericRecord]{
  private val schemaParser = new Schema.Parser()

  override def map(key: LongWritable, value: Text,
                   context: Mapper[LongWritable, Text, Void, GenericRecord]#Context): Unit =
    if (key.get() != 0L) {
      val schemaPath = context.getConfiguration.get(AppConfig.SchemaFilePathKey)
      val schema = schemaParser.parse(new File(schemaPath))
      val record = new GenericData.Record(schema)
      val names = schema.getFields.asScala.map(_.name())
      val values = value.toString.split(",")
      values.zipWithIndex.foreach {
        case (v, i) => record.put(names(i), v)
      }

      context.write(null, record)
    }
}
