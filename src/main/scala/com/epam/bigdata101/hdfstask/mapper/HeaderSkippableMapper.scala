package com.epam.bigdata101.hdfstask.mapper

import com.epam.bigdata101.hdfstask.AppConfig
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.mapreduce.Mapper

abstract class HeaderSkippableMapper[InKey <: LongWritable, InValue, OutKey, OutValue]
    extends Mapper[InKey, InValue, OutKey, OutValue] {
  override def map(key: InKey, value: InValue, context: Mapper[InKey, InValue, OutKey, OutValue]#Context): Unit =
    if (!context.getConfiguration.getBoolean(AppConfig.SkipHeaderKey, true) || key.get() != 0L) {
      process(key, value, context)
    }

  def process(key: InKey, value: InValue, context: Mapper[InKey, InValue, OutKey, OutValue]#Context): Unit
}
