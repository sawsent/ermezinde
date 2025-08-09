ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

lazy val root = (project in file("."))
  .settings(
    name := "ermezinde"
  )

Compile / run / mainClass := Some("saw.ermezinde.Boot")

lazy val PekkoVersion     = "1.1.5"
lazy val PekkoHttpVersion = "1.2.0"

libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-actor"  % PekkoVersion,
  "org.apache.pekko" %% "pekko-http"   % PekkoHttpVersion,
  "org.apache.pekko" %% "pekko-http-spray-json" % PekkoHttpVersion, // spray-json
  "org.apache.pekko" %% "pekko-http-jackson" % PekkoHttpVersion, // or Jackson

  "com.typesafe" % "config" % "1.4.4",

  "org.apache.pekko" %% "pekko-testkit"       % PekkoVersion     % Test,
  "org.apache.pekko" %% "pekko-http-testkit"  % PekkoHttpVersion % Test
)
