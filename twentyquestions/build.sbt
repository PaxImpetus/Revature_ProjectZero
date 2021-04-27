import Dependencies._

ThisBuild / scalaVersion     := "2.13.4"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "TwentyQuestions",
    libraryDependencies += scalaTest % Test,
    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    libraryDependencies += "org.postgresql" % "postgresql" % "42.2.19",
    libraryDependencies += "com.lihaoyi" %% "upickle" % "0.9.5",
    libraryDependencies += "com.lihaoyi" %% "os-lib" % "0.7.3",
    scalacOptions ++= Seq("-deprecation", "-feature"),
    trapExit := false
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
