package com.clebs.celerity.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.clebs.celerity.R
import com.clebs.celerity.models.response.SaveDeviceInformationRequest
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.getDeviceID
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FirebaseNotificationService : FirebaseMessagingService() {
    val TAG = "FBNotification"
    private lateinit var mainRepo: MainRepo

    override fun onCreate() {
        super.onCreate()
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        mainRepo = MainRepo(apiService)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "FCMMessage Data1 ${message.messageId}")
        Log.d(TAG, "FCMMessage Data2 ${message.data} ")
        Log.d(TAG, "FCMMessage Data3 ${message.messageType} ")
        Log.d(TAG, "FCMMessage Data4 ${message.notification} ")
        Log.d(TAG, "FCMMessage Data5 ${message.rawData} ")
        Log.d(TAG, "FCMMessage Data6 ${message.sentTime} ")
        val title = message.notification?.title ?: "Notification Title"
        val messageBody = message.notification?.body ?: "Notification Message"
        Log.d(TAG, "FCMMessage MessageBody ${message.notification?.body} ")
        showCustomNotification(title, messageBody)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM Refreshed token: $token")
        var userID = 0
        if (Prefs.getInstance(applicationContext).userID.isEmpty() || Prefs.getInstance(
                applicationContext
            ).userID == " "
        ) userID = 0
        else userID = Prefs.getInstance(applicationContext).userID.toInt()
        CoroutineScope(Dispatchers.IO).launch {
            mainRepo.SaveDeviceInformation(
                SaveDeviceInformationRequest(
                    FcmToken = token,
                    UsrId = userID,
                    UsrDeviceId = getDeviceID(),
                    UsrDeviceType = "Android"
                )
            )
        }
    }

    private fun showCustomNotification(title: String, message: String) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("destinationFragment", "NotificationsFragment")
        val channel_id = "notification_channel"
        val notificationId = 0

        val pendingIntent = PendingIntent.getActivity(
            this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channel_id, "web_app", NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val builder = NotificationCompat.Builder(this, channel_id)
            .setSmallIcon(R.drawable.logo_new)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setCustomContentView(getCustomDesign(title, message))
        }

        notificationManager.notify(notificationId, builder.build())
    }

    private fun getCustomDesign(title: String, message: String): RemoteViews {
        val remoteViews = RemoteViews(packageName, R.layout.notification_layout)
        remoteViews.setTextViewText(R.id.title, title)
        remoteViews.setTextViewText(R.id.descriptionXX, message)
        remoteViews.setImageViewResource(R.id.icons, R.drawable.logo_new)
        return remoteViews
    }

}