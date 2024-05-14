package com.clebs.celerity.adapters

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import com.clebs.celerity.models.response.InvoiceX
import com.clebs.celerity.utils.PermissionCallback
import com.clebs.celerity.utils.Prefs
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CLSThirdPartyInvoiceAdapter(
    var data: ArrayList<InvoiceX>,
    var context: Context,
    var pref: Prefs,
    var permissionCallback: PermissionCallback
) :
    RecyclerView.Adapter<CLSThirdPartyInvoiceAdapter.CLSThirdPartyInvoicViewHolder>() {
    inner class CLSThirdPartyInvoicViewHolder(val binding: ClsInvoicesAdapterLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.Q)
        fun bind(item: InvoiceX) {
            binding.date.text = convertWeekYearToMonthYear(item.Week, item.Year)
            binding.flname.text = "Invoice Week - ${item.Week}.pdf"
            binding.download.setOnClickListener {
                pref.saveInvoiceX(item)
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CLSThirdPartyInvoicViewHolder {
        val binding = ClsInvoicesAdapterLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CLSThirdPartyInvoicViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: CLSThirdPartyInvoicViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }


    public fun downloadPDF(fileName: String, fileContent: String) {
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
        //BuildConfig.APPLICATION_ID + ".provider", file
        /*       val uri=  FileProvider.getUriForFile(
                   context,
                    BuildConfig.APPLICATION_ID + ".com.vansuita.pickimage.provider", file);*/
        //val uri = FileProvider.getUriForFile(context,  context.packageName + ".fileprovider", file)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
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