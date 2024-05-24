package com.clebs.celerity.utils

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.clebs.celerity.databinding.ActivityMainBinding
import com.clebs.celerity.utils.DependencyProvider.currentUri

class ClsCapture : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            // Workaround for Android Q memory leak issue in IRequestFinishCallback$Stub.
            // (https://issuetracker.google.com/issues/139738913)
            finishAfterTransition()

        } else {
            super.onBackPressed()
        }
    }
    fun passBitmap(crrURI: Uri){
        DependencyProvider.isComingBackFromCLSCapture = true
        currentUri = crrURI
        Log.e("skdhhsjdfhfdh", "passBitmap: "+ currentUri)
        finish()
    }
}
