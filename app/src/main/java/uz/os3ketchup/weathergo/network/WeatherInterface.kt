package uz.os3ketchup.weathergo.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import uz.os3ketchup.weathergo.api.WeatherResponse

interface WeatherInterface {
    @GET("2.5/forecast")
    fun getWeather(
        @Query("lat") lat:Double,
        @Query("lon") lon:Double,
        @Query("units") units:String,
        @Query("appid") appid:String?
    ): Call<WeatherResponse>

}