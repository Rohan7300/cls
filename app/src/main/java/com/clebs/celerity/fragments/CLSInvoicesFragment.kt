package com.clebs.celerity.fragments

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.CLSInvoiceAdapter
import com.clebs.celerity.databinding.FragmentCLSInvoicesBinding
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.PermissionCallback
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.showToast
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.Year
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID


class CLSInvoicesFragment : Fragment(), PermissionCallback {
    lateinit var binding: FragmentCLSInvoicesBinding
    private lateinit var viewModel: MainViewModel
    lateinit var prefs: Prefs
    lateinit var homeActivity: HomeActivity
    private var REQUEST_STORAGE_PERMISSION_CODE = 101
    private var selectedYear = 2024
    private var isDownloading = false
    var isClicked = false
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
        (activity as HomeActivity).ActivityHomeBinding.title.text = "Invoices"
        viewModel = homeActivity.viewModel
        findNavController().currentDestination!!.id = R.id.CLSInvoicesFragment
        showDialog()
        selectedYear = Year.now().value
        binding.selectYearET.setText(selectedYear.toString())
        observers()
        showYearPickerNew()
        val dispatcher = requireActivity().onBackPressedDispatcher
        dispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (activity as HomeActivity).onBackPressed()
            }
        })
        viewModel.GetDriverInvoiceList(prefs.clebUserId.toInt(), selectedYear, 0)

        return binding.root
    }

    private fun observers() {

        adapter = CLSInvoiceAdapter(ArrayList(), requireContext(), prefs, this)
        binding.clsInvoices.adapter = adapter
        binding.clsInvoices.setHasFixedSize(true)
        binding.clsInvoices.layoutManager = LinearLayoutManager(requireContext())
        viewModel.liveDataGetDriverInvoiceList.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                if (it.Invoices.isNotEmpty()) {
                    binding.clsInvoices.visibility = View.VISIBLE
                    binding.noinvoices.visibility = View.GONE
                }else{
                    binding.clsInvoices.visibility = View.GONE
                    binding.noinvoices.visibility = View.VISIBLE
                }
                adapter.data.clear()
                adapter.data.addAll(it.Invoices.reversed())
                adapter.notifyDataSetChanged()
            } else {
                binding.clsInvoices.visibility = View.GONE
                binding.noinvoices.visibility = View.VISIBLE
            }
        }
        viewModel.liveDataDownloadInvoicePDF.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                if (isClicked) {
                    try {
                        val uniqueId = UUID.randomUUID().toString()

                        val fileContent = it.Invoices[0].FileContent
                        val fileName = it.Invoices[0].FileName
                        val currentDate = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                            Date()
                        )
                        val uniqueFileName = "$fileName-$currentDate-$uniqueId.pdf"
                        val storageDir =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        if (!storageDir.exists()) {
                            storageDir.mkdirs()
                        }
                        val file = File(
                            storageDir,
                            uniqueFileName
                        )
                        val fos = FileOutputStream(file)
                        fos.write(Base64.decode(fileContent, Base64.DEFAULT))
                        fos.close()
                        showToast("PDF Downloaded!", requireContext())
                        openPDF(file)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showToast("Failed to download PDF", requireContext())
                    }
                }

            } else {
            }
            isDownloading = false
        }
    }

    override fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_STORAGE_PERMISSION_CODE
        )
        isClicked = true
    }

    override fun onStoragePermissionResult(granted: Boolean) {
        if (granted) {
            val item = prefs.getInvoice()!!
            dowloadPDF(item.InvoiceId, item.FileName)
        } else {
            showToast("Please allow storag permission to download and view pdf", requireContext())
        }
    }

    override fun dowloadPDF(invoiceID: Int, fileName: String) {
        isClicked = true
        if (isDownloading) {
            showToast("Please wait other file is downloading", requireContext())
            return
        }
        isDownloading = true
        showDialog()
        viewModel.DownloadInvoicePDF(prefs.clebUserId.toInt(), invoiceID)

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
                    viewModel.DownloadInvoicePDF(prefs.clebUserId.toInt(), selectedYear)
                }
            },
            currentYear,
            0,
            1
        )



        yearPickerDialog.show()
    }

    private fun showYearPickerNew() {
        val itemsList = mutableListOf<Int>()
        val currentYear = Year.now().value
        for (year in prefs.UsrCreatedOn..currentYear)
            itemsList.add(year)
        val adapter =
            ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, itemsList.reversed())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.selectYearET.setAdapter(adapter)
        binding.selectYearET.setOnItemClickListener { parent, view, position, id ->
            run {
                parent?.let { nonNullParent ->
                        selectedYear = nonNullParent.getItemAtPosition(position) as Int
                        showDialog()
                      //  viewModel.DownloadInvoicePDF(prefs.clebUserId.toInt(), selectedYear)
                        viewModel.GetDriverInvoiceList(prefs.clebUserId.toInt(), selectedYear,0)
                }
            }
        }
    }

    private fun openPDF(file: File) {

        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )
        } else {
            Uri.fromFile(file)
        }

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        try {
            requireContext().startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("No PDF viewer found", requireContext())
        }
    }
}