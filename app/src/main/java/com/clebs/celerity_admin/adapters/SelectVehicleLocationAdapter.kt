package com.clebs.celerity_admin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.databinding.CompanyAdapterBinding
import com.clebs.celerity_admin.databinding.RecylerviewAdapterBinding
import com.clebs.celerity_admin.models.GetVehicleLocationItem
import com.clebs.celerity_admin.models.VehicleReturnModelListItem
import com.clebs.celerity_admin.utils.OnItemClickRecyclerView
import com.clebs.celerity_admin.utils.VehicleLocationClick

class SelectVehicleLocationAdapter(
    var data: ArrayList<GetVehicleLocationItem>,
    var click: OnItemClickRecyclerView
) :
    RecyclerView.Adapter<SelectVehicleLocationAdapter.SelectVehicleLocationAdapterViewHolder>() {
    private val selectedItems = mutableMapOf<Int, String>()
    private var selectedPosition = -1

    inner class SelectVehicleLocationAdapterViewHolder(var binding: RecylerviewAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(item: GetVehicleLocationItem) {
            itemView.setOnClickListener {
                click.OnItemClickRecyclerViewClicks(
                    R.id.rv_vehicle_location,
                    position,
                    item.locationName,item.locId
                )
            }
            binding.tvcompany.setText(item.locationName)

//            binding.tvcompany.setOnClickListener { callback.OnItemClickRecyclerViewClicks(item,data[position]) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectVehicleLocationAdapterViewHolder {
        val binding = RecylerviewAdapterBinding.inflate(LayoutInflater.from(parent.context))
        return SelectVehicleLocationAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: SelectVehicleLocationAdapterViewHolder, position: Int) {
        val item = data[position]
        holder.bindView(item)


    }

    fun getSelectedItem(position: Int): String? {
        return selectedItems[position]
    }
}