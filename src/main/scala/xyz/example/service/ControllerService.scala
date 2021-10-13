package xyz.example.service

import scala.concurrent.Await
import scala.util.{Failure, Success, Try}
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import xyz.example.model.message._
import xyz.example.service.route._

object ControllerService {

  def props: Props = Props(new ControllerService)
}

final class ControllerService extends Actor with ActorLogging {

   val weatherService: ActorRef =
    context.actorOf(WeatherService.props, "WeatherService")

   val historicalWeatherService: ActorRef =
    context.actorOf(HistoricalWeatherService.props,"HistoricalWeatherService")

  val forecastWeatherService: ActorRef =
    context.actorOf(ForecastWeatherService.props, "ForecastWeatherService")

   implicit val system: ActorSystem = context.system


  val route: Route = BaseRoute
    .fromRoutes(List(new WeatherServiceRoute(context.self),new HistoricalServiceRoute(context.self),new ForecastRoute(context.self)))
    .route

  private[this] var serverBinding: ServerBinding = _

  override def preStart(): Unit = {
    import xyz.example.config.ApplicationConfig.config.service.http
    Try(
      Await.result(
        Http().newServerAt(http.host, http.port).bind(route),
        http.serverStartTimeout)) match {
      case Success(value) =>
        log.debug("Server started")
        serverBinding = value
      case Failure(exception) =>
        log.error(exception.getMessage, exception)
        context.stop(context.self)
    }
  }

  override def receive: Receive = {
    case msg: CurrentWeatherMessage =>
      log.debug(s"$msg received at ${context.self.path}")
      weatherService forward msg
    case msg:HistoricalWeatherMessage=>
      log.debug(s"$msg received at ${context.self.path}")
       historicalWeatherService forward msg

    case msg:ForecastWeatherMessage=>
      log.debug(s"$msg received at ${context.self.path}")
      forecastWeatherService forward msg
    case msg: ServiceMessage =>
      log.error(s"$msg is currently unsupported by ${context.self.path}")
  }

  override def postStop(): Unit = {
    system.terminate()
  }
}
