package com.app.weatheringwaves.ui

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.weatheringwaves.BuildConfig
import com.app.weatheringwaves.R
import com.app.weatheringwaves.adapter.LocationListAdapter
import com.app.weatheringwaves.api.response.SearchLocationResponse
import com.app.weatheringwaves.api.retrofit.ApiConfig
import com.google.android.material.shape.MaterialShapeDrawable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchLocationFragment : Fragment() {

    private lateinit var searchViewLocation: SearchView
    private lateinit var rvLocationSearchResult: RecyclerView

    private lateinit var locationSearchList: List<SearchLocationResponse>

    private lateinit var locationListAdapter: LocationListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        searchViewLocation = view.findViewById(R.id.sv_location)

        val shapeDrawable = MaterialShapeDrawable()
        shapeDrawable.setCornerSize(16F)
        shapeDrawable.fillColor = ColorStateList.valueOf(resources.getColor(R.color.gradient_start))

        searchViewLocation.background = shapeDrawable

        searchViewLocation.requestFocus()
        searchViewLocation.isIconified = false
        searchViewLocation.setIconifiedByDefault(false)

        searchViewLocation.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)?.apply {
            drawable?.mutate()
            setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN)
        }
        searchViewLocation.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)?.apply {
            drawable?.mutate()
            setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN)
        }
        searchViewLocation.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)?.apply {
            setTextColor(ContextCompat.getColor(context, R.color.light_gray))
            setHintTextColor(ContextCompat.getColor(context, R.color.light_gray))
        }

        rvLocationSearchResult = view.findViewById(R.id.rv_location_search_result)
        rvLocationSearchResult.layoutManager = LinearLayoutManager(requireContext())

        locationSearchList = arrayListOf()

        locationListAdapter = LocationListAdapter(context = requireContext(), locationSearchList = locationSearchList)
        rvLocationSearchResult.adapter = locationListAdapter

        searchViewLocation.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchQueryLocation(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                when {
                    newText.isNullOrBlank() -> {
                        // if empty, clear data list
                        locationListAdapter.updateData(emptyList())
                    }
                    newText.length > 2 -> {
                        // if character more than 2, do a search
                        searchQueryLocation(newText)
                    }
                    else -> {
                        // if between 1-2 character, don't search and clear data list
                        locationListAdapter.updateData(emptyList())
                    }
                }
                return true
            }
        })

        searchViewLocation.setOnCloseListener {
            locationListAdapter.updateData(emptyList())
            false
        }
    }

    private fun searchQueryLocation(query: String) {
        val client = ApiConfig.getApiService().getSearchData(
            apiKey = BuildConfig.WEATHER_API_KEY,
            location = query
        )
        client.enqueue(object : Callback<List<SearchLocationResponse>> {
            override fun onResponse(
                call: Call<List<SearchLocationResponse>>,
                response: Response<List<SearchLocationResponse>>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        locationSearchList = responseBody
                        locationListAdapter.updateData(locationSearchList)

                        locationListAdapter.setOnItemClickListener(object : LocationListAdapter.OnItemClickListener {
                            override fun onItemClick(position: Int) {

                                val getLat = locationSearchList[position].lat
                                val getLong = locationSearchList[position].lon
                                val sendLatLong = "$getLat,$getLong"

                                // send data to activity
                                val bundle = Bundle().apply {
                                    putString(DATA_KEY, sendLatLong)
                                }

                                parentFragmentManager.setFragmentResult(REQUEST_KEY, bundle)
                                parentFragmentManager.popBackStack()
                            }
                        })
                    }
                }
            }

            override fun onFailure(
                call: Call<List<SearchLocationResponse>>,
                t: Throwable
            ) {
                Log.e("SearchLocationFragment", "onFailure: ${t.message}")
                Toast.makeText(requireContext(), "Failed to load data. Please try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        const val REQUEST_KEY = "200"
        const val DATA_KEY = "100"
    }
}