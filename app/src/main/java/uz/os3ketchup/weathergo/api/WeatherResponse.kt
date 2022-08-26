package uz.os3ketchup.weathergo.api

data class WeatherResponse(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<BasicWeather>,
    val message: Int
)