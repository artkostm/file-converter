package com.epam.bigdata101.hdfstask

import org.apache.avro.mapred.AvroOutputFormat
import org.apache.commons.logging.LogFactory
import org.apache.hadoop.conf.Configured
import org.apache.hadoop.mapred.{FileInputFormat, FileOutputFormat, JobConf}
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.util.Tool

object FileConversionJob extends Configured() with Tool with App {

  private val logger = LogFactory.getLog(FileConversionJob.getClass)

  import org.apache.hadoop.util.ToolRunner

  val code = ToolRunner.run(FileConversionJob, args)
  System.exit(code)

  override def run(args: Array[String]): Int = CliParser.parse(args, AppConfig()) match {
    case Some(appConfig) =>
      AppConfig.print(appConfig)
      val jobConf = appConfig.jobConfiguration
      val job = Job.getInstance(jobConf, getClass.getName)

      jobConf.setOutputFormat(classOf[AvroOutputFormat[_]])
      job.setMapperClass(classOf[mapper.AvroMapper])
      job.setMapperClass(classOf[mapper.ParquetMapper])
      FileInputFormat.setInputPaths(jobConf, appConfig.inputFile)
      FileOutputFormat.setOutputPath(jobConf, appConfig.outputFile)
      ExitCode.Success
    case None => ExitCode.InvalidInput
  }
}
