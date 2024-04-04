package com.clebs.celerity.models.response

data class DocXX(
    val ActionByUserID: Int,
    val ActionByUserName: String,
    val ActionOn: String,
    val ActivityDetail: String,
    val ActivityFileName: String,
    val ActivityID: Int,
    val ActivityType: String,
    val CommentWithDateAndTime: String,
    val FilePath: FilePath,
    val HasAttachment: Boolean,
    val IsRead: Boolean,
    val TicketID: Int,
    val TicketTitle: String
)