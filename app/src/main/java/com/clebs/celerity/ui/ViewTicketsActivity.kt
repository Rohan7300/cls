package com.clebs.celerity.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.AttachmentAdapter
import com.clebs.celerity.databinding.ActivityViewTicketsBinding
import com.clebs.celerity.models.response.Doc
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.DependencyProvider.comingFromViewTickets
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
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                try {
                    finish()
                    DependencyProvider.comingFromViewTickets = true
                } catch (_: Exception) {
                }
            }
        })
        binding.imageViewBack.setOnClickListener {
            finish()
            comingFromViewTickets = true
            //onBackPressed()
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
            binding.ticketID.text = "CLS : " + ticketData!!.UserTicketID.toString()
            if (ticketData!!.OtherRegNo != null) {
                if (ticketData!!.OtherRegNo == "undefined")
                    binding.tvRegistration.text = " - - - - "
                else
                    binding.tvRegistration.text = ticketData!!.OtherRegNo.toString()
            }

            binding.edtTitle.text = ticketData!!.TicketTitle
            binding.edtDes.text = ticketData!!.TicketDescription
            binding.tvDepart.text = ticketData!!.DepartmentName
            binding.tvRequests.text = ticketData!!.ReqTypeName
            binding.llpb.visibility = View.VISIBLE
            viewModel.GetUserTicketDocuments(
                userID = prefs.clebUserId.toInt(),
                ticketId = ticketData!!.UserTicketID.toInt()
            )
        }


        val attachmentAdapter = AttachmentAdapter(this@ViewTicketsActivity, ArrayList())
        binding.attachmentRV.adapter = attachmentAdapter
        binding.attachmentRV.layoutManager = LinearLayoutManager(this)
        binding.llAttachment.visibility = View.GONE

        viewModel.liveDataGetUserTicketDocuments.observe(this) {
            if (it != null) {
                attachmentAdapter.data.clear()
                attachmentAdapter.data.addAll(it.Docs)
                binding.llpb.visibility = View.GONE
                attachmentAdapter.notifyDataSetChanged()
                binding.llAttachment.visibility = View.VISIBLE
            } else {
                binding.llAttachment.visibility = View.GONE
                binding.llpb.visibility = View.GONE
            }
        }
    }
}