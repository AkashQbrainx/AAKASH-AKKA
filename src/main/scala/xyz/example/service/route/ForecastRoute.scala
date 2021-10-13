package xyz.example.service.route
import scala.concurrent.Future
import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import xyz.example.util.LazyLogging
import xyz.example.config.ApplicationConfig._
import xyz.example.model.message.QueryForecastWeather
import xyz.example.model.response.ForecastWeatherResponse
import scala.util.{Failure, Success, Try}
import xyz.example.model.JsonFormat._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

final class ForecastRoute(controller:ActorRef)
  extends BaseRoute
    with LazyLogging {

  import config.service.http

  implicit val requestTimeout: Timeout = http.requestTimeout

  override def route: Route = {

    path("city") {
      parameter("area", "cnt".as[Int]) { (location, cnt) =>
        log.debug(
          s"Request received at `WeatherServiceRoute` with location as $location")
        onCompleteWithTry(
          (controller ? QueryForecastWeather(location,cnt))
            .mapTo[Try[ForecastWeatherResponse]]) {
          case Success(resp) =>
            log.info(resp.toString)
            complete(s"$resp")
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