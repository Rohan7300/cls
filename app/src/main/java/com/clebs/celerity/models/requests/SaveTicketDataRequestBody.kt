package com.clebs.celerity.models.requests

data class SaveTicketDataRequestBody(
    val AssignedToUserIDs: List<Int>,
    val BadgeComment: String,
    val BadgeReturnedStatusId: Int,
    val DaTestDate: String,
    val DaTestTime: String,
    val Description: String,
    val DriverId: Int,
    val EstCompletionDate: String,
    val KeepDeptInLoop: Boolean,
    val NoofPeople: Int,
    val ParentCompanyID: Int,
    val PriorityId: Int,
    val RequestTypeId: Int,
    val TicketDepartmentId: Int,
    val TicketId: Int,
    val TicketUTRNo: String,
    val Title: String,
    val UserStatusId: Int,
    val UserTicketRegNo: String,
    val VmId: Int,
    val WorkingOrder: Int
)