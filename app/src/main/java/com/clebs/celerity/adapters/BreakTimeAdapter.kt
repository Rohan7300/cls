package com.clebs.celerity.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.AdapterBreaktimeBinding
import com.clebs.celerity.models.response.GetDriverBreakTimeInfoResponse
import com.clebs.celerity.models.response.GetDriverBreakTimeInfoResponseItem
import java.text.SimpleDateFormat
import java.util.Locale

class BreakTimeAdapter(
    val data: GetDriverBreakTimeInfoResponse,
    var viewModel: MainViewModel,
    var loadingDialog: () -> Unit
):RecyclerView.Adapter<BreakTimeAdapter.BreakTimeViewHolder>() {
    inner class BreakTimeViewHolder(val binding:AdapterBreaktimeBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(item: GetDriverBreakTimeInfoResponseItem){
            val startTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).parse(item.BreakTimeStart)
            val endTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).parse(item.BreakTimeEnd)

            // Format the parsed times to display only hours and minutes
            val startTimeFormatted = SimpleDateFormat("HH:mm", Locale.getDefault()).format(startTime)
            val endTimeFormatted = SimpleDateFormat("HH:mm", Locale.getDefault()).format(endTime)

            binding.breakStartTime.text = startTimeFormatted
            binding.breakEndTime.text = endTimeFormatted

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