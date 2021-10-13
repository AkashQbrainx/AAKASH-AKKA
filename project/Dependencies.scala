import sbt._

object Dependencies {

  object Akka {

    lazy val version = "2.6.16"
    lazy val httpVersion = "10.2.6"

    lazy val actorTyped = "com.typesafe.akka" %% "akka-actor" % version
    lazy val stream = "com.typesafe.akka" %% "akka-stream" % version
    lazy val http = "com.typesafe.akka" %% "akka-http" % httpVersion
    lazy val httpSprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % httpVersion

    def all = List(actorTyped, stream, http, httpSprayJson)
  }

  object Slf4j {

    lazy val version = "2.0.0-alpha4"

    lazy val api = "org.slf4j" % "slf4j-api" % version
    lazy val log4j12 = "org.slf4j" % "slf4j-log4j12" % version

    def all = List(api, log4j12)
  }
}
