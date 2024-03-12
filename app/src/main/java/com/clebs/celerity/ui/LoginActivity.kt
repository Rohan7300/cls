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
import com.clebs.celerity.models.requests.LoginRequest
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.ErrorDialog
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.NoInternetDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.isNetworkAvailable
import com.clebs.celerity.utils.showErrorDialog


class LoginActivity : AppCompatActivity() {
    lateinit var ActivityLoginBinding: ActivityLoginBinding
    lateinit var mainViewModel: MainViewModel
    lateinit var loadingDialog:LoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        loadingDialog = LoadingDialog(this)


        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo)).get(MainViewModel::class.java)

        ActivityLoginBinding.btLogin.setOnClickListener {

            if (ActivityLoginBinding.edtUser.text!!.isEmpty()) {
                ActivityLoginBinding.edtUser.setError("Please enter username/email")
            }
            if (ActivityLoginBinding.edtPass.text!!.isEmpty()) {
                ActivityLoginBinding.edtPass.setError("Please enter password")
            } else {
                loadingDialog.show()
                login()
            }
        }
    }

    fun login() {
        val fragmentManager = this.supportFragmentManager // Adjust this according to your needs
        if (isNetworkAvailable(this)) {
            mainViewModel.loginUser(
                LoginRequest(
                    ActivityLoginBinding.edtUser.text.toString(),
                    ActivityLoginBinding.edtPass.text.toString()
                )
            ).observe(this@LoginActivity, Observer {
                if (it != null) {

                    if (it.message.equals("Success")) {

                        Prefs.getInstance(applicationContext).accessToken = it.token
                        Prefs.getInstance(applicationContext).userID = it.userID.toString()
                        ActivityLoginBinding.progressbar.visibility = View.GONE
                        Log.e("response", "onCreate: " + it.token)
                        GetDriverSignatureInformation()
                        setLoggedIn(true)


                    } else {
                        ActivityLoginBinding.progressbar.visibility = View.GONE
                        //Toast.makeText(this, "Error in login", Toast.LENGTH_SHORT).show()
                        showErrorDialog(fragmentManager,"LS-01",it.message)
                        setLoggedIn(false)
                    }
                } else {
                    showErrorDialog(fragmentManager,"LS-02","Please check your email and password and try again.")
                    //Toast.makeText(this, "Error in login", Toast.LENGTH_SHORT).show()
                    loadingDialog.dismiss()
                }

            })
        } else {
            loadingDialog.dismiss()
            val dialog = NoInternetDialog()
            dialog.show(fragmentManager, NoInternetDialog.TAG)
        }

    }

    private fun setLoggedIn(isLoggedIn: Boolean) {
        Prefs.getInstance(applicationContext).saveBoolean("isLoggedIn", isLoggedIn)
    }

    fun GetDriverSignatureInformation() {
        loadingDialog.show()
        var userid: Double = 0.0
        if (!Prefs.getInstance(applicationContext).userID.equals(0.0)) {
            userid = Prefs.getInstance(applicationContext).userID.toDouble()
        }

        mainViewModel.getDriverSignatureInfo(userid).observe(this@LoginActivity, Observer {
            if (it != null) {
                //     Prefs.getInstance(applicationContext).saveSignatureInfo(it.toString())
                Prefs.getInstance(applicationContext)
                    .saveBoolean("isSignatureReq", it.isSignatureReq)
                Prefs.getInstance(applicationContext)
                    .saveBoolean("IsamazonSign", it.isAmazonSignatureReq)
                Prefs.getInstance(applicationContext)
                    .saveBoolean("isother", it.isOtherCompanySignatureReq)
                loadingDialog.dismiss()
                if (it.isSignatureReq) {

                    if (it.isAmazonSignatureReq.equals(true) || it.isOtherCompanySignatureReq.equals(
                            true
                        )
                    ) {
                        val intent = Intent(this, PolicyDocsActivity::class.java)
                        // val intent = Intent(this, HomeActivity::class.java)

                        intent.putExtra("signature_required", "0")
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("no_signature_required", "0")
                        startActivity(intent)
                        finish()
                    }
                } else {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("no_signature_required", "0")
                    startActivity(intent)
                    finish()
                }
            } else {
                loadingDialog.dismiss()
            }
        })

    }
}