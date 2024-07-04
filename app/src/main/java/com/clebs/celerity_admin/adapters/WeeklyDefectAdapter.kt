package com.clebs.celerity_admin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity_admin.databinding.WeeklyDefectAdapterBinding
import com.clebs.celerity_admin.models.WeeklyDefectChecksModelItem

class WeeklyDefectAdapter(var data: ArrayList<WeeklyDefectChecksModelItem>) :
    RecyclerView.Adapter<WeeklyDefectAdapter.WeeklyDefectViewHolder>() {
    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1
    lateinit var binding: WeeklyDefectAdapterBinding

    inner class WeeklyDefectViewHolder(var binding: WeeklyDefectAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(item: WeeklyDefectChecksModelItem) {
            binding.tvReg.setText(item.vehRegNo)
            binding.tvDaName.setText(item.dAName)
            binding.tvDaLocationname.setText(item.locationName)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyDefectViewHolder {


        binding = WeeklyDefectAdapterBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return WeeklyDefectViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: WeeklyDefectViewHolder, position: Int) {
        val item = data[position]
        holder.bindView(item)
    }
}