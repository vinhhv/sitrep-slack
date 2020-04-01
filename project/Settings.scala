import Dependencies._
import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile
import sbt._
import sbt.Keys.{ scalacOptions, _ }
import sbt.util.Level
import wartremover._

object Settings {
  val warts = Warts.allBut(Wart.Nothing)

  // see https://docs.scala-lang.org/overviews/compiler-options/index.html#Standard_Settings
  private val stdOptions = Seq(
      "-deprecation"
    , "-encoding"
    , "UTF-8"
    , "-explaintypes"
    , "-feature"
    , "-language:existentials"
    , "-language:higherKinds"
    , "-language:implicitConversions"
    , "-language:postfixOps"
    , "-opt-warnings"
    , "-opt:l:inline"
    , "-opt-inline-from:<source>"
    , "-unchecked"
    , "-Ymacro-annotations"
    , "-Ywarn-extra-implicit"
    , "-Ywarn-numeric-widen"
    , "-Ywarn-self-implicit"
    , "-Ywarn-unused"
    , "-Ywarn-value-discard"
    , "-Xcheckinit"
    , "-Xfatal-warnings"
    , "-Xlint"
    , "-Xlint:inaccessible"
  )
  val commonSettings = {
    Seq(
        scalaVersion := "2.13.1"
      , scalacOptions := stdOptions
      , logLevel := Level.Info
      , version := (version in ThisBuild).value
      , scalafmtOnCompile := true
      , wartremoverErrors in (Compile, compile) ++= warts
      , wartremoverErrors in (Test, compile) ++= warts
      , testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
      , cancelable in Global := true
      , fork in Global := true, // https://github.com/sbt/sbt/issues/2274
      resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    )
  }

  val storageDependencies = List(quill, zio, zioCats)
  val serviceDependencies = List(logging, zioCats, zioMacros, zioTest, zioTestSbt) ++ circe ++ slack
  val backendDependencies = List(flyway, zioConfig)

  val higherKinds = addCompilerPlugin("org.typelevel" %% "kind-projector" % Version.kindProjector)
}
