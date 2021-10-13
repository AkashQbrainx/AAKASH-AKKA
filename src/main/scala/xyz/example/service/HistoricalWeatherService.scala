package xyz.example.service

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{OverflowStrategy, QueueOfferResult}
import akka.stream.scaladsl.{Flow, Sink, Source, SourceQueueWithComplete}
import spray.json.JsValue
import xyz.example.config.ApplicationConfig
import xyz.example.model.HistoricalWeatherData
import xyz.example.model.JsonFormat._
import xyz.example.model.message.{QueryHistoricalWeather, WeatherServiceMessage}
import xyz.example.model.response.HistoricalWeatherResponse
import xyz.example.service.CommonWeatherService.queueRequest
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

object HistoricalWeatherService{

  def props: Props = Props(new HistoricalWeatherService)
}
class HistoricalWeatherService extends Actor with ActorLogging {
  implicit val system: ActorSystem = context.system
  implicit val ec: ExecutionContext = system.dispatcher

  import ApplicationConfig.config.service._
  lazy val connectionPool: Flow[(HttpRequest, Promise[HttpResponse]), (Try[HttpResponse], Promise[HttpResponse]), Http.HostConnectionPool] =
    Http().cachedHostConnectionPool[Promise[HttpResponse]](historicalWeather.api)

  lazy val queue: SourceQueueWithComplete[(HttpRequest, Promise[HttpResponse])] =
    Source
      .queue[(HttpRequest, Promise[HttpResponse])](
        historicalWeather.requestQueueSize,
        OverflowStrategy.dropNew)
      .via(connectionPool)
      .to(Sink.foreach {
        case (Success(resp), promise) => promise.success(resp)
        case (Failure(exception), promise) => promise.failure(exception)
      })
      .run()

  override def receive: Receive = {
    case req@QueryHistoricalWeather(lat, lon, dt) =>
      log.debug(s"$req received at ${context.self.path}")
      handleQueryHistoricalWeather(lat, lon, dt)

    case req: WeatherServiceMessage =>
      log.error(s"$req is currently unsupported by ${context.self.path}")
  }

  def handleQueryHistoricalWeather(lat: Double, lon: Double, dt: Long): Unit = {
    val msgSender: ActorRef = sender()
    queryHistoricalWeatherByCity(lat, lon, dt).map(hourlyWeather =>
      HistoricalWeatherResponse(
        hourlyWeather.current.temp,
        hourlyWeather.current.pressure,
        hourlyWeather.current.humidity,
        hourlyWeather.current.feels_like))
      .onComplete(f => msgSender ! f)
  }

  def queryHistoricalWeatherByCity(lat: Double, lon: Double, dt:Long): Future[HistoricalWeatherData] = {

    val request =
      HttpRequest(uri = s"/data/2.5/onecall/timemachine?lat=$lat&lon=$lon&dt=$dt&appid=${historicalWeather.key}")
    queueRequest(request,queue)
      .flatMap(resp => Unmarshal(resp).to[JsValue])
      .map(jsValue =>
        Try(jsValue.convertTo[HistoricalWeatherData]) match {
          case Success(value) =>
            value
          case Failure(exception) =>
            log.error(s"Error parsing Json \n $jsValue")
            throw exception
        })
  }
}