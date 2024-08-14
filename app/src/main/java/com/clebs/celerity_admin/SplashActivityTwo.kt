package com.clebs.celerity_admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.clebs.celerity_admin.database.OfflineSyncDB
import com.clebs.celerity_admin.utils.Prefs

class SplashActivityTwo : AppCompatActivity() {
    companion object {
        var prefs: Prefs? = null
        var offlineSyncDB: OfflineSyncDB? = null
        lateinit var instance: SplashActivityTwo
            private set

    }

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
        instance = this@SplashActivityTwo
        prefs = Prefs(applicationContext)
        offlineSyncDB = OfflineSyncDB(this@SplashActivityTwo)
        window.statusBarColor = resources.getColor(R.color.transparent, null)
        window.requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val rotateAnimationtwo = AnimationUtils.loadAnimation(this, R.anim.slide_fade)
        val imageview = findViewById<ImageView>(R.id.ls)
        imageview.startAnimation(rotateAnimationtwo)

        android.os.Handler().postDelayed({
            if (isLoggedIn()) {
                navigateToHome()
            } else {
                navigateToIntro()
            }

            finish()
        }, 2000)

    }

    fun navigateToIntro() {
        // Navigate to the login screen
        val i = Intent(
            this@SplashActivityTwo,
            MyApplicationIntro::class.java
        )

        startActivity(i)

    }

    fun navigateToLogin() {

        val i = Intent(
            this@SplashActivityTwo,
            LoginActivityTwo::class.java
        )

        startActivity(i)
    }

    fun navigateToHome() {
        val i = Intent(
            this@SplashActivityTwo, MainActivityTwo::class.java
        )

        startActivity(i)

    }

    private fun isLoggedIn(): Boolean {
        return Prefs.getInstance(applicationContext).getBoolean("isLoggedIn", false)
    }
}

