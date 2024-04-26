package com.clebs.celerity.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.AdapterRideAlongBinding
import com.clebs.celerity.models.response.NotificationResponseItem
import com.clebs.celerity.models.response.RideAlongDriverInfoByDateResponse
import com.clebs.celerity.models.response.leadDriverIdItem
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.RideAlongViewReadyCallback

class RideAlongAdapter(
    var data: RideAlongDriverInfoByDateResponse,
    var navController: NavController,
    var prefs: Prefs,
    var mainViewModel: MainViewModel,
    var loadingDialog: () -> Unit,
    var viewLifecycleOwner: LifecycleOwner,
    var context: Context
) : RecyclerView.Adapter<RideAlongAdapter.RideAlongViewHolder>() {
    var isChanged = false
    inner class RideAlongViewHolder(val binding: AdapterRideAlongBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: leadDriverIdItem) {
            binding.tainerName.text = prefs.userName
            binding.traineeName.text = item.DriverName
            mainViewModel.GetRideAlongDriverFeedbackQuestion(
                item.DriverId,
                item.RtId,
                item.LeadDriverId,
                item.DawId
            )
            mainViewModel.liveDataGetRideAlongDriverFeedbackQuestion.observe(viewLifecycleOwner) {
                if (it != null) {
                    val itemX = asyncListDiffer.currentList.find { driver ->
                        driver.DriverId == it.DriverId
                    }
                    if (itemX != null) {
                        if (itemX.isFeedBackFilled != it.RaIsSubmitted){
                            itemX.isFeedBackFilled = it.RaIsSubmitted
                            isChanged = true
                           // notifyDataSetChanged()
                        }
                    }

                    if (it.DriverId == item.DriverId) {
                        if (it.RaIsSubmitted == true) {
                            binding.trainerFeedbackIV.isClickable = false
                            binding.trainerFeedbackImage.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.done
                                )
                            )
                        }
                    }
                }
            }

            if (item.isFeedBackFilled == true) {
                binding.trainerFeedbackIV.isClickable = false
                binding.trainerFeedbackImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.done
                    )
                )
            }

            mainViewModel.GetRideAlongLeadDriverQuestion(
                item.DriverId,
                item.RtId,
                item.LeadDriverId,
                item.DawId
            )
            mainViewModel.liveDataGetRideAlongLeadDriverQuestion.observe(viewLifecycleOwner) {
                if (it != null) {
                    val itemX = asyncListDiffer.currentList.find { driver ->
                        driver.DriverId == it.RaDriverId
                    }
                    if (itemX != null) {
                        if (itemX.isQuestionsFilled != it.RaIsSubmitted) {
                            itemX.isQuestionsFilled = it.RaIsSubmitted
                            isChanged = true
                          //  notifyDataSetChanged()
                        }
                    }
                    if (it.RaDriverId == item.DriverId) {
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
                    }
                }
            }

            if (item.isQuestionsFilled == true) {
                binding.edtIc.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.done
                    )
                )
                binding.edtIc.isClickable = false
            }
            binding.edtIc.setOnClickListener {
                val bundle = bundleOf(
                    "rideAlongID" to item.DriverId,
                    "leadDriverID" to item.LeadDriverId
                )

                prefs.currRideAlongID = item.DriverId
                prefs.daWID = item.DawId
                prefs.currRtId = item.RtId
                //mainViewModel.currentViewPage.postValue(0)

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
            if(adapterPosition == asyncListDiffer.currentList.size-1&&isChanged){
                asyncListDiffer.submitList(asyncListDiffer.currentList)

            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideAlongViewHolder {
        val binding =
            AdapterRideAlongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RideAlongViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: RideAlongViewHolder, position: Int) {
        val item = asyncListDiffer.currentList[position]
        if (position != 0) {
            holder.binding.trainerHeader.visibility = View.GONE
        }
        holder.bind(item)
    }

    private val diffUtil = object : DiffUtil.ItemCallback<leadDriverIdItem>() {
        override fun areItemsTheSame(
            oldItem: leadDriverIdItem,
            newItem: leadDriverIdItem
        ): Boolean {
            return oldItem.DriverId == newItem.DriverId
        }

        override fun areContentsTheSame(
            oldItem: leadDriverIdItem,
            newItem: leadDriverIdItem
        ): Boolean {
            return oldItem == newItem
        }

    }
    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)
    fun saveData(data:RideAlongDriverInfoByDateResponse){
        asyncListDiffer.submitList(data)
    }
}