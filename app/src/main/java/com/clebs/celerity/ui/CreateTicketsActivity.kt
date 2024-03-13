package com.clebs.celerity.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.ActivityTicketsBinding
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo

class CreateTicketsActivity : AppCompatActivity() {
    lateinit var mbinding: ActivityTicketsBinding
    lateinit var viewmodel: MainViewModel
    lateinit var repo: MainRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = DataBindingUtil.setContentView(this, R.layout.activity_tickets)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        repo = MainRepo(apiService)
        viewmodel =
            ViewModelProvider(this, MyViewModelFactory(repo))[MainViewModel::class.java]


        mbinding.tvdepart.setOnClickListener {

            mbinding.rvList.visibility= View.VISIBLE
        }


    }
}