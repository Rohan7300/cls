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
            if(item.IsCompleted){
                binding.trainerFeedbackIV.isClickable = false
                binding.trainerFeedbackImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.done
                    )
                )
                    binding.edtIc.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.done
                        )
                    )
                    binding.edtIc.isClickable = false
            }else{
                if (item.IsDriverFeedbackCompleted == true) {
                    binding.trainerFeedbackIV.isClickable = false
                    binding.trainerFeedbackImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.done
                        )
                    )
                }else{
                    binding.trainerFeedbackIV.isClickable = true
                }

                if (item.IsTrainerQuestionnaireCompleted == true) {
                    binding.edtIc.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.done
                        )
                    )
                    binding.edtIc.isClickable = false
                }else{
                    binding.edtIc.isClickable = true
                }

/*                mainViewModel.GetRideAlongDriverFeedbackQuestion(
                    item.DriverId,
                    item.RtId,
                    item.LeadDriverId,
                    item.DawId
                )
                mainViewModel.GetRideAlongLeadDriverQuestion(
                    item.DriverId,
                    item.RtId,
                    item.LeadDriverId,
                    item.DawId
                )*/

/*                mainViewModel.liveDataGetRideAlongLeadDriverQuestion.observe(viewLifecycleOwner) {
                    if (it != null) {
                        val itemX = asyncListDiffer.currentList.find { driver ->
                            driver.DriverId == it.RaDriverId
                        }
                        if (itemX != null) {
                            if (itemX.isQuestionsFilled != it.RaIsSubmitted) {
                                itemX.isQuestionsFilled = it.RaIsSubmitted
                                if(it.RaIsSubmitted==true){
                                    binding.edtIc.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            context,
                                            R.drawable.done
                                        )
                                    )
                                }
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
                mainViewModel.liveDataGetRideAlongDriverFeedbackQuestion.observe(viewLifecycleOwner) {
                    if (it != null) {
                        val itemX = asyncListDiffer.currentList.find { driver ->
                            driver.DriverId == it.DriverId
                        }
                        if (itemX != null) {
                            if (itemX.isFeedBackFilled != it.RaIsSubmitted){
                                itemX.isFeedBackFilled = it.RaIsSubmitted
                                isChanged = true
                                if(it.RaIsSubmitted==true){
                                    binding.edtIc.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            context,
                                            R.drawable.done
                                        )
                                    )
                                }
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
                }*/
            }

            binding.edtIc.setOnClickListener {
                if(item.IsTrainerQuestionnaireCompleted!=null){
                    if(!item.IsTrainerQuestionnaireCompleted!!){
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
                }
            }
            binding.trainerFeedbackIV.setOnClickListener {
                if(item.IsDriverFeedbackCompleted!=null){
                    if(!item.IsDriverFeedbackCompleted!!){
                        prefs.currRideAlongID = item.DriverId
                        prefs.daWID = item.DawId
                        prefs.currRtId = item.RtId
                        navController.navigate(R.id.feedbackFragment)
                    }
                }
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
            return oldItem == newItem
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

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}