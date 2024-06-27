package com.clebs.celerity_admin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.databinding.RecylerviewAdapterBinding
import com.clebs.celerity_admin.models.GetVehicleLocationItem
import com.clebs.celerity_admin.models.GetvehicleOilLevelListItem
import com.clebs.celerity_admin.utils.OnItemClickRecyclerView

class VehicleOilLevelAdapter(var data:ArrayList<GetvehicleOilLevelListItem>, var click:OnItemClickRecyclerView):
    RecyclerView.Adapter<VehicleOilLevelAdapter.VehicleLevelAdapterViewHolder>() {
    inner class VehicleLevelAdapterViewHolder(var binding: RecylerviewAdapterBinding):RecyclerView.ViewHolder(binding.root){
        fun bindView(item: GetvehicleOilLevelListItem) {
            itemView.setOnClickListener {
                click.OnItemClickRecyclerViewClicks(
                    R.id.rv_vehicle_oillevel,
                    position,
                    item.vehOilLevelName
                )
            }
            binding.tvcompany.setText(item.vehOilLevelName)

//            binding.tvcompany.setOnClickListener { callback.OnItemClickRecyclerViewClicks(item,data[position]) }
        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VehicleLevelAdapterViewHolder {
        val binding = RecylerviewAdapterBinding.inflate(LayoutInflater.from(parent.context))
        return VehicleLevelAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int {
     return data.size
    }

    override fun onBindViewHolder(holder: VehicleLevelAdapterViewHolder, position: Int) {
        val item = data[position]
        holder.bindView(item)
    }
}