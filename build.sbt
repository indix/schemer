import Dependencies._
import com.typesafe.sbt.packager.Keys.{daemonUser, dockerBaseImage, dockerExposedPorts, dockerRepository, packageName}

val libVersion = sys.env.get("TRAVIS_TAG") orElse sys.env.get("BUILD_LABEL") getOrElse s"1.0.0-${System.currentTimeMillis / 1000}-SNAPSHOT"

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  pgpSecretRing := file("local.secring.gpg"),
  pgpPublicRing := file("local.pubring.gpg"),
  pgpPassphrase := Some(sys.env.getOrElse("GPG_PASSPHRASE", "").toCharArray),
  credentials += Credentials(
    "Sonatype Nexus Repository Manager",
    "oss.sonatype.org",
    System.getenv("SONATYPE_USERNAME"),
    System.getenv("SONATYPE_PASSWORD")
  ),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  publishArtifact in Test := false,
  pomIncludeRepository := { _ =>
    false
  },
  pomExtra :=
    <url>https://github.com/indix/schemer</url>
      <licenses>
        <license>
          <name>Apache License</name>
          <url>https://raw.githubusercontent.com/indix/schemer/master/LICENSE</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:indix/schemer.git</url>
        <connection>scm:git:git@github.com:indix/schemer.git</connection>
      </scm>
      <developers>
        <developer>
          <id>indix</id>
          <name>Indix</name>
          <url>http://www.indix.com</url>
        </developer>
      </developers>
)

lazy val schemer = Project(
  id = "schemer",
  base = file(".")
) aggregate (core, registry)

lazy val core = (project in file("schemer-core"))
  .settings(
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
  .settings(publishSettings: _*)

lazy val registry = (project in file("schemer-registry"))
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(AshScriptPlugin)
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(
    dockerBaseImage := "anapsix/alpine-java:8u131b11_server-jre_unlimited",
    packageName in Docker := "schemer-registry",
    dockerExposedPorts := Seq(9000),
    version in Docker := libVersion,
    daemonUser in Docker := "root",
    dockerRepository := Some("indix")
  )
  .settings(
    inThisBuild(
      List(
        organization := "com.indix",
        scalaVersion := "2.11.11",
        version := libVersion,
        scalafmtOnCompile := true
      )
    ),
    name := "schemer-registry",
    libraryDependencies ++= sparkStack ++ akkaStack ++ loggingStack ++ Seq(
      hadoopAws,
      sangria,
      sangriaSpray,
      postgres,
      quill,
      quillAsyncPostgres,
      flyway,
      scalaTest
    ),
    excludeDependencies ++= Seq(
      ExclusionRule("com.typesafe.scala-logging", "scala-logging-slf4j_2.11")
    )
  ) dependsOn core
