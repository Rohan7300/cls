package com.clebs.celerity.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.ActivityViewTicketsBinding
import com.clebs.celerity.models.response.Doc
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.Prefs

class ViewTicketsActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel
    lateinit var loadingDialog: LoadingDialog
    private var ticketID: Int? = null
    lateinit var prefs: Prefs
    var ticketData: Doc? = null
    lateinit var binding: ActivityViewTicketsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_tickets)
        setContentView(binding.root)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        viewModel = ViewModelProvider(
            this,
            MyViewModelFactory(MainRepo(apiService))
        )[MainViewModel::class.java]
        prefs = Prefs.getInstance(this)
        ticketData = prefs.getCurrentTicket()
        binding.imageViewBack.setOnClickListener {
            onBackPressed()
        }
        binding.commentIV.setOnClickListener {
            if (ticketData != null) {

                val intent = Intent(this, AddCommentActivity::class.java).apply {
                    putExtra("ticketID", ticketData!!.UserTicketID)
                }
                startActivity(intent)
            }
        }

        if (ticketData != null) {
            binding.ticketID.text = ticketData!!.UserTicketID.toString()
            if (ticketData!!.RegNo != null)
                binding.tvRegistration.text = ticketData!!.RegNo.toString()
            binding.edtTitle.text = ticketData!!.TicketTitle
            binding.edtDes.text = ticketData!!.TicketDescription
            binding.tvDepart.text = ticketData!!.DepartmentName
            binding.tvRequests.text = ticketData!!.ReqTypeName
        }
    }
}