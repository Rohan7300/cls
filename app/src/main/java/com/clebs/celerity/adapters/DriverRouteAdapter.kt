package com.clebs.celerity.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.AdapterDriverRouteBinding
import com.clebs.celerity.models.response.GetDriverRouteInfoByDateResponse
import com.clebs.celerity.models.response.GetDriverRouteInfoByDateResponseItem
import com.clebs.celerity.models.response.RideAlongDriverInfoByDateResponse
import com.clebs.celerity.models.response.leadDriverIdItem
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.navigateTo

class DriverRouteAdapter(
    var list: GetDriverRouteInfoByDateResponse,
    var loadingDialog: () -> Unit,
    var mainViewModel: MainViewModel,
    var findNavController: NavController,
    var requireContext: Context,
    var pref: Prefs
) :
    RecyclerView.Adapter<DriverRouteAdapter.DriverRouteAdapterViewHolder>() {

    inner class DriverRouteAdapterViewHolder(private val binding: AdapterDriverRouteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GetDriverRouteInfoByDateResponseItem) {
            binding.routeNameTwo.text = item.RtName
            if (item.RtIsByod)
                binding.byodIC.setColorFilter(ContextCompat.getColor(requireContext,R.color.green_new))
            else
                binding.byodIC.setColorFilter(ContextCompat.getColor(requireContext,R.color.light_grey))
            binding.delRouteIV.setOnClickListener {
                loadingDialog()
                mainViewModel.DeleteOnRouteDetails(item.RtId)
            }
            binding.edtIc.setOnClickListener {
                pref.saveDriverRouteInfoByDate(item)
                navigateTo(R.id.updateOnRoadHoursFragment, requireContext, findNavController)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DriverRouteAdapterViewHolder {

        val binding = AdapterDriverRouteBinding.inflate(LayoutInflater.from(parent.context))

        return DriverRouteAdapterViewHolder(
            binding
        )
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: DriverRouteAdapterViewHolder, position: Int) {
        val item = asyncListDiffer.currentList[position]
        holder.bind(item)
    }

    private val diffUtil = object : DiffUtil.ItemCallback<GetDriverRouteInfoByDateResponseItem>() {
        override fun areItemsTheSame(
            oldItem: GetDriverRouteInfoByDateResponseItem,
            newItem: GetDriverRouteInfoByDateResponseItem
        ): Boolean {
            return oldItem.RtUsrId == newItem.RtUsrId
        }

        override fun areContentsTheSame(
            oldItem: GetDriverRouteInfoByDateResponseItem,
            newItem: GetDriverRouteInfoByDateResponseItem
        ): Boolean {
            return oldItem == newItem
        }

    }
    val asyncListDiffer = AsyncListDiffer(this, diffUtil)
    fun saveData(data: GetDriverRouteInfoByDateResponse) {
        asyncListDiffer.submitList(data)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}