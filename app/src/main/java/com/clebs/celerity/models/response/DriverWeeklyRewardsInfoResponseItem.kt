package com.clebs.celerity.models.response

data class DriverWeeklyRewardsInfoResponseItem(
    val AdminComment: Any,
    val ColumnName: String,
    val DriverName: String,
    val LocationName: String,
    val PositionNumber: Int,
    val RewardCC: Double,
    val RewardCE: Double,
    val RewardConcessions: Double,
    val RewardDCR: Double,
    val RewardDEX: Double,
    val RewardDelivered: Int,
    val RewardFocusArea: String,
    val RewardID: Int,
    val RewardIsActive: Boolean,
    val RewardLmID: Int,
    val RewardPHR: Double,
    val RewardPOD: Double,
    val RewardSC: Any,
    val RewardTotalScore: Double,
    val RewardUserID: Int,
    val RewardWeek: Int,
    val RewardYear: Int,
    val StatusID: Int,
    val StatusName: String,
    val TransporterID: String
)