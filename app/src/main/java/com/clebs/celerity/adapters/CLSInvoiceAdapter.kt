package com.clebs.celerity.adapters

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.databinding.ClsInvoicesAdapterLayoutBinding
import com.clebs.celerity.models.response.Invoice
import com.clebs.celerity.utils.showToast
import java.io.File
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.multidex.BuildConfig
import com.clebs.celerity.utils.PermissionCallback
import com.clebs.celerity.utils.Prefs
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Objects

class CLSInvoiceAdapter(
    var data: ArrayList<Invoice>,
    var context: Context,
    var pref: Prefs,
    var permissionCallback: PermissionCallback
) : RecyclerView.Adapter<CLSInvoiceAdapter.CLSInvoicViewHolder>() {
    inner class CLSInvoicViewHolder(val binding: ClsInvoicesAdapterLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Invoice) {
            binding.date.text = ""
            binding.flname.text = "CLS Invoice Week ${item.Week}.pdf"
            binding.download.setOnClickListener {
                pref.saveInvoice(item)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionCallback.requestStoragePermission()
                    } else {
                        downloadPDF(item.FileName, item.FileContent)
                    }
                } else {
                    downloadPDF(item.FileName, item.FileContent)
                }
            }
        }
    }

    private fun convertWeekYearToMonthYear(week: Int, year: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.WEEK_OF_YEAR, week)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY) // Assuming week starts on Sunday
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CLSInvoicViewHolder {
        val binding = ClsInvoicesAdapterLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CLSInvoicViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: CLSInvoicViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }


    fun downloadPDF(fileName: String, fileContent: String) {
        try {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )
            val fos = FileOutputStream(file)
            fos.write(Base64.decode(fileContent, Base64.DEFAULT))
            fos.close()
            showToast("PDF Downloaded!", context)
            openPDF(file)
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Failed to download PDF", context)
        }
    }


    private fun openPDF(file: File) {

        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } else {
            Uri.fromFile(file)
        }

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("No PDF viewer found", context)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}