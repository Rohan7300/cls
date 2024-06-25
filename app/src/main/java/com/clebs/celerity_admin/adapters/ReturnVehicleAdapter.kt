package com.clebs.celerity_admin.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity_admin.databinding.CompanyAdapterBinding
import com.clebs.celerity_admin.models.DriverListResponseModelItem
import com.clebs.celerity_admin.models.VehicleReturnModelList
import com.clebs.celerity_admin.models.VehicleReturnModelListItem
import com.clebs.celerity_admin.utils.OnReturnVehicle

class ReturnVehicleAdapter(var data:ArrayList<VehicleReturnModelListItem>, var callback:OnReturnVehicle) :
    RecyclerView.Adapter<ReturnVehicleAdapter.ReturnVehicleAdapterViewHolder>() {
    inner class ReturnVehicleAdapterViewHolder(val binding: CompanyAdapterBinding) :RecyclerView.ViewHolder(binding.root){
        fun bindView(item: VehicleReturnModelListItem) {

            binding.tvcompany.setText(item.vehicleRegNo)

            binding.tvcompany.setOnClickListener { callback.onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReturnVehicleAdapterViewHolder {
        val binding = CompanyAdapterBinding.inflate(LayoutInflater.from(parent.context))
        return ReturnVehicleAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int {
     return data.size
    }

    override fun onBindViewHolder(holder: ReturnVehicleAdapterViewHolder, position: Int) {
        val item = data[position]
        holder.bindView(item)
    }
}