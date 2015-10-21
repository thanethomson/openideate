name := """openideate"""

version := "0.1.0-alpha"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.postgresql" % "postgresql" % "9.4-1204-jdbc42",
  "be.objectify" %% "deadbolt-java" % "2.4.3",
  "com.feth" %% "play-authenticate" % "0.7.0-SNAPSHOT",
  "org.webjars" %% "webjars-play" % "2.4.0-1",
  "org.webjars.bower" % "jquery" % "2.1.4",
  "org.webjars.bower" % "bootstrap" % "3.3.5",
  "org.webjars.bower" % "font-awesome" % "4.4.0",
  "org.webjars.bower" % "bootstrap-social" % "4.10.1",
  "org.webjars.bower" % "jquery-dateFormat" % "1.0.2",
  "org.webjars.bower" % "markdown-it" % "4.2.1",
  "org.webjars.bower" % "angularjs" % "1.4.7",
  "org.webjars.bower" % "angular-resource" % "1.4.7",
  "org.webjars.bower" % "angular-route" % "1.4.7"
)

resolvers += Resolver.sonatypeRepo("snapshots")

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
