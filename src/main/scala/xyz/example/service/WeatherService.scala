package xyz.example.service

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Sink, Source, SourceQueueWithComplete}
import spray.json._
import xyz.example.config.ApplicationConfig
import xyz.example.model.JsonFormat._
import xyz.example.model.message.{QueryCurrentWeather, WeatherServiceMessage}
import xyz.example.model.response.CurrentWeatherResponse
import xyz.example.model.{WeatherCondition, WeatherData}
import xyz.example.service.CommonWeatherService.queueRequest

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util._
object WeatherService {

  def props: Props = Props(new WeatherService)
}

 class WeatherService extends Actor with ActorLogging {
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

   override def receive: Receive = {
     case req@QueryCurrentWeather(location) =>
       log.debug(s"$req received at ${context.self.path}")
       handleQueryCurrentWeather(location)

     case req: WeatherServiceMessage =>
       log.error(s"$req is currently unsupported by ${context.self.path}")
   }

   def handleQueryCurrentWeather(location: String): Unit = {
     val msgSender: ActorRef = sender()
     queryCurrentWeatherByCity(location)
       .map(currentWeather =>
         CurrentWeatherResponse(
           currentWeather.main.temp,
           currentWeather.main.pressure,
           isUmbrellaRequest(currentWeather.weather.head.main)))
       .onComplete(f => msgSender ! f)
   }

   def isUmbrellaRequest(weatherCondition: WeatherCondition): Boolean =
     weatherCondition match {
       case WeatherCondition.Thunderstorm => true
       case WeatherCondition.Drizzle => true
       case WeatherCondition.Rain => true
       case _ => false
     }

   def queryCurrentWeatherByCity(city: String): Future[WeatherData] = {
     val request =
       HttpRequest(uri = s"/data/2.5/weather?q=$city&appid=${openWeather.key}")
     queueRequest(request,queue)
       .flatMap(resp => Unmarshal(resp).to[JsValue])
       .map(jsValue =>
         Try(jsValue.convertTo[WeatherData]) match {
           case Success(value) => value
           case Failure(exception) =>
             log.error(s"Error parsing Json \n $jsValue")
             throw exception
         })
   }
 }
