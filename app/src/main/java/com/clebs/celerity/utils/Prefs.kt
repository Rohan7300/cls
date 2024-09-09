package com.clebs.celerity.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.clebs.celerity.models.response.Doc
import com.clebs.celerity.models.response.GetDAVehicleExpiredDocumentsResponse
import com.clebs.celerity.models.response.GetDriverRouteInfoByDateResponseItem
import com.clebs.celerity.models.response.Invoice
import com.clebs.celerity.models.response.InvoiceX
import com.clebs.celerity.models.response.InvoiceXX
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Stack


class Prefs(context: Context) {

    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val USER_ACCESS_TOKEN = "celerity_shared_prefs"
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

    var thridPartyAcess: Boolean
        get() {
            return sharedPreferences.getBoolean("thirdPartyAccess", false)
        }
        set(value) = sharedPreferences.edit().putBoolean("thirdPartyAccess", value).apply()

    var vmRegNo: String
        get() {
            return sharedPreferences.getString("vmRegNo", "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString("vmRegNo", value).apply()
        }

    var Isfirst: Boolean?
        get() {
            return sharedPreferences.getBoolean("Isfirst", true)
        }
        set(value) {
            sharedPreferences.edit().putBoolean("Isfirst", value ?: false).apply()
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
    var isFirst: Boolean?
        get() {
            return sharedPreferences.getBoolean("isFirst", false)
        }
        set(value) {
            sharedPreferences.edit().putBoolean("isFirst", value ?: false).apply()
        }

    var isClientIDUplaoded: Boolean?
        get() {
            return sharedPreferences.getBoolean("isClientIDUplaoded", false)
        }
        set(value) {
            sharedPreferences.edit().putBoolean("isClientIDUplaoded", value ?: false).apply()
        }
    var inspectionID: String
        get() {
            return sharedPreferences.getString("inspectionID", "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString("inspectionID", value).apply()
        }
    var inspectionIDForBreakDown: String
        get() {
            return sharedPreferences.getString("inspectionIDForBreakDown", "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString("inspectionIDForBreakDown", value).apply()
        }
    var returnInspectionFirstTime: Boolean?
        get() {
            return sharedPreferences.getBoolean("returnInspectionFirstTime", false)
        }
        set(value) {
            sharedPreferences.edit().putBoolean("returnInspectionFirstTime", value ?: false).apply()
        }
    var baseVmID: String
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
            sharedPreferences.edit().putBoolean("isBirthdayCardShown", value ?: false).apply()
        }

    var isdone: Boolean?
        get() {
            return sharedPreferences.getBoolean("isdone", false)
        }
        set(value) {
            sharedPreferences.edit().putBoolean("isdone", value ?: false).apply()
        }

    var isstarted: Boolean?
        get() {
            return sharedPreferences.getBoolean("isstarted", false)
        }
        set(value) {
            sharedPreferences.edit().putBoolean("isstarted", value ?: false).apply()
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
            return sharedPreferences.getString("clebUserId", "0") ?: "0"
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
        val editor = sharedPreferences.edit()
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        editor.putString("last_screen_set_dt",currentDate)
        editor.putInt("last_screen_id", screenId)
        editor.apply()
    }

    // Function to retrieve the last visited screen ID
    fun getLastVisitedScreenId(context: Context): Int {
        val savedDate = sharedPreferences.getString("last_screen_set_dt",null)
        return if(isToday(savedDate))
        sharedPreferences.getInt("last_screen_id", 0)
        else
            0
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

    fun getDriverRouteInfoByDate(): GetDriverRouteInfoByDateResponseItem? {
        val gson = Gson()
        val data = sharedPreferences.getString("CurrRouteInfo", null)
        return gson.fromJson(data, GetDriverRouteInfoByDateResponseItem::class.java) ?: null
    }

    fun saveExpiredDocuments(data: GetDAVehicleExpiredDocumentsResponse) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(data)
        editor.putString("ExpiredDocuments", json)
        editor.apply()
    }

    fun getExpiredDocuments(): GetDAVehicleExpiredDocumentsResponse? {
        val gson = Gson()
        val data = sharedPreferences.getString("ExpiredDocuments", null)
        return gson.fromJson(data, GetDAVehicleExpiredDocumentsResponse::class.java) ?: null
    }

    fun saveCurrentTicket(data: Doc) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(data)
        editor.putString("CurrentTicket", json)
        editor.apply()
    }

    fun getCurrentTicket(): Doc? {
        val gson = Gson()
        val data = sharedPreferences.getString("CurrentTicket", null)
        return gson.fromJson(data, Doc::class.java) ?: null
    }

    fun saveInvoice(item: InvoiceXX) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(item)
        editor.putString("CurrInvoice", json)
        editor.apply()
    }

    fun getInvoice(): InvoiceXX? {
        val gson = Gson()
        val data = sharedPreferences.getString("CurrInvoice", null)
        return gson.fromJson(data, InvoiceXX::class.java) ?: null
    }



    fun saveInvoiceX(item: InvoiceXX) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(item)
        editor.putString("CurrInvoiceX", json)
        editor.apply()
    }

    fun getInvoiceX(): InvoiceXX? {
        val gson = Gson()
        val data = sharedPreferences.getString("CurrInvoiceX", null)
        return gson.fromJson(data, InvoiceXX::class.java) ?: null
    }

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

    fun updateRaiseTicketStatus() {
        val editor = sharedPreferences.edit()
        val lastRaiseTicketDateTime = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
            Date()
        )
        editor.putBoolean("ticket_raised_today", true)
        editor.putString("last_ticket_raised_datetime", lastRaiseTicketDateTime)
        editor.apply()
    }

    fun isTicketRaisedToday(): Boolean {
        val isTicketRaisedToday = sharedPreferences.getBoolean("ticket_raised_today", false)
        if (isTicketRaisedToday) {
            val lastTicketRaisedDateTimeString =
                sharedPreferences.getString("last_ticket_raised_datetime", "")
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            //   Log.d("isInspectionDoneToday", "$currentDate \n$lastTicketRaisedDateTimeString")

            return lastTicketRaisedDateTimeString == currentDate
        } else {
            return false
        }
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

    fun isFaceMaskAddedToday(): Boolean {
        val isFaceMaskAddedToday = sharedPreferences.getBoolean("isFaceMaskAddedToday", false)
        if (isFaceMaskAddedToday) {
            val lastInspectionDateTimeString =
                sharedPreferences.getString("last_fm_datetime", "")
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            Log.d("isFaceMaskAddedToday", "$currentDate \n $lastInspectionDateTimeString")

            return lastInspectionDateTimeString == currentDate
        } else {
            return false
        }
    }

    fun updateFaceMaskStatus(isFaceMaskAdded: Boolean) {
        val editor = sharedPreferences.edit()

        val lastInspectionDateTime = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
            Date()
        )
        editor.putBoolean("isFaceMaskAddedToday", isFaceMaskAdded)
        if (isFaceMaskAdded) {
            editor.putString("last_fm_datetime", lastInspectionDateTime)
        }
        editor.apply()
    }

    fun isWeeklyRotaApprovalCheckToday() :Boolean{
        val isWeeklyRotaChecked = sharedPreferences.getBoolean("isWeeklyRotaChecked", false)
        if (isWeeklyRotaChecked) {
            val lastCheckDateTime = sharedPreferences.getString("last_rota_check_time", "")
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            return lastCheckDateTime == currentDate
        }else {
            return false
        }
    }

    fun updateWeeklyRotaApprovalCheck(status:Boolean) {
        val editor = sharedPreferences.edit()
        val lastCheckDateTime = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        editor.putBoolean("isWeeklyRotaChecked", status)
        editor.putString("last_rota_check_time", lastCheckDateTime)
        editor.apply()
    }
    fun isPolicyCheckToday() :Boolean{
        val isWeeklyRotaChecked = sharedPreferences.getBoolean("isPolicyChecked", false)
        return if (isWeeklyRotaChecked) {
            val lastCheckDateTime = sharedPreferences.getString("last_policy_check_time", "")
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            lastCheckDateTime == currentDate
        }else {
            false
        }
    }

    fun updatePolicyCheckToday(status:Boolean) {
        val editor = sharedPreferences.edit()
        val lastCheckDateTime = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        editor.putBoolean("isPolicyChecked", status)
        editor.putString("last_policy_check_time", lastCheckDateTime)
        editor.apply()
    }

    var faceMaskUri: String?
        get() {
            val savedDate = sharedPreferences.getString("faceMaskUri_date", null)
            return if (isToday(savedDate)) {
                sharedPreferences.getString("faceMaskUri", null)
            } else {
                null
            }
        }
        set(value) {
            val editor = sharedPreferences.edit()
            if (value != null) {
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                editor.putString("faceMaskUri", value)
                editor.putString("faceMaskUri_date", currentDate)
            } else {
                editor.remove("faceMaskUri")
                editor.remove("faceMaskUri_date")
            }
            editor.apply()
        }

    var faceMaskRequired: Boolean
        get() {
            val savedDate = sharedPreferences.getString("faceMaskRequired_date", null)
            return if (isToday(savedDate)) {
                sharedPreferences.getBoolean("faceMaskRequired", false)
            } else {
                false
            }
        }
        set(value) {
            val editor = sharedPreferences.edit()
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            editor.putBoolean("faceMaskRequired", value)
            editor.putString("faceMaskRequired_date", currentDate)
            editor.apply()
        }

    var addBlueUri: String?
        get() {
            val savedDate = sharedPreferences.getString("addBlueUri_date", null)
            return if (isToday(savedDate)) {
                sharedPreferences.getString("addBlueUri", null)
            } else {
                null
            }
        }
        set(value) {
            val editor = sharedPreferences.edit()
            if (value != null) {
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                editor.putString("addBlueUri", value)
                editor.putString("addBlueUri_date", currentDate)
            } else {
                editor.remove("addBlueUri")
                editor.remove("addBlueUri_date")
            }
            editor.apply()
        }

    var addBlueRequired: Boolean
        get() {
            val savedDate = sharedPreferences.getString("addBlueRequired_date", null)
            return if (isToday(savedDate)) {
                sharedPreferences.getBoolean("addBlueRequired", false)
            } else {
                false
            }
        }
        set(value) {
            val editor = sharedPreferences.edit()
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            editor.putBoolean("addBlueRequired", value)
            editor.putString("addBlueRequired_date", currentDate)
            editor.apply()
        }

    var oilLevelUri: String?
        get() {
            val savedDate = sharedPreferences.getString("oilLevelUri_date", null)
            return if (isToday(savedDate)) {
                sharedPreferences.getString("oilLevelUri", null)
            } else {
                null
            }
        }
        set(value) {
            val editor = sharedPreferences.edit()
            if (value != null) {
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                editor.putString("oilLevelUri", value)
                editor.putString("oilLevelUri_date", currentDate)
            } else {
                editor.remove("oilLevelUri")
                editor.remove("oilLevelUri_date")
            }
            editor.apply()
        }

    var oilLevelRequired: Boolean
        get() {
            val savedDate = sharedPreferences.getString("oilLevelRequired_date", null)
            return if (isToday(savedDate)) {
                sharedPreferences.getBoolean("oilLevelRequired", false)
            } else {
                false
            }
        }
        set(value) {
            val editor = sharedPreferences.edit()
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            editor.putBoolean("oilLevelRequired", value)
            editor.putString("oilLevelRequired_date", currentDate)
            editor.apply()
        }

    fun isToday(dateString: String?): Boolean {
        if (dateString != null) {
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            return dateString == currentDate
        } else {
            return false
        }
    }

    var tokenExpiredOn: String
        get() = sharedPreferences.getString("TokenExpiredOn", "2024-06-19T06:49:38Z")
            ?: "2024-06-19T06:49:38Z"
        set(value) = sharedPreferences.edit().putString("TokenExpiredOn", value).apply()

    var VinNumber: String?
        get() = sharedPreferences.getString("VinNumber", null)
        set(value) = sharedPreferences.edit().putString("VinNumber", value).apply()

    var VehicleMake: String?
        get() = sharedPreferences.getString("VehicleMake", "Van") ?: "Van"
        set(value) = sharedPreferences.edit().putString("VehicleMake", value).apply()

    var VehicleBodyStyle: String
        get() = sharedPreferences.getString("VehicleBodyStyle", "Van") ?: "Van"
        set(value) = sharedPreferences.edit().putString("VehicleBodyStyle", value).apply()

    var VehicleModel: String
        get() = sharedPreferences.getString("VehicleModel", "Any Model") ?: "Any Model"
        set(value) = sharedPreferences.edit().putString("VehicleModel", value).apply()

    var VmCreatedDate: String?
        get() = sharedPreferences.getString("VmCreatedDate", null)
        set(value) = sharedPreferences.edit().putString("VmCreatedDate", value).apply()


    var destinationFragment: String?
        get() = sharedPreferences.getString("destinationFragment", "HomeFragment")
        set(value) = sharedPreferences.edit().putString("destinationFragment", value).apply()
    var actionToperform: String?
        get() = sharedPreferences.getString("actionToperform", "undef")
        set(value) = sharedPreferences.edit().putString("actionToperform", value).apply()
    var actionID: String?
        get() = sharedPreferences.getString("actionID", "0")
        set(value) = sharedPreferences.edit().putString("actionID", value).apply()
    var tokenUrl: String?
        get() = sharedPreferences.getString("tokenUrl", "undef")
        set(value) = sharedPreferences.edit().putString("tokenUrl", value).apply()

    var notificationId: String?
        get() = sharedPreferences.getString("notificationId", "0")
        set(value) = sharedPreferences.edit().putString("notificationId", value).apply()

    var isInspectionIDFailedToUpload: Boolean
        get() = sharedPreferences.getBoolean("isInspectionIDFailedToUpload", false)
        set(value) = sharedPreferences.edit().putBoolean("isInspectionIDFailedToUpload", value)
            .apply()

    var isBiometricChecked: Boolean
        get() = sharedPreferences.getBoolean("isBiometricChecked", false)
        set(value) = sharedPreferences.edit().putBoolean("isBiometricChecked", value).apply()

    var useBiometric: Boolean
        get() = sharedPreferences.getBoolean("useBiometric", true)
        set(value) = sharedPreferences.edit().putBoolean("useBiometric", value).apply()

    var inspectionDateTime:String?
        get() = sharedPreferences.getString("inspectionDateTime","0")
        set(value) = sharedPreferences.edit().putString("inspectionDateTime",value).apply()

}