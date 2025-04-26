package com.app.weatheringwaves.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.weatheringwaves.R
import com.app.weatheringwaves.api.response.HourItem
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class ForecastHourlyAdapter(
    private val forecastHourlyList: List<HourItem>,
    private val context: Context
) :
    RecyclerView.Adapter<ForecastHourlyAdapter.ForecastHourlyViewHolder>() {

    class ForecastHourlyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvForecastTemperatureHourly: TextView =
            view.findViewById(R.id.tv_item_forecast_hourly_temperature)
        val imgForecastIconHourlyWeather: ImageView =
            view.findViewById(R.id.img_icon_forecast_hourly_weather)
        val tvForecastHourlyCurrentTime: TextView =
            view.findViewById(R.id.tv_current_time_forecast_hourly)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ForecastHourlyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forecast_hourly, parent, false)
        return ForecastHourlyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ForecastHourlyViewHolder,
        position: Int
    ) {
        val getForecastList = forecastHourlyList[position]

        // temperature
        val temperature = getForecastList.tempC.toString().substringBefore(".") // remove the (.)
        val getStringTemperature =
            context.getString(
                R.string.temperature_format,
                temperature,
                context.getString(R.string.degrees_celcius)
            )
        holder.tvForecastTemperatureHourly.text = getStringTemperature

        // time
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val getCurrentTime = getForecastList.time.toString()
        val inputCurrentTime = inputDateFormat.parse(getCurrentTime)
        val outputCurrentTime = outputDateFormat.format(inputCurrentTime!!)
        holder.tvForecastHourlyCurrentTime.text = outputCurrentTime

        // icon
        val getIcon = getForecastList.condition.icon
        val iconUrl = "https:$getIcon"

        Glide.with(holder.imgForecastIconHourlyWeather.context)
            .load(iconUrl)
            .into(holder.imgForecastIconHourlyWeather)
    }

    override fun getItemCount(): Int = forecastHourlyList.size
}