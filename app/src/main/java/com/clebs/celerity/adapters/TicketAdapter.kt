package com.clebs.celerity.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.databinding.AdapterTicketItemBinding
import com.clebs.celerity.models.response.Doc
import com.clebs.celerity.models.response.GetUserTicketsResponse

class TicketAdapter(var ticketList:GetUserTicketsResponse):RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    inner class TicketViewHolder(val binding:AdapterTicketItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(ticketItem:Doc){
            binding.ticketTitleTV.text = ticketItem.TicketTitle
            binding.ticketTitleTV.text = ticketItem.TicketDescription
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