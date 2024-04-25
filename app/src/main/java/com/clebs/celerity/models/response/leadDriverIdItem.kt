package com.clebs.celerity.models.response

data class leadDriverIdItem(
    val AddedOn: String,
    val DawId: Int,
    val DriverId: Int,
    val DwId: Int,
    val IsActive: Boolean,
    val IsCompleted: Boolean,
    val IsCurrent: Boolean,
    val IsRetraining: Boolean,
    val LeadDriverId: Int,
    val RaDriverQuesId: Any,
    val RaLeadDriverQuesId: Any,
    val DriverName:String,
    val RideAlongBasicDetailId: Int,
    val RtId: Int,
    val SecondLeadDriverId: Any,
    val TrainingStartDate: String,
    val TrainingTotalDays: Int,
    var isFeedBackFilled:Boolean?=false,
    var isQuestionsFilled:Boolean?=false
)