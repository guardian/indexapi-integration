name := """gooIndexApi"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "com.amazonaws" % "aws-java-sdk" % "1.10.77",
  "com.amazonaws" % "aws-java-sdk-kinesis" % "1.10.77",
  "com.gu" %% "content-api-firehose-client" % "0.7"
)

libraryDependencies += "org.mockito" % "mockito-core" % "2.7.19"