package com.clebs.celerity.models.response

data class Invoice(
    val FileContent: String,
    val FileName: String,
    val FileType: String,
    val Week: Int,
    val Year: Int
)