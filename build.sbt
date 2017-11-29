import Dependencies._
import com.typesafe.sbt.packager.Keys.{
  daemonUser,
  dockerBaseImage,
  dockerExposedPorts,
  dockerExposedVolumes,
  dockerRepository,
  packageName
}

val libVersion = sys.env.get("TRAVIS_TAG") orElse sys.env.get("BUILD_LABEL") getOrElse s"1.0.0-${System.currentTimeMillis / 1000}-SNAPSHOT"

lazy val schemer = Project(
  id = "schemer",
  base = file(".")
) aggregate (core, registry)

lazy val core = (project in file("schemer-core")).settings(
  inThisBuild(
    List(
      organization := "com.indix",
      scalaVersion := "2.11.11",
      crossScalaVersions := Seq("2.11.11"),
      version := libVersion,
      scalafmtOnCompile := true
    )
  ),
  name := "schemer-core",
  libraryDependencies ++= sparkStackProvided ++ Seq(jsonSchemaValidator, scalaTest)
)

lazy val registry = (project in file("schemer-registry"))
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(DockerPlugin)
  .settings(
    inThisBuild(
      List(
        organization := "com.indix",
        scalaVersion := "2.11.11",
        version := libVersion,
        scalafmtOnCompile := true,
        dockerBaseImage := "anapsix/alpine-java:8u131b11_server-jre_unlimited",
        packageName in Docker := "schemer-registry",
        dockerExposedPorts := Seq(9000),
        version in Docker := libVersion,
        daemonUser in Docker := "root",
        dockerRepository := Some("indix/schemer-registry")
      )
    ),
    name := "schemer-registry",
    libraryDependencies ++= sparkStack ++ akkaStack ++ loggingStack ++ Seq(hadoopAws, sangria, sangriaSpray, scalaTest)
  ) dependsOn core
