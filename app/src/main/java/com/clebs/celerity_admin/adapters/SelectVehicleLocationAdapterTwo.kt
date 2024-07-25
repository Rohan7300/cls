package com.clebs.celerity_admin.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.databinding.RecylerviewAdapterBinding
import com.clebs.celerity_admin.models.GetVehicleLocationItem
import com.clebs.celerity_admin.utils.OnItemClickRecyclerView

class SelectVehicleLocationAdapterTwo(
    var data: ArrayList<GetVehicleLocationItem>,
    var click: OnItemClickRecyclerView
) : RecyclerView.Adapter<SelectVehicleLocationAdapterTwo.SelectVehicleLocationAdapterViewHolder>() {
    private val selectedItems = mutableMapOf<Int, String>()
    private var selectedPosition = -1

    inner class SelectVehicleLocationAdapterViewHolder(var binding: RecylerviewAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(item: GetVehicleLocationItem) {
            itemView.setOnClickListener {
                click.OnItemClickRecyclerViewClicks(
                    R.id.tvcompany,
                    item.locId,
                    item.locationName
                )
/*                if(data.indexOf(item)!=0 && item.locationName!="ALL") {

                }else{
                    click.OnItemClickRecyclerViewClicks(
                        R.id.tvcompany,
                        0,
                       "ALL"
                    )
                }*/
            }


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
        return data.size +1
    }

    override fun onBindViewHolder(holder: SelectVehicleLocationAdapterViewHolder, position: Int) {
        try {
            val item = data[position]
            holder.bindView(item)
/*
            if (position == 0) {
                // This is the default item
                holder.itemView.findViewById<TextView>(R.id.tvcompany).setText("ALL")
            } else {*/
                // This is a regular item
                val itema = data[position]
                holder.itemView.findViewById<TextView>(R.id.tvcompany).setText(itema.locationName)
            //}
        }catch (e:Exception){
            Log.d("BindView","BindViewHolderException $e")
        }
    }

    fun getSelectedItem(position: Int): String? {
        return selectedItems[position]
    }
}