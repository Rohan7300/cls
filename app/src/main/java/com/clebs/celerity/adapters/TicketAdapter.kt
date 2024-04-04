package com.clebs.celerity.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.databinding.AdapterTicketItemBinding
import com.clebs.celerity.models.response.Doc
import com.clebs.celerity.models.response.GetUserTicketsResponse
import com.clebs.celerity.ui.AddCommentActivity

class TicketAdapter(var ticketList: GetUserTicketsResponse, var context: Context) :
    RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    inner class TicketViewHolder(val binding: AdapterTicketItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ticketItem: Doc) {
            binding.ticketTitleTV.text = "CLS - ${ticketItem.UserTicketID}"
            binding.ticketSubjectTV.text = ticketItem.TicketDescription
            var time = try {
                ticketItem.UserTicketCreatedOn.split("T")[0] + " " + ticketItem.UserTicketCreatedOn.split(
                    "T"
                )[1]
            } catch (_: Exception) {
                ticketItem.UserTicketCreatedOn
            }
            binding.ticketTime.text = time
            binding.commentIV.setOnClickListener {
                val intent = Intent(context, AddCommentActivity::class.java).apply {
                    putExtra("ticketID", ticketItem.UserTicketID)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val binding = AdapterTicketItemBinding.inflate(LayoutInflater.from(parent.context))
        return TicketViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return ticketList.Docs.size
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val item = ticketList.Docs[position]
        holder.bind(item)
    }
}