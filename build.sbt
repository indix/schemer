import Dependencies._

val libVersion = sys.env.get("TRAVIS_TAG") orElse sys.env.get("BUILD_LABEL") getOrElse s"1.0.0-${System.currentTimeMillis / 1000}-SNAPSHOT"

lazy val schemer = (project in file(".")).settings(
  inThisBuild(
    List(
      organization := "com.indix",
      scalaVersion := "2.11.11",
      crossScalaVersions := Seq("2.11.11"),
      version := libVersion,
      scalafmtOnCompile := true
    )
  ),
  name := "schemer",
  libraryDependencies ++= Seq(sparkCore, sparkSql, sparkAvro, scalaTest)
)
