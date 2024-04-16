package com.clebs.celerity.models.response


import com.google.gson.annotations.SerializedName

data class DriversBasicInformationModel(


    @SerializedName("EmailID")
    val emailID: String,
    @SerializedName("FirstName")
    val firstName: String,
    @SerializedName("LastName")
    val lastName: String,
    @SerializedName("LmID")
    val lmID: Int,
    @SerializedName("UserID")
    val userID: Int,
    @SerializedName("VmID")
    val vmID: Int,
    @SerializedName("VmRegNo")
    val vmRegNo: String?,
    @SerializedName("PhoneNumber")
    val PhoneNumber: String,
    @SerializedName("Address")
    val Address: String,
    @SerializedName("IsUsrProfileUpdateReqin90days")
    val IsUsrProfileUpdateReqin90days: Boolean,
    @SerializedName("IsLeadDriver")
    val IsLeadDriver: Boolean,
    @SerializedName("IsThirdPartyChargeAccessAllowed")
    val IsThirdPartyChargeAccessAllowed: Boolean,
    @SerializedName("CurrentLocation")
    val currentlocation:String,
    @SerializedName("WorkingLocation")
    val workinglocation:String,
    @SerializedName("IsThirdPartyChargeAccessApplied")
val IsThirdPartyChargeAccessApplied:Boolean
)
