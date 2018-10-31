package com.epam.bigdata101.hdfstask

import org.apache.hadoop.fs.Path

object ConverterType {
  sealed trait Converter
  case object Avro extends Converter
  case object Parquet extends Converter
}

final case class AppConfig(
                                inputFile: Path = new Path(Path.CUR_DIR),
                                outputFile: Path = new Path(Path.CUR_DIR),
                                converter: ConverterType.Converter = new ConverterType.Converter {},
                                skipHeaders: Boolean = true
                              )

object CliParser extends scopt.OptionParser[AppConfig]("file-converter") {
  import ConverterType._

  head("File-Converter", "0.1")

  opt[String]('i', "in").required().valueName("<file>").action((x, c) => c.copy(inputFile = new Path(x))).
    text("in is a required file property")
  opt[String]('o', "out").required().valueName("<file>").action((x, c) => c.copy(outputFile = new Path(x))).
    text("out is a required file property")

  cmd("avro").action((_, c) => c.copy(converter = ConverterType.Avro)).text("avro is a command.").
    children()

  cmd("parquet").action((_, c) => c.copy(converter = ConverterType.Parquet)).text("parquet is a command.").
    children()

  opt[Unit]('h', "header").action((_, c) => c.copy(skipHeaders = false)).text("header is a flag. if present, header will not be ignored")

  checkConfig(_.converter match {
    case Avro | Parquet => success
    case _ => failure("Please use either [avro] or [parquet] command!")
  })
}
