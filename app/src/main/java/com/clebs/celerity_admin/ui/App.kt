package com.clebs.celerity_admin.ui

import android.app.Application
import com.clebs.celerity_admin.database.OfflineSyncDB
import com.clebs.celerity_admin.utils.LottieDialog
import com.clebs.celerity_admin.utils.Prefs


class App: Application()

{

    companion object {
        var prefs: Prefs? = null
        lateinit var instance: App
        var  offlineSyncDB:OfflineSyncDB?=null
            private set
    }
  
    override fun onCreate() {
        super.onCreate()

        instance = this
        prefs = Prefs(applicationContext)
       offlineSyncDB = OfflineSyncDB(applicationContext)
      LottieDialog.init(instance)


/*        val connectivityWorker = OneTimeWorkRequestBuilder<NetworkChangeReceiver>().build()
        WorkManager.getInstance(this).enqueue(connectivityWorker)*/
    }
}