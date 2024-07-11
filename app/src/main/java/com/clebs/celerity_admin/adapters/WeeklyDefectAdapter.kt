package com.clebs.celerity_admin.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.databinding.WeeklyDefectAdapterBinding
import com.clebs.celerity_admin.models.WeeklyDefectChecksModelItem

class WeeklyDefectAdapter(var context: Context, var data: ArrayList<WeeklyDefectChecksModelItem>,var listener:WeeklyDefectsClickListener) :
    RecyclerView.Adapter<WeeklyDefectAdapter.WeeklyDefectViewHolder>() {
    lateinit var binding: WeeklyDefectAdapterBinding

    interface WeeklyDefectsClickListener{
        fun docClickAction(item:WeeklyDefectChecksModelItem)
    }
    inner class WeeklyDefectViewHolder(var binding: WeeklyDefectAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(item: WeeklyDefectChecksModelItem) {
            binding.tvReg.setText(item.vehRegNo)
            binding.tvDaName.setText(item.dAName)
            binding.tvDaLocationname.setText(item.locationName)
            if(item.VdhCheckIsApproved){

                binding.viewfiles.backgroundTintList=ContextCompat.getColorStateList(context,R.color.green)
                binding.cards.strokeColor=ContextCompat.getColor(context,R.color.green)
                binding.osmCheck.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.baseline_check_circle_outline_24))
                binding.osmName.text = item.OsmName
            }else{
                binding.viewfiles.backgroundTintList=ContextCompat.getColorStateList(context,R.color.red)

                binding.cards.strokeColor=ContextCompat.getColor(context,R.color.black)
                binding.osmCheck.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.cancel))
                binding.osmName.text= " ----- "
            }
            binding.viewfiles.setOnClickListener {
                listener.docClickAction(item)
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyDefectViewHolder {


        binding = WeeklyDefectAdapterBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return WeeklyDefectViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: WeeklyDefectViewHolder, position: Int) {
        val item = data[position]
        holder.bindView(item)
    }
}