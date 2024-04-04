package com.clebs.celerity.models.response

data class FilePath(
    val AsyncState: Any,
    val CreationOptions: Int,
    val Exception: Any,
    val Id: Int,
    val IsCanceled: Boolean,
    val IsCompleted: Boolean,
    val IsCompletedSuccessfully: Boolean,
    val IsFaulted: Boolean,
    val Result: String,
    val Status: Int
)