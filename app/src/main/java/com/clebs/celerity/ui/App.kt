package com.clebs.celerity.ui

import android.app.Application
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
    }
}