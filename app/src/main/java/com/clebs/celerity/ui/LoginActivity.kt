package com.clebs.celerity.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
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
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.utils.NetworkManager
import com.clebs.celerity.dialogs.NoInternetDialog
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.getDeviceID
import com.clebs.celerity.utils.showErrorDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


class LoginActivity : AppCompatActivity() {
    lateinit var ActivityLoginBinding: ActivityLoginBinding
    lateinit var mainViewModel: MainViewModel
    lateinit var loadingDialog: LoadingDialog
    lateinit var fragmentManager: FragmentManager
    lateinit var dialog: NoInternetDialog
    private var isNetworkActive: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        fragmentManager = this.supportFragmentManager
        dialog = NoInternetDialog()
        val networkManager = NetworkManager(this)

        networkManager.observe(this) {
            if (it) {
                isNetworkActive = true
                dialog.hideDialog()
            } else {
                isNetworkActive = false
                dialog.showDialog(fragmentManager)
            }
        }
        if (intent.hasExtra("downloadCQ")) {

            Prefs.getInstance(App.instance).Isfirst = intent.getBooleanExtra("downloadCQ", false)
        }
        loadingDialog = LoadingDialog(this)
        mainViewModel = DependencyProvider.getMainVM(this)

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

        var isPasswordVisible = false

        ActivityLoginBinding.passIcon.setOnClickListener {
            val cursorPosition = ActivityLoginBinding.edtPass.selectionEnd
            if (!isPasswordVisible) {
                ActivityLoginBinding.edtPass.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                ActivityLoginBinding.passIcon.setImageDrawable(getDrawable(R.drawable.visible2))
            } else {
                ActivityLoginBinding.edtPass.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                ActivityLoginBinding.passIcon.setImageDrawable(getDrawable(R.drawable.hidden2))
            }
            ActivityLoginBinding.edtPass.setSelection(cursorPosition)
            isPasswordVisible = !isPasswordVisible
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
        if (isNetworkActive) {
            mainViewModel.loginUser(
                LoginRequest(
                    ActivityLoginBinding.edtUser.text.toString(),
                    ActivityLoginBinding.edtPass.text.toString()
                )
            ).observe(this@LoginActivity, Observer {
                if (it != null) {

                    if (it.message.equals("Success")) {
                        if (!it.UserRole.isNullOrBlank()) {
                            if (it.UserRole.equals("C")) {
                                Prefs.getInstance(this).tokenExpiredOn = it.tokenExpiredOn
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
                                                UsrId = Prefs.getInstance(this).clebUserId.toInt(),
                                                UsrDeviceId = getDeviceID(),
                                                UsrDeviceType = "Android"
                                            )
                                        )
                                        Log.d("LoginActivity", "FCM Token $token")
                                    })

                                Prefs.getInstance(applicationContext).accessToken = it.token
                                Prefs.getInstance(applicationContext).clebUserId =
                                    it.userID.toString()
                                ActivityLoginBinding.progressbar.visibility = View.GONE
                                Log.e("response", "onCreate: " + it.token)
                                GetDriverSignatureInformation()
                                setLoggedIn(true)

                            } else {
                                ActivityLoginBinding.progressbar.visibility = View.GONE
                                showErrorDialog(
                                    fragmentManager,
                                    "LS-05",
                                    "Please check your email and password and try again."
                                )
                                setLoggedIn(false)
                                loadingDialog.dismiss()
                            }
                        } else {
                            ActivityLoginBinding.progressbar.visibility = View.GONE
                            showErrorDialog(
                                fragmentManager,
                                "LS-05",
                                "Please check your email and password and try again."
                            )
                            setLoggedIn(false)
                            loadingDialog.dismiss()
                        }

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
            dialog.showDialog(fragmentManager)
        }

    }

    private fun setLoggedIn(isLoggedIn: Boolean) {
        Prefs.getInstance(applicationContext).saveBoolean("isLoggedIn", isLoggedIn)
    }

    fun GetDriverSignatureInformation() {
        loadingDialog.show()
        var userid: Double = 0.0
        var pref = Prefs.getInstance(applicationContext)
        if (!pref.clebUserId.equals(0.0)) {
            userid = pref.clebUserId.toDouble()
        }

        mainViewModel.getDriverSignatureInfo(userid).observe(this@LoginActivity, Observer {
            if (it != null) {
                loadingDialog.dismiss()
                pref.handbookId = it.handbookId
                if (it!!.isSignatureReq.equals(true) && (it.isAmazonSignatureReq || it.isOtherCompanySignatureReq)) {
                    Prefs.getInstance(applicationContext)
                        .saveBoolean("isSignatureReq", it.isSignatureReq)
                    Prefs.getInstance(applicationContext)
                        .saveBoolean("IsamazonSign", it.isAmazonSignatureReq)
                    Prefs.getInstance(applicationContext)
                        .saveBoolean("isother", it.isOtherCompanySignatureReq)


                    val intent = Intent(this, PolicyDocsActivity::class.java)

                    intent.putExtra("signature_required", "0")
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("destinationFragment", "HomeFragment")
                    intent.putExtra("actionToperform", "undef")
                    intent.putExtra("actionID", "0")
                    intent.putExtra("tokenUrl", "undef")
                    intent.putExtra("notificationId", "0")
                    startActivity(intent)
                    finish()
                }
            } else {
                loadingDialog.dismiss()
            }
        })

    }
}