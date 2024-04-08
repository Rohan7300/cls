package com.clebs.celerity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.CLSInvoiceAdapter
import com.clebs.celerity.databinding.FragmentCLSInvoicesBinding
import com.clebs.celerity.databinding.FragmentInvoicesBinding
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.Prefs
import java.io.File


class CLSInvoicesFragment : Fragment() {
    lateinit var binding: FragmentCLSInvoicesBinding
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
        binding = FragmentCLSInvoicesBinding.inflate(layoutInflater)
        prefs = Prefs.getInstance(requireContext())
        homeActivity = (activity as HomeActivity)
        viewModel = homeActivity.viewModel
        showDialog()
        observers()
        viewModel.DownloadInvoicePDF(prefs.userID.toInt(), 2024)
        return binding.root
    }

    private fun observers() {

        val adapter = CLSInvoiceAdapter(ArrayList(),requireContext())
        binding.clsInvoices.adapter = adapter
        binding.clsInvoices.layoutManager = LinearLayoutManager(requireContext())
        viewModel.liveDataDownloadInvoicePDF.observe(viewLifecycleOwner) {
            hideDialog()
            if(it!=null){
                if(it.Invoices.size>0){
                    binding.clsInvoices.visibility = View.VISIBLE
                    binding.noinvoices.visibility = View.GONE
                }
                adapter.data.clear()
                adapter.data.addAll(it.Invoices)
                adapter.notifyDataSetChanged()
            }else{
                    binding.clsInvoices.visibility = View.GONE
                    binding.noinvoices.visibility = View.VISIBLE
            }
        }
    }
}