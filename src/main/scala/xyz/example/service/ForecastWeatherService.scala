package xyz.example.service

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Sink, Source, SourceQueueWithComplete}
import spray.json.JsValue
import xyz.example.config.ApplicationConfig
import xyz.example.model.ForecastData
import xyz.example.model.JsonFormat._
import xyz.example.model.message.{QueryForecastWeather, WeatherServiceMessage}
import xyz.example.model.response.ForecastWeatherResponse
import xyz.example.service.CommonWeatherService.queueRequest

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}
object ForecastWeatherService{
  def props: Props = Props(new ForecastWeatherService)
}
class ForecastWeatherService extends Actor with ActorLogging{

  implicit val system: ActorSystem = context.system
  implicit val ec: ExecutionContext = system.dispatcher

  import ApplicationConfig.config.service._

  lazy val connectionPool: Flow[(HttpRequest, Promise[HttpResponse]), (Try[HttpResponse], Promise[HttpResponse]), Http.HostConnectionPool] =
    Http().cachedHostConnectionPool[Promise[HttpResponse]](openWeather.api)

  lazy val queue: SourceQueueWithComplete[(HttpRequest, Promise[HttpResponse])] =
    Source
      .queue[(HttpRequest, Promise[HttpResponse])](
        openWeather.requestQueueSize,
        OverflowStrategy.dropNew)
      .via(connectionPool)
      .to(Sink.foreach {
        case (Success(resp), promise) => promise.success(resp)
        case (Failure(exception), promise) => promise.failure(exception)
      })
      .run()

  override def receive: Receive ={
  case req@QueryForecastWeather(location,cnt) =>
  log.debug(s"$req received at ${context.self.path}")
  handleQueryForecastWeather(location,cnt)

  case req: WeatherServiceMessage =>
    log.error(s"$req is currently unsupported by ${context.self.path}")
}
  def handleQueryForecastWeather(location: String,cnt:Int): Unit = {
    val msgSender: ActorRef = sender()
    queryForecastWeatherByCity(location,cnt)
      .map(forecastWeather =>ForecastWeatherResponse(
        forecastWeather.city,
        forecastWeather.cod
      )).onComplete(f => msgSender ! f)
  }
  def queryForecastWeatherByCity(city: String,cnt:Int): Future[ForecastData] = {
    val request =
      HttpRequest(uri = s"/data/2.5/forecast/daily?q=$city&cnt=$cnt&appid=${openWeather.key}")
    queueRequest(request,queue)
      .flatMap(resp => Unmarshal(resp).to[JsValue])
      .map(jsValue =>
        Try(jsValue.convertTo[ForecastData]) match {
          case Success(value) => value
          case Failure(exception) =>
            log.error(s"Error parsing Json \n $jsValue")
            throw exception
        })
  }
}
