package com.epam.bigdata101.hdfstask.mapper

import org.apache.hadoop.io.{LongWritable, NullWritable, Text}
import org.apache.hadoop.mapreduce.Mapper
import org.apache.parquet.example.data.{Group, GroupFactory}
import org.apache.parquet.example.data.simple.SimpleGroupFactory
import org.apache.parquet.hadoop.example.GroupWriteSupport

import scala.collection.JavaConverters._

class ParquetMapper extends HeaderSkippableMapper[LongWritable, Text, Void, Group] {
  private var groupFactory: GroupFactory = _

  override def setup(context: Mapper[LongWritable, Text, Void, Group]#Context): Unit =
    groupFactory = new SimpleGroupFactory(GroupWriteSupport.getSchema(context.getConfiguration))

  override def process(key: LongWritable,
                       value: Text,
                       context: Mapper[LongWritable, Text, Void, Group]#Context): Unit = {
    val values = value.toString.split(",")
    val group  = groupFactory.newGroup()

    val groupType = group.getType()
    val names     = groupType.getFields.asScala.map(_.getName)

    values.zipWithIndex.foreach {
      case (v, i) => group.add(names(i), v)
    }

    context.write(null, group)
  }
}
