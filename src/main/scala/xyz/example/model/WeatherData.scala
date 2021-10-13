package xyz.example.model

/** Classes that represents json received from open-weather-api
  *
  * Json Example :-
  *
  *        {
  *          "coord": {
  *            "lon": -122.08,
  *            "lat": 37.39
  *          },
  *          "weather": [
  *            {
  *              "id": 800,
  *              "main": "Clear",
  *              "description": "clear sky",
  *              "icon": "01d"
  *            }
  *          ],
  *          "base": "stations",
  *          "main": {
  *            "temp": 282.55,
  *            "feels_like": 281.86,
  *            "temp_min": 280.37,
  *            "temp_max": 284.26,
  *            "pressure": 1023,
  *            "humidity": 100
  *          },
  *          "visibility": 16093,
  *          "wind": {
  *            "speed": 1.5,
  *            "deg": 350
  *          },
  *          "clouds": {
  *            "all": 1
  *          },
  *          "dt": 1560350645,
  *          "sys": {
  *            "type": 1,
  *            "id": 5122,
  *            "message": 0.0139,
  *            "country": "US",
  *            "sunrise": 1560343627,
  *            "sunset": 1560396563
  *          },
  *          "timezone": -25200,
  *          "id": 420006353,
  *          "name": "Mountain View",
  *          "cod": 200
  *        }
  *
  * The above json is being converted to an object of WeatherData
  */

final case class WeatherData(
    base: String,
    cod: Int,
    dt: Long,
    id: Int,
    name: String,
    timezone: Int,
    visibility: Int,
    main: Main,
    coord: Coordinates,
    sys: Sys,
    clouds: Clouds,
    weather: List[OpenWeather],
    wind: Wind)

final case class Main(
    feels_like:Double,
    humidity: Int,
    pressure: Int,
    temp: Double,
    temp_max: Double,
    temp_min: Double)

final case class Coordinates(lat: Double, lon: Double)
final case class Clouds(all: Int)
final case class Wind(deg: Double, speed: Double)
final case class Sys(
    country: String,
    id: Option[Double],
    sunrise: Long,
    sunset: Long,
    `type`: Option[Int])

final case class OpenWeather(
                          description:String,
                          icon: String,
                          id: Int,
                          main: WeatherCondition)
sealed trait WeatherCondition

object WeatherCondition {

  case object Thunderstorm extends WeatherCondition

  case object Drizzle extends WeatherCondition

  case object Rain extends WeatherCondition

  case object Snow extends WeatherCondition

  case object Mist extends WeatherCondition

  case object Smoke extends WeatherCondition

  case object Haze extends WeatherCondition

  case object Dust extends WeatherCondition

  case object Fog extends WeatherCondition

  case object Ash extends WeatherCondition

  case object Squall extends WeatherCondition

  case object Tornado extends WeatherCondition

  case object Clear extends WeatherCondition

  case object Clouds extends WeatherCondition

  def values: List[WeatherCondition] = List(
    Thunderstorm,
    Drizzle,
    Rain,
    Snow,
    Mist,
    Smoke,
    Haze,
    Dust,
    Fog,
    Ash,
    Squall,
    Tornado,
    Clear,
    Clouds)

  def fromString(input: String): Option[WeatherCondition] =
    values.find(_.toString.contains(input))
}
