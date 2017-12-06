addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.12")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.7.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.2")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.0")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.0")
scalafmtOnCompile in ThisBuild := true