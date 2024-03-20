package com.clebs.celerity.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.databinding.AdapterDriverRouteBinding
import com.clebs.celerity.models.response.GetDriverRouteInfoByDateResponse
import com.clebs.celerity.models.response.GetDriverRouteInfoByDateResponseItem

class DriverRouteAdapter(var list: GetDriverRouteInfoByDateResponse) :
    RecyclerView.Adapter<DriverRouteAdapter.DriverRouteAdapterViewHolder>() {

    inner class DriverRouteAdapterViewHolder(private val binding: AdapterDriverRouteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GetDriverRouteInfoByDateResponseItem) {
            binding.routeNameTwo.text = item.RtName
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DriverRouteAdapterViewHolder {

        val binding = AdapterDriverRouteBinding.inflate(LayoutInflater.from(parent.context))

        return DriverRouteAdapterViewHolder(
            binding
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DriverRouteAdapterViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }
}