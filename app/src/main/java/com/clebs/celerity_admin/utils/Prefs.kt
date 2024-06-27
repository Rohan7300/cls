package com.clebs.celerity_admin.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Stack


class Prefs(context: Context) {

    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val USER_ACCESS_TOKEN = "celerity_admin_shared_prefs"
        private const val USER_ACCESS_TOKEN_TWO = "celerity_shared_prefs"
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

    var vmId: Int
        get() = sharedPreferences.getInt("vmIdx", 0)
        set(value) = sharedPreferences.edit().putInt("vmIdx", value).apply()
    var scannedVmRegNo: String
        get() {
            return sharedPreferences.getString("scannedVmRegNo", "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString("scannedVmRegNo", value).apply()
        }
    var vmIdReturnveh: String
        get() {
            return sharedPreferences.getString("vmIdS", "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString("vmIdS", value).apply()
        }

    var inspectionID: String
        get() {
            return sharedPreferences.getString("inspectionID", "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString("inspectionID", value).apply()
        }

    var VmID: String
        get() {
            return sharedPreferences.getString("vm_ID", "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString("vm_ID", value).apply()
        }

    var saveNnext: Int
        get() {
            return sharedPreferences.getInt("saveNnext", 0)
        }
        set(value) {
            sharedPreferences.edit().putInt("saveNnext", value).apply()
        }
    var lmid: Int
        get() {
            return sharedPreferences.getInt("lmID", 0)
        }
        set(value) {
            sharedPreferences.edit().putInt("lmID", value).apply()
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

    var handbookId: Int
        get() {
            return sharedPreferences.getInt("currHandbook", 0)
        }
        set(value) {
            sharedPreferences.edit().putInt("currHandbook", value).apply()
        }

    var currLocationName: String
        get() {
            return sharedPreferences.getString("currLocationName", "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString("currLocationName", value).apply()
        }

    var workLocationName: String
        get() {
            return sharedPreferences.getString("workLocationName", "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString("workLocationName", value).apply()
        }

    var currLocationId: Int
        get() {
            return sharedPreferences.getInt("currLocationId", 0)
        }
        set(value) {
            sharedPreferences.edit().putInt("currLocationId", value).apply()
        }

    var workLocationId: Int
        get() {
            return sharedPreferences.getInt("workLocationId", 0)
        }
        set(value) {
            sharedPreferences.edit().putInt("workLocationId", value).apply()
        }

    var dob: String?
        get() {
            return sharedPreferences.getString("dob", null)
        }
        set(value) {
            sharedPreferences.edit().putString("dob", value).apply()
        }

    var isBirthdayCardShown: Boolean?
        get() {
            return sharedPreferences.getBoolean("isBirthdayCardShown", false)
        }
        set(value) {
            sharedPreferences.edit().putBoolean("isBirthdayCardShown", value?:false).apply()
        }

    var Isfirst: Boolean?
        get() {
            return sharedPreferences.getBoolean("Isfirst", false)
        }
        set(value) {
            sharedPreferences.edit().putBoolean("Isfirst", value?:false).apply()
        }
    var quesID: Int
        get() = sharedPreferences.getInt("quesID", 0)
        set(value) = sharedPreferences.edit().putInt("quesID", value).apply()

    var submittedFeedback: Boolean
        get() = sharedPreferences.getBoolean("feedback", false)
        set(value) = sharedPreferences.edit().putBoolean("feedback", value).apply()

    var submittedRideAlong: Boolean
        get() = sharedPreferences.getBoolean("rideAlong", false)
        set(value) = sharedPreferences.edit().putBoolean("rideAlong", value).apply()

    var submittedPrepardness: Boolean
        get() = sharedPreferences.getBoolean("submittedPrepardness", false)
        set(value) = sharedPreferences.edit().putBoolean("submittedPrepardness", value).apply()

    var submittedStartUp: Boolean
        get() = sharedPreferences.getBoolean("submittedStartUp", false)
        set(value) = sharedPreferences.edit().putBoolean("submittedStartUp", value).apply()

    var submittedGoingOn: Boolean
        get() = sharedPreferences.getBoolean("submittedGoingOn", false)
        set(value) = sharedPreferences.edit().putBoolean("submittedGoingOn", value).apply()

    var submittedDeliveryProcedures: Boolean
        get() = sharedPreferences.getBoolean("submittedDeliveryProcedures", false)
        set(value) = sharedPreferences.edit().putBoolean("submittedDeliveryProcedures", value)
            .apply()

    var submittedReturnToStation: Boolean
        get() = sharedPreferences.getBoolean("submittedReturnToStation", false)
        set(value) = sharedPreferences.edit().putBoolean("submittedReturnToStation", value).apply()

    var submittedFinalAssesmentFragment: Boolean
        get() = sharedPreferences.getBoolean("submittedFinalAssesmentFragment", false)
        set(value) = sharedPreferences.edit().putBoolean("submittedFinalAssesmentFragment", value)
            .apply()


    var accessTokenclearquote: String
        get() {
            return sharedPreferences.getString(USER_ACCESS_TOKEN, " ") ?: " "
        }
        set(value) {
            sharedPreferences.edit().putString(USER_ACCESS_TOKEN, value).apply()
        }
    var clebUserId: String
        get() {
            return sharedPreferences.getString("clebUserId", " ") ?: " "
        }
        set(value) {
            sharedPreferences.edit().putString("clebUserId", value).apply()
        }
    var days: String
        get() {
            return sharedPreferences.getString("90days", " ") ?: " "
        }
        set(value) {
            sharedPreferences.edit().putString("90days", value).apply()
        }



    var userName: String
        get() = sharedPreferences.getString("userName", "") ?: ""
        set(value) = sharedPreferences.edit().putString("userName", value).apply()

    var qStage: Int
        get() = sharedPreferences.getInt("qStage", 0) ?: 0
        set(value) = sharedPreferences.edit().putInt("qStage", value).apply()

    var UsrCreatedOn: Int
        get() = sharedPreferences.getInt("UsrCreatedOn", 2024)
        set(value) = sharedPreferences.edit().putInt("UsrCreatedOn", value).apply()

    var canClockOut: Boolean
        get() = sharedPreferences.getBoolean("canClockOut", false) ?: false
        set(value) = sharedPreferences.edit().putBoolean("canClockOut", value).apply()


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


    fun updateInspectionStatus(isInspectionDone: Boolean) {
        val editor = sharedPreferences.edit()

        val lastInspectionDateTime = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
            Date()
        )
        editor.putBoolean("is_inspection_done", isInspectionDone)
        if (isInspectionDone) {
            editor.putString("last_inspection_datetime", lastInspectionDateTime)
        }
        editor.apply()
    }

    fun isInspectionDoneToday(): Boolean {
        val isInspectionDone = sharedPreferences.getBoolean("is_inspection_done", false)
        if (isInspectionDone) {
            val lastInspectionDateTimeString =
                sharedPreferences.getString("last_inspection_datetime", "")
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            Log.d("isInspectionDoneToday", "$currentDate \n$lastInspectionDateTimeString")

            return lastInspectionDateTimeString == currentDate
        } else {
            return false
        }
    }


}