package com.clebs.celerity_admin.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity_admin.databinding.CompanyAdapterBinding
import com.clebs.celerity_admin.databinding.RecylerviewAdapterBinding
import com.clebs.celerity_admin.models.CompanyListResponseItem
import com.clebs.celerity_admin.models.GetVehicleRequestType
import com.clebs.celerity_admin.models.GetVehicleRequestTypeItem
import com.clebs.celerity_admin.utils.Onclick

class CompanyListAdapter(var data: ArrayList<GetVehicleRequestTypeItem>, var callback: Onclick) :
    RecyclerView.Adapter<CompanyListAdapter.Companylistviewholder>() {

    inner class Companylistviewholder(val binding: RecylerviewAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: GetVehicleRequestTypeItem) {

            binding.tvcompany.setText(item.name)
            Log.e("djkfdjfhdfdj", "bindView: " + item.name)
            binding.tvcompany.setOnClickListener { callback.onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Companylistviewholder {
        val binding = RecylerviewAdapterBinding.inflate(LayoutInflater.from(parent.context))
        return Companylistviewholder(binding)

    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: Companylistviewholder, position: Int) {
        val item = data[position]
        holder.bindView(item)

    }
}