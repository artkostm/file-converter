package com.epam.bigdata101.hdfstask.util
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

class HdfsUtil(config: Configuration) {

  def exists(file: Path): Boolean = FileSystem.get(config).exists(file)

  def makeQualified(file: Path): Path = FileSystem.get(config).makeQualified(file)
}