package com.clebs.celerity.models.response

data class ExpiringDocumentsResponseItem(
    val DocumentType: String,
    val DocumentTypeID: Int,
    val ExpiryDate: String,
    val LocationId: Int,
    val LocationLinkedEmail: String,
    val UserId: Int,
    val UsrDocId: Int,
    val UsrEmailId: String,
    val UsrFirstName: String,
    val UsrLastName: String,
    val UsrPhoneNo: String
)