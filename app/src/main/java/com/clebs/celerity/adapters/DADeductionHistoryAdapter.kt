package com.clebs.celerity.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.databinding.AdapterDadeductionHistoryBinding
import com.clebs.celerity.models.response.GetDriverDeductionHistoryResponse
import com.clebs.celerity.models.response.GetDriverDeductionHistoryResponseItem

class DADeductionHistoryAdapter :
    RecyclerView.Adapter<DADeductionHistoryAdapter.DADeductionHistoryAdapterViewHolder>() {
    lateinit var binding: AdapterDadeductionHistoryBinding

    inner class DADeductionHistoryAdapterViewHolder(val binding: AdapterDadeductionHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GetDriverDeductionHistoryResponseItem) {

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DADeductionHistoryAdapterViewHolder {
        binding = AdapterDadeductionHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DADeductionHistoryAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: DADeductionHistoryAdapterViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
    }

    private val diffUtil =
        object : DiffUtil.ItemCallback<GetDriverDeductionHistoryResponseItem>() {
            override fun areItemsTheSame(
                oldItem: GetDriverDeductionHistoryResponseItem,
                newItem: GetDriverDeductionHistoryResponseItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: GetDriverDeductionHistoryResponseItem,
                newItem: GetDriverDeductionHistoryResponseItem
            ): Boolean {
                return oldItem == newItem
            }
        }

    private var asyncListDiffer = AsyncListDiffer(this, diffUtil)
    fun submitList(data: GetDriverDeductionHistoryResponse) {
        asyncListDiffer.submitList(data)
    }
}