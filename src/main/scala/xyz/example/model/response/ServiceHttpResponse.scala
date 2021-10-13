package xyz.example.model.response

import xyz.example.model.{City, FeelsLike, Temperature}

trait ServiceHttpResponse

/** Represents http response.
  *
  * For example :-
  *
  * Request:- http://localhost:2020/current?location=India
  * Response:-
  *      {
  *          "pressure": 997.0,
  *          "temp": 308.51,
  *          "umbrella": true
  *      }
  */
final case class CurrentWeatherResponse(
    temp: Double,
    pressure: Double,
    umbrella: Boolean)
    extends ServiceHttpResponse

final case class HistoricalWeatherResponse(
    temp:Double,
    pressure:Double,
    humidity:Int,
    feels_like: Double) extends ServiceHttpResponse

final case class ForecastWeatherResponse(
                                          city:City,cod:Int
                                        ) extends ServiceHttpResponse