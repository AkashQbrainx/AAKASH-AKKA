package xyz.example.model.message

// Messages that we send to WeatherService
sealed trait WeatherServiceMessage extends ServiceMessage


sealed trait CurrentWeatherMessage extends WeatherServiceMessage
sealed trait HistoricalWeatherMessage extends WeatherServiceMessage
sealed trait ForecastWeatherMessage  extends WeatherServiceMessage

final case class QueryCurrentWeather(location: String)
    extends CurrentWeatherMessage

final case class QueryHistoricalWeather(lat:Double,lon:Double,dt:Long)
    extends HistoricalWeatherMessage

final case class QueryForecastWeather(location: String,cnt:Int)
  extends ForecastWeatherMessage
