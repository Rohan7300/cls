package com.clebs.celerity.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.AdapterDriverRouteBinding
import com.clebs.celerity.models.response.GetDriverRouteInfoByDateResponse
import com.clebs.celerity.models.response.GetDriverRouteInfoByDateResponseItem
import com.clebs.celerity.utils.LoadingDialog

class DriverRouteAdapter(var list: GetDriverRouteInfoByDateResponse,var loadingDialog: LoadingDialog,var mainViewModel: MainViewModel) :
    RecyclerView.Adapter<DriverRouteAdapter.DriverRouteAdapterViewHolder>() {

    inner class DriverRouteAdapterViewHolder(private val binding: AdapterDriverRouteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GetDriverRouteInfoByDateResponseItem) {
            binding.routeNameTwo.text = item.RtName
            binding.delRouteIV.setOnClickListener {
                loadingDialog.show()
                mainViewModel.DeleteOnRouteDetails(item.RtId)
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
        return list.size
    }

    override fun onBindViewHolder(holder: DriverRouteAdapterViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }
}