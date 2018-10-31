package com.epam.bigdata101.hdfstask.mapper

import org.apache.avro.Schema
import org.apache.avro.Schema.Type
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.hadoop.io._
import org.apache.hadoop.mapreduce.Mapper

import scala.collection.JavaConverters._

class AvroMapper extends Mapper[LongWritable, Text, Void, GenericRecord]{


  override def map(key: LongWritable, value: Text,
                   context: Mapper[LongWritable, Text, Void, GenericRecord]#Context): Unit =
    if (key.get() != 0L) {
      val record = new GenericData.Record(Schema.create(Type.BYTES))
      val s = Schema.create(Type.BYTES)
      val names = s.getFields.asScala.map(_.name())
      val values = value.toString.split(",")
      values.zipWithIndex.foreach {
        case (v, i) => record.put(names(i), v)
      }

      context.write(null, record)
    }
}
