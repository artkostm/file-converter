package by.artsiom.bigdata101.hdfstask

import java.io.File
import java.nio.charset.StandardCharsets

import by.artsiom.bigdata101.hdfstask.config.{AppConfig, CliParser, ExitCode}
import by.artsiom.bigdata101.hdfstask.config.ConverterType.{Avro, Parquet}
import org.apache.avro.Schema
import org.apache.avro.mapred.AvroOutputFormat
import org.apache.avro.mapreduce.AvroJob
import org.apache.commons.io.FileUtils
import org.apache.commons.logging.LogFactory
import org.apache.hadoop.conf.Configured
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.util.Tool
import org.apache.parquet.example.data.Group
import org.apache.parquet.hadoop.ParquetOutputFormat
import org.apache.parquet.hadoop.example.ExampleOutputFormat
import org.apache.parquet.hadoop.metadata.CompressionCodecName
import org.apache.parquet.schema.MessageTypeParser

import scala.util.{Failure, Success, Try}

object FileConversionJob extends Configured() with Tool with App {

  private val logger = LogFactory.getLog(FileConversionJob.getClass)

  import org.apache.hadoop.util.ToolRunner

  val code = ToolRunner.run(FileConversionJob, args)
  System.exit(code)

  override def run(args: Array[String]): Int =
    CliParser.parse(args, AppConfig(jobConfiguration = new JobConf(getConf))) match {
      case Some(appConfig) =>
        Try {
          val jobConf = appConfig.jobConfiguration
          val job     = Job.getInstance(jobConf, getClass.getName)
          job.setJarByClass(getClass)

          appConfig.converter match {
            case Avro =>
              job.setMapperClass(classOf[mapper.AvroMapper])
              jobConf.setOutputFormat(classOf[AvroOutputFormat[_]])
              AvroJob.setMapOutputValueSchema(job, new Schema.Parser().parse(new File(appConfig.schemaFile)))
            case Parquet =>
              job.setOutputValueClass(classOf[Group])
              job.setOutputFormatClass(classOf[ExampleOutputFormat])
              ExampleOutputFormat.setSchema(job, MessageTypeParser.parseMessageType(readFile(appConfig.schemaFile)))
              ParquetOutputFormat.setCompression(job, CompressionCodecName.GZIP)
              ParquetOutputFormat.setBlockSize(job, 500 * 1024 * 1024)
              job.setMapperClass(classOf[mapper.ParquetMapper])
            case _ =>
          }

          job.setNumReduceTasks(0)
          FileInputFormat.setInputPaths(job, appConfig.inputFile)
          FileOutputFormat.setOutputPath(job, appConfig.outputFile)
          job.waitForCompletion(true) match {
            case true  => ExitCode.Success
            case false => ExitCode.Failure
          }
        } match {
          case Success(code) => code
          case Failure(exception) =>
            logger.error(s"Unexpected error: ${exception.getMessage}", exception)
            ExitCode.Failure
        }
      case None => ExitCode.InvalidInput
    }

  import scala.collection.JavaConverters._

  def readFile(file: String): String =
    // cannot use sys.props("line.separator")(for Windows, it is '\r\n' while in Unix it is just '\n')
    // because MetaTypeParser reads '\r' as a type retention
    FileUtils.readLines(new File(file), StandardCharsets.UTF_8).asScala.mkString("\n")
}
