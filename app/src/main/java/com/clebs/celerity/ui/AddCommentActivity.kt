package com.clebs.celerity.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.CommentAdapter
import com.clebs.celerity.databinding.ActivityAddCommentBinding
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.showToast

class AddCommentActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddCommentBinding
    lateinit var viewModel: MainViewModel
    lateinit var loadingDialog: LoadingDialog
    private var ticketID: Int? = null
    lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_comment)
        setContentView(binding.root)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        viewModel = ViewModelProvider(
            this,
            MyViewModelFactory(MainRepo(apiService))
        )[MainViewModel::class.java]
        prefs = Prefs.getInstance(this)
        ticketID = intent.getIntExtra("ticketID", -1)
        loadingDialog = LoadingDialog(this)
        //binding.commentRv.visibility = View.GONE
        //binding.noCommentLayout.visibility = View.VISIBLE
        val commentAdapter = CommentAdapter(arrayListOf())
        binding.commentRv.adapter = commentAdapter
        binding.commentRv.layoutManager = LinearLayoutManager(this)
        viewModel.liveDataGetTicketCommentList.observe(this) {
            loadingDialog.cancel()
            if (it != null) {
                if(it.Docs.isNotEmpty()){
                    binding.commentRv.visibility = View.VISIBLE
                    binding.noCommentLayout.visibility = View.GONE
                }
                val reversedList = it.Docs
                commentAdapter.arrayList.clear()
                commentAdapter.arrayList.addAll(reversedList)
                commentAdapter.notifyDataSetChanged()
            }else{
                binding.commentRv.visibility = View.GONE
                binding.noCommentLayout.visibility = View.VISIBLE
            }
        }
        viewModel.liveDataSaveTicketComment.observe(this) {
            loadingDialog.cancel()
            if (it != null) {
                binding.commentET.setText("")
                loadingDialog.show()
                viewModel.GetTicketCommentList(prefs.userID.toInt(), ticketID!!)
            }
        }

        binding.imageViewBack.setOnClickListener {
            onBackPressed()
        }

        loadingDialog.show()
        viewModel.GetTicketCommentList(prefs.userID.toInt(), ticketID!!)
        binding.submitAddComment.setOnClickListener {
            if (!binding.commentET.text.isNullOrEmpty()) {
                loadingDialog.show()
                viewModel.SaveTicketComment(
                    prefs.userID.toInt(),
                    ticketID!!,
                    binding.commentET.text.toString()
                )
            } else {
                showToast("Please add comment first", this)
            }
        }


    }
}