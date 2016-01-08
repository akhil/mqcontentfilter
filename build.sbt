name := "MQContentFilter"

version := "1.0"

scalaVersion := "2.11.7"

val camelVersion = "2.16.1"
val akkaVersion = "2.4.1"
val sprayVersion = "1.3.3"

libraryDependencies ++= Seq(
  "org.webjars" % "json-editor" % "0.7.21",
  "io.spray" %% "spray-can" % sprayVersion,
  "io.spray" %% "spray-httpx" % sprayVersion,
  "io.spray" %% "spray-routing" % sprayVersion,
  "io.spray" %% "spray-servlet" % sprayVersion,
  "com.h2database" % "h2" % "1.4.190",
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.5",
  "org.json4s" %% "json4s-jackson" % "3.3.0",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-camel" % akkaVersion,
  "org.apache.activemq" % "activemq-all" % "5.13.0",
  "org.apache.activemq" % "activemq-camel" % "5.13.0",
  "org.apache.camel" % "camel-scala" % camelVersion,
  "org.apache.camel" % "camel-jms" % camelVersion,
  "org.apache.camel" % "camel-stream" % camelVersion,
  "org.apache.camel" % "camel-jsonpath" % camelVersion
)