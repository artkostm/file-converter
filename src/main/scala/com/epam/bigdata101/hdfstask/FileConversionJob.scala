package com.epam.bigdata101.hdfstask

import java.io.File

import org.apache.avro.Schema
import org.apache.avro.mapred.AvroOutputFormat
import org.apache.avro.mapreduce.AvroJob
import org.apache.commons.logging.LogFactory
import org.apache.hadoop.conf.Configured
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.util.Tool

import scala.util.Try

object FileConversionJob extends Configured() with Tool with App {

  private val logger = LogFactory.getLog(FileConversionJob.getClass)

  import org.apache.hadoop.util.ToolRunner

  val code = ToolRunner.run(FileConversionJob, args)
  System.exit(code)

  override def run(args: Array[String]): Int = CliParser.parse(args, AppConfig()) match {
    case Some(appConfig) => Try {
      AppConfig.print(appConfig)
      val jobConf = appConfig.jobConfiguration
      val job = Job.getInstance(jobConf, getClass.getName)
      job.setJarByClass(getClass)

      jobConf.setOutputFormat(classOf[AvroOutputFormat[_]])

      job.setMapperClass(classOf[mapper.AvroMapper])
      //job.setMapperClass(classOf[mapper.ParquetMapper])

      AvroJob.setMapOutputValueSchema(job, new Schema.Parser().parse(new File(appConfig.schemaFile)))

      job.setNumReduceTasks(0)

      FileInputFormat.setInputPaths(job, appConfig.inputFile)
      FileOutputFormat.setOutputPath(job, appConfig.outputFile)
      job.waitForCompletion(true)
    } match {
      case scala.util.Success(res) =>
        println("Success: " + res)
        if (res) ExitCode.Success
        else ExitCode.Failure
      case scala.util.Failure(exception) =>
        println(s"Unexpected error: ${exception.getMessage}")
        exception.printStackTrace()
        logger.error(s"Unexpected error: ${exception.getMessage}", exception)
        ExitCode.Failure
    }
    case None => ExitCode.InvalidInput
  }
}
