package com.clebs.celerity.models.response

data class DocX(
    val ActionByUserID: Int,
    val ActionByUserName: String,
    val ActionOn: String,
    val ActivityDetail: String,
    val ActivityFileName: Any,
    val ActivityID: Int,
    val ActivityType: String,
    val CommentWithDateAndTime: String,
    val HasAttachment: Boolean,
    val IsRead: Boolean,
    val TicketID: Int,
    val TicketTitle: String
)