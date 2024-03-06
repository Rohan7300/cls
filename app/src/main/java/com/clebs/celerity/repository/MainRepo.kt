package com.clebs.celerity.repository

import android.util.Log
import com.clebs.celerity.models.requests.AddOnRouteInfoRequest
import com.clebs.celerity.models.response.DriversBasicInformationModel
import com.clebs.celerity.models.response.GetVechileInformationResponse
import com.clebs.celerity.models.response.GetsignatureInformation
import com.clebs.celerity.models.requests.LoginRequest
import com.clebs.celerity.models.requests.SaveBreakTimeRequest
import com.clebs.celerity.models.requests.SaveVechileDefectSheetRequest
import com.clebs.celerity.models.requests.UpdateDriverAgreementSignatureRequest
import com.clebs.celerity.models.response.LoginResponse
import com.clebs.celerity.models.requests.logoutModel
import com.clebs.celerity.models.response.BaseResponseTwo
import com.clebs.celerity.models.response.CheckIFTodayCheckIsDone
import com.clebs.celerity.models.response.DailyWorkInfoByIdResponse
import com.clebs.celerity.models.response.GetDriverBreakTimeInfoResponse
import com.clebs.celerity.models.response.GetDriverSignatureInformationResponse
import com.clebs.celerity.models.response.GetRideAlongRouteTypeInfoResponse
import com.clebs.celerity.models.response.GetRouteLocationInfoResponse
import com.clebs.celerity.models.response.GetVehicleDefectSheetInfoResponse
import com.clebs.celerity.models.response.GetVehicleImageUploadInfoResponse
import com.clebs.celerity.models.response.SaveVehDefectSheetResponse
import com.clebs.celerity.models.response.SimpleStatusMsgResponse
import com.clebs.celerity.network.ApiService
import com.google.gson.Gson
import okhttp3.MultipartBody
import java.lang.IllegalArgumentException

class MainRepo(private val ApiService: ApiService) {

    suspend fun loginUser(requestModel: LoginRequest): LoginResponse? {
        val response = ApiService.login(requestModel)

        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }

    suspend fun getVechileinformation(
        userID: Double,
        LmID: Double,
        VechileRegistrationno: String
    ): GetVechileInformationResponse? {
        val response = ApiService.getVehicleInformation(userID, LmID, VechileRegistrationno)
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }

    suspend fun getDriverSignatureInfo(userID: Double): GetsignatureInformation? {
        val response = ApiService.getDriverSignatureInfoforPolicy(userID)
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }

    suspend fun logout(): logoutModel? {
        val response = ApiService.Logout()
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }

    suspend fun GetDriversBasicInfo(userID: Double): DriversBasicInformationModel? {
        val response = ApiService.GetDriversBasicInfo(userID)
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }

    suspend fun CheckIFTodayCheckIsDone(): CheckIFTodayCheckIsDone? {
        val response = ApiService.CheckifTodayCheckIsDone()
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }


    suspend fun UseEmailAsUsername(userID: Double,emailAdddress:String) : BaseResponseTwo?{
        val response = ApiService.UseEmailAsUsername(userID,emailAdddress)
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }

    suspend fun UpdateDAprofileninetydays(userID: Double,emailAdddress:String,phonenumber:String) : BaseResponseTwo?{
        val response = ApiService.updateDAProfile90days(userID,emailAdddress,phonenumber)
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }

    suspend fun GetVehicleDefectSheetInfo(userID: Int): GetVehicleDefectSheetInfoResponse? {
        val response = ApiService.GetVehicleDefectSheetInfo(userID)
        if (response.isSuccessful)
            return response.body()
        return null
    }

    suspend fun SaveVehDefectSheet(vehicleDefectSheetInfoResponse: SaveVechileDefectSheetRequest): SaveVehDefectSheetResponse? {
        val response = ApiService.SaveVehDefectSheet(vehicleDefectSheetInfoResponse)
        if (response.isSuccessful)
            return response.body()
        return null
    }

    suspend fun GetVehicleInformation(
        userID: Int,
        vehRegNo: String
    ): GetVechileInformationResponse? {
        val response = ApiService.GetVehicleInformation(userID, vehRegNo)
        if (response.isSuccessful)
            return response.body()
        return null
    }

    suspend fun GetVehicleImageUploadInfo(userID: Int): GetVehicleImageUploadInfoResponse? {
        val response = ApiService.GetVehicleImageUploadInfo(userID)
        Log.d("GetVehicleImageUploadInfoRes : ", "$response")
        if (response.isSuccessful)
            return response.body()
        else if (response.code() == 404) {
            val res = response.errorBody()?.string()
            return Gson().fromJson(res, GetVehicleImageUploadInfoResponse::class.java)
        }
        return null
    }

    suspend fun uploadVehicleImage(
        userID: Int,
        image: MultipartBody.Part,
        type: Int
    ): SimpleStatusMsgResponse? {
        val response = when (type) {
            0 -> ApiService.UploadFaceMaskFile(userID, image)
            1 -> ApiService.uploadVehicleDashboardImage(userID, image)
            2 -> ApiService.uploadVehFrontImage(userID, image)
            3 -> ApiService.uploadVehNearSideImage(userID, image)
            4 -> ApiService.uploadVehRearImage(userID, image)
            5 -> ApiService.UploadVehicleOilLevelFile(userID, image)
            6 -> ApiService.uploadVehOffSideImage(userID, image)
            7 -> ApiService.UploadVehicleAddBlueFile(userID, image)
            else ->
                throw IllegalArgumentException()
        }
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error response body: $errorBody")
        }
        return null
    }

    suspend fun GetDailyWorkInfobyId(userID: Int):DailyWorkInfoByIdResponse?{
        val response = ApiService.GetDailyWorkInfobyId(userID)
        if (response.isSuccessful){
            return response.body()
        }else{
            val errorBody = response.errorBody()?.string()
            println("Error response body: $errorBody")
        }
        return null
    }

    suspend fun GetRouteLocationInfo(locID: Int): GetRouteLocationInfoResponse?{
        val response = ApiService.GetRouteLocationInfo(locID)
        if (response.isSuccessful){
            return response.body()
        }else{
            val errorBody = response.errorBody()?.string()
            println("Error response body: $errorBody")
        }
        return null
    }

    suspend fun GetRideAlongRouteTypeInfo(userID: Int): GetRideAlongRouteTypeInfoResponse?{
        val response = ApiService.GetRideAlongRouteTypeInfo(userID)
        if(response.isSuccessful)
            return response.body()
        else{
            val errorBody = response.errorBody()?.string()
            println("Error response body: $errorBody")
        }
        return null
    }
    suspend fun GetDriverSignatureInformation(userID: Int): GetDriverSignatureInformationResponse?{
        val response = ApiService.GetDriverSignatureInformation(userID)
        if(response.isSuccessful)
            return response.body()
        else{
            val errorBody = response.errorBody()?.string()
            println("Error response body: $errorBody")
        }
        return null
    }

    suspend fun UpdateDriverAgreementSignature(updateDriverSignatureRequest: UpdateDriverAgreementSignatureRequest):SimpleStatusMsgResponse?{
        val response = ApiService.UpdateDriverAgreementSignature(updateDriverSignatureRequest)
        if(response.isSuccessful)
            return response.body()
        else{
            val errorBody = response.errorBody()?.string()
            println("Error response body: $errorBody")
        }
        return null
    }

    suspend fun AddOnRouteInfo(addOnRouteInfoRequest: AddOnRouteInfoRequest):SimpleStatusMsgResponse?{
        val response = ApiService.AddOnRouteInfo(addOnRouteInfoRequest)
        if(response.isSuccessful){
            return response.body()
        }else{
            val errorBody = response.errorBody()?.string()
            println("Error response body: $errorBody")
        }
        return null
    }

    suspend fun SaveBreakTime(saveBreakTimeRequest: SaveBreakTimeRequest):SimpleStatusMsgResponse?{
        val response = ApiService.SaveBreakTime(saveBreakTimeRequest)
        if(response.isSuccessful){
            return response.body()
        }else{
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }

    suspend fun GetDriverBreakInfo(driverId:Int):GetDriverBreakTimeInfoResponse?{
        val response = ApiService.GetDriverBreakInfo(driverId)
        if(response.isSuccessful)
            return response.body()
        else{
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }
    suspend fun UpdateClockInTime(driverId:Int):SimpleStatusMsgResponse?{
        val response = ApiService.UpdateClockInTime(driverId)
        if(response.isSuccessful)
            return response.body()
        else{
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }
    suspend fun UpdateClockOutTime(driverId:Int):SimpleStatusMsgResponse?{
        val response = ApiService.UpdateClockOutTime(driverId)
        if(response.isSuccessful)
            return response.body()
        else{
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }



}