package com.clebs.celerity.adapters

import android.content.Intent
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.databinding.CommentsAdapterBinding
import com.clebs.celerity.models.response.DocX
import com.clebs.celerity.models.response.DocXX
import com.clebs.celerity.ui.AddCommentActivity
import com.clebs.celerity.ui.CommentDetailActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CommentAdapter(var arrayList: ArrayList<DocXX>, var addCommentActivity: AddCommentActivity) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(var binding: CommentsAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DocXX) {
            /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                 binding.tvDes.text =
                     Html.fromHtml(item.CommentWithDateAndTime, Html.FROM_HTML_MODE_COMPACT)
             } else {
                 @Suppress("DEPRECATION")
                 binding.tvDes.text = Html.fromHtml(item.CommentWithDateAndTime)
             }*/

            binding.tvDes.text = item.ActivityDetail
            binding.commentUser.text = item.ActionByUserName

            if(item.IsRead){
                binding.tickRead.visibility = View.VISIBLE
                binding.tick.visibility = View.GONE
            }else{
                binding.tickRead.visibility = View.GONE
                binding.tick.visibility = View.VISIBLE
            }

            var formattedDate = item.ActionOn
            try {

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                val truncatedString =
                    item.ActionOn.substring(0, 19) // Truncate to remove milliseconds
                val dateTime = LocalDateTime.parse(truncatedString, formatter)

                val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                formattedDate = dateTime.format(outputFormatter)

            } catch (_: Exception) {

            }
            binding.tvDate.text = formattedDate
            binding.icMessage.setOnClickListener {
                var intent = Intent(
                    addCommentActivity,
                    CommentDetailActivity::class.java
                )
                intent.putExtra("ticketID", item.TicketID)
                intent.putExtra("ticketSub", item.TicketTitle)
                addCommentActivity.startActivity(
                    intent
                )
            }
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

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}