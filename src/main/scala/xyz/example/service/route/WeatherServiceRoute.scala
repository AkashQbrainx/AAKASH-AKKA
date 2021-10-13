package xyz.example.service.route

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import xyz.example.config.ApplicationConfig.config
import xyz.example.model.JsonFormat._
import xyz.example.model.message.QueryCurrentWeather
import xyz.example.model.response.CurrentWeatherResponse
import xyz.example.util.LazyLogging

// Weather service route
final class WeatherServiceRoute(controller: ActorRef)
    extends BaseRoute
    with LazyLogging {

  import config.service.http

  implicit val requestTimeout: Timeout = http.requestTimeout

  override def route: Route =
    path("current") {
      parameter("location") { location =>
        log.debug(
          s"Request received at `WeatherServiceRoute` with location as $location")
        onCompleteWithTry(
          (controller ? QueryCurrentWeather(location))
            .mapTo[Try[CurrentWeatherResponse]]) {
          case Success(resp) =>
            log.info(resp.toString)
            complete(resp)
          case Failure(exception) =>
            log.error(exception.getMessage, exception)
            complete(StatusCodes.InternalServerError)
        }
      }
    }

  private def onCompleteWithTry[T](future: Future[Try[T]])(
      fxn: Try[T] => Route): Route =
    onComplete(future) { f => fxn(f.flatten) }
}
