package com.clebs.celerity.models.response

data class GetDriverWeeklyRewardsInfoResponseItem(
    val AdminComment: Any?="-",
    val ColumnName: String? = "-",
    val DriverName: String? = "-",
    val LocationName: String? = "-",
    val PositionNumber: Int?=0,
    val RewardCC: Int?=0,
    val RewardCE: Int?=0,
    val RewardConcessions: Int?=0,
    val RewardDCR: Int?=0,
    val RewardDEX: Any?=null,
    val RewardDelivered: Int?=0,
    val RewardFocusArea: String? = "-",
    val RewardID: Int?=0,
    val RewardIsActive: Boolean,
    val RewardLmID: Int?=0,
    val RewardPHR: Int?=0,
    val RewardPOD: Int?=0,
    val RewardSC: Any?=null,
    val RewardTotalScore: Int?=0,
    val RewardUserID: Int?=0,
    val RewardWeek: Int?=0,
    val RewardYear: Int?=0,
    val StatusID: Int?=0,
    val StatusName: String? = "-",
    val TransporterID: String? = "-"
)