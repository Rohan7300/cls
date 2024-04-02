package com.clebs.celerity.adapters

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.databinding.CommentsAdapterBinding
import com.clebs.celerity.models.response.DocX
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CommentAdapter(var arrayList: ArrayList<DocX>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(var binding: CommentsAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DocX) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.tvDes.text =
                    Html.fromHtml(item.CommentWithDateAndTime, Html.FROM_HTML_MODE_COMPACT)
            } else {
                @Suppress("DEPRECATION")
                binding.tvDes.text = Html.fromHtml(item.CommentWithDateAndTime)
            }

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
            val dateTime = LocalDateTime.parse(item.ActionOn, formatter)


            val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val formattedDate = dateTime.format(outputFormatter)
            binding.tvDate.text = formattedDate

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        var binding = CommentsAdapterBinding.inflate(LayoutInflater.from(parent.context))
        return CommentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(arrayList[position])
    }
}