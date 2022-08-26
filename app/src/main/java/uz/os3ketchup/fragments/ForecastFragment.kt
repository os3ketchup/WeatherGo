package uz.os3ketchup.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import uz.os3ketchup.weathergo.R
import uz.os3ketchup.weathergo.api.WeatherResponse
import uz.os3ketchup.weathergo.databinding.FragmentForecastBinding
import uz.os3ketchup.weathergo.models.Constants.PREFERENCE_NAME
import uz.os3ketchup.weathergo.models.Constants.WEATHER_RESPONSE_DATA
import java.text.DateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class ForecastFragment : Fragment() {
    private lateinit var binding: FragmentForecastBinding
    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentForecastBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.materialToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        mSharedPreferences =
            requireContext().getSharedPreferences(PREFERENCE_NAME, AppCompatActivity.MODE_PRIVATE)
        val weatherResponseJsonString = mSharedPreferences.getString(WEATHER_RESPONSE_DATA, "")
        val weatherList = Gson().fromJson(weatherResponseJsonString, WeatherResponse::class.java)
        with(binding) {
            Log.i("myInfo", " toooo  $weatherList ")

            val day2 = weatherList.list[8].dt_txt.subSequence(0..9).toString()
            val day3 = weatherList.list[17].dt_txt.subSequence(0..9).toString()
            val day4 = weatherList.list[25].dt_txt.subSequence(0..9).toString()
            val day5 = weatherList.list[33].dt_txt.subSequence(0..9).toString()
            tvToday.text = "Today"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tvTomorrow.text = getWeekDayName(day2)
                tvAfterTomorrow.text = getWeekDayName(day3)
                tv4Day.text = getWeekDayName(day4)
                tv5Day.text = getWeekDayName(day5)
            }


            tvTempToday.text = weatherList.list[1].main.temp.roundToInt().toString() + " °C"
            tvTempTomorrow.text = weatherList.list[9].main.temp.roundToInt().toString() + " °C"
            tvTempAfterTomorrow.text =
                weatherList.list[17].main.temp.roundToInt().toString() + " °C"
            tvTemp4Day.text = weatherList.list[25].main.temp.roundToInt().toString() + " °C"
            tvTemp5Day.text = weatherList.list[33].main.temp.roundToInt().toString() + " °C"

            when (weatherList.list[1].weather[0].icon) {
                "01d" -> ivToday.setImageResource(R.drawable.clear_sky)
                "02d" -> ivToday.setImageResource(R.drawable.few_clouds)
                "03d" -> ivToday.setImageResource(R.drawable.scattered_clouds)
                "04d" -> ivToday.setImageResource(R.drawable.broken_clouds)
                "09d" -> ivToday.setImageResource(R.drawable.rain)
                "10d" -> ivToday.setImageResource(R.drawable.shower_rain)
                "11d" -> ivToday.setImageResource(R.drawable.thunderstrom)
                "13d" -> ivToday.setImageResource(R.drawable.snow)
                "50d" -> ivToday.setImageResource(R.drawable.mist)

                "01n" -> ivToday.setImageResource(R.drawable.clear_sky_night)
                "02n" -> ivToday.setImageResource(R.drawable.few_clouds_night)
                "03n" -> ivToday.setImageResource(R.drawable.scattered_clouds)
                "04n" -> ivToday.setImageResource(R.drawable.broken_clouds)
                "09n" -> ivToday.setImageResource(R.drawable.rain_night)
                "10n" -> ivToday.setImageResource(R.drawable.shower_rain)
                "11n" -> ivToday.setImageResource(R.drawable.thunderstrom)
                "13n" -> ivToday.setImageResource(R.drawable.snow)
                "50n" -> ivToday.setImageResource(R.drawable.mist)
            }
            when (weatherList.list[9].weather[0].icon) {
                "01d" -> ivTomorrow.setImageResource(R.drawable.clear_sky)
                "02d" -> ivTomorrow.setImageResource(R.drawable.few_clouds)
                "03d" -> ivTomorrow.setImageResource(R.drawable.scattered_clouds)
                "04d" -> ivTomorrow.setImageResource(R.drawable.broken_clouds)
                "09d" -> ivTomorrow.setImageResource(R.drawable.rain)
                "10d" -> ivTomorrow.setImageResource(R.drawable.shower_rain)
                "11d" -> ivTomorrow.setImageResource(R.drawable.thunderstrom)
                "13d" -> ivTomorrow.setImageResource(R.drawable.snow)
                "50d" -> ivTomorrow.setImageResource(R.drawable.mist)

                "01n" -> ivTomorrow.setImageResource(R.drawable.clear_sky_night)
                "02n" -> ivTomorrow.setImageResource(R.drawable.few_clouds_night)
                "03n" -> ivTomorrow.setImageResource(R.drawable.scattered_clouds)
                "04n" -> ivTomorrow.setImageResource(R.drawable.broken_clouds)
                "09n" -> ivTomorrow.setImageResource(R.drawable.rain_night)
                "10n" -> ivTomorrow.setImageResource(R.drawable.shower_rain)
                "11n" -> ivTomorrow.setImageResource(R.drawable.thunderstrom)
                "13n" -> ivTomorrow.setImageResource(R.drawable.snow)
                "50n" -> ivTomorrow.setImageResource(R.drawable.mist)
            }
            when (weatherList.list[17].weather[0].icon) {
                "01d" -> ivAfterTomorrow.setImageResource(R.drawable.clear_sky)
                "02d" -> ivAfterTomorrow.setImageResource(R.drawable.few_clouds)
                "03d" -> ivAfterTomorrow.setImageResource(R.drawable.scattered_clouds)
                "04d" -> ivAfterTomorrow.setImageResource(R.drawable.broken_clouds)
                "09d" -> ivAfterTomorrow.setImageResource(R.drawable.rain)
                "10d" -> ivAfterTomorrow.setImageResource(R.drawable.shower_rain)
                "11d" -> ivAfterTomorrow.setImageResource(R.drawable.thunderstrom)
                "13d" -> ivAfterTomorrow.setImageResource(R.drawable.snow)
                "50d" -> ivAfterTomorrow.setImageResource(R.drawable.mist)

                "01n" -> ivAfterTomorrow.setImageResource(R.drawable.clear_sky_night)
                "02n" -> ivAfterTomorrow.setImageResource(R.drawable.few_clouds_night)
                "03n" -> ivAfterTomorrow.setImageResource(R.drawable.scattered_clouds)
                "04n" -> ivAfterTomorrow.setImageResource(R.drawable.broken_clouds)
                "09n" -> ivAfterTomorrow.setImageResource(R.drawable.rain_night)
                "10n" -> ivAfterTomorrow.setImageResource(R.drawable.shower_rain)
                "11n" -> ivAfterTomorrow.setImageResource(R.drawable.thunderstrom)
                "13n" -> ivAfterTomorrow.setImageResource(R.drawable.snow)
                "50n" -> ivAfterTomorrow.setImageResource(R.drawable.mist)
            }
            when (weatherList.list[25].weather[0].icon) {
                "01d" -> iv4Day.setImageResource(R.drawable.clear_sky)
                "02d" -> iv4Day.setImageResource(R.drawable.few_clouds)
                "03d" -> iv4Day.setImageResource(R.drawable.scattered_clouds)
                "04d" -> iv4Day.setImageResource(R.drawable.broken_clouds)
                "09d" -> iv4Day.setImageResource(R.drawable.rain)
                "10d" -> iv4Day.setImageResource(R.drawable.shower_rain)
                "11d" -> iv4Day.setImageResource(R.drawable.thunderstrom)
                "13d" -> iv4Day.setImageResource(R.drawable.snow)
                "50d" -> iv4Day.setImageResource(R.drawable.mist)

                "01n" -> iv4Day.setImageResource(R.drawable.clear_sky_night)
                "02n" -> iv4Day.setImageResource(R.drawable.few_clouds_night)
                "03n" -> iv4Day.setImageResource(R.drawable.scattered_clouds)
                "04n" -> iv4Day.setImageResource(R.drawable.broken_clouds)
                "09n" -> iv4Day.setImageResource(R.drawable.rain_night)
                "10n" -> iv4Day.setImageResource(R.drawable.shower_rain)
                "11n" -> iv4Day.setImageResource(R.drawable.thunderstrom)
                "13n" -> iv4Day.setImageResource(R.drawable.snow)
                "50n" -> iv4Day.setImageResource(R.drawable.mist)
            }
            when (weatherList.list[33].weather[0].icon) {
                "01d" -> iv5Day.setImageResource(R.drawable.clear_sky)
                "02d" -> iv5Day.setImageResource(R.drawable.few_clouds)
                "03d" -> iv5Day.setImageResource(R.drawable.scattered_clouds)
                "04d" -> iv5Day.setImageResource(R.drawable.broken_clouds)
                "09d" -> iv5Day.setImageResource(R.drawable.rain)
                "10d" -> iv5Day.setImageResource(R.drawable.shower_rain)
                "11d" -> iv5Day.setImageResource(R.drawable.thunderstrom)
                "13d" -> iv5Day.setImageResource(R.drawable.snow)
                "50d" -> iv5Day.setImageResource(R.drawable.mist)

                "01n" -> iv5Day.setImageResource(R.drawable.clear_sky_night)
                "02n" -> iv5Day.setImageResource(R.drawable.few_clouds_night)
                "03n" -> iv5Day.setImageResource(R.drawable.scattered_clouds)
                "04n" -> iv5Day.setImageResource(R.drawable.broken_clouds)
                "09n" -> iv5Day.setImageResource(R.drawable.rain_night)
                "10n" -> iv5Day.setImageResource(R.drawable.shower_rain)
                "11n" -> iv5Day.setImageResource(R.drawable.thunderstrom)
                "13n" -> iv5Day.setImageResource(R.drawable.snow)
                "50n" -> iv5Day.setImageResource(R.drawable.mist)
            }
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeekDayName(s: String?): String {
        val dtfInput = DateTimeFormatter.ofPattern("u-M-d", Locale.ENGLISH)
        val dtfOutput = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH)
        return LocalDate.parse(s, dtfInput).format(dtfOutput)
    }
}


