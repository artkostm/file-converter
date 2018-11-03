package com.epam.bigdata101.hdfstask

import java.io.File

import com.epam.bigdata101.hdfstask.config.AppConfig
import com.epam.bigdata101.hdfstask.mapper.{AvroMapper, ParquetMapper}
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.avro.hadoop.io.AvroSerialization
import org.apache.avro.mapred.AvroValue
import org.apache.hadoop.io._
import org.apache.hadoop.io.serializer.JavaSerialization
import org.apache.hadoop.mrunit.mapreduce.MapDriver
import org.apache.parquet.example.data.Group
import org.apache.parquet.hadoop.example.GroupWriteSupport
import org.apache.parquet.schema.MessageTypeParser
import org.scalatest.FlatSpec

class FileConversionJobSpec extends FlatSpec {
  val headers = List("field1", "field2", "field3")
  val values = List("12", "10.3", "some text")

  def withArvroMapDriver(schemaFile: String)
                        (testCode: MapDriver[LongWritable, Text, NullWritable, AvroValue[GenericRecord]] => Unit): Unit = {
    val mapper = new AvroMapper
    val driver = MapDriver.newMapDriver(mapper)
    val config = driver.getConfiguration
    AvroSerialization.addToConfiguration(config)
    AvroSerialization.setValueWriterSchema(config, new Schema.Parser().parse(new File(schemaFile)))
    testCode(driver)
  }

  def withParquetMapDriver(schemaFile: String)
                          (testCode: MapDriver[LongWritable, Text, Void, Group] => Unit): Unit = {
    val mapper = new ParquetMapper
    val driver = MapDriver.newMapDriver(mapper)
    val config = driver.getConfiguration
    val rawschema = FileConversionJob.readFile(schemaFile)
    config.setStrings("io.serializations", config.get("io.serializations"),
      classOf[JavaSerialization].getName())
    GroupWriteSupport.setSchema(MessageTypeParser.parseMessageType(rawschema), config)
    testCode(driver)
  }

  "AvroMapper" should "convert input to avro" in withArvroMapDriver("src/test/resources/testSchema.avsc") {
    driver =>
      driver.addInput(new LongWritable(0), new Text(headers.mkString(",")))
      driver.addInput(new LongWritable(1), new Text(values.mkString(",")))

      val value = driver.run()

      assert(value.size() == 1)
      (0 until headers.size) foreach { i =>
        assert(value.get(0).getSecond.datum().get(headers(i)) == values(i))
      }
  }

  ignore should "convert input to parquet" in withParquetMapDriver("src/test/resources/testSchema.schema") {
    driver =>
      driver.addInput(new LongWritable(0), new Text(headers.mkString(",")))
      driver.addInput(new LongWritable(1), new Text(values.mkString(",")))

      val value = driver.run()

      assert(value.size() == 1)
  }
}
