package com.clebs.celerity_admin.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.databinding.WeeklyDefectAdapterBinding
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.models.WeeklyDefectChecksModelItem
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.ui.ClSReports.WeeklyDefectsFragment
import com.clebs.celerity_admin.viewModels.MainViewModel

class WeeklyDefectAdapter(
    var context: Context,
    var data: ArrayList<WeeklyDefectChecksModelItem>,
    var listener: WeeklyDefectsClickListener

) :
    RecyclerView.Adapter<WeeklyDefectAdapter.WeeklyDefectViewHolder>() {
    lateinit var binding: WeeklyDefectAdapterBinding
    private lateinit var mainViewModel: MainViewModel

    interface WeeklyDefectsClickListener {
        fun docClickAction(item: WeeklyDefectChecksModelItem)
    }

    inner class WeeklyDefectViewHolder(var binding: WeeklyDefectAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {




        fun bindView(item: WeeklyDefectChecksModelItem) {

            binding.tvReg.setText(item.vehRegNo)
            binding.tvDaName.setText(item.dAName)
            binding.tvDaLocationname.setText(item.locationName)

            if (item.OsmName.isNotEmpty()) {
                binding.osmName.text = item.OsmName
            } else {
                binding.osmName.setText("_ _ _ _")
            }

            Log.e("dkfdkjfdkfjd", "bindView: " + item.OsmName)
            if (item.VdhCheckIsApproved) {
                binding.txtchecks.setText("Checks completed by : ${item.OsmName}")
                binding.viewfiles.backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.green)
                binding.cards.strokeColor = ContextCompat.getColor(context, R.color.green)
                binding.cards.strokeWidth = 2
                binding.osmCheck.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.baseline_check_circle_outline_24
                    )
                )


            } else {
                binding.txtchecks.setText("Checks pending by OSM")
                binding.viewfiles.backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.red)
                binding.cards.strokeWidth = 2
                binding.cards.strokeColor = ContextCompat.getColor(context, R.color.red)
                binding.osmCheck.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.cancel
                    )
                )

            }
            itemView.setOnClickListener {
                listener.docClickAction(item)
            }
            binding.viewfiles.setOnClickListener {
                listener.docClickAction(item)
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyDefectViewHolder {


        binding =
            WeeklyDefectAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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