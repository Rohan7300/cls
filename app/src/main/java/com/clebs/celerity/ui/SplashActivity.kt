package com.clebs.celerity.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.ActivitySplashBinding
import com.clebs.celerity.models.response.SaveDeviceInformationRequest
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.NetworkManager
import com.clebs.celerity.dialogs.NoInternetDialog
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.MyCustomAppIntro
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.getCurrentAppVersion
import com.clebs.celerity.utils.getDeviceID
import com.clebs.celerity.utils.showToast
import com.github.appintro.AppIntro
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


class SplashActivity : AppCompatActivity() {
    private lateinit var ActivitySplashBinding: ActivitySplashBinding
    val TAG = "SPLASHACTIVIITY"
    var isNetworkActive: Boolean = true
    lateinit var dialog: NoInternetDialog
    lateinit var fragmentManager: FragmentManager
    private lateinit var mainViewModel: MainViewModel


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            //showToast("Notification Permission denied",this)
            next()
        } else {
            showToast("Notification Permission is required!!", this)
            next()
        }
    }

    private fun askNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                next()
                return
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                next()
            } else {

                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        val window = window
        val winParams = window.attributes
        winParams.flags =
            winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        window.attributes = winParams
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                )
        window.statusBarColor = resources.getColor(io.clearquote.assessment.cq_sdk.R.color.transparent, null)
        window.requestFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState)


        ActivitySplashBinding = DataBindingUtil.setContentView(this@SplashActivity, R.layout.activity_splash)

        ActivitySplashBinding.appVersionTv.text = getCurrentAppVersion(this)
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.anam_two)
        val rotateAnimationtwo = AnimationUtils.loadAnimation(this, R.anim.slide_slide)
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            askNotificationPermission()
        } else {
            next()
        }


        ActivitySplashBinding.ls.startAnimation(rotateAnimationtwo)
//        ActivitySplashBinding.imgCircleLogo.startAnimation(rotateAnimation)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)

        /*mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo)).get(MainViewModel::class.java)*/


        mainViewModel = DependencyProvider.getMainVM(this)



        mainViewModel.liveDataSaveDeviceInformation.observe(this) {
            if (it != null) {
                Log.d("SaveDeviceInformation", "Submitted $it")
            } else {
                Log.d("SaveDeviceInformation", "Submitted $it")
            }
        }

    }

    fun next() {
        android.os.Handler().postDelayed({
            if (isLoggedIn()) {
                retrieveAndSaveFCMToken()
                navigateToHomeScreen()
            } else {
                navigateToLoginScreen()
            }
            finish()
        }, 2000)
    }

    private fun isLoggedIn(): Boolean {
        return Prefs.getInstance(applicationContext).getBoolean("isLoggedIn", false)
    }

    private fun retrieveAndSaveFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result
            if (isNetworkActive) {
                mainViewModel.SaveDeviceInformation(
                    SaveDeviceInformationRequest(
                        FcmToken = token,
                        UsrId = Prefs.getInstance(this).clebUserId.toInt(),
                        UsrDeviceId = getDeviceID(),
                        UsrDeviceType = "Android"
                    )
                )
            }
            Log.d(TAG, "FCM Token $token")
        })
    }

    fun navigateToLoginScreen() {
        // Navigate to the login screen
        val i = Intent(
            this@SplashActivity,
            MyCustomAppIntro::class.java
        )

        startActivity(i)

    }

    fun navigateToHomeScreen() {

        if (Prefs.getInstance(applicationContext).getBoolean("isSignatureReq", false)
                .equals(true)
        ) {
            val i = Intent(
                this@SplashActivity,
                MyCustomAppIntro::class.java
            )
            startActivity(i)
        } else {
            val i = Intent(
                this@SplashActivity,
                MyCustomAppIntro::class.java
            )
            intent.putExtra("destinationFragment", "HomeFragment")
            intent.putExtra("actionToperform", "undef")
            intent.putExtra("actionID", "0")
            intent.putExtra("tokenUrl", "undef")
            intent.putExtra("notificationId", "0")
            startActivity(i)
        }
    }

    fun GetDriverSignatureInformation() {
        var userid: Double = 0.0
        if (!Prefs.getInstance(applicationContext).clebUserId.equals(0.0)) {
            userid = Prefs.getInstance(applicationContext).clebUserId.toDouble()
        }
        if (isNetworkActive) {
            mainViewModel.getDriverSignatureInfo(userid).observe(this@SplashActivity, Observer {
                if (it != null) {
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
                }
            })
        }

    }

}


