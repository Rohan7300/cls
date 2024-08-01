package com.clebs.celerity.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.R
import com.clebs.celerity.models.MovieModel
import com.clebs.celerity.models.RewardsModel
import com.google.android.material.card.MaterialCardView
import com.kotlinpermissions.notNull
import kotlin.math.ceil


class TableViewAdapter(var context: Context, var rewardList: ArrayList<RewardsModel>) :
    RecyclerView.Adapter<TableViewAdapter.RowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.table_list_item, parent, false)
        return RowViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        val rowPos = rewardList[position]
        holder.bind(rowPos,position)
    }

    override fun getItemCount(): Int {
        return rewardList.size
    }

    inner class RowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item:RewardsModel,position: Int){
            if(position%2==0){
                itemView.findViewById<MaterialCardView>(R.id.cardWhite).visibility = View.VISIBLE
                itemView.findViewById<MaterialCardView>(R.id.cardBlue).visibility = View.GONE
                itemView.findViewById<TextView>(R.id.rewardTitle).text = item.title
                itemView.findViewById<TextView>(R.id.rewardValue).text = item.value
            }else{
                itemView.findViewById<MaterialCardView>(R.id.cardWhite).visibility = View.GONE
                itemView.findViewById<MaterialCardView>(R.id.cardBlue).visibility = View.VISIBLE
                itemView.findViewById<TextView>(R.id.rewardTitleblue).text = item.title
                itemView.findViewById<TextView>(R.id.rewardValueblue).text = item.value
            }
        }

    }
}