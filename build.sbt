
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

lazy val root = (project in file("."))
  .settings(
    name := "ermezinde"
  )

Compile / run / mainClass := Some("saw.ermezinde.Boot")

lazy val PekkoVersion     = "1.1.5"
lazy val PekkoHttpVersion = "1.2.0"
lazy val LogBackVersion   = "1.5.18"
lazy val ScalaTestVersion = "3.2.19"

libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-actor"  % PekkoVersion,
  "org.apache.pekko" %% "pekko-http"   % PekkoHttpVersion,
  "org.apache.pekko" %% "pekko-http-spray-json" % PekkoHttpVersion,
  "org.apache.pekko" %% "pekko-http-jackson" % PekkoHttpVersion,

  "com.typesafe" % "config" % "1.4.4",

  "org.apache.pekko" %% "pekko-slf4j"     % PekkoVersion,
  "ch.qos.logback"   %  "logback-classic" % LogBackVersion,

  "org.scalatest"    %% "scalatest"           % ScalaTestVersion % Test,
  "org.apache.pekko" %% "pekko-testkit"       % PekkoVersion     % Test,
  "org.apache.pekko" %% "pekko-http-testkit"  % PekkoHttpVersion % Test,
  "org.mockito" %% "mockito-scala" % "2.0.0" % Test,
  "org.mockito" %% "mockito-scala-scalatest" % "2.0.0" % Test,
)
