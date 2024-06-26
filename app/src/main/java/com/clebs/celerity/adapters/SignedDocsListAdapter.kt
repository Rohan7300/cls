package com.clebs.celerity.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.databinding.AdapterCompanyDocsBinding
import com.clebs.celerity.models.response.GetCompanySignedDocumentListResponse
import com.clebs.celerity.models.response.GetCompanySignedDocumentListResponseItem

class SignedDocsListAdapter :
    RecyclerView.Adapter<SignedDocsListAdapter.SignedDocsListViewHolder>() {
    lateinit var binding: AdapterCompanyDocsBinding

    inner class SignedDocsListViewHolder(var binding: AdapterCompanyDocsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GetCompanySignedDocumentListResponseItem) {
            binding.date.text = item.HBReadDate
            binding.driverName.text = item.DriverName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignedDocsListViewHolder {
        binding = AdapterCompanyDocsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SignedDocsListViewHolder(
            binding
        )
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: SignedDocsListViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
        if (position == 0)
            binding.header.visibility = View.VISIBLE
        else
            binding.header.visibility = View.GONE
    }

    private val diffUtil =
        object : DiffUtil.ItemCallback<GetCompanySignedDocumentListResponseItem>() {
            override fun areItemsTheSame(
                oldItem: GetCompanySignedDocumentListResponseItem,
                newItem: GetCompanySignedDocumentListResponseItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: GetCompanySignedDocumentListResponseItem,
                newItem: GetCompanySignedDocumentListResponseItem
            ): Boolean {
                return oldItem == newItem
            }
        }

    var asyncListDiffer = AsyncListDiffer(this, diffUtil)
    fun submitList(data: GetCompanySignedDocumentListResponse) {
        asyncListDiffer.submitList(data)
    }
}