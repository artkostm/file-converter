package com.epam.bigdata101.hdfstask.config

import com.epam.bigdata101.hdfstask.util.HdfsUtil
import org.apache.hadoop.fs._
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec

class CliParserSpec extends FlatSpec with MockFactory {
  val OutputFilePath = "/output/"
  val InputFilePath = "/input"
  val OutputFile = new Path(OutputFilePath)
  val InputFile = new Path(InputFilePath)

  def withValidAppConfig(test: AppConfig => Unit): Unit = {
    val hdfsMock = mock[HdfsUtil]
    inAnyOrderWithLogging {
      (hdfsMock.exists _) expects InputFile returning true
      (hdfsMock.exists _) expects OutputFile returning false
    }
    test(AppConfig(inputFile = InputFile, outputFile = OutputFile, converter = ConverterType.Avro, hdfsFactory = _ => hdfsMock))
  }

  def withUnknownPath(test: AppConfig => Unit): Unit = {
    val hdfsMock = mock[HdfsUtil]
    inAnyOrderWithLogging {
      (hdfsMock.exists _) expects * returning false
    }
    test(AppConfig(inputFile = InputFile, outputFile = OutputFile, converter = ConverterType.Avro, hdfsFactory = _ => hdfsMock))
  }

  def withExistingPath(test: AppConfig => Unit): Unit = {
    val hdfsMock = mock[HdfsUtil]
    inAnyOrderWithLogging {
      (hdfsMock.exists _) expects * returning true repeat 2
      (hdfsMock.makeQualified _) expects * returning new Path("/unknown")
    }
    test(AppConfig(inputFile = InputFile, outputFile = OutputFile, converter = ConverterType.Avro, hdfsFactory = _ => hdfsMock))
  }

  it should "return correct app configuration object for avro" in withValidAppConfig { config =>
    CliParser.parse(Seq("avro", "--in", InputFilePath, "--out", OutputFilePath, "--schema", "."), config) match {
      case Some(AppConfig(_, _, _, converter, _, _, _)) => assert(converter == ConverterType.Avro)
      case _ => fail("valid app config cannot be None")
    }
  }

  it should "return correct app configuration object for parquet" in withValidAppConfig { config =>
    CliParser.parse(Seq("parquet", "-i", InputFilePath, "-o", OutputFilePath, "-s", "."), config) match {
      case Some(AppConfig(_, _, _, converter, _, _, _)) => assert(converter == ConverterType.Parquet)
      case _ => fail("valid app config cannot be None")
    }
  }

  it should "return None for args with no command" in withValidAppConfig { config =>
    CliParser.parse(Seq("-i", InputFilePath, "-o", OutputFilePath, "-s", "."), config.copy(converter = null)) match {
      case Some(_) => fail("a command should be specified")
      case None => succeed
    }
  }

  it should "return None for args with no input" in withUnknownPath { config =>
    CliParser.parse(Seq("parquet", "-o", OutputFilePath, "-s", "."), config) match {
      case Some(_) => fail("cannot succeed for no input specified")
      case None => succeed
    }
  }

  it should "return None for args with existing output" in withExistingPath { config =>
    CliParser.parse(Seq("parquet", "-i", InputFilePath, "-o", OutputFilePath, "-s", "."), config) match {
      case Some(_) => fail("cannot succeed for no output specified")
      case None => succeed
    }
  }

  it should "return None for args with wrong schema" in withValidAppConfig { config =>
    CliParser.parse(Seq("parquet", "-i", InputFilePath, "-o", OutputFilePath, "-s", "./unknown"), config) match {
      case Some(_) => fail("cannot succeed for wrong schema")
      case None => succeed
    }
  }
}
