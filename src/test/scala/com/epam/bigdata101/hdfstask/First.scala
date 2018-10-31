package com.epam.bigdata101.hdfstask

import com.epam.bigdata101.hdfstask.mapper.AvroMapper
import org.apache.hadoop.io._
import org.apache.hadoop.mrunit.mapreduce.MapDriver
import org.scalatest.FlatSpec

class First extends FlatSpec {
  "An empty Set" should "have size 0" in {
    val mapper = new AvroMapper
    val driver = MapDriver.newMapDriver(mapper)

    driver.addInput(null, new Text(""))
    driver.run()
  }
}
