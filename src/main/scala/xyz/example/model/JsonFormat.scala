package xyz.example.model

import spray.json._
import xyz.example.model.response.{CurrentWeatherResponse, ForecastWeatherResponse, HistoricalWeatherResponse}
object JsonFormat extends DefaultJsonProtocol {

  // service-http-response json formats
  implicit val currentWeatherResponseJsonFormat
      : RootJsonFormat[CurrentWeatherResponse] =
    jsonFormat3(CurrentWeatherResponse)

  implicit val historicalWeatherResponse
  : RootJsonFormat[HistoricalWeatherResponse] =
    jsonFormat4(HistoricalWeatherResponse)

  implicit val forecastRes=jsonFormat2(ForecastWeatherResponse)

  //historical-weather json formats:
  implicit val historicalWeatherJsonFormat: RootJsonFormat[Weather] =jsonFormat4(Weather)
  implicit val hourlyJsonFormat: RootJsonFormat[Hourly] =jsonFormat11(Hourly)
  implicit val currentJsonFormat: RootJsonFormat[Current] =jsonFormat13(Current)
  implicit val historicalWeatherDataJsonFormat: RootJsonFormat[HistoricalWeatherData] =jsonFormat6(HistoricalWeatherData)

  // service-model json formats
  implicit val cloudsJsonFormat: RootJsonFormat[Clouds] = jsonFormat1(Clouds)
  implicit val coordinatesJsonFormat: RootJsonFormat[Coordinates] = jsonFormat2(
    Coordinates)
  implicit val mainJsonFormat: RootJsonFormat[Main] = jsonFormat6(Main)
  implicit val sysJsonFormat: RootJsonFormat[Sys] = jsonFormat5(Sys)
  implicit val weatherConditionJsonFormat: RootJsonFormat[WeatherCondition] =
    new RootJsonFormat[WeatherCondition] {
      override def write(obj: WeatherCondition): JsValue = JsString(
        obj.toString)
      override def read(json: JsValue): WeatherCondition =
        WeatherCondition.fromString(json.asInstanceOf[JsString].value) match {
          case Some(value) => value
          case None        => throw new Exception(s"Not a valid category $json")
        }
    }
  implicit val weatherJsonFormat: RootJsonFormat[OpenWeather] = jsonFormat4(OpenWeather)
  implicit val windJsonFormat: RootJsonFormat[Wind] = jsonFormat2(Wind)
  implicit val weatherDataJsonFormat: RootJsonFormat[WeatherData] =
    jsonFormat13(WeatherData)

//forecast-details json format

  implicit val feelsJsonFormat: RootJsonFormat[FeelsLike] =jsonFormat4(FeelsLike)
  implicit val temperatureJsonFormat: RootJsonFormat[Temperature] =jsonFormat6(Temperature)
  implicit val weatherDetailsJsonFormat: RootJsonFormat[WeatherDetails] =jsonFormat13(WeatherDetails)
  implicit val cityJsonFormat: RootJsonFormat[City] =jsonFormat6(City)
  implicit val forecastJsonFormat: RootJsonFormat[ForecastData] =jsonFormat5(ForecastData)


}
