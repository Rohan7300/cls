package com.clebs.celerity.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.clebs.celerity.R
import com.clebs.celerity.databinding.ActivitySplashBinding
import java.util.logging.Handler

class SplashActivity : AppCompatActivity() {
lateinit var ActivitySplashBinding : ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivitySplashBinding=DataBindingUtil.setContentView(this@SplashActivity,R.layout.activity_splash)
        android.os.Handler().postDelayed({
            // on below line we are
            // creating a new intent
            val i = Intent(
                this@SplashActivity,
                LoginActivity::class.java
            )
            // on below line we are
            // starting a new activity.
            startActivity(i)

            // on the below line we are finishing
            // our current activity.
            finish()
        }, 3000)
    }

}