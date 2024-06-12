package com.clebs.celerity_admin.ui

import android.app.Application
import com.clebs.celerity_admin.utils.Prefs

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