name := """openideate"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.postgresql" % "postgresql" % "9.4-1204-jdbc42",
  "be.objectify" %% "deadbolt-java" % "2.4.3",
  "org.webjars" %% "webjars-play" % "2.4.0-1",
  "org.webjars.bower" % "jquery" % "2.1.4",
  "org.webjars.bower" % "bootstrap" % "3.3.5",
  "org.webjars.bower" % "font-awesome" % "4.4.0",
  "org.webjars.bower" % "bootstrap-social" % "4.10.1"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
