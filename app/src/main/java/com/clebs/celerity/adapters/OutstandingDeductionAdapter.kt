package com.clebs.celerity.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.R
import com.clebs.celerity.databinding.AdapterOutstandingDeductionsBinding
import com.clebs.celerity.models.response.GetDAOutStandingDeductionListResponse
import com.clebs.celerity.models.response.GetDAOutStandingDeductionListResponseItem
import com.clebs.celerity.utils.parseToInt

interface DeductionListListener {
    fun onClick(i: Int)
}

class OutstandingDeductionAdapter(var listener: DeductionListListener, var context: Context, var type: Int) :
    RecyclerView.Adapter<OutstandingDeductionAdapter.OutstandingDeductionViewHolder>() {
    lateinit var binding: AdapterOutstandingDeductionsBinding

    inner class OutstandingDeductionViewHolder(val binding: AdapterOutstandingDeductionsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GetDAOutStandingDeductionListResponseItem) {
            if (type == 1){
                binding.deductionAmount.text = item.CLSTotalDeductionAmount
                try {
                    if (item.CLSTotalDeductionAmount == "0.00" || parseToInt(item.CLSTotalDeductionAmount) == 0) {
                        binding.viewDocs.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.invisible))
                    }else{
                        binding.viewDocs.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.eye))
                        binding.viewDocs.setOnClickListener {
                            if (type == 1)
                                listener.onClick(1)
                            else
                                listener.onClick(2)
                        }
                    }
                } catch (_: Exception) {

                }
            }
            else{
                binding.deductionAmount.text = item.CHTotalDeductionAmount
                try {
                    if (item.CHTotalDeductionAmount == "0.00" || parseToInt(item.CHTotalDeductionAmount) == 0) {
                        binding.viewDocs.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.invisible))
                    }else{
                        binding.viewDocs.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.eye))
                        binding.viewDocs.setOnClickListener {
                            if (type == 1)
                                listener.onClick(1)
                            else
                                listener.onClick(2)
                        }
                    }
                } catch (_: Exception) {

                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OutstandingDeductionViewHolder {
        binding = AdapterOutstandingDeductionsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OutstandingDeductionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: OutstandingDeductionViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
    }

    private val diffUtil =
        object : DiffUtil.ItemCallback<GetDAOutStandingDeductionListResponseItem>() {
            override fun areItemsTheSame(
                oldItem: GetDAOutStandingDeductionListResponseItem,
                newItem: GetDAOutStandingDeductionListResponseItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: GetDAOutStandingDeductionListResponseItem,
                newItem: GetDAOutStandingDeductionListResponseItem
            ): Boolean {
                return oldItem == newItem
            }
        }

    private var asyncListDiffer = AsyncListDiffer(this, diffUtil)
    fun submitList(data: GetDAOutStandingDeductionListResponse) {
        asyncListDiffer.submitList(data)
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}