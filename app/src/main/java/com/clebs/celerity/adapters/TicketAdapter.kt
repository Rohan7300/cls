package com.clebs.celerity.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.R
import com.clebs.celerity.databinding.AdapterTicketItemBinding
import com.clebs.celerity.models.response.Doc
import com.clebs.celerity.models.response.GetUserTicketsResponse
import com.clebs.celerity.ui.AddCommentActivity
import com.clebs.celerity.ui.ViewTicketsActivity
import com.clebs.celerity.utils.Prefs
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class TicketAdapter(var ticketList: GetUserTicketsResponse, var context: Context, var pref: Prefs) :
    RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    inner class TicketViewHolder(val binding: AdapterTicketItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(ticketItem: Doc) {
            binding.ticketTitleTV.text = "CLS - ${ticketItem.UserTicketID}"
            val maxWords = 20
            val ticketDescription = ticketItem.TicketDescription

            val words = ticketDescription.split("\\s+".toRegex())
            val wordCount = words.size

            if (wordCount > maxWords) {
                val truncatedDescription = words.take(maxWords).joinToString(" ")
                binding.ticketSubjectTV.text = "$truncatedDescription ..."
            } else {
                binding.ticketSubjectTV.text = ticketDescription
            }

/*            if(ticketItem.IsActive){
                binding.ticketStatus.text = "Active"
            }*/
            if(ticketItem.IsCompleted!=null){
                binding.ticketStatus.text = "Completed"
            }else{
                binding.ticketStatus.text = "Active"
            }
/*            var time = try {
                ticketItem.UserTicketCreatedOn.split("T")[0] + " " + ticketItem.UserTicketCreatedOn.split(
                    "T"
                )[1]
            } catch (_: Exception) {
                ticketItem.UserTicketCreatedOn
            }*/
            var formattedDate = ticketItem.UserTicketCreatedOn
            var formattedTime = "00:00"
            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                val truncatedString =
                    ticketItem.UserTicketCreatedOn.substring(0, 19) // Truncate to remove milliseconds
                val dateTime = LocalDateTime.parse(truncatedString, formatter)

                val outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH)
                formattedDate = dateTime.format(outputFormatter).toUpperCase()

                val outputTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                formattedTime = dateTime.format(outputTimeFormatter)

            } catch (_: Exception) {
            }

            binding.date.text = formattedDate
            binding.ticketTime.text = formattedTime

            binding.commentIV.setOnClickListener {
                val intent = Intent(context, AddCommentActivity::class.java).apply {
                    putExtra("ticketID", ticketItem.UserTicketID)
                }
                context.startActivity(intent)
            }

            binding.ticketTitleTV.setOnClickListener {
                pref.saveCurrentTicket(ticketItem)
                val intent = Intent(context, ViewTicketsActivity::class.java)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val binding = AdapterTicketItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TicketViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return ticketList.Docs.size
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
//        holder.itemView.animation=AnimationUtils.loadAnimation(holder.itemView.context,R.anim.slide_down)
        val item = ticketList.Docs[position]


        holder.bind(item)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}