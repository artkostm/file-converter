package com.epam.bigdata101.hdfstask.config
import org.apache.hadoop.fs.{FSDataOutputStreamBuilder, FileSystem, Path}
import org.scalamock.scalatest.MockFactory
import org.scalamock.util.Defaultable
import org.scalatest.FlatSpec

class CliParserSpec extends FlatSpec with MockFactory {
  val OutputFile = new Path("/output/")
  val InputFile = new Path("/input")

  def withValidAppConfig(test: AppConfig => Unit): Unit = {
    implicit val dFSDOSB = new Defaultable[FSDataOutputStreamBuilder[_, _]] {
      override val default: FSDataOutputStreamBuilder[_, _] = null
    }
    val hdfsMock = mock[FileSystem]
    inAnyOrderWithLogging {
      (hdfsMock.exists _) expects InputFile returning true
      (hdfsMock.exists _) expects OutputFile returning false
    }
    test(AppConfig(inputFile = InputFile, outputFile = OutputFile, converter = ConverterType.Avro, hdfsFactory = _ => hdfsMock))
  }

  it should "return correct app configuration object" in withValidAppConfig { config =>
    CliParser.parse(Seq(), config) match {
      case Some(AppConfig(_, _, _, converter, _, _, _)) => assert(converter == ConverterType.Avro)
      case _ => fail("valid app config cannot be None")
    }
  }
}
