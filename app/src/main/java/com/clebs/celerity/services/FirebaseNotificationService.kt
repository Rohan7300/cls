package com.clebs.celerity.services

import android.util.Log
import com.clebs.celerity.models.response.SaveDeviceInformationRequest
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
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
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM Refreshed token: $token")
        var userID = 0
        if (Prefs.getInstance(applicationContext).userID.isEmpty() || Prefs.getInstance(
                applicationContext
            ).userID == " "
        )
            userID = 0
        else
            userID = Prefs.getInstance(applicationContext).userID.toInt()
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

}