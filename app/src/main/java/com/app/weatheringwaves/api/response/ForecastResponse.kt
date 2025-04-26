package com.app.weatheringwaves.api.response

import com.google.gson.annotations.SerializedName

data class ForecastResponse(

    @field:SerializedName("current")
    val current: Current,

    @field:SerializedName("location")
    val location: Location,

    @field:SerializedName("forecast")
    val forecast: Forecast
)

data class Astro(

    @field:SerializedName("sunrise")
    val sunrise: String,

    @field:SerializedName("sunset")
    val sunset: String
)

data class Location(

    @field:SerializedName("localtime")
    val localtime: String,

    @field:SerializedName("localtime_epoch")
    val localtimeEpoch: Int,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("region")
    val region: String,

    @field:SerializedName("lon")
    val lon: Any,

    @field:SerializedName("lat")
    val lat: Any,

    @field:SerializedName("tz_id")
    val tzId: String
)

data class Current(
    @field:SerializedName("temp_c")
    val tempC: Any,

    @field:SerializedName("condition")
    val condition: Condition,

    @field:SerializedName("air_quality")
    val airQuality: AirQuality
)

data class HourItem(

    @field:SerializedName("temp_c")
    val tempC: Any,

    @field:SerializedName("condition")
    val condition: Condition,

    @field:SerializedName("time_epoch")
    val timeEpoch: Int,

    @field:SerializedName("time")
    val time: String
)

data class Condition(

    @field:SerializedName("code")
    val code: Int,

    @field:SerializedName("icon")
    val icon: String,

    @field:SerializedName("text")
    val text: String
)

data class AirQuality(

    @field:SerializedName("no2")
    val no2: Any,

    @field:SerializedName("o3")
    val o3: Any,

    @field:SerializedName("us-epa-index")
    val usEpaIndex: Int,

    @field:SerializedName("so2")
    val so2: Any,

    @field:SerializedName("pm2_5")
    val pm25: Any,

    @field:SerializedName("pm10")
    val pm10: Any,

    @field:SerializedName("co")
    val co: Any
)

data class Day(

    @field:SerializedName("uv")
    val uv: Any,

    @field:SerializedName("avgtemp_c")
    val avgtempC: Any,

    @field:SerializedName("maxtemp_c")
    val maxtempC: Any,

    @field:SerializedName("mintemp_c")
    val mintempC: Any,

    @field:SerializedName("air_quality")
    val airQuality: AirQuality,

    @field:SerializedName("condition")
    val condition: Condition,
)

data class ForecastdayItem(

    @field:SerializedName("date")
    val date: String,

    @field:SerializedName("astro")
    val astro: Astro,

    @field:SerializedName("date_epoch")
    val dateEpoch: Int,

    @field:SerializedName("hour")
    val hour: List<HourItem>,

    @field:SerializedName("day")
    val day: Day
)

data class Forecast(

    @field:SerializedName("forecastday")
    val forecastday: List<ForecastdayItem>
)

// CATATAN :
// Hindari penggunaan Any jika sudah tahu tipe data yang akan digunakan.
