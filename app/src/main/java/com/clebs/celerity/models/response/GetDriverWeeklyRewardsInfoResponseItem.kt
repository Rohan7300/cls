package com.clebs.celerity.models.response

data class GetDriverWeeklyRewardsInfoResponseItem(
    val AdminComment: Any?="-",
    val ColumnName: String? = "-",
    val DriverName: String? = "-",
    val LocationName: String? = "-",
    val PositionNumber: Double?=0.00,
    val RewardCC: Double?=0.00,
    val RewardCE: Double?=0.00,
    val RewardConcessions: Double?=0.00,
    val RewardDCR: Double?=0.00,
    val RewardDEX: Any?=null,
    val RewardDelivered: Double?=0.00,
    val RewardFocusArea: String? = "-",
    val RewardID: Double?=0.00,
    val RewardIsActive: Boolean,
    val RewardLmID: Double?=0.00,
    val RewardPHR: Double?=0.00,
    val RewardPOD: Double?=0.00,
    val RewardSC: Any?=null,
    val RewardTotalScore: Double?=0.00,
    val RewardUserID: Double?=0.00,
    val RewardWeek: Double?=0.00,
    val RewardYear: Double?=0.00,
    val StatusID: Double?=0.00,
    val StatusName: String? = "-",
    val TransporterID: String? = "-"
)