package com.clebs.celerity.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.AdapterBreaktimeBinding
import com.clebs.celerity.models.response.GetDriverBreakTimeInfoResponse
import com.clebs.celerity.models.response.GetDriverBreakTimeInfoResponseItem

class BreakTimeAdapter(
    val data: GetDriverBreakTimeInfoResponse,
    var viewModel: MainViewModel,
    var loadingDialog: () -> Unit
):RecyclerView.Adapter<BreakTimeAdapter.BreakTimeViewHolder>() {
    inner class BreakTimeViewHolder(val binding:AdapterBreaktimeBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(item: GetDriverBreakTimeInfoResponseItem){
            binding.breakStartTime.text = item.BreakTimeStart
            binding.breakEndTime.text = item.BreakTimeEnd
            binding.breakDelete.setOnClickListener {
                loadingDialog()
                viewModel.DeleteBreakTime(item.DawDriverBreakId)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreakTimeViewHolder {
        val binding = AdapterBreaktimeBinding.inflate(LayoutInflater.from(parent.context))
        return BreakTimeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BreakTimeViewHolder, position: Int) {
        if(position!=0){
            holder.binding.heading1.visibility = View.GONE
        }
        holder.bind(data[position])
    }
}