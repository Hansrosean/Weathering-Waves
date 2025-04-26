package com.app.weatheringwaves.api.retrofit

import com.app.weatheringwaves.api.response.ForecastResponse
import com.app.weatheringwaves.api.response.SearchLocationResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("forecast.json")
    fun getForecastData (
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: String,
        @Query("aqi") airQuality: String,
        @Query("alerts") alerts: String
    ): Call<ForecastResponse>

    @GET("search.json")
    fun getSearchData (
        @Query("key") apiKey: String,
        @Query("q") location: String
    ): Call<List<SearchLocationResponse>>
}