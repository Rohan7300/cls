package com.clebs.celerity.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.R
import com.clebs.celerity.databinding.AdapterRideAlongBinding
import com.clebs.celerity.models.response.RideAlongDriverInfoByDateResponse
import com.clebs.celerity.models.response.leadDriverIdItem
import com.clebs.celerity.utils.Prefs

class RideAlongAdapter(
    var data: RideAlongDriverInfoByDateResponse,
    var navController: NavController,var prefs: Prefs
) : RecyclerView.Adapter<RideAlongAdapter.RideAlongViewHolder>() {
    inner class RideAlongViewHolder(private val binding: AdapterRideAlongBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: leadDriverIdItem) {
            binding.tainerName.text = prefs.userName
            binding.traineeName.text = item.DriverName
            binding.edtIc.setOnClickListener {
                val bundle = bundleOf(
                    "rideAlongID" to item.DriverId,
                    "leadDriverID" to item.LeadDriverId
                )
                prefs.currRideAlongID = item.DriverId
                prefs.daWID = item.DawId
                prefs.currRtId = item.RtId
                navController.navigate(R.id.questinareFragment, bundle)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideAlongViewHolder {
        val binding = AdapterRideAlongBinding.inflate(LayoutInflater.from(parent.context))
        return RideAlongViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RideAlongViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }
}