package com.epam.bigdata101.hdfstask

import java.io.File

import com.epam.bigdata101.hdfstask.mapper.AvroMapper
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.avro.hadoop.io.AvroSerialization
import org.apache.avro.mapred.AvroValue
import org.apache.hadoop.io._
import org.apache.hadoop.mrunit.mapreduce.MapDriver
import org.scalatest.FlatSpec

class FileConversionJobSpec extends FlatSpec {
  val headers = List("field1", "field2", "field3")
  val values = List("12", "10.3", "some text")

  def withArvroMapDriver(schemaFile: String)
                        (testCode: (MapDriver[LongWritable, Text, NullWritable, AvroValue[GenericRecord]]) => Unit): Unit = {
    val mapper = new AvroMapper
    val driver = MapDriver.newMapDriver(mapper)
    val config = driver.getConfiguration
    AvroSerialization.addToConfiguration(config)
    AvroSerialization.setValueWriterSchema(config, new Schema.Parser().parse(new File(schemaFile)))
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
}
