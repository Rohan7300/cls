package com.clebs.celerity_admin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity_admin.databinding.AdapterRequestTypeListBinding

class AddFilesAdapter(var data: MutableList<String>) :
    RecyclerView.Adapter<AddFilesAdapter.AddFileViewHolder>() {
    inner class AddFileViewHolder(binding: AdapterRequestTypeListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {

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