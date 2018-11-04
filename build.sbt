name := "file-converter"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies ++= Dependencies.main
libraryDependencies ++= Dependencies.test

enablePlugins(JavaAppPackaging)

mainClass in Compile := Some("by.artsiom.bigdata101.hdfstask.FileConversionJob")
mainClass in assembly := Some("by.artsiom.bigdata101.hdfstask.FileConversionJob")

assemblyJarName in assembly := "file-converter.jar"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _                             => MergeStrategy.first
}
