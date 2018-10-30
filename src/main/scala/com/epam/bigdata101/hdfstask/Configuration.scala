package com.epam.bigdata101.hdfstask

final case class Configuration(
                                inputFilePath: String = ".",
                                outputFilePath: String = ".",
                                toAvro: Boolean = false,
                                toParquet: Boolean = false
                              )

object CliParser extends scopt.OptionParser[Configuration]("file-converter") {
  head("File-Converter", "0.1")

  opt[String]('i', "in").required().valueName("<file>").action((x, c) => c.copy(inputFilePath = x)).
    text("in is a required file property")
  opt[String]('o', "out").required().valueName("<file>").action((x, c) => c.copy(outputFilePath = x)).
    text("out is a required file property")

  cmd("avro").action((_, c) => c.copy(toAvro = true)).text("avro is a command.").
    children()

  cmd("parquet").action((_, c) => c.copy(toParquet = true)).text("parquet is a command.").
    children()

  checkConfig(c =>
    if (c.toAvro == c.toParquet) failure("avro and parquet commands cannot be used together")
    else success
  )
}
