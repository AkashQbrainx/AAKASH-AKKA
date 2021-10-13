package xyz.example

import akka.actor.ActorSystem
import xyz.example.config.ApplicationConfig
import xyz.example.service.ControllerService

object Application extends App {

  val system = ActorSystem("WeatherService", ApplicationConfig.config.config)

  system.actorOf(ControllerService.props, "ControllerService")
}
