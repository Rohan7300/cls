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
import com.clebs.celerity.models.response.InvoiceXX
import com.clebs.celerity.utils.PermissionCallback
import com.clebs.celerity.utils.Prefs
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CLSThirdPartyInvoiceAdapter(
    var data: ArrayList<InvoiceXX>,
    var context: Context,
    var pref: Prefs,
    var permissionCallback: PermissionCallback
) :
    RecyclerView.Adapter<CLSThirdPartyInvoiceAdapter.CLSThirdPartyInvoicViewHolder>() {
    inner class CLSThirdPartyInvoicViewHolder(val binding: ClsInvoicesAdapterLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.Q)
        fun bind(item: InvoiceXX) {
            binding.date.text = convertWeekYearToMonthYear(item.Week, item.Year)
            binding.flname.text = "Invoice Week - ${item.Week}.pdf"
            binding.download.setOnClickListener {
                pdfDownload(item)
            }
            binding.fullClickPDFDownload.setOnClickListener {
                pdfDownload(item)
            }
        }
    }

    public fun pdfDownload(item: InvoiceXX) {
        pref.saveInvoiceX(item)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionCallback.requestStoragePermission()
            } else {
                permissionCallback.dowloadPDF(item.InvoiceId, item.FileName)
            }
        } else {
            permissionCallback.dowloadPDF(item.InvoiceId, item.FileName)
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


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}