package com.clebs.celerity.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.ActivityLoginBinding
import com.clebs.celerity.models.LoginRequest
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.Prefs


class LoginActivity : AppCompatActivity() {
    lateinit var ActivityLoginBinding: ActivityLoginBinding
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)

        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo)).get(MainViewModel::class.java)

        ActivityLoginBinding.btLogin.setOnClickListener {

            if (ActivityLoginBinding.edtUser.text!!.isEmpty()) {
                ActivityLoginBinding.edtUser.setError("Please enter username/email")

            } else if (ActivityLoginBinding.edtUser.text!!.isEmpty()) {
                ActivityLoginBinding.edtPass.setError("Please enter password")

            }
//            else if (!isValidPassword(ActivityLoginBinding.edtPass.text.toString())){
//                ActivityLoginBinding.edtPass.setError("Please enter valid password")
//
//            }
            else {
                ActivityLoginBinding.progressbar.visibility = View.VISIBLE
                login()
            }

//                login()
//            val intent = Intent(this, HomeActivity::class.java)
//            startActivity(intent)
//            login()
        }


    }

    fun login() {

        mainViewModel.loginUser(
            LoginRequest(
                ActivityLoginBinding.edtUser.text.toString(),
                ActivityLoginBinding.edtPass.text.toString()
            )
        ).observe(this@LoginActivity, Observer {
            if (it != null) {
                if (it.message.equals("Success")) {

                    Prefs.getInstance(applicationContext).accessToken = it.token
                    ActivityLoginBinding.progressbar.visibility = View.GONE
                    Log.e("response", "onCreate: " + it.token)
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)

                } else {
                    ActivityLoginBinding.progressbar.visibility = View.GONE
                    Toast.makeText(this, "Error in login", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }
}