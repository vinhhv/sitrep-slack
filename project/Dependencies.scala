import sbt._

object Dependencies {

  val flyway = "org.flywaydb" % "flyway-core" % Version.flyway
  val quill  = "io.getquill"  %% "quill-core" % Version.quill

  val circeCore    = "io.circe" %% "circe-core" % Version.circe
  val circeExtras  = "io.circe" %% "circe-generic-extras" % Version.circeExtras
  val circeGeneric = "io.circe" %% "circe-generic" % Version.circe
  val circeParser  = "io.circe" %% "circe-parser" % Version.circe
  val circe        = List(circeGeneric, circeCore, circeParser, circeExtras)

  val http4sBlaze = "org.http4s" %% "http4s-blaze-server" % Version.http4s
  val http4sCirce = "org.http4s" %% "http4s-circe" % Version.http4s
  val http4sDsl   = "org.http4s" %% "http4s-dsl" % Version.http4s
  val http4s      = List(http4sBlaze, http4sCirce, http4sDsl)

  val logging = "com.typesafe.scala-logging" %% "scala-logging" % Version.logging

  val bolt        = "com.slack.api" % "bolt" % Version.slack
  val boltJetty   = "com.slack.api" % "bolt-jetty" % Version.slack
  val slackClient = "com.slack.api" % "slack-api-client" % Version.slack
  val slack       = List(bolt, boltJetty, slackClient)

  val zio     = "dev.zio" %% "zio" % Version.zio
  val zioCats = ("dev.zio" %% "zio-interop-cats" % Version.zioCats).excludeAll(ExclusionRule("dev.zio"))

  val zioConfigCore     = "dev.zio" %% "zio-config" % Version.zioConfig
  val zioConfigMagnolia = "dev.zio" %% "zio-config-magnolia" % Version.zioConfig
  val zioConfigTypesafe = "dev.zio" %% "zio-config-typesafe" % Version.zioConfig
  val zioConfig         = List(zioConfigCore, zioConfigMagnolia, zioConfigTypesafe)

  val zioMacros  = "dev.zio" %% "zio-macros"   % Version.zio
  val zioTest    = "dev.zio" %% "zio-test"     % Version.zio % "test"
  val zioTestSbt = "dev.zio" %% "zio-test-sbt" % Version.zio % "test"
}

object Version {
  val flyway = "6.2.0"
  val quill  = "3.5.1"

  val circe       = "0.13.0"
  val circeExtras = "0.12.2"

  val http4s        = "0.21.2"
  val kindProjector = "0.10.3"
  val logging       = "3.9.2"

  val slack = "1.0.1"

  val zio       = "1.0.0-RC18-2+59-0c8f8197-SNAPSHOT"
  val zioCats   = "2.0.0.0-RC11"
  val zioConfig = "1.0.0-RC13-1"
}
