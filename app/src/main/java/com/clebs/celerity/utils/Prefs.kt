package com.clebs.celerity.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Stack


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

    fun saveNavigationHistory(fragmentStack: Stack<Int>) {
        val editor = sharedPreferences.edit()
        val historyJson = Gson().toJson(fragmentStack)
        editor.putString("history", historyJson)
        editor.apply()
    }

    private inline fun <reified T> Gson.fromJson(json: String): T =
        fromJson(json, object: TypeToken<T>() {}.type)

    fun getNavigationHistory(): Stack<Int> {
        val history = sharedPreferences.getString("history", null)
        return if (history != null) {
            Gson().fromJson(history)
        } else {
            Stack()
        }
    }

    fun saveLocationID(locID:Int){
        val editor = sharedPreferences.edit()
        editor.putInt("locID", locID)
        editor.apply()
    }

    fun getLocationID():Int{
        return sharedPreferences.getInt("locID",0)
    }

/*    fun saveSignatureInfo(jsonInfo:String){
        val editor = sharedPreferences.edit()
        editor.putString("SignaturInfo",jsonInfo)
        editor.apply()
    }

    fun getSignaturInfo():String?{
        return sharedPreferences.getString("SignaturInfo",null)
    }*/

}