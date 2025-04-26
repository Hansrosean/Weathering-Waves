package com.app.weatheringwaves.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.weatheringwaves.BuildConfig
import com.app.weatheringwaves.R
import com.app.weatheringwaves.adapter.ForecastHourlyAdapter
import com.app.weatheringwaves.api.response.ForecastResponse
import com.app.weatheringwaves.api.response.HourItem
import com.app.weatheringwaves.api.retrofit.ApiConfig
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {
    private lateinit var tvCityName: TextView
    private lateinit var tvCurrentDate: TextView
    private lateinit var tvCurrentWeather: TextView
    private lateinit var tvCurrentTemperature: TextView
    private lateinit var imgCurrentWeatherIcon: ImageView
    private lateinit var imgLocationDetails: ImageView
    private lateinit var imgSearchIcon: ImageView
    private lateinit var recyclerView: RecyclerView

    private lateinit var materialCardView: MaterialCardView

    private lateinit var forecastHourlyList: List<HourItem>

    private lateinit var forecastHourlyAdapter: ForecastHourlyAdapter

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    var selectedLatLong = ""
    var defaultLatLong = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvCityName = findViewById(R.id.tv_city_name)
        tvCurrentDate = findViewById(R.id.tv_current_date)
        tvCurrentWeather = findViewById(R.id.tv_current_weather)
        tvCurrentTemperature = findViewById(R.id.tv_current_temperature)
        imgCurrentWeatherIcon = findViewById(R.id.img_icon_current_weather)
        imgSearchIcon = findViewById(R.id.img_icon_search)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getCurrentLocation()

        forecastHourlyList = arrayListOf()

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        recyclerView = findViewById(R.id.rv_forecast_hourly)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = linearLayoutManager

        imgSearchIcon.setOnClickListener {
            openSearchFragment()
        }

        // set selected location from search
        supportFragmentManager.setFragmentResultListener(REQUEST_KEY, this) { _, bundle ->
            selectedLatLong = bundle.getString(DATA_KEY) ?: ""
            getCurrentForecastData(selectedLatLong)
        }

        imgLocationDetails = findViewById(R.id.img_icon_location_details)
        imgLocationDetails.setOnClickListener {
            showDetailLocationData()
        }

        // material card view rounded top only
        val shapeDrawable = MaterialShapeDrawable().apply {
            shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setTopLeftCorner(CornerFamily.ROUNDED, 70f)
                .setTopRightCorner(CornerFamily.ROUNDED, 70f)
                .build()
        }
        materialCardView = findViewById(R.id.materialCardView_hourly)
        materialCardView.background = shapeDrawable
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        when {
            permission[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                getCurrentLocation()
            }

            permission[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                getCurrentLocation()
            }

            else -> {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getCurrentLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latLong = "${location.latitude},${location.longitude}"
                    getCurrentForecastData(latLong)
                    Log.d("LatLong", latLong)
                } else {
                    Log.e("LatLong", "Location Not Found")
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun getCurrentForecastData(location: String) {
        val client = ApiConfig.getApiService().getForecastData(
            apiKey = BuildConfig.WEATHER_API_KEY,
            location = location,
            days = "2",
            airQuality = "yes",
            alerts = "no"
        )
        client.enqueue(object : Callback<ForecastResponse> {
            override fun onResponse(
                call: Call<ForecastResponse>, response: Response<ForecastResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        // city name
                        tvCityName.text = responseBody.location.name

                        // current date
                        val inputDateFormat =
                            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        val outputDateFormat =
                            SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())
                        val getLocalTime = responseBody.location.localtime
                        val inputLocalTime = inputDateFormat.parse(getLocalTime)
                        val outputLocalTime = outputDateFormat.format(inputLocalTime!!)
                        tvCurrentDate.text = outputLocalTime

                        // current weather
                        tvCurrentWeather.text = responseBody.current.condition.text

                        // current weather icon
                        Glide.with(this@MainActivity)
                            .load("https:" + responseBody.current.condition.icon).override(300, 300)
                            .into(imgCurrentWeatherIcon)

                        // current temperature
                        val temperature =
                            responseBody.current.tempC.toString().substringBefore(".") + "°C"
                        tvCurrentTemperature.text = temperature

                        // forecast hourly (hanya satu hari saja)
//                        val hours = responseBody.forecast.forecastday[0].hour
//                        val time = responseBody.location.localtime
//
//                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
//
//                        val now = Date()
//
//                        val filtered = hours.filter { hourData ->
//                            val itemDate = dateFormat.parse(hourData.time)
//                            itemDate?.after(now) == true && itemDate.before(Date(now.time + 7 * 60 * 60 * 1000))
//                        }
//                         val result = filtered.take(5)

                        // forecast hourly hari ini dan besok (apabila waktu melebihi 23.00)
                        val tzId = responseBody.location.tzId
                        val timezone = TimeZone.getTimeZone(tzId)

                        val allHours = responseBody.forecast.forecastday.flatMap { it.hour }

                        val calendarNow = Calendar.getInstance(timezone)
                        val now = calendarNow.time

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        dateFormat.timeZone = timezone

                        // filter dari waktu sekarang hingga +8 jam ke depan
                        val filteredHours = allHours.filter { hour ->
                            val hourDate = dateFormat.parse(hour.time)
                            hourDate?.after(now) == true && hourDate.before(Date(now.time + 8 * 60 * 60 * 1000))
                        }

                        forecastHourlyList = filteredHours
                        forecastHourlyAdapter = ForecastHourlyAdapter(
                            context = this@MainActivity, forecastHourlyList = forecastHourlyList
                        )
                        recyclerView.adapter = forecastHourlyAdapter
                    }
                }
                Log.d("Success", "onResponse: ${response.code()}")
            }

            override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                Toast.makeText(
                    this@MainActivity, "Failed to load data. Please try again.", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun showDetailLocationData() {
        // send default location or selected location
        if (checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION) && checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    defaultLatLong = "${location.latitude},${location.longitude}"
                    Log.d("LatLong", "${location.latitude},${location.longitude}")

                    val intent = Intent(this, LocationDetailActivity::class.java)
                    intent.putExtra(DEFAULT_LOC_KEY, defaultLatLong)
                    intent.putExtra(SELECTED_LOC_KEY, selectedLatLong)
                    startActivity(intent)
                } else {
                    Log.e("LatLong", "Location Not Found")
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    private fun openSearchFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SearchLocationFragment()).addToBackStack(null)
            .commit()

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                findViewById<View>(R.id.main).visibility = View.VISIBLE
            } else {
                findViewById<View>(R.id.main).visibility = View.GONE
            }
        }
    }

    companion object {
        const val REQUEST_KEY = "200"
        const val DATA_KEY = "100"
        const val SELECTED_LOC_KEY = "1"
        const val DEFAULT_LOC_KEY = "0"
    }
}