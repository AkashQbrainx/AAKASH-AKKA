import Dependencies. _

name := "weather-service"
organization := "xyz.example"
version := "0.1"

scalaVersion := "2.13.6"

libraryDependencies ++= Akka.all ++ Slf4j.all

startYear := Some(2021)
description := "Weather Service"
