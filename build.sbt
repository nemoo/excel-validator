name := """excel-validator"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"

libraryDependencies += "org.apache.poi" % "poi" % "3.10.1"

libraryDependencies += "org.apache.poi" % "poi-ooxml" % "3.10.1"

libraryDependencies += "commons-io" % "commons-io" % "2.4"
