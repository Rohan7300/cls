package com.clebs.celerity_admin.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity_admin.databinding.AdapterRequestTypeListBinding
import com.clebs.celerity_admin.utils.generateUniqueFilename

class AddFilesAdapter(var data: MutableList<String>) :
    RecyclerView.Adapter<AddFilesAdapter.AddFileViewHolder>() {
    inner class AddFileViewHolder(var binding: AdapterRequestTypeListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.requstName.text = generateUniqueFilename()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddFileViewHolder {
        val binding = AdapterRequestTypeListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddFileViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: AddFileViewHolder, position: Int) {
        holder.bind(data[position])
    }
}