package com.clebs.celerity.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.R
import com.clebs.celerity.databinding.AdapterOtherCompanyPolicyBinding
import com.clebs.celerity.models.response.CompanyDocument
import com.clebs.celerity.models.response.GetDriverOtherCompaniesPolicyResponse

class OtherCompanyPolicyAdapter :
    RecyclerView.Adapter<OtherCompanyPolicyAdapter.OtherCompanyPolicyViewHolder>() {

    inner class OtherCompanyPolicyViewHolder(val binding: AdapterOtherCompanyPolicyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CompanyDocument) {
            binding.otherpolicy = item
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<CompanyDocument>() {

        override fun areItemsTheSame(
            oldItem: CompanyDocument,
            newItem: CompanyDocument
        ): Boolean {
            return oldItem.CompanyId == newItem.CompanyId
        }

        override fun areContentsTheSame(
            oldItem: CompanyDocument,
            newItem: CompanyDocument
        ): Boolean {
            return oldItem == newItem
        }
    }

    private var asyncListDiffer = AsyncListDiffer(this, diffUtil)

    fun saveData(data: GetDriverOtherCompaniesPolicyResponse) {
        asyncListDiffer.submitList(data.CompanyDocuments)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OtherCompanyPolicyViewHolder {
        val binding = DataBindingUtil.inflate<AdapterOtherCompanyPolicyBinding>(
            LayoutInflater.from(parent.context),
            R.layout.adapter_other_company_policy,
            parent,
            false
        )
        return OtherCompanyPolicyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: OtherCompanyPolicyViewHolder, position: Int) {
        val currItem = asyncListDiffer.currentList[position]
        holder.bind(currItem)
    }
}