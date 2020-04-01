import sbt._
import Settings._

lazy val domain = project
  .settings(commonSettings)

lazy val storage = project
  .settings(commonSettings)
  .settings(libraryDependencies ++= storageDependencies)
  .dependsOn(domain)

lazy val service = project
  .settings(commonSettings)
  .settings(libraryDependencies ++= serviceDependencies)
  .settings(higherKinds)
  .dependsOn(storage)

lazy val backend = project
  .settings(commonSettings)
  .settings(libraryDependencies ++= backendDependencies)
  .dependsOn(service)

lazy val `sitrep-slack` = Project("sitrep-slack", file("."))
  .settings(commonSettings)
  .settings(organization := "vinhhv.io")
  .settings(moduleName := "sitrep-slack")
  .settings(name := "sitrep-slack")
  .aggregate(
      domain
    , storage
    , service
    , backend
  )
