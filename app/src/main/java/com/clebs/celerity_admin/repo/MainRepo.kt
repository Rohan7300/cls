package com.clebs.celerity_admin.repo

import android.util.Log
import com.clebs.celerity_admin.models.CompanyListResponse
import com.clebs.celerity_admin.models.DDAMandateModel
import com.clebs.celerity_admin.models.DriverListResponseModel
import com.clebs.celerity_admin.models.GetAllDriversInspectionListResponse
import com.clebs.celerity_admin.models.GetAllVehicleInspectionListResponse
import com.clebs.celerity_admin.models.GetReturnVmID
import com.clebs.celerity_admin.models.GetVehOilLevelListResponse
import com.clebs.celerity_admin.models.GetVehWindScreenConditionStatusResponse
import com.clebs.celerity_admin.models.GetVehicleDamageWorkingStatusResponse
import com.clebs.celerity_admin.models.GetVehicleFuelLevelList
import com.clebs.celerity_admin.models.GetVehicleLocation
import com.clebs.celerity_admin.models.GetVehicleRequestType
import com.clebs.celerity_admin.models.GetWeeklyDefectCheckImagesResponse
import com.clebs.celerity_admin.models.GetvehicleOilLevelList
import com.clebs.celerity_admin.models.LastMileageInfo
import com.clebs.celerity_admin.models.LoginRequest
import com.clebs.celerity_admin.models.LoginResponse
import com.clebs.celerity_admin.models.OtherDefectCheckImagesInDropBoxResponse
import com.clebs.celerity_admin.models.RepoInfoModel
import com.clebs.celerity_admin.models.SaveDefectSheetWeeklyOSMCheckRequest
import com.clebs.celerity_admin.models.ResponseInspectionDone
import com.clebs.celerity_admin.models.SaveInspectionRequestBody
import com.clebs.celerity_admin.models.SaveVehicleBreakDownInspectionRequest
import com.clebs.celerity_admin.models.SucessStatusMsgResponse
import com.clebs.celerity_admin.models.VehicleReturnModelList
import com.clebs.celerity_admin.models.WeekYearModel
import com.clebs.celerity_admin.models.WeeklyDefectChecksModel
import com.clebs.celerity_admin.models.basemodel.SimpleNetworkResponse
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.utils.DefectFileType
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Part
import retrofit2.http.Query

class MainRepo(private val ApiService: ApiService) {
    private inline fun <T> safeApiCall(apiCall: () -> Response<T>): SimpleNetworkResponse<T> {
        return try {
            SimpleNetworkResponse.success(apiCall.invoke())
        } catch (e: Exception) {
            Log.d("SafeException", "$e")
            SimpleNetworkResponse.failure(e)
        }
    }

    suspend fun loginUser(requestModel: LoginRequest): SimpleNetworkResponse<LoginResponse> {
        return safeApiCall {
            ApiService.login(requestModel)
        }
    }


    suspend fun Getcompanylist(): SimpleNetworkResponse<CompanyListResponse> {
        return safeApiCall {
            ApiService.GetCompanyList()
        }
    }

    suspend fun GetDriverlist(): SimpleNetworkResponse<DriverListResponseModel> {
        return safeApiCall {
            ApiService.GetDriverList()
        }
    }

    suspend fun GetVehiclelist(): SimpleNetworkResponse<VehicleReturnModelList> {
        return safeApiCall {
            ApiService.GetvehicleList()
        }
    }

    suspend fun GetVehiclelocationList(): SimpleNetworkResponse<GetVehicleLocation> {
        return safeApiCall {
            ApiService.GetVehicleLocationList()
        }
    }

    suspend fun GetVehicleFuelList(): SimpleNetworkResponse<GetVehicleFuelLevelList> {
        return safeApiCall {
            ApiService.GetVehicleFuelList()
        }
    }

    suspend fun GetVehicleOilList(): SimpleNetworkResponse<GetvehicleOilLevelList> {
        return safeApiCall {
            ApiService.GetVehicleOilList()
        }
    }

    suspend fun GetVehicleDDAMandate(ddaid: String): SimpleNetworkResponse<DDAMandateModel> {
        return safeApiCall {
            ApiService.GetDDAALlocatedVehandLocation(ddaid)
        }
    }

    suspend fun GetVehicleDDAMandateReturn(ddaid: String): SimpleNetworkResponse<GetReturnVmID> {
        return safeApiCall {
            ApiService.GetCurrentAllocatedDAforReturnVehicle(ddaid)
        }
    }

    suspend fun GetRepoInfoModel(ddaid: String): SimpleNetworkResponse<RepoInfoModel> {
        return safeApiCall {
            ApiService.GetvehicleRepoInfo(ddaid)
        }
    }

    suspend fun GetVehicleRequestType(): SimpleNetworkResponse<GetVehicleRequestType> {
        return safeApiCall {
            ApiService.GetVehicleRequestType()
        }
    }

    suspend fun GetDAEmergencyContact(
        userID: Int
    ): SimpleNetworkResponse<String> {
        return safeApiCall {
            ApiService.GetDAEmergencyContact(userID)
        }
    }

    suspend fun GetLastMileageInfo(
        vmid: String
    ): SimpleNetworkResponse<LastMileageInfo> {
        return safeApiCall {
            ApiService.GetVehicleLastMileage(vmid)
        }
    }

    suspend fun GetCurrentWeakAndYear(

    ): SimpleNetworkResponse<WeekYearModel> {
        return safeApiCall {
            ApiService.getCurrentWeekAndYear()
        }
    }


    suspend fun GetWeeklyDefectCheckList(
        weekno: Double, year: Double, driverid: Double, lmid: Double, showdefects: Boolean

    ): SimpleNetworkResponse<WeeklyDefectChecksModel> {
        return safeApiCall {
            ApiService.getWeeklyDefectCHeckList(weekno, year, driverid, lmid, showdefects)
        }
    }

    suspend fun GetWeeklyDefectCheckImages(vdhCheckId: Int): SimpleNetworkResponse<GetWeeklyDefectCheckImagesResponse> {
        return safeApiCall {
            ApiService.GetWeeklyDefectCheckImages(vdhCheckId)
        }
    }

    suspend fun GetVehOilLevelList(): SimpleNetworkResponse<GetVehOilLevelListResponse> {
        return safeApiCall {
            ApiService.GetVehOilLevelList()
        }
    }

    suspend fun GetVehWindScreenConditionStatus(): SimpleNetworkResponse<GetVehWindScreenConditionStatusResponse> {
        return safeApiCall {
            ApiService.GetVehWindScreenConditionStatus()
        }
    }

    suspend fun UploadVehOSMDefectChkFile(
        vdhDefectCheckId: Int,
        fileType: DefectFileType,
        date: String,
        image: MultipartBody.Part
    ): SimpleNetworkResponse<SucessStatusMsgResponse> {
        return safeApiCall {
            ApiService.UploadVehOSMDefectChkFile(vdhDefectCheckId, fileType.toString(), date, image)
        }
    }

    suspend fun UploadOtherPictureOfPartsFile(
        vdhDefectCheckId: Int,
        fileType: DefectFileType,
        date: String,
        image: MultipartBody.Part
    ): SimpleNetworkResponse<SucessStatusMsgResponse> {
        return safeApiCall {
            ApiService.UploadOtherPictureOfPartsFile(
                vdhDefectCheckId,
                fileType.toString(),
                date,
                image
            )
        }
    }

    suspend fun SaveDefectSheetWeeklyOSMCheck(
        body: SaveDefectSheetWeeklyOSMCheckRequest
    ): SimpleNetworkResponse<SucessStatusMsgResponse> {
        return safeApiCall {
            ApiService.SaveDefectSheetWeeklyOSMCheck(body)
        }
    }

    suspend fun SaveVehWeeklyDefectSheetInspectionInfo(
        saveInspectionRequestBody: SaveInspectionRequestBody
    ): SimpleNetworkResponse<SucessStatusMsgResponse> {
        return safeApiCall {
            ApiService.SaveVehWeeklyDefectSheetInspectionInfo(saveInspectionRequestBody)
        }
    }

    suspend fun GetVehWeeklyDefectSheetInspectionInfo(
        vdhCheckId: Int
    ): SimpleNetworkResponse<ResponseInspectionDone> {
        return safeApiCall {
            ApiService.GetVehWeeklyDefectSheetInspectionInfo(vdhCheckId)
        }
    }

    suspend fun Uploadvideo360(
        vdhDefectCheckId: Int,
        date: String,
        @Part file: MultipartBody.Part
    ): SimpleNetworkResponse<SucessStatusMsgResponse> {
        return safeApiCall {
            ApiService.UploadVideo360(vdhDefectCheckId, date, file)
        }
    }

    suspend fun GetOtherDefectCheckImagesInDropBox(
        vdhDefectCheckId: Int,
        fileType: String
    ): SimpleNetworkResponse<OtherDefectCheckImagesInDropBoxResponse> {
        return safeApiCall {
            ApiService.GetOtherDefectCheckImagesInDropBox(vdhDefectCheckId, fileType)
        }
    }
    suspend fun GetLocationListbyUserId(
       userID: Double
    ): SimpleNetworkResponse<GetVehicleLocation> {
        return safeApiCall {
            ApiService.GetLocationListbyUserId(userID)
        }
    }
    suspend fun GetAllVehicleInspectionList():SimpleNetworkResponse<GetAllVehicleInspectionListResponse>{
        return safeApiCall {
            ApiService.GetAllVehicleInspectionList()
        }
    }
    suspend fun GetAllDriversInspectionList():SimpleNetworkResponse<GetAllDriversInspectionListResponse>{
        return safeApiCall {
            ApiService.GetAllDriversInspectionList()
        }
    }
    suspend fun GetVehicleDamageWorkingStatus():SimpleNetworkResponse<GetVehicleDamageWorkingStatusResponse>{
        return safeApiCall {
            ApiService.GetVehicleDamageWorkingStatus()
        }
    }

    suspend fun SaveVehicleBreakDownInspectionInfo(request: SaveVehicleBreakDownInspectionRequest):SimpleNetworkResponse<SucessStatusMsgResponse>{
        return safeApiCall {
            ApiService.SaveVehicleBreakDownInspectionInfo(request)
        }
    }
}