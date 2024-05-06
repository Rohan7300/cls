package com.clebs.celerity.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.DetailCommentAdapter
import com.clebs.celerity.databinding.ActivityCommentDetailBinding
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.utils.Prefs

class CommentDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityCommentDetailBinding
    lateinit var viewModel: MainViewModel
    lateinit var loadingDialog: LoadingDialog
    private var ticketID: Int? = null
    private var ticketSub:String?= null
    lateinit var prefs: Prefs
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_comment_detail)
        setContentView(binding.root)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        viewModel = ViewModelProvider(
            this,
            MyViewModelFactory(MainRepo(apiService))
        )[MainViewModel::class.java]
        prefs = Prefs.getInstance(this)
        ticketID = intent.getIntExtra("ticketID", -1)
        ticketSub = intent.getStringExtra("ticketSub")
        val commentAdapter = DetailCommentAdapter(arrayListOf(),this)
        loadingDialog = LoadingDialog(this)
        binding.imageViewBack.setOnClickListener {
            onBackPressed()
        }
        binding.tktID.text = ticketID.toString()
        binding.tktSub.text = ticketSub?:""

        binding.commentDetailRV.adapter = commentAdapter
        binding.commentDetailRV.layoutManager = LinearLayoutManager(this)
        viewModel.liveDataGetTicketCommentList.observe(this) {
            loadingDialog.cancel()
            if (it != null) {
                val reversedList = it.Docs
                commentAdapter.arrayList.clear()
                commentAdapter.arrayList.addAll(reversedList)
                commentAdapter.notifyDataSetChanged()
            }
        }

        loadingDialog.show()
        viewModel.GetTicketCommentList(prefs.clebUserId.toInt(), ticketID!!)
    }
}