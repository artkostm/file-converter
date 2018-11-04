package by.artsiom.bigdata101.hdfstask.mapper

import by.artsiom.bigdata101.hdfstask.FileConversionJob
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.{RecordWriter, TaskAttemptID}
import org.apache.parquet.example.data.Group
import org.apache.parquet.hadoop.example.GroupWriteSupport
import org.apache.parquet.schema.MessageTypeParser
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec

class ParquetMapperSpec extends FlatSpec with MockFactory with MapperData {
  def parquetMapper(configuration: => Configuration): Unit = {
    val mapper = new ParquetMapper

    it should "skip header and write one record" in {
      val writer = mock[RecordWriter[Void, Group]]
      val context = new mapper.Context(configuration, new TaskAttemptID(), null, writer, null, null, null)
      (writer.write _) expects(*, *) onCall { (_, value: Group) =>
        (0 until headers.size) foreach { i =>
          assert(value.getString(headers(i), 0) == values(i))
        }
      }
      mapper.setup(context)
      mapper.map(new LongWritable(0), new Text(headers.mkString(",")), context)
      mapper.map(new LongWritable(1), new Text(values.mkString(",")), context)
    }

    it should "not skip header" in {
      val config = configuration
      config.setBoolean(HeaderSkippableMapper.SkipHeaderKey, false)
      val writer = mock[RecordWriter[Void, Group]]
      val context = new mapper.Context(config, new TaskAttemptID(), null, writer, null, null, null)
      (writer.write _) expects(*, *) onCall { (_, value: Group) =>
        (0 until headers.size) foreach { i =>
          assert(value.getString(headers(i), 0) == headers(i))
        }
      }
      mapper.setup(context)
      mapper.map(new LongWritable(0), new Text(headers.mkString(",")), context)
    }
  }

  def parquetConfiguration(schema: String, skipHeader: Boolean = true): Configuration = {
    val config = new Configuration()
    config.setBoolean(HeaderSkippableMapper.SkipHeaderKey, skipHeader)
    val rawschema = FileConversionJob.readFile(schema)
    GroupWriteSupport.setSchema(MessageTypeParser.parseMessageType(rawschema), config)
    config
  }

  it should behave like parquetMapper(parquetConfiguration("src/test/resources/testSchema.schema"))
}
