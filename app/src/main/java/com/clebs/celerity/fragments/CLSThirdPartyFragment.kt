package com.clebs.celerity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.CLSThirdPartyInvoiceAdapter
import com.clebs.celerity.databinding.FragmentCLSThirdPartyBinding
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.Prefs


class CLSThirdPartyFragment : Fragment() {
    lateinit var binding: FragmentCLSThirdPartyBinding
    private lateinit var viewModel: MainViewModel
    lateinit var prefs: Prefs
    lateinit var homeActivity: HomeActivity
    val showDialog: () -> Unit = {
        (activity as HomeActivity).showDialog()
    }
    val hideDialog: () -> Unit = {
        (activity as HomeActivity).hideDialog()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCLSThirdPartyBinding.inflate(layoutInflater)
        prefs = Prefs.getInstance(requireContext())
        homeActivity = (activity as HomeActivity)
        viewModel = homeActivity.viewModel
        showDialog()
        observers()
        viewModel.DownloadThirdPartyInvoicePDF(prefs.userID.toInt(), 2024)
        return binding.root
    }

    private fun observers() {
        val adapter = CLSThirdPartyInvoiceAdapter(ArrayList(), requireContext())
        binding.clsInvoicesThirdParty.adapter = adapter
        binding.clsInvoicesThirdParty.layoutManager = LinearLayoutManager(requireContext())
        viewModel.liveDataDownloadThirdPartyInvoicePDF.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                if (it.Invoices.size > 0) {
                    binding.noinvoices.visibility = View.GONE
                    binding.clsInvoicesThirdParty.visibility = View.VISIBLE
                }
                adapter.data.clear()
                adapter.data.addAll(it.Invoices)
                adapter.notifyDataSetChanged()
            } else {
                binding.noinvoices.visibility = View.VISIBLE
                binding.clsInvoicesThirdParty.visibility = View.GONE
            }
        }
    }

}