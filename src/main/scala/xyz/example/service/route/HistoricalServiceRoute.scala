package xyz.example.service.route

import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import xyz.example.config.ApplicationConfig.config
import xyz.example.model.JsonFormat._
import xyz.example.model.message.QueryHistoricalWeather
import xyz.example.model.response.HistoricalWeatherResponse
import xyz.example.util.LazyLogging
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

final class HistoricalServiceRoute(controller: ActorRef)
  extends BaseRoute
    with LazyLogging {

  import config.service.http

  implicit val requestTimeout: Timeout = http.requestTimeout

  override def route: Route = path("city"){

    parameter("lat".as[Double] ,"lon".as[Double],"dt".as[Long]) {
      { (lat,lon,dt) =>
        onCompleteWithTry(
          (controller ? QueryHistoricalWeather(lat,lon,dt))
            .mapTo[Try[HistoricalWeatherResponse]]) {
          case Success(resp) =>
            log.info(resp.toString)
            complete(resp)
          case Failure(exception) =>
            log.error(exception.getMessage, exception)
            complete(StatusCodes.InternalServerError)
        }
      }
    }
  }
  private def onCompleteWithTry[T](future: Future[Try[T]])(
    fxn: Try[T] => Route): Route =
    onComplete(future) { f => fxn(f.flatten) }
}