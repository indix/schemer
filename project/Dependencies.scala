import sbt.{ExclusionRule, _}

object Versions {
  val sparkVersion = "2.1.1"
  val akkaHttpVersion = "10.0.10"
}

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3" % Test
  lazy val sparkCore = "org.apache.spark" %% "spark-core" % Versions.sparkVersion
  lazy val sparkSql = "org.apache.spark" %% "spark-sql" % Versions.sparkVersion
  lazy val sparkAvro = "com.databricks" %% "spark-avro" % "4.0.0"

  lazy val sparkStack = Seq(sparkCore, sparkSql, sparkAvro)
  lazy val sparkStackProvided = sparkStack.map(_ % Provided)

  lazy val hadoopAws = "org.apache.hadoop" % "hadoop-aws" % "2.6.0"

  lazy val jsonSchemaValidator = "com.github.fge" % "json-schema-validator" % "2.2.6" excludeAll {
    ExclusionRule("javax.mail")
  }

  lazy val akkaHttpCore = "com.typesafe.akka" %% "akka-http-core" % Versions.akkaHttpVersion
  lazy val akkaHttp = "com.typesafe.akka" %% "akka-http" % Versions.akkaHttpVersion
  lazy val sprayJsonAkka = "com.typesafe.akka" %% "akka-http-spray-json" % Versions.akkaHttpVersion
  lazy val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttpVersion % Test
  lazy val akkaStack =
    Seq(akkaHttpCore, akkaHttp, sprayJsonAkka, akkaHttpTestkit)

  lazy val sangria = "org.sangria-graphql" %% "sangria" % "1.2.0"
  lazy val sangriaSpray = "org.sangria-graphql" %% "sangria-spray-json" % "1.0.0"

  val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.1.5"
  val slf4jApi = "org.slf4j" % "slf4j-api" % "1.7.25"
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"

  val loggingStack = Seq(slf4jApi, logbackClassic, scalaLogging)

  val postgres = "org.postgresql" % "postgresql" % "9.4.1208"
  val quill = "io.getquill" %% "quill-jdbc" % "2.3.1"
  val quillAsyncPostgres = "io.getquill" %% "quill-async-postgres" % "2.3.1"
  val flyway = "org.flywaydb" % "flyway-core" % "4.1.1"
}
