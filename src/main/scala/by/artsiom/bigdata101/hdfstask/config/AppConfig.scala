package com.epam.bigdata101.hdfstask.config

import java.io.File

import com.epam.bigdata101.hdfstask.mapper.HeaderSkippableMapper
import com.epam.bigdata101.hdfstask.util.HdfsUtil
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.mapred.JobConf

final case class AppConfig(
    inputFile: Path = new Path(Path.CUR_DIR),
    outputFile: Path = new Path(Path.CUR_DIR),
    schemaFile: String = Path.CUR_DIR,
    converter: ConverterType.Converter = new ConverterType.Converter {},
    skipHeaders: Boolean = true,
    jobConfiguration: JobConf = new JobConf(),
    hdfsFactory: Configuration => HdfsUtil = new HdfsUtil(_)
)

object ConverterType {
  sealed trait Converter
  case object Avro    extends Converter
  case object Parquet extends Converter
}

object CliParser extends scopt.OptionParser[AppConfig]("file-converter") {
  import ConverterType._

  head("File-Converter", "0.1")

  opt[String]('i', "in")
    .required()
    .valueName("<hdfs file>")
    .action((x, c) => c.copy(inputFile = new Path(x)))
    .text("in is a required file property")

  opt[String]('o', "out")
    .required()
    .valueName("<hdfs directory>")
    .action((x, c) => c.copy(outputFile = new Path(x)))
    .text("out is a required file property")

  opt[String]('s', "schema")
    .required()
    .valueName("<local file>")
    .validate(new File(_).exists() match {
      case true  => success
      case false => failure("Schema file does not exist!")
    })
    .action((x, c) => c.copy(schemaFile = x))
    .text("schema is a required file property")

  opt[Unit]('h', "header")
    .action { (_, c) =>
      c.jobConfiguration.setBoolean(HeaderSkippableMapper.SkipHeaderKey, false)
      c.copy(skipHeaders = false)
    }
    .text("header is a flag. if present, header will not be ignored")

  cmd("avro").action((_, c) => c.copy(converter = ConverterType.Avro)).text("avro is a command.")

  cmd("parquet").action((_, c) => c.copy(converter = ConverterType.Parquet)).text("parquet is a command.")

  checkConfig(_.converter match {
    case Avro | Parquet => success
    case _              => failure("Please use either [avro] or [parquet] command!")
  })

  checkConfig { c =>
    val hdfs = c.hdfsFactory(c.jobConfiguration)
    if (!hdfs.exists(c.inputFile)) {
      failure("Input file does not exist!")
    } else if (hdfs.exists(c.outputFile)) {
      failure(s"Output file already exists: ${hdfs.makeQualified(c.outputFile).toString}")
    } else {
      success
    }
  }
}
