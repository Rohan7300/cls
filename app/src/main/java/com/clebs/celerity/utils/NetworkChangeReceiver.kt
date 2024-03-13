package com.clebs.celerity.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class NetworkChangeReceiver(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return Result.failure()
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities)
                ?: return Result.failure()

        return if (actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            hideNoInternetDialog()
            Result.success()

        } else {
            showNoInternetDialog()
            Result.failure()
        }
    }

    private fun showNoInternetDialog() {
        val fragmentManager = (applicationContext as? AppCompatActivity)?.supportFragmentManager
        if (fragmentManager?.findFragmentByTag(NoInternetDialog.TAG) == null) {
            NoInternetDialog().show(fragmentManager!!, NoInternetDialog.TAG)
        }
    }

    private fun hideNoInternetDialog() {
        val fragmentManager = (applicationContext as? AppCompatActivity)?.supportFragmentManager
        val existingFragment =
            fragmentManager?.findFragmentByTag(NoInternetDialog.TAG) as? NoInternetDialog
        existingFragment?.dismissAllowingStateLoss()
    }


}
