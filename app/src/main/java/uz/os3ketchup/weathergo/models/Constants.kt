package uz.os3ketchup.weathergo.models

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.lang.reflect.Type

object Constants {
    const val APP_ID:String = "9e9611961343d6606c7bb0c299fb5094"
    const val BASE_URL = "https://api.openweathermap.org/data/"

    const val METRIC_UNIT = "metric"
    const val PREFERENCE_NAME = "WeatherAppPreference"
    const val WEATHER_RESPONSE_DATA = "weather_response_data"


    @SuppressLint("ObsoleteSdkInt")
    fun isNetworkAvailable(context:Context):Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                val network = connectivityManager.activeNetwork ?: return false
                val activeNetwork = connectivityManager.getNetworkCapabilities(network)  ?: return false
                return when{
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)-> true
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)-> true
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)-> true

                    else->false
                }
        }else{
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        }


    }
}