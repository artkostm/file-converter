import sbt._

object Dependencies {
  val versions = new {
    val hadoop = "1.2.1"
    val hadoopCommon = "3.1.1"
    val scopt = "3.7.0"
    val mrunit = "1.1.0"
    val scalatest = "3.0.5"
  }
  
  lazy val main = Seq(
    "org.apache.hadoop" % "hadoop-core" % versions.hadoop % Provided intransitive(),
    "org.apache.hadoop" % "hadoop-common" % versions.hadoopCommon % Provided,
    "org.apache.avro" % "avro-mapred" % "1.8.2" classifier("hadoop2"),
    "com.github.scopt" %% "scopt" % versions.scopt
  )
  
  lazy val test = Seq(
    "org.scalatest" %% "scalatest" % versions.scalatest,
    "org.apache.mrunit" % "mrunit" % versions.mrunit classifier("hadoop1"),
    "org.apache.mrunit" % "mrunit" % versions.mrunit classifier("hadoop2")
  ).map(_ % Test)
}
