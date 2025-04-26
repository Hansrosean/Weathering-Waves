package com.app.weatheringwaves.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.weatheringwaves.R
import com.app.weatheringwaves.api.response.ForecastdayItem
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class ForecastDailyAdapter(
    private val forecastDailyList: List<ForecastdayItem>,
    private val context: Context
) : RecyclerView.Adapter<ForecastDailyAdapter.ForecastDailyViewHolder>() {

    class ForecastDailyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvForecastTemperatureDaily: TextView =
            view.findViewById(R.id.tv_item_forecast_daily_temperature)
        val imgForecastIconDailyWeather: ImageView =
            view.findViewById(R.id.img_icon_forecast_daily_weather)
        val tvDayNameForecastDaily: TextView =
            view.findViewById(R.id.tv_day_name_forecast_daily)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ForecastDailyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_forecast_daily, parent, false)
        return ForecastDailyViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastDailyViewHolder, position: Int) {
        val getDailyForecastList = forecastDailyList[position]

        // temperature
        val temperature = getDailyForecastList.day.avgtempC.toString().substringBefore(".") // remove the (.)

        val getStringTemperature = context.getString(
            R.string.temperature_format, temperature,
            context.getString(R.string.degrees_celcius)
        )
        holder.tvForecastTemperatureDaily.text = getStringTemperature

        // day name
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val getDayName = getDailyForecastList.date.toString()
        val inputDate = inputDateFormat.parse(getDayName)
        val outputDate = outputDateFormat.format(inputDate!!)
        holder.tvDayNameForecastDaily.text = outputDate

        // icon
        val getIcon = getDailyForecastList.day.condition.icon
        val iconUrl = "https:$getIcon"

        Glide.with(holder.imgForecastIconDailyWeather)
            .load(iconUrl)
            .into(holder.imgForecastIconDailyWeather)
    }

    override fun getItemCount(): Int = forecastDailyList.size
}