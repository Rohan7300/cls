package com.clebs.celerity_admin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.databinding.RecylerviewAdapterBinding
import com.clebs.celerity_admin.models.CompanyListResponseItem
import com.clebs.celerity_admin.models.GetVehicleFuelLevelListItem
import com.clebs.celerity_admin.utils.OnItemClickRecyclerView

class FinalCompanyListAdapter(
    var data: ArrayList<CompanyListResponseItem>,
    var click: OnItemClickRecyclerView
) : RecyclerView.Adapter<FinalCompanyListAdapter.FinalCompanyListViewHolder>() {

    inner class FinalCompanyListViewHolder(var binding: RecylerviewAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(item: CompanyListResponseItem) {
            itemView.setOnClickListener {
                click.OnItemClickRecyclerViewClicks(
                    R.id.sp1,
                    position,
                    item.name
                )
            }
            binding.tvcompany.setText(item.name)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinalCompanyListViewHolder {
        val binding = RecylerviewAdapterBinding.inflate(LayoutInflater.from(parent.context))
        return FinalCompanyListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: FinalCompanyListViewHolder, position: Int) {
        val item = data[position]

        holder.bindView(item)
    }
}