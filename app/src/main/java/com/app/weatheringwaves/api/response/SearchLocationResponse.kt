package com.app.weatheringwaves.api.response

import com.google.gson.annotations.SerializedName

data class SearchLocationResponse(

	@field:SerializedName("country")
	val country: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("lon")
	val lon: Any,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("region")
	val region: String,

	@field:SerializedName("lat")
	val lat: Any,

	@field:SerializedName("url")
	val url: String
)
