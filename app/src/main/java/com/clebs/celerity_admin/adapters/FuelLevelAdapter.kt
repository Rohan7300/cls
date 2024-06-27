package com.clebs.celerity_admin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.databinding.RecylerviewAdapterBinding
import com.clebs.celerity_admin.models.GetVehicleFuelLevelList
import com.clebs.celerity_admin.models.GetVehicleFuelLevelListItem
import com.clebs.celerity_admin.utils.OnItemClickRecyclerView

class FuelLevelAdapter(var data:ArrayList<GetVehicleFuelLevelListItem>, var click:OnItemClickRecyclerView):
    RecyclerView.Adapter<FuelLevelAdapter.FuelLevelViewHolder>() {
    inner class FuelLevelViewHolder(var binding: RecylerviewAdapterBinding) :RecyclerView.ViewHolder(binding.root){
        fun bindView(item:GetVehicleFuelLevelListItem){
            itemView.setOnClickListener {
                click.OnItemClickRecyclerViewClicks(
                    R.id.rv_vehicle_fuellevel,
                    position,
                    item.vehFuelLevelName
                )
            }
            binding.tvcompany.setText(item.vehFuelLevelName)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FuelLevelViewHolder {
        val binding = RecylerviewAdapterBinding.inflate(LayoutInflater.from(parent.context))
        return FuelLevelViewHolder(binding)
    }

    override fun getItemCount(): Int {
         return  data.size
    }

    override fun onBindViewHolder(holder: FuelLevelViewHolder, position: Int) {
        val item = data[position]
        holder.bindView(item)
    }
}