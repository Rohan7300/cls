package com.clebs.celerity.models.requests

data class SubmitFinalQuestionairebyLeadDriverRequest(
    val Assessment: String,
    val DaDailyWorkId: Int,
    val LeadDriverId: Int,
    val QuestionId: Int,
    val RoutetId: Int
)