import sbt._

object Dependencies {
  val versions = new {
    val hadoop        = "1.2.1"
    val hadoopCommon  = "3.1.1"
    val scopt         = "3.7.0"
    val parquetHadoop = "1.10.0"

    val mrunit     = "1.1.0"
    val scalatest  = "3.0.5"
    val scalaCheck = "1.14.0"
    val scalaMock  = "4.1.0"
  }

  lazy val main = Seq(
    "org.apache.hadoop"  % "hadoop-core"           % versions.hadoop % Provided intransitive (),
    "org.apache.hadoop"  % "hadoop-common"         % versions.hadoopCommon % Provided,
    "org.apache.avro"    % "avro-mapred"           % "1.8.2" classifier ("hadoop2"),
    "org.apache.parquet" % "parquet-hadoop-bundle" % versions.parquetHadoop,
    "com.github.scopt"   %% "scopt"                % versions.scopt
  )

  lazy val test = Seq(
    "org.scalatest"     %% "scalatest"  % versions.scalatest,
    "org.apache.mrunit" % "mrunit"      % versions.mrunit classifier ("hadoop1"),
    "org.scalacheck"    %% "scalacheck" % versions.scalaCheck,
    "org.scalamock"     %% "scalamock"  % versions.scalaMock,
    // have to add this dependency to be able to mock mapper context
    "org.apache.hadoop" % "hadoop-common" % versions.hadoopCommon
  ).map(_ % Test)
}
