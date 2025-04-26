package com.app.weatheringwaves.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.app.weatheringwaves.BuildConfig
import com.app.weatheringwaves.R
import com.app.weatheringwaves.api.response.ForecastResponse
import com.app.weatheringwaves.api.retrofit.ApiConfig
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AirQualityDialogFragment : DialogFragment() {

    private lateinit var tvCo: TextView
    private lateinit var tvNo2: TextView
    private lateinit var tvO3: TextView
    private lateinit var tvSo2: TextView
    private lateinit var tvPm25: TextView
    private lateinit var tvPm10: TextView
    private lateinit var btnClose: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_air_quality_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvCo = view.findViewById(R.id.tv_co)
        tvNo2 = view.findViewById(R.id.tv_no2)
        tvO3 = view.findViewById(R.id.tv_o3)
        tvSo2 = view.findViewById(R.id.tv_so2)
        tvPm25 = view.findViewById(R.id.tv_pm25)
        tvPm10 = view.findViewById(R.id.tv_pm10)
        btnClose = view.findViewById(R.id.btn_close)

        btnClose.setOnClickListener {
            dialog?.cancel()
        }

        setData()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun setData() {
        // get location data from MainActivity
        val defaultLatLong = arguments?.getString(DATA_KEY)
        val selectedLatLong = arguments?.getString(DATA_KEY)

        // set location data
        if (selectedLatLong.isNullOrEmpty()) {
            getLocationData(defaultLatLong.toString())
        } else {
            getLocationData(selectedLatLong.toString())
        }
    }

    private fun getLocationData(location: String) {
        lifecycleScope.launch {
            try {
                val client = ApiConfig.getApiService().getForecastData(
                    apiKey = BuildConfig.WEATHER_API_KEY,
                    location = location,
                    days = "1",
                    airQuality = "yes",
                    alerts = "no"
                )
                client.enqueue(object : Callback<ForecastResponse> {
                    override fun onResponse(
                        call: Call<ForecastResponse>,
                        response: Response<ForecastResponse>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null) {
                                tvCo.text = responseBody.current.airQuality.co.toString()

                                tvNo2.text = responseBody.current.airQuality.no2.toString()
                                tvO3.text = responseBody.current.airQuality.o3.toString()
                                tvSo2.text = responseBody.current.airQuality.so2.toString()
                                tvPm25.text = responseBody.current.airQuality.pm25.toString()
                                tvPm10.text = responseBody.current.airQuality.pm10.toString()
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<ForecastResponse>,
                        t: Throwable
                    ) {
                        Log.e("AirQualityDialogFragment", "onFailure: ${t.message}", t)
                        Toast.makeText(requireContext(), "Failed to load data. Please try again.", Toast.LENGTH_SHORT).show()
                    }

                })
            }
            catch (e: Exception) {
                Log.e("error", "getDailyForecastData: ${e.message}")
            }
        }
    }

    companion object {
        const val DATA_KEY = "100"
    }
}