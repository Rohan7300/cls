package com.clebs.celerity.utils

import android.content.Context
import android.content.SharedPreferences
import com.clebs.celerity.models.response.GetDriverRouteInfoByDateResponseItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Stack


class Prefs(context: Context) {

    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val USER_ACCESS_TOKEN = "sybyl_shared_pref"
        private const val USER_ACCESS_TOKEN_TWO = "sybyl_shared_prefs"
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

    var vmRegNo: String
        get() {
            return sharedPreferences.getString("vmRegNo", "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString("vmRegNo", value).apply()
        }

    var saveNnext: Int
        get() {
            return sharedPreferences.getInt("saveNnext", 0)
        }
        set(value) {
            sharedPreferences.edit().putInt("saveNnext", value).apply()
        }

    var currRideAlongID: Int
        get() {
            return sharedPreferences.getInt("currRideAlongID", 0)
        }
        set(value) {
            sharedPreferences.edit().putInt("currRideAlongID", value).apply()
        }

    var daWID: Int
        get() {
            return sharedPreferences.getInt("daWID", 0)
        }
        set(value) {
            sharedPreferences.edit().putInt("daWID", value).apply()
        }

    var currRtId: Int
        get() {
            return sharedPreferences.getInt("currRtId", 0)
        }
        set(value) {
            sharedPreferences.edit().putInt("currRtId", value).apply()
        }

    var quesID: Int
        get() = sharedPreferences.getInt("quesID", 0)
        set(value) = sharedPreferences.edit().putInt("quesID", value).apply()

    var submittedFeedback:Boolean
        get() = sharedPreferences.getBoolean("feedback",false)
        set(value) = sharedPreferences.edit().putBoolean("feedback",value).apply()

    var submittedRideAlong:Boolean
        get() = sharedPreferences.getBoolean("rideAlong",false)
        set(value) = sharedPreferences.edit().putBoolean("rideAlong",value).apply()

    var accessTokenclearquote: String
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

    var userName:String
        get() = sharedPreferences.getString("userName","")?:""
        set(value) = sharedPreferences.edit().putString("userName",value).apply()

    var qStage:Int
        get() = sharedPreferences.getInt("qStage",0)?:0
        set(value) = sharedPreferences.edit().putInt("qStage",value).apply()

    var canClockOut:Boolean
        get() = sharedPreferences.getBoolean("canClockOut",false)?:false
        set(value) = sharedPreferences.edit().putBoolean("canClockOut",value).apply()

    fun save(key: String?, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
        sharedPreferences.edit().apply()
    }

    // ============================================//
    operator fun get(key: String?): String? {
        return sharedPreferences.getString(key, key)
    }

    fun setLastVisitedScreenId(context: Context, screenId: Int) {
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
        fromJson(json, object : TypeToken<T>() {}.type)

    fun getNavigationHistory(): Stack<Int> {
        val history = sharedPreferences.getString("history", null)
        return if (history != null) {
            Gson().fromJson(history)
        } else {
            Stack()
        }
    }

    var vehicleLastMileage: Int?
        get() {
            return sharedPreferences.getInt("vehicleLastMileage", 0).takeIf { it != 0 } ?: 0
        }
        set(value) {
            sharedPreferences.edit().putInt("vehicleLastMileage", value ?: 0).apply()
        }

    fun clearNavigationHistory() {
        val emptyStack = Stack<Int>()
        sharedPreferences.edit().putString("history", Gson().toJson(emptyStack)).apply()
    }

    fun saveLocationID(locID: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("locID", locID)
        editor.apply()
    }

    fun getLocationID(): Int {
        return sharedPreferences.getInt("locID", 0)
    }

    fun saveCQSdkKey(key: String) {
        val editor = sharedPreferences.edit()
        editor.putString("CQ_KEY", key)
        editor.apply()
    }

    /*    fun saveSignatureInfo(jsonInfo:String){
            val editor = sharedPreferences.edit()
            editor.putString("SignaturInfo",jsonInfo)
            editor.apply()
        }

        fun getSignaturInfo():String?{
            return sharedPreferences.getString("SignaturInfo",null)
        }*/

    fun saveDriverRouteInfoByDate(data: GetDriverRouteInfoByDateResponseItem?) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val gson = Gson()
        val json = data.let { gson.toJson(it) }
        editor.putString("CurrRouteInfo", json)
        editor.apply()
    }
    fun getDriverRouteInfoByDate():GetDriverRouteInfoByDateResponseItem?{
        val gson = Gson()
        val data = sharedPreferences.getString("CurrRouteInfo",null)
        return gson.fromJson(data,GetDriverRouteInfoByDateResponseItem::class.java)?:null
    }


}