package com.epam.bigdata101.hdfstask

import com.epam.bigdata101.hdfstask.mapper.AvroMapper
import org.apache.hadoop.io._
import org.apache.hadoop.mrunit.mapreduce.MapDriver
import org.scalatest.FlatSpec

class First extends FlatSpec {
  "An empty Set" should "have size 0" in {
    val mapper = new AvroMapper
    val driver = MapDriver.newMapDriver(mapper)

    driver.addInput(new LongWritable(0), new Text(""))
    val value = driver.run()

    assert(value.isEmpty)
  }
}
