package com.clebs.celerity.utils

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.StatusBarManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.STATUS_BAR_SERVICE
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import com.clebs.celerity.utils.DependencyProvider.isComingFromPolicyNotification
import com.clebs.celerity.utils.DependencyProvider.policyDocPDFURI
import java.lang.reflect.Method


class NotificationBroadcastReciever : BroadcastReceiver() {
    @SuppressLint("WrongConstant")
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            if(isComingFromPolicyNotification&&policyDocPDFURI!=null){
                isComingFromPolicyNotification = false

//                val closeIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
//                context.sendBroadcast(closeIntent)
                val intent = Intent(Intent.ACTION_VIEW)

                intent.setDataAndType(policyDocPDFURI, "application/pdf")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) // Add the FLAG_ACTIVITY_NEW_TASK flag
                try {
                    it.startActivity(intent)

                    // Call the dismissNotificationDrawer() method

                } catch (e: Exception) {
                    e.printStackTrace()
                    showToast("No PDF viewer found", it)
                }
            }
            val notificationManager = it.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationId = intent?.getIntExtra("notification_id", -1)

            notificationId?.let { id ->
                if (id != -1) {
                    notificationManager.cancel(id)
                }
            }
        }
    }

}
