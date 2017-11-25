import sbt._

object Versions {
  val sparkVersion = "2.1.1"
}

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3" % Test
  lazy val sparkCore = "org.apache.spark" %% "spark-core" % Versions.sparkVersion % Provided
  lazy val sparkSql = "org.apache.spark" %% "spark-sql" % Versions.sparkVersion % Provided
  lazy val sparkAvro = "com.databricks" %% "spark-avro" % "4.0.0" % Provided
}
