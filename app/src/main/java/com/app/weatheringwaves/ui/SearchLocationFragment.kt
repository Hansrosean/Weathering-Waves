package com.app.weatheringwaves.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.weatheringwaves.BuildConfig
import com.app.weatheringwaves.R
import com.app.weatheringwaves.adapter.LocationListAdapter
import com.app.weatheringwaves.api.response.SearchLocationResponse
import com.app.weatheringwaves.api.retrofit.ApiConfig
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
        searchViewLocation.requestFocus()

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
                newText?.takeIf { it.length > 2 }?.let { searchQueryLocation(it) }

                if (newText.isNullOrEmpty() || newText.isBlank()) {
                    locationListAdapter.updateData(emptyList())
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

                                val getLat = locationSearchList[0].lat
                                val getLong = locationSearchList[0].lon
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