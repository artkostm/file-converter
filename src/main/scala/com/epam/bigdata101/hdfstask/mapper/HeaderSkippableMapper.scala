package com.epam.bigdata101.hdfstask.mapper

import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.mapreduce.Mapper

/**
  * An abstract mapper class which is able to skip header of a file
  *
  * By default, header will be ignored. To not ignore the header,
  * set SkipHeaderKey property 'hdfstask.csv.skip.header' to false using the Configuration object
  *
  * @tparam InKey - input key type
  * @tparam InValue - input value type
  * @tparam OutKey - output key type
  * @tparam OutValue - output value type
  * @tparam SetupValue - result calling withSetup
  */
abstract class HeaderSkippableMapper[InKey <: LongWritable, InValue, OutKey, OutValue, SetupValue]
    extends Mapper[InKey, InValue, OutKey, OutValue] {
  import HeaderSkippableMapper._

  private var setupMethod: Option[Mapper[InKey, InValue, OutKey, OutValue]#Context => SetupValue] = None
  private var mapMethod
    : Option[(InKey, InValue, Mapper[InKey, InValue, OutKey, OutValue]#Context, SetupValue) => Unit] = None
  private var setupValue: SetupValue                                                                 = _

  /**
    * Helper function to setup Mapper
    */
  protected def withSetup(setupF: Mapper[InKey, InValue, OutKey, OutValue]#Context => SetupValue): Unit =
    setupMethod = Some(setupF)

  /**
    * Helper function to process rows
    */
  protected def withMap(
      mapF: (InKey, InValue, Mapper[InKey, InValue, OutKey, OutValue]#Context, SetupValue) => Unit): Unit =
    mapMethod = Some(mapF)

  override def setup(context: Mapper[InKey, InValue, OutKey, OutValue]#Context): Unit =
    setupValue = setupMethod.map(_(context)).getOrElse(null.asInstanceOf[SetupValue])

  override def map(key: InKey, value: InValue, context: Mapper[InKey, InValue, OutKey, OutValue]#Context): Unit =
    if (!context.getConfiguration.getBoolean(SkipHeaderKey, true) || key.get() != 0L) {
      mapMethod.map(_(key, value, context, setupValue)).orNull
    }
}

object HeaderSkippableMapper {
  val SkipHeaderKey = "hdfstask.csv.skip.header"
}
