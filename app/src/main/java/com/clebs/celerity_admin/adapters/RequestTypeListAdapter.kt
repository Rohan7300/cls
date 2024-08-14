package com.clebs.celerity_admin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity_admin.databinding.AdapterRequestTypeListBinding
import com.clebs.celerity_admin.models.GetVehicleDamageWorkingStatusResponseItem

interface DeleteCallback{
    fun onDelete(item: GetVehicleDamageWorkingStatusResponseItem,position: Int)
}


class RequestTypeListAdapter(var callbackClass:DeleteCallback) :
    RecyclerView.Adapter<RequestTypeListAdapter.RequestTypeListViewHolder>() {
    inner class RequestTypeListViewHolder(var binding: AdapterRequestTypeListBinding) :
        RecyclerView.ViewHolder(binding.root){
            fun bind(item:GetVehicleDamageWorkingStatusResponseItem,position: Int){
                binding.delRequestIV.setOnClickListener {
                    callbackClass.onDelete(item,position)
                }
                binding.requstName.text = item.Name
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestTypeListViewHolder {
        val binding = AdapterRequestTypeListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RequestTypeListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return  asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: RequestTypeListViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position],position)
    }

    private val diffUtil = object : DiffUtil.ItemCallback<GetVehicleDamageWorkingStatusResponseItem>() {
        override fun areItemsTheSame(
            oldItem: GetVehicleDamageWorkingStatusResponseItem,
            newItem: GetVehicleDamageWorkingStatusResponseItem
        ): Boolean {
            return oldItem.Id == newItem.Id
        }

        override fun areContentsTheSame(
            oldItem: GetVehicleDamageWorkingStatusResponseItem,
            newItem: GetVehicleDamageWorkingStatusResponseItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    private var asyncListDiffer = AsyncListDiffer(this, diffUtil)

    fun saveData(items:List<GetVehicleDamageWorkingStatusResponseItem>){
        asyncListDiffer.submitList(items)
    }
}