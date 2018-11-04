package by.artsiom.bigdata101.hdfstask.mapper
import org.scalatest.FlatSpec

trait MapperData { this: FlatSpec =>
  val headers = List("field1", "field2", "field3")
  val values = List("12", "10.3", "some text")
}
