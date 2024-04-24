package com.clebs.celerity.fragments

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.CLSThirdPartyInvoiceAdapter
import com.clebs.celerity.databinding.FragmentCLSThirdPartyBinding
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.PermissionCallback
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.showToast
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.Year
import java.util.Calendar


class CLSThirdPartyFragment : Fragment(), PermissionCallback {
    lateinit var binding: FragmentCLSThirdPartyBinding
    private lateinit var viewModel: MainViewModel
    lateinit var prefs: Prefs
    lateinit var adapter: CLSThirdPartyInvoiceAdapter
    lateinit var homeActivity: HomeActivity
    private var selectedYear = 2024
    val showDialog: () -> Unit = {
        (activity as HomeActivity).showDialog()
    }
    val hideDialog: () -> Unit = {
        (activity as HomeActivity).hideDialog()
    }
    private var REQUEST_STORAGE_PERMISSION_CODE = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCLSThirdPartyBinding.inflate(layoutInflater)
        prefs = Prefs.getInstance(requireContext())
        homeActivity = (activity as HomeActivity)
        viewModel = homeActivity.viewModel
        /*
                binding.dateTV.text = selectedYear.toString()
            binding.dateUpdater.setOnClickListener {
                    showYearPicker()
                }*/

        selectedYear = Year.now().value
        binding.selectYearET.setText(selectedYear.toString())
        showYearPickerNew()
        showDialog()
        observers()
        viewModel.DownloadThirdPartyInvoicePDF(prefs.userID.toInt(), selectedYear)
        return binding.root
    }

    private fun observers() {
        adapter = CLSThirdPartyInvoiceAdapter(ArrayList(), requireContext(), prefs, this)
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

    override fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_STORAGE_PERMISSION_CODE
        )
    }

    override fun onStoragePermissionResult(granted: Boolean) {
        if (granted) {
            val item = prefs.getInvoiceX()!!
            adapter.downloadPDF(item.FileName, item.FileContent)
        } else {
            showToast("Please allow storag permission to download and view pdf", requireContext())
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
                    viewModel.DownloadThirdPartyInvoicePDF(prefs.userID.toInt(), selectedYear)
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

    private fun showYearPickerNew() {
        val itemsList = mutableListOf<Int>()
        val currentYear = Year.now().value
        for (year in prefs.UsrCreatedOn..currentYear)
            itemsList.add(year)
        val adapter =
            ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, itemsList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.selectYearET.setAdapter(adapter)
        binding.selectYearET.setOnItemClickListener { parent, view, position, id ->
            run {
                parent?.let { nonNullParent ->
                    if (position != 0) {
                        selectedYear = nonNullParent.getItemAtPosition(position) as Int
                        showDialog()
                        viewModel.DownloadThirdPartyInvoicePDF(prefs.userID.toInt(), selectedYear)
                    }
                }
            }
        }
    }

}