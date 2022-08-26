package uz.os3ketchup.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.os3ketchup.weathergo.R
import uz.os3ketchup.weathergo.api.WeatherResponse
import uz.os3ketchup.weathergo.databinding.FragmentMainBinding
import uz.os3ketchup.weathergo.models.Constants.APP_ID
import uz.os3ketchup.weathergo.models.Constants.BASE_URL
import uz.os3ketchup.weathergo.models.Constants.METRIC_UNIT
import uz.os3ketchup.weathergo.models.Constants.PREFERENCE_NAME
import uz.os3ketchup.weathergo.models.Constants.WEATHER_RESPONSE_DATA
import uz.os3ketchup.weathergo.models.Constants.isNetworkAvailable
import uz.os3ketchup.weathergo.network.WeatherInterface
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mSharedPreferences: SharedPreferences
    private var mProgressDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)


        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            mainToolbar.title = "testing"
            mainToolbar.setOnMenuItemClickListener { menu_item ->
                when (menu_item.itemId) {
                    R.id.refresh_item -> {
                        onViewCreated(view, savedInstanceState)
                        requestLocationData()
                        findNavController().navigate(R.id.mainFragment)

                        true
                    }
                    else -> {
                        false
                    }
                }

            }


            btn5Day.setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_forecastFragment)


            }
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        mSharedPreferences =
            requireContext().getSharedPreferences(PREFERENCE_NAME, AppCompatActivity.MODE_PRIVATE)
        setupUI()

        if (!isLocationEnabled()) {
            Toast.makeText(
                requireContext(),
                "Your location provider is turned off. Please turned it on",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            requestLocationData()
        } else {

            Dexter.withContext(requireContext())
                .withPermissions(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener, PermissionListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            requestLocationData()
                        }

                        if (report.isAnyPermissionPermanentlyDenied) {
                            Toast.makeText(
                                requireContext(),
                                "You denied permission. Please enable them as it is mandatory for the app to work ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {

                        showRationalDialogForPermission()
                    }

                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {

                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {

                    }
                }).onSameThread()
                .check()
        }

    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun showRationalDialogForPermission() {
        AlertDialog.Builder(requireContext())
            .setMessage("It looks like you have turned off permissions required for this future. It can be enabled under Application Settings ")
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", requireContext().packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        val weatherResponseJsonString = mSharedPreferences.getString(WEATHER_RESPONSE_DATA, "")
        if (!weatherResponseJsonString.isNullOrEmpty()) {
            val weatherList =
                Gson().fromJson(weatherResponseJsonString, WeatherResponse::class.java)
            with(binding) {
                mainToolbar.title = weatherList.city.name
                tvTitleSunriseSunset.text = "SUNRISE & SUNSET"
                tvCity.text = weatherList.city.name
                tvTemperature.text = weatherList.list[1].main.temp.roundToInt().toString() + " °C"
                tvCurrentTemperature.text = weatherList.list[1].main.temp.roundToInt().toString() + " °C"
                tvTemp3.text = weatherList.list[2].main.temp.roundToInt().toString() + " °C"
                tvTemp6.text = weatherList.list[3].main.temp.roundToInt().toString() + " °C"
                tvTemp9.text = weatherList.list[4].main.temp.roundToInt().toString() + " °C"
                tv3.text = weatherList.list[2].dt_txt.subSequence(10..weatherList.list[2].dt_txt.length - 4)
                tv6.text = weatherList.list[3].dt_txt.subSequence(10..weatherList.list[3].dt_txt.length - 4)
                tv9.text = weatherList.list[4].dt_txt.subSequence(10..weatherList.list[4].dt_txt.length - 4)
                tvCurrentWindSpeed.text = weatherList.list[1].wind.speed.toString() + " km/h"
                tvWindSpeed3.text = weatherList.list[2].wind.speed.toString() + " km/h"
                tvWindSpeed6.text = weatherList.list[3].wind.speed.toString() + " km/h"
                tvWindSpeed9.text = weatherList.list[4].wind.speed.toString() + " km/h"
                tvSunrise.text = unixTime(weatherList.city.sunrise.toLong()) + " AM"
                tvSunset.text = unixTime(weatherList.city.sunset.toLong()) + " PM"
                val lengthDay = weatherList.city.sunset.toLong() - weatherList.city.sunrise.toLong()
                val hours = lengthDay / 3600
                val minutes = (lengthDay % 3600) / 60
                tvNominalLengthOfDay.text = "$hours" + "H " + "$minutes" + "M"

                val currentTime = System.currentTimeMillis() / 1000
                val remainingTime = weatherList.city.sunset.toLong() - currentTime
                val hoursOfRemain = remainingTime / 3600
                val minutesOfRemain = (remainingTime % 3600) / 60

                tvNominalRemainingDaylight.text = "$hoursOfRemain" + "H " + "$minutesOfRemain" + "M"
                tvHumidity.text = weatherList.list[1].main.humidity.toString() + " %"
                tvWindSpeedMain.text = weatherList.list[1].wind.speed.toString() + " km/h"


                Log.i("degree", "setupUI:${weatherList.list[3].weather[0].icon} ")
                when (weatherList.list[1].wind.deg) {
                    in 271..359 -> ivDirectionNow.setImageResource(R.drawable.ic_south_east)
                    270 -> ivDirectionNow.setImageResource(R.drawable.ic_south)
                    180 -> ivDirectionNow.setImageResource(R.drawable.ic_west)
                    0 -> ivDirectionNow.setImageResource(R.drawable.ic_east)
                    in 181..269 -> ivDirectionNow.setImageResource(R.drawable.ic_south_west)
                    in 91..179 -> ivDirectionNow.setImageResource(R.drawable.ic_north_west)
                    90 -> ivDirectionNow.setImageResource(R.drawable.ic_north)
                    in 1..89 -> ivDirectionNow.setImageResource(R.drawable.ic_north_east)
                    else -> ivDirectionNow.setImageResource(R.drawable.ic_north_east)

                }

                when (weatherList.list[2].wind.deg) {
                    in 271..359 -> ivDirectionNow.setImageResource(R.drawable.ic_south_east)
                    270 -> ivDirectionNow.setImageResource(R.drawable.ic_south)
                    180 -> ivDirectionNow.setImageResource(R.drawable.ic_west)
                    0 -> ivDirectionNow.setImageResource(R.drawable.ic_east)
                    in 181..269 -> ivDirectionNow.setImageResource(R.drawable.ic_south_west)
                    in 91..179 -> ivDirectionNow.setImageResource(R.drawable.ic_north_west)
                    90 -> ivDirectionNow.setImageResource(R.drawable.ic_north)
                    in 1..89 -> ivDirectionNow.setImageResource(R.drawable.ic_north_east)
                    else -> ivDirectionNow.setImageResource(R.drawable.ic_north_east)

                }
                when (weatherList.list[3].wind.deg) {
                    in 271..359 -> ivDirectionNow.setImageResource(R.drawable.ic_south_east)
                    270 -> ivDirectionNow.setImageResource(R.drawable.ic_south)
                    180 -> ivDirectionNow.setImageResource(R.drawable.ic_west)
                    0 -> ivDirectionNow.setImageResource(R.drawable.ic_east)
                    in 181..269 -> ivDirectionNow.setImageResource(R.drawable.ic_south_west)
                    in 91..179 -> ivDirectionNow.setImageResource(R.drawable.ic_north_west)
                    90 -> ivDirectionNow.setImageResource(R.drawable.ic_north)
                    in 1..89 -> ivDirectionNow.setImageResource(R.drawable.ic_north_east)
                    else -> ivDirectionNow.setImageResource(R.drawable.ic_north_east)
                }
                when (weatherList.list[4].wind.deg) {
                    in 271..359 -> ivDirectionNow.setImageResource(R.drawable.ic_south_east)
                    270 -> ivDirectionNow.setImageResource(R.drawable.ic_south)
                    180 -> ivDirectionNow.setImageResource(R.drawable.ic_west)
                    0 -> ivDirectionNow.setImageResource(R.drawable.ic_east)
                    in 181..269 -> ivDirectionNow.setImageResource(R.drawable.ic_south_west)
                    in 91..179 -> ivDirectionNow.setImageResource(R.drawable.ic_north_west)
                    90 -> ivDirectionNow.setImageResource(R.drawable.ic_north)
                    in 1..89 -> ivDirectionNow.setImageResource(R.drawable.ic_north_east)
                    else -> ivDirectionNow.setImageResource(R.drawable.ic_north_east)
                }





                when (weatherList.list[1].weather[0].icon) {
                    "01d" -> ivConditionWeather.setImageResource(R.drawable.clear_sky)
                    "02d" -> ivConditionWeather.setImageResource(R.drawable.few_clouds)
                    "03d" -> ivConditionWeather.setImageResource(R.drawable.scattered_clouds)
                    "04d" -> ivConditionWeather.setImageResource(R.drawable.broken_clouds)
                    "09d" -> ivConditionWeather.setImageResource(R.drawable.rain)
                    "10d" -> ivConditionWeather.setImageResource(R.drawable.shower_rain)
                    "11d" -> ivConditionWeather.setImageResource(R.drawable.thunderstrom)
                    "13d" -> ivConditionWeather.setImageResource(R.drawable.snow)
                    "50d" -> ivConditionWeather.setImageResource(R.drawable.mist)
                    "01n" -> ivConditionWeather.setImageResource(R.drawable.clear_sky_night)
                    "02n" -> ivConditionWeather.setImageResource(R.drawable.few_clouds_night)
                    "03n" -> ivConditionWeather.setImageResource(R.drawable.scattered_clouds)
                    "04n" -> ivConditionWeather.setImageResource(R.drawable.broken_clouds)
                    "09n" -> ivConditionWeather.setImageResource(R.drawable.rain_night)
                    "10n" -> ivConditionWeather.setImageResource(R.drawable.shower_rain)
                    "11n" -> ivConditionWeather.setImageResource(R.drawable.thunderstrom)
                    "13n" -> ivConditionWeather.setImageResource(R.drawable.snow)
                    "50n" -> ivConditionWeather.setImageResource(R.drawable.mist)


                }
                when (weatherList.list[1].weather[0].icon) {
                    "01d" -> ivCurrent.setImageResource(R.drawable.clear_sky)
                    "02d" -> ivCurrent.setImageResource(R.drawable.few_clouds)
                    "03d" -> ivCurrent.setImageResource(R.drawable.scattered_clouds)
                    "04d" -> ivCurrent.setImageResource(R.drawable.broken_clouds)
                    "09d" -> ivCurrent.setImageResource(R.drawable.shower_rain)
                    "10d" -> ivCurrent.setImageResource(R.drawable.rain)
                    "11d" -> ivCurrent.setImageResource(R.drawable.thunderstrom)
                    "13d" -> ivCurrent.setImageResource(R.drawable.snow)
                    "50d" -> ivCurrent.setImageResource(R.drawable.mist)

                    "01n" -> ivCurrent.setImageResource(R.drawable.clear_sky_night)
                    "02n" -> ivCurrent.setImageResource(R.drawable.few_clouds_night)
                    "03n" -> ivCurrent.setImageResource(R.drawable.scattered_clouds)
                    "04n" -> ivCurrent.setImageResource(R.drawable.broken_clouds)
                    "09n" -> ivCurrent.setImageResource(R.drawable.rain_night)
                    "10n" -> ivCurrent.setImageResource(R.drawable.shower_rain)
                    "11n" -> ivCurrent.setImageResource(R.drawable.thunderstrom)
                    "13n" -> ivCurrent.setImageResource(R.drawable.snow)
                    "50n" -> ivCurrent.setImageResource(R.drawable.mist)
                }


                when (weatherList.list[2].weather[0].icon) {
                    "01d" -> iv3.setImageResource(R.drawable.clear_sky)
                    "02d" -> iv3.setImageResource(R.drawable.few_clouds)
                    "03d" -> iv3.setImageResource(R.drawable.scattered_clouds)
                    "04d" -> iv3.setImageResource(R.drawable.broken_clouds)
                    "09d" -> iv3.setImageResource(R.drawable.shower_rain)
                    "10d" -> iv3.setImageResource(R.drawable.rain)
                    "11d" -> iv3.setImageResource(R.drawable.thunderstrom)
                    "13d" -> iv3.setImageResource(R.drawable.snow)
                    "50d" -> iv3.setImageResource(R.drawable.mist)

                    "01n" -> iv3.setImageResource(R.drawable.clear_sky_night)
                    "02n" -> iv3.setImageResource(R.drawable.few_clouds_night)
                    "03n" -> iv3.setImageResource(R.drawable.scattered_clouds)
                    "04n" -> iv3.setImageResource(R.drawable.broken_clouds)
                    "09n" -> iv3.setImageResource(R.drawable.rain_night)
                    "10n" -> iv3.setImageResource(R.drawable.shower_rain)
                    "11n" -> iv3.setImageResource(R.drawable.thunderstrom)
                    "13n" -> iv3.setImageResource(R.drawable.snow)
                    "50n" -> iv3.setImageResource(R.drawable.mist)
                }
                when (weatherList.list[3].weather[0].icon) {
                    "01d" -> iv6.setImageResource(R.drawable.clear_sky)
                    "02d" -> iv6.setImageResource(R.drawable.few_clouds)
                    "03d" -> iv6.setImageResource(R.drawable.scattered_clouds)
                    "04d" -> iv6.setImageResource(R.drawable.broken_clouds)
                    "09d" -> iv6.setImageResource(R.drawable.shower_rain)
                    "10d" -> iv6.setImageResource(R.drawable.rain)
                    "11d" -> iv6.setImageResource(R.drawable.thunderstrom)
                    "13d" -> iv6.setImageResource(R.drawable.snow)
                    "50d" -> iv6.setImageResource(R.drawable.mist)

                    "01n" -> iv6.setImageResource(R.drawable.clear_sky_night)
                    "02n" -> iv6.setImageResource(R.drawable.few_clouds_night)
                    "03n" -> iv6.setImageResource(R.drawable.scattered_clouds)
                    "04n" -> iv6.setImageResource(R.drawable.broken_clouds)
                    "09n" -> iv6.setImageResource(R.drawable.rain_night)
                    "10n" -> iv6.setImageResource(R.drawable.shower_rain)
                    "11n" -> iv6.setImageResource(R.drawable.thunderstrom)
                    "13n" -> iv6.setImageResource(R.drawable.snow)
                    "50n" -> iv6.setImageResource(R.drawable.mist)
                }
                when (weatherList.list[4].weather[0].icon) {
                    "01d" -> iv9.setImageResource(R.drawable.clear_sky)
                    "02d" -> iv9.setImageResource(R.drawable.few_clouds)
                    "03d" -> iv9.setImageResource(R.drawable.scattered_clouds)
                    "04d" -> iv9.setImageResource(R.drawable.broken_clouds)
                    "09d" -> iv9.setImageResource(R.drawable.shower_rain)
                    "10d" -> iv9.setImageResource(R.drawable.rain)
                    "11d" -> iv9.setImageResource(R.drawable.thunderstrom)
                    "13d" -> iv9.setImageResource(R.drawable.snow)
                    "50d" -> iv9.setImageResource(R.drawable.mist)

                    "01n" -> iv9.setImageResource(R.drawable.clear_sky_night)
                    "02n" -> iv9.setImageResource(R.drawable.few_clouds_night)
                    "03n" -> iv9.setImageResource(R.drawable.scattered_clouds)
                    "04n" -> iv9.setImageResource(R.drawable.broken_clouds)
                    "09n" -> iv9.setImageResource(R.drawable.rain_night)
                    "10n" -> iv9.setImageResource(R.drawable.shower_rain)
                    "11n" -> iv9.setImageResource(R.drawable.thunderstrom)
                    "13n" -> iv9.setImageResource(R.drawable.snow)
                    "50n" -> iv9.setImageResource(R.drawable.mist)
                }
            }

        }
    }


    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val mLastLocation = locationResult.lastLocation
            val latitude = mLastLocation?.latitude!!
            Log.i("Current Latitude", "$latitude ")
            val longitude = mLastLocation.longitude
            Log.i("Current longitude", "$longitude ")
            getLocationWeatherDetails(latitude, longitude)
        }
    }

    private fun getLocationWeatherDetails(latitude: Double, longitude: Double) {
        if (isNetworkAvailable(requireContext())) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(WeatherInterface::class.java)
            val listCall = service.getWeather(
                latitude, longitude, METRIC_UNIT, APP_ID
            )
            showCustomDialog()


            listCall.enqueue(object : Callback<WeatherResponse> {
                @SuppressLint("CommitPrefEdits")
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {

                    if (response.isSuccessful) {
                        hideProgressDialog()
                        val weatherList = response.body()!!
                        val weatherResponseJsonString = Gson().toJson(weatherList)
                        val editor = mSharedPreferences.edit()
                        editor.putString(WEATHER_RESPONSE_DATA, weatherResponseJsonString)
                        editor.apply()
                        setupUI()
                        Log.i("Response result", "$weatherList ")
                    } else {

                        when (response.code()) {
                            400 -> {
                                Log.e("Error 400", "Bad request")
                            }
                            404 -> {
                                Log.e("Error 404", "Not found")
                            }
                            else -> {
                                Log.e("Error", "Generic Error")
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    hideProgressDialog()
                    Log.e("Failure: ", t.message.toString())
                }
            })

        } else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCustomDialog() {
        mProgressDialog = Dialog(requireContext())
        mProgressDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mProgressDialog!!.setContentView(R.layout.custom_dialog)
        mProgressDialog!!.show()
    }

    private fun hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }

    private fun getUnit(value: String): String {
        var value = "C"
        if ("US" == value || "LR" == value || "MM" == value) {
            value = "F"
        }
        return value
    }

    @SuppressLint("SimpleDateFormat")
    private fun unixTime(timex: Long): String? {
        val date = Date(timex * 1000)
        val sdf = SimpleDateFormat("HH:mm")
        return sdf.format(date)
    }

}