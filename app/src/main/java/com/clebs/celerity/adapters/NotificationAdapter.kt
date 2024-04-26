package com.clebs.celerity.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.databinding.NotificationAdapterDialogBinding
import com.clebs.celerity.models.response.NotificationResponseItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class NotificationAdapter() :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<NotificationResponseItem>() {
        override fun areItemsTheSame(
            oldItem: NotificationResponseItem,
            newItem: NotificationResponseItem
        ): Boolean {
            return oldItem.NotificationId == newItem.NotificationId && oldItem.NotificationBody == newItem.NotificationBody
        }

        override fun areContentsTheSame(
            oldItem: NotificationResponseItem,
            newItem: NotificationResponseItem
        ): Boolean {
            return oldItem == newItem
        }

    }
    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)
    fun saveData(data: List<NotificationResponseItem>) {
        asyncListDiffer.submitList(data)
    }

    inner class NotificationViewHolder(val binding: NotificationAdapterDialogBinding) :
        RecyclerView.ViewHolder(binding.root){
            fun bind(item:NotificationResponseItem){
                binding.title.text = item.NotificationTitle
                binding.descripotionX.text  = item.NotificationBody

                var formattedDate = item.NotificationSentOn
                var formattedTime = "00:00"
                try {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                    val truncatedString =
                        item.NotificationSentOn.substring(0, 19) // Truncate to remove milliseconds
                    val dateTime = LocalDateTime.parse(truncatedString, formatter)

                    val outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH)
                    formattedDate = dateTime.format(outputFormatter).toUpperCase()

                    val outputTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                    formattedTime = dateTime.format(outputTimeFormatter)
                    binding.time.text = "$formattedTime  $formattedDate"
                }catch (_:Exception){
                    binding.time.text = item.NotificationSentOn
                }

                binding.overallNotification.setOnClickListener {

                }

            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = NotificationAdapterDialogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = asyncListDiffer.currentList[position]
        holder.bind(item)
    }
}