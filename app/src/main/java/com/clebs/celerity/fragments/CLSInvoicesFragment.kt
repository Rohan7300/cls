package com.clebs.celerity.fragments

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.CLSInvoiceAdapter
import com.clebs.celerity.databinding.FragmentCLSInvoicesBinding
import com.clebs.celerity.databinding.FragmentInvoicesBinding
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.PermissionCallback
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.showToast
import com.google.android.material.datepicker.MaterialDatePicker
import java.io.File
import java.util.Calendar


class CLSInvoicesFragment : Fragment(), PermissionCallback {
    lateinit var binding: FragmentCLSInvoicesBinding
    private lateinit var viewModel: MainViewModel
    lateinit var prefs: Prefs
    lateinit var homeActivity: HomeActivity
    var REQUEST_STORAGE_PERMISSION_CODE = 101
    private var selectedYear = 2024

    lateinit var adapter: CLSInvoiceAdapter
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
        binding.dateTV.text = selectedYear.toString()
        binding.dateUpdater.setOnClickListener {
            showYearPicker()
        }
        observers()
        viewModel.DownloadInvoicePDF(prefs.userID.toInt(), selectedYear)
        return binding.root
    }

    private fun observers() {

        adapter = CLSInvoiceAdapter(ArrayList(), requireContext(), prefs,this)
        binding.clsInvoices.adapter = adapter
        binding.clsInvoices.layoutManager = LinearLayoutManager(requireContext())
        viewModel.liveDataDownloadInvoicePDF.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                if (it.Invoices.size > 0) {
                    binding.clsInvoices.visibility = View.VISIBLE
                    binding.noinvoices.visibility = View.GONE
                }
                adapter.data.clear()
                adapter.data.addAll(it.Invoices)
                adapter.notifyDataSetChanged()
            } else {
                binding.clsInvoices.visibility = View.GONE
                binding.noinvoices.visibility = View.VISIBLE
            }
        }
    }

    override fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_STORAGE_PERMISSION_CODE
        )
    }

    override fun onStoragePermissionResult(granted: Boolean) {
        if (granted) {
            val item = prefs.getInvoice()!!
            adapter.downloadPDF(item.FileName, item.FileContent)
        }else{
            showToast("Please allow storag permission to download and view pdf",requireContext())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_PERMISSION_CODE) {
            val granted = grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
            onStoragePermissionResult(granted)
        }
    }

    private fun showYearPicker() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val yearPickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, _, _ ->
                run {
                    selectedYear = year
                    binding.dateTV.text = year.toString()
                    showDialog()
                    viewModel.DownloadInvoicePDF(prefs.userID.toInt(), selectedYear)
                  //  showToast("Selected Year: $selectedYear", requireContext())
                }
            },
            currentYear,
            0,
            1
        )
        yearPickerDialog.datePicker.maxDate = System.currentTimeMillis()
        yearPickerDialog.show()
    }
}