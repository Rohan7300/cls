package com.clebs.celerity.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.AdapterRideAlongBinding
import com.clebs.celerity.models.response.RideAlongDriverInfoByDateResponse
import com.clebs.celerity.models.response.leadDriverIdItem
import com.clebs.celerity.utils.Prefs

class RideAlongAdapter(
    var data: RideAlongDriverInfoByDateResponse,
    var navController: NavController,
    var prefs: Prefs,
    var mainViewModel: MainViewModel,
    var loadingDialog: () -> Unit,
    var viewLifecycleOwner: LifecycleOwner,
    var context: Context
) : RecyclerView.Adapter<RideAlongAdapter.RideAlongViewHolder>() {
    inner class RideAlongViewHolder(val binding: AdapterRideAlongBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: leadDriverIdItem) {
            binding.tainerName.text = prefs.userName
            binding.traineeName.text = item.DriverName

            mainViewModel.GetRideAlongLeadDriverQuestion(
                item.DriverId,
                item.RtId,
                item.LeadDriverId,
                item.DawId
            )
            mainViewModel.liveDataGetRideAlongLeadDriverQuestion.observe(viewLifecycleOwner) {
                if (it != null) {
                    if (it.RaIsSubmitted) {
                        binding.edtIc.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.done
                            )
                        )
                        binding.edtIc.isClickable = false
                    } else {
                        binding.edtIc.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.edit_orange
                            )
                        )
                        binding.edtIc.isClickable = true
                    }
                } else {
                    binding.edtIc.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.edit_orange
                        )
                    )
                    binding.edtIc.isClickable = true
                }
            }


            binding.edtIc.setOnClickListener {
                val bundle = bundleOf(
                    "rideAlongID" to item.DriverId,
                    "leadDriverID" to item.LeadDriverId
                )

                prefs.currRideAlongID = item.DriverId
                prefs.daWID = item.DawId
                prefs.currRtId = item.RtId
                navController.navigate(R.id.questinareFragment, bundle)
            }
            binding.trainerFeedbackIV.setOnClickListener {
                prefs.currRideAlongID = item.DriverId
                prefs.daWID = item.DawId
                prefs.currRtId = item.RtId
                navController.navigate(R.id.feedbackFragment)
            }
            binding.deleteRideAlong.setOnClickListener {
                loadingDialog()
                mainViewModel.DeleteOnRideAlongRouteInfo(item.RtId)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideAlongViewHolder {
        val binding = AdapterRideAlongBinding.inflate(LayoutInflater.from(parent.context))
        return RideAlongViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RideAlongViewHolder, position: Int) {
        val item = data[position]
        if(position!=0){
            holder.binding.trainerHeader.visibility = View.GONE
        }
        holder.bind(item)
    }
}