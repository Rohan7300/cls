package com.clebs.celerity.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.databinding.ItemExpiringDocsBinding
import com.clebs.celerity.models.response.ExpiringDocumentsResponseItem
import com.clebs.celerity.models.response.VehicleExpiringDocumentsResponseItem
import com.clebs.celerity.utils.convertDateFormat

interface VehicleExpiringUploadListener {
    fun uploadIntent(documentTypeID: Int)
}

class VehicleExpiringDocAdapter(val uploadCallback: VehicleExpiringUploadListener) :
    RecyclerView.Adapter<VehicleExpiringDocAdapter.VehicleExpiringDocViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<VehicleExpiringDocumentsResponseItem>() {
        override fun areItemsTheSame(
            oldItem: VehicleExpiringDocumentsResponseItem,
            newItem: VehicleExpiringDocumentsResponseItem
        ): Boolean {
            return oldItem.VehDocId == newItem.VehDocId
        }

        override fun areContentsTheSame(
            oldItem: VehicleExpiringDocumentsResponseItem,
            newItem: VehicleExpiringDocumentsResponseItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)

    fun saveData(list: ArrayList<VehicleExpiringDocumentsResponseItem>) {
        asyncListDiffer.submitList(list)
    }

    inner class VehicleExpiringDocViewHolder(val binding: ItemExpiringDocsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VehicleExpiringDocumentsResponseItem, position: Int) {
            binding.birthCertTV.text = item.DocTypeName
            binding.expiryDateTV.text = convertDateFormat(item.VehDocEndDate)
            if (position != 0)
                binding.headerh1.visibility = View.GONE
            binding.uploadDocIV.setOnClickListener {
                uploadCallback.uploadIntent(item.VehDocTypeId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleExpiringDocViewHolder {
        val binding =
            ItemExpiringDocsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VehicleExpiringDocViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: VehicleExpiringDocViewHolder, position: Int) {
        val item = asyncListDiffer.currentList[position]
        holder.bind(item, position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}