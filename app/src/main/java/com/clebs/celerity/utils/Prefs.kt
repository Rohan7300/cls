package com.clebs.celerity.utils

import android.R.id.edit
import android.content.Context
import android.content.SharedPreferences


class Prefs(context: Context) {

    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val USER_ACCESS_TOKEN = "sybyl_shared_pref"
        private var instance: Prefs? = null

        fun getInstance(context: Context): Prefs {
            return instance ?: synchronized(this) {
                instance ?: Prefs(context.applicationContext).also { instance = it }
            }
        }
    }

    init {
        sharedPreferences = context.getSharedPreferences(USER_ACCESS_TOKEN, Context.MODE_PRIVATE)
    }

    var accessToken: String
        get() {
            return sharedPreferences.getString(USER_ACCESS_TOKEN, " ") ?: " "
        }
        set(value) {
            sharedPreferences.edit().putString(USER_ACCESS_TOKEN, value).apply()
        }

    fun save(key: String?, value: String?) {
        sharedPreferences. edit().putString(key, value).apply()

    }

    // ============================================//
     fun getSaveStrings(key: String?) {
        sharedPreferences.edit().putString(key, key).apply()
    }


    fun clearPreferences() {
        sharedPreferences.edit().clear().apply()
    }
}