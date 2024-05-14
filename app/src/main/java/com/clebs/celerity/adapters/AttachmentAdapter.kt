package com.clebs.celerity.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.databinding.AdapterAttachmentBinding
import com.clebs.celerity.models.response.DocXXX
import com.clebs.celerity.ui.ViewTicketsActivity
import com.clebs.celerity.utils.showToast

class AttachmentAdapter(var context: ViewTicketsActivity, var data: ArrayList<DocXXX>) :
    RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder>() {
    inner class AttachmentViewHolder(val binding: AdapterAttachmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DocXXX) {
            binding.imgName.text = item.FileName
            binding.mainll.setOnClickListener {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.Path))
                    context.startActivity(intent)
                } catch (_: Exception) {
                    showToast("Unable to open link!!", context)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {
        val binding = AdapterAttachmentBinding.inflate(LayoutInflater.from(parent.context))
        return AttachmentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}