package by.artsiom.bigdata101.hdfstask.mapper

import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.parquet.example.data.{Group, GroupFactory}
import org.apache.parquet.example.data.simple.SimpleGroupFactory
import org.apache.parquet.hadoop.example.GroupWriteSupport

import scala.collection.JavaConverters._

/**
  * Mapper to convert a text file to a parquet file
  */
class ParquetMapper extends HeaderSkippableMapper[LongWritable, Text, Void, Group, GroupFactory] {

  withSetup { context =>
    new SimpleGroupFactory(GroupWriteSupport.getSchema(context.getConfiguration))
  }

  withMap { (_, value, context, groupFactory) =>
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
