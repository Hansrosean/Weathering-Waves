package com.app.weatheringwaves.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.weatheringwaves.R
import com.app.weatheringwaves.api.response.SearchLocationResponse

class LocationListAdapter (private var locationSearchList: List<SearchLocationResponse>, private val context: Context) :
    RecyclerView.Adapter<LocationListAdapter.LocationListViewHolder>() {

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        mListener = clickListener
    }

    class LocationListViewHolder(view: View, clicklistener: OnItemClickListener) : RecyclerView.ViewHolder(view) {
        val tvLocationNameRegion : TextView = view.findViewById(R.id.tv_result_search_location_name_region)
        val tvLocationCountry : TextView = view.findViewById(R.id.tv_result_search_location_country)

        init {
            itemView.setOnClickListener {
                clicklistener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LocationListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)
        return LocationListViewHolder(view, mListener)
    }

    override fun onBindViewHolder(
        holder: LocationListViewHolder,
        position: Int
    ) {
        val getLocationList = locationSearchList[position]

        val locationName = getLocationList.name
        val locationRegion = getLocationList.region
        holder.tvLocationNameRegion.text = context.getString(R.string.location_format, locationName, locationRegion)
        holder.tvLocationCountry.text = getLocationList.country
    }

    override fun getItemCount(): Int = locationSearchList.size

    fun updateData(newDataLocationSearchList: List<SearchLocationResponse>) {
        locationSearchList = newDataLocationSearchList
        notifyDataSetChanged()
    }
}