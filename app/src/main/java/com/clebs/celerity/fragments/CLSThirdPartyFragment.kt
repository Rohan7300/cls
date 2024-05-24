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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.CLSThirdPartyInvoiceAdapter
import com.clebs.celerity.databinding.FragmentCLSThirdPartyBinding
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


class CLSThirdPartyFragment : Fragment(), PermissionCallback {
    lateinit var binding: FragmentCLSThirdPartyBinding
    private lateinit var viewModel: MainViewModel
    lateinit var prefs: Prefs
    lateinit var adapter: CLSThirdPartyInvoiceAdapter
    lateinit var homeActivity: HomeActivity
    private var selectedYear = 2024
    var isClicked = false
    val showDialog: () -> Unit = {
        (activity as HomeActivity).showDialog()
    }
    val hideDialog: () -> Unit = {
        (activity as HomeActivity).hideDialog()
    }
    private var REQUEST_STORAGE_PERMISSION_CODE = 101
    private var isDownloading = false

    companion object {
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }.toTypedArray()
    }

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

        viewModel.GetThirdPartyInvoiceList(prefs.clebUserId.toInt(), selectedYear, 0)

        return binding.root
    }

    private fun observers() {
        adapter = CLSThirdPartyInvoiceAdapter(ArrayList(), requireContext(), prefs, this)
        binding.clsInvoicesThirdParty.adapter = adapter
        binding.clsInvoicesThirdParty.layoutManager = LinearLayoutManager(requireContext())
        viewModel.liveDataGetThirdPartyInvoiceList.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                if (it.Invoices.size > 0) {
                    binding.noinvoices.visibility = View.GONE
                    binding.clsInvoicesThirdParty.scheduleLayoutAnimation();
                    binding.clsInvoicesThirdParty.visibility = View.VISIBLE
                }
                adapter.data.clear()
                adapter.data.addAll(it.Invoices.reversed())
                adapter.notifyDataSetChanged()
            } else {
                binding.noinvoices.visibility = View.VISIBLE
                binding.clsInvoicesThirdParty.visibility = View.GONE
            }
        }
        viewModel.liveDataDownloadThirdPartyInvoicePDF.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                if (isClicked) {
                    val fileContent = it.Invoices[0].FileContent
                    val fileName = it.Invoices[0].FileName
                    try {
                        if (checkForStoragePermission())
                            downloadPDFData(fileName, fileContent)
                    } catch (_: Exception) {
                    }
                }
            }
            isDownloading = false
        }
    }

    override fun requestStoragePermission() {
        isClicked = true
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_STORAGE_PERMISSION_CODE
        )
    }

    override fun onStoragePermissionResult(granted: Boolean) {
        if (granted) {
            val item = prefs.getInvoiceX()!!
            dowloadPDF(item.InvoiceId, item.FileName)
        } else {
            showToast("Please allow storage permission to download and view pdf", requireContext())
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
        viewModel.DownloadThirdPartyInvoicePDF(prefs.clebUserId.toInt(), invoiceID)

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
                    viewModel.DownloadThirdPartyInvoicePDF(prefs.clebUserId.toInt(), selectedYear)
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
                        viewModel.DownloadThirdPartyInvoicePDF(
                            prefs.clebUserId.toInt(),
                            selectedYear
                        )
                    }
                }
            }
        }
    }

    private fun downloadPDFData(fileName: String, fileContent: String) {
        try {
        val currentDate = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
            Date()
        )
        val uniqueId = UUID.randomUUID().toString()
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


    private fun openPDF(file: File) {
        //BuildConfig.APPLICATION_ID + ".provider", file
        /*       val uri=  FileProvider.getUriForFile(
                   context,
                    BuildConfig.APPLICATION_ID + ".com.vansuita.pickimage.provider", file);*/
        //val uri = FileProvider.getUriForFile(context,  context.packageName + ".fileprovider", file)
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

    private fun checkForStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT <= 32) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_STORAGE_PERMISSION_CODE
                )
            } else {
                return true
            }
        } else {
            return true
        }
        return false
    }

}