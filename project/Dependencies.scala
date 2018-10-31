import sbt._

object Dependencies {
  val versions = new {
    val hadoop = "1.2.1"
    val scopt = "3.7.0"
    val mrunit = "1.1.0"
    val scalatest = "3.0.5"
  }
  
  lazy val main = Seq(
    "org.apache.hadoop" % "hadoop-core" % versions.hadoop,// % Provided intransitive(),
    "com.github.scopt" %% "scopt" % versions.scopt,
    "org.apache.avro" % "avro-mapred" % "1.8.2",
    "org.apache.avro" % "avro-tools" % "1.8.2"
  )
  
  lazy val test = Seq(
    "org.apache.mrunit" % "mrunit" % versions.mrunit,
    "org.scalatest" %% "scalatest" % versions.scalatest
  ).map(_ % Test)
}
