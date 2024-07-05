package com.clebs.celerity.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt

import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
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
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.getCurrentAppVersion
import com.clebs.celerity.utils.getDeviceID
import com.clebs.celerity.utils.showToast
import com.clebs.celerity.utils.toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import org.jetbrains.anko.find
import java.util.concurrent.Executor


class SplashActivity : AppCompatActivity() {
    private lateinit var ActivitySplashBinding: ActivitySplashBinding
    val TAG = "SPLASHACTIVIITY"
    var isNetworkActive: Boolean = true
    lateinit var deleteDialog: AlertDialog
    lateinit var deleteDialogtwo: AlertDialog
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    lateinit var dialog: NoInternetDialog
    lateinit var fragmentManager: FragmentManager
    private lateinit var mainViewModel: MainViewModel
    var destinationFragment = "HomeFragment"
    var actionToPerform = "undef"
    var tokenUrl = "undef"
    var actionID = "0"
    var notificationID = "0"

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            //showToast("Notification Permission denied",this)
            if (isLoggedIn()) {
                if (isBioMetricEnable()) {
                    if (!isLoggedInBio()) {
                        BiometricEnableDisableDialog()
                    } else {
                        useBiometric()
                    }
                }
//                useBiometric()
            } else {
                next()
            }

        } else {
            showToast("Notification Permission is required!!", this)
            if (isLoggedIn()) {
                if (isBioMetricEnable()) {
                    if (!isLoggedInBio()) {
                        BiometricEnableDisableDialog()
                    } else {
                        useBiometric()
                    }
                }
//                useBiometric()
            } else {
                next()
            }

        }
    }

    private fun askNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                if (isLoggedIn()) {
                    if (isBioMetricEnable()) {
                        if (!isLoggedInBio()) {
                            BiometricEnableDisableDialog()
                        } else {
                            useBiometric()
                        }
                    }
//                useBiometric()
                } else {
                    next()
                }

                return
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                if (isLoggedIn()) {
                    if (isBioMetricEnable()) {
                        if (!isLoggedInBio()) {
                            BiometricEnableDisableDialog()
                        } else {
                            useBiometric()
                        }
                    }
//                useBiometric()
                } else {
                    next()
                }
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
        window.statusBarColor = resources.getColor(R.color.transparent, null)
        window.requestFeature(Window.FEATURE_NO_TITLE);
        isBioMetricEnable()
        super.onCreate(savedInstanceState)
        deleteDialog =
            AlertDialog.Builder(this).create()
        deleteDialogtwo = AlertDialog.Builder(this).create()
        ActivitySplashBinding =
            DataBindingUtil.setContentView(this@SplashActivity, R.layout.activity_splash)

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
            if (isLoggedIn()) {
                if (isBioMetricEnable()) {
                    if (!isLoggedInBio()) {
                        BiometricEnableDisableDialog()
                    } else {
                        useBiometric()
                    }
                }
//                useBiometric()
            } else {
                next()
            }
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

    private fun isLoggedInBio(): Boolean {
        return Prefs.getInstance(applicationContext).getBoolean("isLoggedInBio", false)
    }

    private fun setLoggedInBio(isLoggedIn: Boolean) {
        Prefs.getInstance(applicationContext).saveBoolean("isLoggedInBio", isLoggedIn)
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
            LoginActivity::class.java
        )

        startActivity(i)

    }

    fun navigateToHomeScreen() {

        if (Prefs.getInstance(applicationContext).getBoolean("isSignatureReq", false)
                .equals(true)
        ) {
            val i = Intent(
                this@SplashActivity,
                PolicyDocsActivity::class.java
            )
            startActivity(i)
        } else {
            val i = Intent(
                this@SplashActivity,
                HomeActivity::class.java
            )
            intent.putExtra("destinationFragment", destinationFragment)
            intent.putExtra("actionToperform", actionToPerform)
            intent.putExtra("actionID", actionID)
            intent.putExtra("tokenUrl", tokenUrl)
            intent.putExtra("notificationId", notificationID)
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
                        intent.putExtra("destinationFragment", destinationFragment)
                        intent.putExtra("actionToperform", actionToPerform)
                        intent.putExtra("actionID", actionID)
                        intent.putExtra("tokenUrl", tokenUrl)
                        intent.putExtra("notificationId", notificationID)
                        startActivity(intent)
                        finish()
                    }
                }
            })
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        destinationFragment = intent?.getStringExtra("destinationFragment") ?: ""
        actionToPerform = intent?.getStringExtra("actionToperform") ?: "undef"
        tokenUrl = intent?.getStringExtra("tokenUrl") ?: "undef"
        actionID = intent?.getStringExtra("actionID") ?: "0"
        notificationID = intent?.getStringExtra("notificationId") ?: "0"
    }

    fun useBiometric() {

        setLoggedInBio(true)
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {

                    super.onAuthenticationError(errorCode, errString)

                    Log.e(TAG, "onAuthenticationError: " + errString)
                    if (errString.contains("Fingerprint operation cancelled by user.") || errString.contains(
                            "close"
                        )
                    ) {
                        BiometricDialog()

                    } else if (errString.contains("Too many attempts. Use screen lock instead.")) {
                        showToast("Too many attempts.", this@SplashActivity)
//

                    } else if (errString.contains("Authentication cancelled")) {
                        BiometricDialog()
                    } else if (errString.contains("Authentication canceled by user")) {
                        BiometricDialog()
                    } else {
                        next()
                    }

                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {

                    super.onAuthenticationSucceeded(result)
                    deleteDialog.dismiss()
                    Toast.makeText(
                        applicationContext,
                        "Authentication succeeded!", Toast.LENGTH_SHORT
                    )

                        .show()
                    next()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    next()


                    Log.e(TAG, "onAuthenticationFailed: ")
//                    Toast.makeText(
//                        applicationContext, "Authentication failed",
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
//                    next()

                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Verify your Identity")
            .setSubtitle("Use your fingerprint or PIN")
            .setAllowedAuthenticators(
                BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL or BIOMETRIC_WEAK
            )
            .build()



        biometricPrompt.authenticate(promptInfo)

    }

    fun BiometricDialog() {
        val factory = LayoutInflater.from(this)
        val view: View = factory.inflate(R.layout.login_dialog, null)

        deleteDialog.setView(view)
        val biometric: TextView = view.findViewById(R.id.saveBio)

        biometric.setOnClickListener {
            useBiometric()
        }


        deleteDialog.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                true
            } else {
                false
            }
        }


        deleteDialog.setCancelable(false)
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        deleteDialog.show();

    }

    fun BiometricEnableDisableDialog() {
        val factory = LayoutInflater.from(this)
        val view: View = factory.inflate(R.layout.enablebiometric, null)

        deleteDialogtwo.setView(view)
        val biometric: TextView = view.findViewById(R.id.saveBio)
        val biometeric: TextView = view.findViewById(R.id.saveBiono)

        biometeric.setOnClickListener {
            next()
            deleteDialogtwo.dismiss()
        }

        biometric.setOnClickListener {
            Toast.makeText(this, "Biometric Auth Enabled", Toast.LENGTH_SHORT).show()
            useBiometric()
            deleteDialogtwo.dismiss()

        }


        deleteDialogtwo.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                true
            } else {
                false
            }
        }


        deleteDialogtwo.setCancelable(false)
        deleteDialogtwo.setCanceledOnTouchOutside(false);
        deleteDialogtwo.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        deleteDialogtwo.show();

    }

    fun isBioMetricEnable(): Boolean {
        var canAuth = true
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate()) {

            BiometricManager.BIOMETRIC_SUCCESS -> {
                canAuth = true
                Log.e(TAG, "useBiometric: ")
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                canAuth = false
                Log.e(TAG, "useBiometric: ")
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                canAuth = false
                Log.e(TAG, "useBiometric: ")
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                canAuth = false
                Log.e(TAG, "useBiometric: ")
            }

        }
        return canAuth
    }
}


