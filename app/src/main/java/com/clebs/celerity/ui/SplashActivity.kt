package com.clebs.celerity.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.ActivitySplashBinding
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.Prefs
import java.util.logging.Handler

class SplashActivity : AppCompatActivity() {
    lateinit var ActivitySplashBinding: ActivitySplashBinding
    lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivitySplashBinding =
            DataBindingUtil.setContentView(this@SplashActivity, R.layout.activity_splash)

        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.anam)

        // Set the animation on the circles
        ActivitySplashBinding.imgCircleLogo.startAnimation(rotateAnimation)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)

        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo)).get(MainViewModel::class.java)





        android.os.Handler().postDelayed({

            if (isLoggedIn()) {
                navigateToHomeScreen()
            } else {
                navigateToLoginScreen()
            }

            finish()
        }, 3000)
    }

    private fun isLoggedIn(): Boolean {
        return Prefs.getInstance(applicationContext).getBoolean("isLoggedIn", false)

    }

    fun navigateToLoginScreen() {
        // Navigate to the login screen
        val i = Intent(
            this@SplashActivity,
            LoginActivity::class.java
        )
        // on below line we are
        // starting a new activity.
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
            // on below line we are
            // starting a new activity.
            startActivity(i)
        } else {
            val i = Intent(
                this@SplashActivity,
                HomeActivity::class.java
            )
            // on below line we are
            // starting a new activity.
            startActivity(i)
        }
        // Navigate to the home screen or any other screen that follows the login screen


    }

    fun GetDriverSignatureInformation() {
        var userid: Double = 0.0
        if (!Prefs.getInstance(applicationContext).userID.equals(0.0)) {
            userid = Prefs.getInstance(applicationContext).userID.toDouble()
        }

        mainViewModel.getDriverSignatureInfo(userid).observe(this@SplashActivity, Observer {
          if(it!=null){
              if (it!!.isSignatureReq.equals(true)) {
                  Prefs.getInstance(applicationContext)
                      .saveBoolean("isSignatureReq", it.isSignatureReq)
                  Prefs.getInstance(applicationContext)
                      .saveBoolean("IsamazonSign", it.isAmazonSignatureReq)
                  Prefs.getInstance(applicationContext)
                      .saveBoolean("isother", it.isOtherCompanySignatureReq)

                  val intent = Intent(this, PolicyDocsActivity::class.java)

                  intent.putExtra("signature_required", "0")
                  startActivity(intent)
              } else {
                  val intent = Intent(this, HomeActivity::class.java)
                  intent.putExtra("no_signature_required", "0")
                  startActivity(intent)
              }
          }

        })

    }

}


