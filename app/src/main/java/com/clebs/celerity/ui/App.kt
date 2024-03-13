package com.clebs.celerity.ui

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.clebs.celerity.utils.NetworkChangeReceiver
import com.clebs.celerity.utils.Prefs
import java.util.concurrent.TimeUnit


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