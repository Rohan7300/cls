package com.clebs.celerity.utils

import android.R.id.edit
import android.content.Context
import android.content.SharedPreferences


class Prefs(context: Context) {

    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val USER_ACCESS_TOKEN = "celerity_shared_pref"
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
    var userID: String
        get() {
            return sharedPreferences.getString("userID", " ") ?: " "
        }
        set(value) {
            sharedPreferences.edit().putString("userID", value).apply()
        }
    fun save(key: String?, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
        sharedPreferences.edit().apply()
    }

    // ============================================//
    operator fun get(key: String?): String? {
        return sharedPreferences.getString(key, key)
    }
    fun setLastVisitedScreenId(context: Context,screenId: Int) {
        sharedPreferences.edit().putInt("last_screen_id", screenId).apply()
    }

    // Function to retrieve the last visited screen ID
    fun getLastVisitedScreenId(context: Context): Int {
        return sharedPreferences.getInt("last_screen_id", 0)
    }
    fun saveBoolean(key: String?, value: Boolean?) {
        sharedPreferences.edit().putBoolean(key, value!!).apply()
        sharedPreferences.edit().apply()
    }

    fun getBoolean(key: String?, keys: Boolean?): Boolean {
        return sharedPreferences.getBoolean(key, keys!!)
    }
    fun clearPreferences() {
        sharedPreferences.edit().clear().apply()
    }
}