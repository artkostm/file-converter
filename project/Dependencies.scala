import sbt._

object Dependencies {
  val versions = new {
    val hadoop        = "2.7.3"
    val scopt         = "3.7.0"
    val parquetHadoop = "1.10.0"

    val mrunit     = "1.1.0"
    val scalatest  = "3.0.5"
    val scalaCheck = "1.14.0"
    val scalaMock  = "4.1.0"
  }

  lazy val main = Seq(
    "org.apache.hadoop"  % "hadoop-client"         % versions.hadoop % Provided,
    "org.apache.avro"    % "avro-mapred"           % "1.8.2" classifier ("hadoop2"),
    "org.apache.parquet" % "parquet-hadoop-bundle" % versions.parquetHadoop,
    "com.github.scopt"   %% "scopt"                % versions.scopt
  )

  lazy val test = Seq(
    "org.scalatest"     %% "scalatest"  % versions.scalatest,
    "org.scalacheck"    %% "scalacheck" % versions.scalaCheck,
    "org.scalamock"     %% "scalamock"  % versions.scalaMock,
    "org.apache.mrunit" % "mrunit"      % versions.mrunit classifier ("hadoop1"),
    "org.apache.mrunit" % "mrunit"      % versions.mrunit classifier ("hadoop2")
  ).map(_ % Test)
}
