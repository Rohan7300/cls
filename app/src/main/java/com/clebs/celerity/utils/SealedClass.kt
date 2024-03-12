package com.clebs.celerity.utils

import com.clebs.celerity.models.response.LoginResponse

sealed class LoginResult {
    data class Success(val response: LoginResponse) : LoginResult()
    object NoInternet : LoginResult()
    data class Error(val message: String) : LoginResult()
}

