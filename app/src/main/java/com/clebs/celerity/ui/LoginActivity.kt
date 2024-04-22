package com.clebs.celerity.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.ActivityLoginBinding
import com.clebs.celerity.models.requests.LoginRequest
import com.clebs.celerity.models.response.SaveDeviceInformationRequest
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.NoInternetDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.getDeviceID
import com.clebs.celerity.utils.isNetworkAvailable
import com.clebs.celerity.utils.showErrorDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


class LoginActivity : AppCompatActivity() {
    lateinit var ActivityLoginBinding: ActivityLoginBinding
    lateinit var mainViewModel: MainViewModel
    lateinit var loadingDialog: LoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        loadingDialog = LoadingDialog(this)

        //getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo)).get(MainViewModel::class.java)

        ActivityLoginBinding.btLogin.setOnClickListener {

            if (ActivityLoginBinding.edtUser.text!!.isEmpty()) {
                ActivityLoginBinding.textError.visibility = View.VISIBLE
            } else if (ActivityLoginBinding.edtPass.text!!.isEmpty()) {
                ActivityLoginBinding.textErrorTwo.visibility = View.VISIBLE
                ActivityLoginBinding.textError.visibility = View.GONE
            } else {
                ActivityLoginBinding.textError.visibility = View.GONE
                ActivityLoginBinding.textErrorTwo.visibility = View.GONE
                loadingDialog.show()
                login()
            }
        }

        mainViewModel.liveDataSaveDeviceInformation.observe(this) {
            if (it != null) {
                Log.d("SaveDeviceInformation", "Submitted $it")
            } else {
                Log.d("SaveDeviceInformation", "Submitted $it")
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

                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
                            OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Log.w(
                                        "LoginActivity",
                                        "Fetching FCM registration token failed",
                                        task.exception
                                    )
                                    return@OnCompleteListener
                                }

                                val token = task.result
                                mainViewModel.SaveDeviceInformation(
                                    SaveDeviceInformationRequest(
                                        FcmToken = token,
                                        UsrId = Prefs.getInstance(this).userID.toInt(),
                                        UsrDeviceId = getDeviceID(),
                                        UsrDeviceType = "Android"
                                    )
                                )
                                Log.d("LoginActivity", "FCM Token $token")
                            })

                        Prefs.getInstance(applicationContext).accessToken = it.token
                        Prefs.getInstance(applicationContext).userID = it.userID.toString()
                        ActivityLoginBinding.progressbar.visibility = View.GONE
                        Log.e("response", "onCreate: " + it.token)
                        GetDriverSignatureInformation()
                        setLoggedIn(true)


                    } else {
                        ActivityLoginBinding.progressbar.visibility = View.GONE
                        //Toast.makeText(this, "Error in login", Toast.LENGTH_SHORT).show()
                        showErrorDialog(fragmentManager, "LS-01", it.message)
                        setLoggedIn(false)
                    }
                } else {
                    showErrorDialog(
                        fragmentManager,
                        "LS-02",
                        "Please check your email and password and try again."
                    )
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
        var pref = Prefs.getInstance(applicationContext)
        if (!pref.userID.equals(0.0)) {
            userid = pref.userID.toDouble()
        }

        mainViewModel.getDriverSignatureInfo(userid).observe(this@LoginActivity, Observer {
            if (it != null) {
                //     Prefs.getInstance(applicationContext).saveSignatureInfo(it.toString())
                pref.saveBoolean("isSignatureReq", it.isSignatureReq)
                pref.saveBoolean("IsamazonSign", it.isAmazonSignatureReq)
                pref.saveBoolean("isother", it.isOtherCompanySignatureReq)
                pref.handbookId = it.handbookId

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
                        //   val options = ActivityOptions.makeSceneTransitionAnimation(this)
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("no_signature_required", "0")
                        intent.putExtra("destinationFragment", "HomeFragment")
                        startActivity(intent)
                        finish()
                    }
                } else {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("destinationFragment", "HomeFragment")
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