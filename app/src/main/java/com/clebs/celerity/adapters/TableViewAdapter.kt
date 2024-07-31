package com.clebs.celerity.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.R
import com.clebs.celerity.models.MovieModel
import com.kotlinpermissions.notNull
import kotlin.math.ceil


class TableViewAdapter(var Context: Context, private val movieList: ArrayList<MovieModel>) :
    RecyclerView.Adapter<TableViewAdapter.RowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.table_list_item, parent, false)
        return RowViewHolder(itemView)
    }

    private fun setHeaderBg(view: TextView) {
        view.setBackgroundResource(R.drawable.tablecontentheader)
        view.typeface = Typeface.DEFAULT_BOLD
        view.setTextColor(ContextCompat.getColor(Context, R.color.black))
        view.setTextSize(16f)

    }

    private fun setContentBg(view: TextView) {
        view.setBackgroundResource(R.drawable.tablecontent)
        view.typeface = Typeface.DEFAULT

        view.setTextSize(12f)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        val rowPos = holder.bindingAdapterPosition

        if (rowPos == 0) {
            // Header Cells. Main Headings appear here
            holder.apply {
                setHeaderBg(txtWeek)
                setHeaderBg(txtTransporterID)
                setHeaderBg(txtDriver)
                setHeaderBg(txtstatus)
                setHeaderBg(txtlocation)
                setHeaderBg(txttotal)
                setHeaderBg(txtdelive)
                setHeaderBg(txtdcr)
                setHeaderBg(txtdnr)
                setHeaderBg(txtpod)
                setHeaderBg(txtcc)
                setHeaderBg(txtdcr)
                setHeaderBg(txtlsc)
                setHeaderBg(txtphr)
                setHeaderBg(txtce)



                txtWeek.text = "Week"
                txtTransporterID.text = "Transporter ID"
                txtDriver.text = "Driver"
                txtlocation.text = "Location"
                txtstatus.text = "Status"
                txttotal.text = "Total score"
                txtdelive.text = "Delivered"
                txtdcr.text = "DCR"

                txtdnr.text = "DNR"
                txtpod.text = "POD"
                txtcc.text = "CC"
                txtlsc.text = "SC"
                txtphr.text = "PHR"
                txtce.text = "CE"

            }
        } else {
            val modal = movieList[rowPos - 1]

            holder.apply {
                setContentBg(txtWeek)
                setContentBg(txtTransporterID)
                setContentBg(txtDriver)
                setContentBg(txtstatus)
                setContentBg(txtlocation)
                setContentBg(txttotal)
                setContentBg(txtdelive)
                setContentBg(txtdcr)
                setContentBg(txtdnr)
                setContentBg(txtpod)
                setContentBg(txtcc)
                setContentBg(txtdcr)
                setContentBg(txtlsc)
                setContentBg(txtphr)
                setContentBg(txtce)



                txtWeek.text = modal.movieNames
                txtTransporterID.text = modal.movieNames
                txtDriver.text = modal.movieNames
                txtlocation.text = modal.movieNames
                txtstatus.text = modal.movieNames
                txttotal.text = modal.movieNames
                txtdelive.text = modal.movieNames
                txtdcr.text = modal.movieNames

                txtdnr.text = modal.movieNames
                txtpod.text = modal.movieNames
                txtcc.text = modal.movieNames
                txtlsc.text = modal.movieNames
                txtphr.text = modal.movieNames
                txtce.text = modal.movieNames
            }
        }
    }

    override fun getItemCount(): Int {
        return movieList.size + 1 // one more to add header row
    }

    inner class RowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtWeek: TextView = itemView.findViewById(R.id.txtweek)
        val txtTransporterID: TextView = itemView.findViewById(R.id.txtTransporterID)
        val txtDriver: TextView = itemView.findViewById(R.id.txtDriver)
        val txtlocation: TextView = itemView.findViewById(R.id.txtLocation)
        val txtstatus: TextView = itemView.findViewById(R.id.txtStatus)
        val txttotal: TextView = itemView.findViewById(R.id.txtTotal)
        val txtdelive: TextView = itemView.findViewById(R.id.txtDeliver)
        val txtdcr: TextView = itemView.findViewById(R.id.txtDCR)
        val txtdnr: TextView = itemView.findViewById(R.id.txtDNR)
        val txtpod: TextView = itemView.findViewById(R.id.txtpod)
        val txtcc: TextView = itemView.findViewById(R.id.txtcc)
        val txtlsc: TextView = itemView.findViewById(R.id.txtsc)
        val txtphr: TextView = itemView.findViewById(R.id.txtphr)
        val txtce: TextView = itemView.findViewById(R.id.txtce)

    }
}