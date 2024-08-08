package com.clebs.celerity.ui

import android.app.Application
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.ShowToast


class App: Application()
{

    companion object {
        var prefs: Prefs? = null
        var showToastX:ShowToast? = null
        lateinit var instance: App
            private set
    }
  
    override fun onCreate() {
        super.onCreate()
        instance = this
        prefs = Prefs(applicationContext)
        showToastX = ShowToast()
      //  RetrofitService.initialize(applicationContext)

/*        val connectivityWorker = OneTimeWorkRequestBuilder<NetworkChangeReceiver>().build()
        WorkManager.getInstance(this).enqueue(connectivityWorker)*/
    }
}