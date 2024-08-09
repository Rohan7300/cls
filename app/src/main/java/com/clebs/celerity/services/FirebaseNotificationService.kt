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
import com.clebs.celerity.ui.SplashActivity
import com.clebs.celerity.utils.DependencyProvider.notificationWatcher
import com.clebs.celerity.utils.DependencyProvider.notify
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.getDeviceID
import com.clebs.celerity.utils.parseToInt
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FirebaseNotificationService : FirebaseMessagingService() {
    val TAG = "FBNotification"
    private lateinit var mainRepo: MainRepo
    var isNotificationShowing = false

    override fun onCreate() {
        super.onCreate()
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        mainRepo = MainRepo(apiService)
    }


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        notify.postValue(true)
        Log.d(TAG, "FCMMessage Data1 ${message.messageId}")
        Log.d(TAG, "FCMMessage Data2 ${message.data} ")
        Log.d(TAG, "FCMMessage Data3 ${message.messageType} ")
        Log.d(TAG, "FCMMessage Data4 ${message.notification} ")
        Log.d(TAG, "FCMMessage Data5 ${message.rawData} ")
        Log.d(TAG, "FCMMessage Data6 ${message.sentTime} ")
        val title = message.notification?.title ?: "Notification Title"
        val messageBody = message.notification?.body ?: "Notification Message"
        val actionToperform = message.data["alertType"] ?: "undefined"
        var actionID = "0.0"
        var tokenUrl = ""
        var notificationId = "0"
        if (message.data["actionId"] != null) {
            actionID = message.data["actionId"].toString()
        }
        if (message.data["url"] != null) {
            tokenUrl = message.data["url"].toString()
        }
        if (message.data["notificationId"] != null) {
            notificationId = message.data["notificationId"].toString()
        }

        Log.d(TAG, "FCMMessage MessageBody ${message.notification?.body} ")
        Log.d(TAG, "FCMMessage AlertType ${message.data["alertType"]} ")
        if (!isNotificationShowing) {
            showCustomNotification(
                title,
                messageBody,
                actionToperform,
                actionID,
                tokenUrl,
                notificationId
            )
            isNotificationShowing = true
        }
    }

    override fun handleIntent(intent: Intent?) {
        isNotificationShowing = false
        Log.d("TAG", "NewIntent : ${intent!!.extras}")
        if (intent != null) {
            val messageBody = intent.extras!!.getString("body") ?: "Notification Message"
            val title = intent.extras!!.getString("title") ?: "Notification Title"
            val actionID = intent.extras!!.getString("actionId") ?: "0.0"
            val actionToperform = intent.extras!!.getString("alertType") ?: "undefined"
            val tokenUrl = intent.extras!!.getString("gcm.notification.url") ?: "undefined"
            val notificationId = intent.extras!!.getString("notificationId") ?: "0"

            Log.d(TAG, "message : $messageBody")
            Log.d(TAG, "title : $title")
            Log.d(TAG, "actionID : $actionID")
            Log.d(TAG, "actionToperform : $actionToperform")
            Log.d(TAG, "tokenUrl : $tokenUrl")
            Log.d(TAG, "notificationId : $notificationId")
            if (!isNotificationShowing) {

                if (notificationId != "0") {
                    if (actionToperform == "Deductions" || actionToperform == "Driver Deduction with Agreement" || actionToperform == "DriverDeductionWithAgreement")
                        notificationWatcher.postValue(2)
                 else if (actionToperform == "Weekly Location Rota" || actionToperform == "Weekly Rota Approval" || actionToperform == "WeeklyRotaApproval")
                        notificationWatcher.postValue(3)
                    else if (actionToperform == "Expired Document" || actionToperform == "ExpiredDocuments")
                        notificationWatcher.postValue(1)
                    showCustomNotification(
                        title,
                        messageBody,
                        actionToperform,
                        actionID,
                        tokenUrl,
                        notificationId
                    )
                }
                isNotificationShowing = true
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM Refreshed token: $token")
        var userID = 0
        if (Prefs.getInstance(applicationContext).clebUserId.isEmpty() || Prefs.getInstance(
                applicationContext
            ).clebUserId == " "
        ) userID = 0
        else userID = Prefs.getInstance(applicationContext).clebUserId.toInt()
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

    private fun showCustomNotification(
        title: String,
        message: String,
        actionToPerform: String,
        actionID: String,
        tokenUrl: String,
        notificationID: String
    ) {
        var intent: Intent? = null

        if (Prefs.getInstance(applicationContext).getBoolean("isLoggedIn", false)) {
            intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("destinationFragment", "NotificationsFragment")
            intent.putExtra("actionToperform", actionToPerform)
            intent.putExtra("actionID", actionID)
            intent.putExtra("tokenUrl", tokenUrl)
            intent.putExtra("notificationId", notificationID)
        } else {
            intent = Intent(this, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val channel_id = "notification_channel"
        val notificationId = try {
            parseToInt(actionID) ?: 0
        } catch (_: Exception) {
            1
        }

        var pendingIntent: PendingIntent? = null
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                this,
                notificationId,
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        /*        val pendingIntent = PendingIntent.getActivity(
                    this,
                    notificationId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )*/

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            "notification_channel", "web_app", NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(notificationChannel)

        val builder = NotificationCompat.Builder(this, "notification_channel")
            .setSmallIcon(R.drawable.logo_new)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setVibrate(longArrayOf(2000, 2000, 2000, 2000, 2000))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setCustomContentView(getCustomDesign(title, message))
        }

        notificationManager.notify(notificationId, builder.build())
    }

    private fun getCustomDesign(title: String, message: String): RemoteViews {
        val remoteViews = RemoteViews(packageName, R.layout.notification_layout)

        when (title) {
            "UserExpiringDocuments" -> remoteViews.setTextViewText(
                R.id.title,
                "User Expiring Documents"
            )

            "VehicleExpiringDocuments" -> remoteViews.setTextViewText(
                R.id.title,
                "Vehicle Expiring Documents"
            )

            "ExpiredDocuments" -> remoteViews.setTextViewText(R.id.title, "Expired Documents")
            "WeeklyRotaApproval" -> remoteViews.setTextViewText(R.id.title, "Weekly Rota Approval")
            "DailyRotaApproval" -> remoteViews.setTextViewText(R.id.title, "Daily Rota Approval")
            "InvoiceReadyToReview" -> remoteViews.setTextViewText(
                R.id.title,
                "Invoice Ready ToReview"
            )

            "DriverDeductionWithAgreement" -> remoteViews.setTextViewText(
                R.id.title,
                "Driver Deduction With Agreement"
            )

            "ThirdPartyAccessRequestNotification" -> remoteViews.setTextViewText(
                R.id.title,
                "Third Party Access Request Notification"
            )
        }
//        remoteViews.setTextViewText(R.id.title, title)
        remoteViews.setTextViewText(R.id.descriptionXX, message)
        remoteViews.setImageViewResource(R.id.icons, R.drawable.logo_new)
        return remoteViews
    }

}