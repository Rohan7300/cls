package com.clebs.celerity.ui

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.provider.Settings
import androidx.core.app.ActivityManagerCompat
import com.clebs.celerity.utils.Prefs


class App: Application()
{
    companion object {
        var prefs: Prefs? = null
        lateinit var instance: App
            private set
    }
  
    override fun onCreate() {
        super.onCreate()
        
        instance = this
        prefs = Prefs(applicationContext)


/*        val connectivityWorker = OneTimeWorkRequestBuilder<NetworkChangeReceiver>().build()
        WorkManager.getInstance(this).enqueue(connectivityWorker)*/
    }
}