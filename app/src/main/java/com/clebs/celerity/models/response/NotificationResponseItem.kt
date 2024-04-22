package com.clebs.celerity.models.response

data class NotificationResponseItem(
    val ActionToPerform: String,
    val IsNotificationActive: Boolean,
    val NotificationActionId: Int,
    val NotificationBody: String,
    val NotificationId: Int,
    val NotificationSendById: Int,
    val NotificationSentOn: String,
    val NotificationTitle: String,
    val NotificationUrl: String,
    val UserId: Int
)