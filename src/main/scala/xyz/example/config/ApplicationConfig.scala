package xyz.example.config

import scala.concurrent.duration.FiniteDuration

import com.typesafe.config.{Config, ConfigFactory}
import xyz.example.config.ApplicationConfig._

final class ApplicationConfig(val config: Config) {

  val service: ServiceConfig = new ServiceConfig(config.getConfig("service"))
}

object ApplicationConfig {

  lazy val config: ApplicationConfig = new ApplicationConfig(
    ConfigFactory.load().getConfig("weather-service"))


  final class ServiceConfig(val config: Config) {

    val http: HttpConfig = new HttpConfig(config.getConfig("http"))
    val openWeather: OpenWeatherConfig = new OpenWeatherConfig(
      config.getConfig("open-weather"))
    val historicalWeather=new HistoricalWeatherConfig(
      config.getConfig("historical-weather"))
  }

  final class HttpConfig(val config: Config) {

    import scala.jdk.DurationConverters._

    val host: String = config.getString("host")
    val port: Int = config.getInt("port")
    val serverStartTimeout: FiniteDuration =
      config.getDuration("serverStartTimeout").toScala
    val requestTimeout: FiniteDuration =
      config.getDuration("requestTimeout").toScala
  }

  final class OpenWeatherConfig(val config: Config) {

    val api: String = config.getString("api")
    val key: String = config.getString("key")
    val requestQueueSize: Int = config.getInt("requestQueueSize")
  }
  final class HistoricalWeatherConfig(val config:Config){
    val api: String =config.getString("api")
    val key: String = config.getString("key")
    val requestQueueSize: Int = config.getInt("requestQueueSize")
  }

}
