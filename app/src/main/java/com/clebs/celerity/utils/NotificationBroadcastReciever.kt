package com.clebs.celerity.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityManager
import com.clebs.celerity.utils.DependencyProvider.isComingFromPolicyNotification
import com.clebs.celerity.utils.DependencyProvider.policyDocPDFURI


class NotificationBroadcastReciever : BroadcastReceiver() {
    lateinit var notificationManager:NotificationManager
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            if (isComingFromPolicyNotification && policyDocPDFURI != null) {
                isComingFromPolicyNotification = false
            notificationManager   =
                    it.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notificationId = intent?.getIntExtra("notification_id", -1)

                notificationId?.let { id ->
                    if (id != -1) {
                        notificationManager.cancel(id)
                    }
                }
                //it.startActivity(Intent(context,HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(policyDocPDFURI, "application/pdf")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) // Add the FLAG_ACTIVITY_NEW_TASK flag
                try {
                    it.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    showToast("No PDF viewer found", it)
                }
            }
/*            Handler(Looper.getMainLooper()).postDelayed({
                val closeIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                closeIntent.setPackage("com.android.systemui")
                context.sendBroadcast(closeIntent)
            }, 100)*/


        }
    }
}
