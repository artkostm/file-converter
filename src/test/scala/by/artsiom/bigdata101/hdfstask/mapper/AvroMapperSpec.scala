package by.artsiom.bigdata101.hdfstask.mapper

import java.io.File

import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.avro.hadoop.io.AvroSerialization
import org.apache.avro.mapred.AvroValue
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io
import org.apache.hadoop.io.{NullWritable, Text}
import org.apache.hadoop.mapreduce.{RecordWriter, TaskAttemptID}
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec

class AvroMapperSpec extends FlatSpec with MockFactory with MapperData {

  def avroMapper(configuration: => Configuration): Unit = {
    val mapper = new AvroMapper

    it should "skip header and write one record" in {
      val context = mock[mapper.Context]
      (context.getConfiguration _) expects() returning configuration anyNumberOfTimes()
      (context.write _) expects(*, *) onCall { (_: NullWritable, value: AvroValue[GenericRecord]) =>
        val record = value.datum()
        (0 until headers.size) foreach { i =>
          assert(record.get(headers(i)) == values(i))
        }
      }
      mapper.setup(context)
      mapper.map(new io.LongWritable(0), new Text(headers.mkString(",")), context)
      mapper.map(new io.LongWritable(1), new Text(values.mkString(",")), context)
    }

    it should "not skip header" in {
      val config = configuration
      config.setBoolean(HeaderSkippableMapper.SkipHeaderKey, false)
      val context = mock[mapper.Context]
      (context.getConfiguration _) expects() returning config anyNumberOfTimes()
      (context.write _) expects(*, *) onCall { (_: NullWritable, value: AvroValue[GenericRecord]) =>
        val record = value.datum()
        (0 until headers.size) foreach { i =>
          assert(record.get(headers(i)) == headers(i))
        }
      }
      mapper.setup(context)
      mapper.map(new io.LongWritable(0), new Text(headers.mkString(",")), context)
    }
  }

  def avroConfiguration(schema: String, skipHeader: Boolean = true): Configuration = {
    val config = new Configuration()
    config.setBoolean(HeaderSkippableMapper.SkipHeaderKey, skipHeader)
    AvroSerialization.addToConfiguration(config)
    AvroSerialization.setValueWriterSchema(config, new Schema.Parser().parse(new File(schema)))
    config
  }

  it should behave like avroMapper(avroConfiguration("src/test/resources/testSchema.avsc"))
}
