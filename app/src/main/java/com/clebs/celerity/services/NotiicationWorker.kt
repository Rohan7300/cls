package com.clebs.celerity.services

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.clebs.celerity.utils.showToast

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val context = applicationContext

        val isComingFromPolicyNotification = inputData.getBoolean("isComingFromPolicyNotification", false)
        val policyDocPDFURIString = inputData.getString("policyDocPDFURI")
        val policyDocPDFURI = policyDocPDFURIString?.let { Uri.parse(it) }

        if (isComingFromPolicyNotification && policyDocPDFURI != null) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationId = inputData.getInt("notification_id", -1)

            if (notificationId != -1) {
                notificationManager.cancel(notificationId)
            }

            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(policyDocPDFURI, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("No PDF viewer found", context)
            }
        }

/*        Handler(Looper.getMainLooper()).postDelayed({
            val closeIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            closeIntent.setPackage("com.android.systemui")
            context.sendBroadcast(closeIntent)
        }, 100)*/

        return Result.success()
    }


}
