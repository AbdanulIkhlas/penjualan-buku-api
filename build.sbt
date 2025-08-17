name         := """penjualan-buku"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.16"

libraryDependencies ++= Seq(
  guice,
  evolutions,
  jdbc,
  "org.playframework.anorm" %% "anorm"        % "2.7.0",
  "org.postgresql"           % "postgresql"   % "42.7.3",
  // For file upload handling
  "commons-io" % "commons-io" % "2.16.1"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
