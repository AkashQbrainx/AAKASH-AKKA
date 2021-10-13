package xyz.example.model
//{
//"lat": 60.99,
//"lon": 30.9,
//"timezone": "Europe/Moscow",
//"timezone_offset": 10800"
//    "current": {
//"dt": 1586468027,
//"sunrise": 1586487424,
//"sunset": 1586538297,
//"temp": 274.31,
//"feels_like": 269.79,
//"pressure": 1006,
//"humidity": 72,
//"dew_point": 270.21,
//"clouds": 0,
//"visibility": 10000,
//"wind_speed": 3,
//"wind_deg": 260,
//"weather": [
//{
//"id": 800,
//"main": "Clear",
//"description": "clear sky",
//"icon": "01n"
//}
//]
//},
//"hourly": [
//{
//"dt": 1586390400,
//"temp": 278.41,
//"feels_like": 269.43,
//"pressure": 1006,
//"humidity": 65,
//"dew_point": 272.46,
//"clouds": 0,
//"wind_speed": 9.83,
//"wind_deg": 60,
//"wind_gust": 15.65,
//"weather": [
//{
//"id": 800,
//"main": "Clear",
//"description": "clear sky",
//"icon": "01n"
//}
//]
//},
final case class HistoricalWeatherData(
  lat:Double,
  lon:Double,
  timezone:String,
  timezone_offset:Int,
  current:Current,
  hourly:Hourly)

final case class Current(
  dt: Option[ Long],
  sunrise: Long,
  sunset: Long,
  temp: Double,
  feels_like:Double,
  pressure: Int,
  humidity: Int,
  dew_point: Double,
  clouds:Int,
  visibility: Int,
  wind_speed: Double,
  wind_deg: Int,
  weather: List[Weather])
  final case class Weather(
  id:Int,
  main: String,
  description: String,
  icon: String)

final case class Hourly(
  dt: Option[Long],
  temp: Double,
  feels_like: Double,
  pressure:Int,
  humidity: Int,
  dew_point: Double,
  clouds: Int,
  wind_speed: Double,
  wind_deg: Int,
  wind_gust: Double,
  weather: List[Weather])


