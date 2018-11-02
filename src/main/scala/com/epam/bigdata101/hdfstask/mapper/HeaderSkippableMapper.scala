package com.epam.bigdata101.hdfstask.mapper

import com.epam.bigdata101.hdfstask.AppConfig
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.mapreduce.Mapper

class HeaderSkippableMapper[InKey <: LongWritable, InValue, OutKey, OutValue](
    process: (InKey, InValue, Mapper[InKey, InValue, OutKey, OutValue]#Context) => Unit
) extends Mapper[InKey, InValue, OutKey, OutValue] {
  override def map(key: InKey, value: InValue, context: Mapper[InKey, InValue, OutKey, OutValue]#Context): Unit =
    if (!context.getConfiguration.getBoolean(AppConfig.SkipHeaderKey, true) || key.get() != 0L) {
      process(key, value, context)
    }
}
