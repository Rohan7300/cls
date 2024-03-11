package com.clebs.celerity.utils
/*

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class NetworkChangeReceiver(context: Context,params: WorkerParameters):
    CoroutineWorker(context,params)  {
    override suspend fun doWork(): Result {
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return Result.failure()
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return Result.failure()

        return if(actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)){
            Result.success()
        }else{
            Result.failure()
        }
    }

    private fun showNoInternetDialog() {
        val fragmentManager = (applicationContext as? AppCompatActivity)?.supportFragmentManager
        if (fragmentManager?.findFragmentByTag(NoInternetDialogFragment.TAG) == null) {
            NoInternetDialogFragment().show(fragmentManager!!, NoInternetDialogFragment.TAG)
        }
    }

    private fun hideNoInternetDialog() {
        val fragmentManager = (applicationContext as? AppCompatActivity)?.supportFragmentManager
        val existingFragment = fragmentManager?.findFragmentByTag(NoInternetDialogFragment.TAG) as? NoInternetDialogFragment
        existingFragment?.dismissAllowingStateLoss()
    }

    companion object {
        const val TAG = "NoInternetDialogFragment"
    }

}

*/
