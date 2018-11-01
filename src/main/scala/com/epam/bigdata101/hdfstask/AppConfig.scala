package com.epam.bigdata101.hdfstask

import java.io.{File, StringWriter}

import org.apache.commons.logging.LogFactory
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.mapred.JobConf

object ConverterType {
  sealed trait Converter
  case object Avro extends Converter
  case object Parquet extends Converter
}

final case class AppConfig(
                                inputFile: Path = new Path(Path.CUR_DIR),
                                outputFile: Path = new Path(Path.CUR_DIR),
                                schemaFile: String = Path.CUR_DIR,
                                converter: ConverterType.Converter = new ConverterType.Converter {},
                                skipHeaders: Boolean = true
                              ) {
  lazy val jobConfiguration: JobConf = new JobConf()
  lazy val hdfs: FileSystem = FileSystem.get(jobConfiguration)
}

object AppConfig {
  private val logger = LogFactory.getLog(AppConfig.getClass)
  val SchemaFilePathKey = "hdfstask.schema.file"

  private def printJobConfig(jobConf: JobConf): Unit = {
    val writer = new StringWriter()
    jobConf.writeXml(writer)
    logger.debug(writer.toString)
  }

  def print(appConfig: AppConfig): Unit = {
    logger.debug("Application configuration: " + AppConfig.unapply(appConfig).get)
    printJobConfig(appConfig.jobConfiguration)
  }
}

object CliParser extends scopt.OptionParser[AppConfig]("file-converter") {
  import ConverterType._

  head("File-Converter", "0.1")

  opt[String]('i', "in").required().valueName("<file>").action((x, c) => c.copy(inputFile = new Path(x))).
    text("in is a required file property")

  opt[String]('o', "out").required().valueName("<file>").action((x, c) => c.copy(outputFile = new Path(x))).
    text("out is a required file property")

  opt[String]('s', "schema").required().valueName("<file>").validate(new File(_).exists() match {
    case true => success
    case false => failure("Schema file does not exist!")
  }).action{ (x, c) =>
    c.jobConfiguration.set(AppConfig.SchemaFilePathKey, x)
    c.copy(schemaFile = x)
  }.text("schema is a required file property")

  opt[Unit]('h', "header").action((_, c) => c.copy(skipHeaders = false))
    .text("header is a flag. if present, header will not be ignored")


  cmd("avro").action((_, c) => c.copy(converter = ConverterType.Avro)).text("avro is a command.")

  cmd("parquet").action((_, c) => c.copy(converter = ConverterType.Parquet)).text("parquet is a command.")

  checkConfig(_.converter match {
    case Avro | Parquet => success
    case _ => failure("Please use either [avro] or [parquet] command!")
  })

  checkConfig { c =>
    if (!c.hdfs.exists(c.inputFile)) {
      failure("Input file does not exist!")
    } else if (c.hdfs.exists(c.outputFile)) {
      failure(s"Output file already exists: ${c.hdfs.makeQualified(c.outputFile).toString}")
    } else {
      success
    }
  }
}
