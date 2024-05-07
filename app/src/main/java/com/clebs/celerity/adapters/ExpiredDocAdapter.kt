package com.clebs.celerity.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.databinding.ItemExpiredDocsBinding
import com.clebs.celerity.models.response.GetDAVehicleExpiredDocumentsItem

class ExpiredDocAdapter : RecyclerView.Adapter<ExpiredDocAdapter.ExpiredDocViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<GetDAVehicleExpiredDocumentsItem>() {
        override fun areItemsTheSame(
            oldItem: GetDAVehicleExpiredDocumentsItem,
            newItem: GetDAVehicleExpiredDocumentsItem
        ): Boolean {
            return oldItem.VehDocId == newItem.VehDocId
        }

        override fun areContentsTheSame(
            oldItem: GetDAVehicleExpiredDocumentsItem,
            newItem: GetDAVehicleExpiredDocumentsItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)

    fun saveData(list: ArrayList<GetDAVehicleExpiredDocumentsItem>) {
        asyncListDiffer.submitList(list)
    }

    inner class ExpiredDocViewHolder(val binding: ItemExpiredDocsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GetDAVehicleExpiredDocumentsItem, position: Int) {
            binding.birthCertTV.text = item.DocTypeName
            binding.expiryDateTV.text = item.VehDocEndDate
            if (position != 0)
                binding.headerh1.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpiredDocViewHolder {
        val binding =
            ItemExpiredDocsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpiredDocViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: ExpiredDocViewHolder, position: Int) {
        val item = asyncListDiffer.currentList[position]
        holder.bind(item, position)
    }
}