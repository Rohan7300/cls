package com.clebs.celerity.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.databinding.ItemExpiringDocsBinding
import com.clebs.celerity.models.response.ExpiringDocumentsResponseItem
import com.clebs.celerity.utils.convertDateFormat

interface ExpiringDocUploadListener {
    fun uploadIntent(documentTypeID: Int)
}

class ExpiringDocAdapter(val uploadCallback: ExpiringDocUploadListener) :
    RecyclerView.Adapter<ExpiringDocAdapter.ExpiringDocViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<ExpiringDocumentsResponseItem>() {
        override fun areItemsTheSame(
            oldItem: ExpiringDocumentsResponseItem,
            newItem: ExpiringDocumentsResponseItem
        ): Boolean {
            return oldItem.UsrDocId == newItem.UsrDocId
        }

        override fun areContentsTheSame(
            oldItem: ExpiringDocumentsResponseItem,
            newItem: ExpiringDocumentsResponseItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)

    fun saveData(list: ArrayList<ExpiringDocumentsResponseItem>) {
        asyncListDiffer.submitList(list)
    }

    inner class ExpiringDocViewHolder(val binding: ItemExpiringDocsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ExpiringDocumentsResponseItem, position: Int) {
            binding.birthCertTV.text = item.DocumentType
            binding.expiryDateTV.text = convertDateFormat(item.ExpiryDate)
            if (position != 0)
                binding.headerh1.visibility = View.GONE
            binding.uploadDocIV.setOnClickListener {
                uploadCallback.uploadIntent(item.DocumentTypeID)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpiringDocViewHolder {
        val binding =
            ItemExpiringDocsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpiringDocViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: ExpiringDocViewHolder, position: Int) {
        val item = asyncListDiffer.currentList[position]
        holder.bind(item, position)
    }
}