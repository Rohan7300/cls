package com.clebs.celerity.ViewModel

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clebs.celerity.models.requests.AddOnRideAlongRouteInfoRequest
import com.clebs.celerity.models.requests.AddOnRouteInfoRequest
import com.clebs.celerity.models.response.DriversBasicInformationModel
import com.clebs.celerity.models.response.GetVechileInformationResponse
import com.clebs.celerity.models.response.GetsignatureInformation
import com.clebs.celerity.models.requests.LoginRequest
import com.clebs.celerity.models.requests.SaveBreakTimeRequest
import com.clebs.celerity.models.requests.SaveVechileDefectSheetRequest
import com.clebs.celerity.models.requests.UpdateDriverAgreementSignatureRequest
import com.clebs.celerity.models.requests.UpdateProfileRequestBody
import com.clebs.celerity.models.response.LoginResponse
import com.clebs.celerity.models.requests.logoutModel
import com.clebs.celerity.models.response.BaseResponseTwo
import com.clebs.celerity.models.response.CheckIFTodayCheckIsDone
import com.clebs.celerity.models.response.DailyWorkInfoByIdResponse
import com.clebs.celerity.models.response.GetDriverBreakTimeInfoResponse
import com.clebs.celerity.models.response.GetDriverSignatureInformationResponse
import com.clebs.celerity.models.response.GetRideAlongDriversListResponse
import com.clebs.celerity.models.response.GetRideAlongRouteInfoByIdRes
import com.clebs.celerity.models.response.GetRideAlongRouteTypeInfoResponse
import com.clebs.celerity.models.response.GetRideAlongVehicleLists
import com.clebs.celerity.models.response.GetRouteInfoByIdRes
import com.clebs.celerity.models.response.GetRouteLocationInfoResponse
import com.clebs.celerity.models.response.GetVehicleDefectSheetInfoResponse
import com.clebs.celerity.models.response.GetVehicleImageUploadInfoResponse
import com.clebs.celerity.models.response.SaveVehDefectSheetResponse
import com.clebs.celerity.models.response.SimpleStatusMsgResponse
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.ui.App
import com.clebs.celerity.utils.Prefs
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class MainViewModel(
    private val repo: MainRepo
) : ViewModel() {
    val getVehicleDefectSheetInfoLiveData = MutableLiveData<GetVehicleDefectSheetInfoResponse?>()
    val SaveVehDefectSheetResponseLiveData = MutableLiveData<SaveVehDefectSheetResponse?>()
    val vechileInformationLiveData = MutableLiveData<GetVechileInformationResponse?>()
    val vehicleImageUploadInfoLiveData = MutableLiveData<GetVehicleImageUploadInfoResponse?>()
    val uploadVehicleImageLiveData = MutableLiveData<SimpleStatusMsgResponse?>()
    val livedataDailyWorkInfoByIdResponse = MutableLiveData<DailyWorkInfoByIdResponse?>()
    val liveDataRouteLocationResponse = MutableLiveData<GetRouteLocationInfoResponse>()
    val liveDataRideAlongRouteTypeInfo = MutableLiveData<GetRideAlongRouteTypeInfoResponse>()
    val liveDataGetDriverSignatureInformation =
        MutableLiveData<GetDriverSignatureInformationResponse?>()
    val livedataupdateDriverAgreementSignature = MutableLiveData<SimpleStatusMsgResponse?>()
    val livedataAddOnRouteInfo = MutableLiveData<SimpleStatusMsgResponse?>()
    val livedataSaveBreakTime = MutableLiveData<SimpleStatusMsgResponse?>()
    val livedataDriverBreakInfo = MutableLiveData<GetDriverBreakTimeInfoResponse?>()
    val livedataClockInTime = MutableLiveData<SimpleStatusMsgResponse?>()
    val livedataUpdateClockOutTime = MutableLiveData<SimpleStatusMsgResponse?>()
    val livedataGetRideAlongDriversList = MutableLiveData<GetRideAlongDriversListResponse?>()
    val livedataGetRideAlongVehicleLists = MutableLiveData<GetRideAlongVehicleLists?>()
    val livedataGetRouteInfoById = MutableLiveData<GetRouteInfoByIdRes?>()
    val livedataRideAlongSubmitApiRes = MutableLiveData<SimpleStatusMsgResponse?>()
    val updateprofilelivedata = MutableLiveData<SimpleStatusMsgResponse?>()
    val livedataRideAlongRouteInfoById = MutableLiveData<GetRideAlongRouteInfoByIdRes?>()
    val updateprofileregular = MutableLiveData<SimpleStatusMsgResponse?>()

    fun loginUser(requestModel: LoginRequest): MutableLiveData<LoginResponse?> {
        val responseLiveData = MutableLiveData<LoginResponse?>()

        viewModelScope.launch {
            val response = repo.loginUser(requestModel)
            responseLiveData.postValue(response)
        }

        return responseLiveData
    }

    fun updateProfilepassword(userID: Double, oldpass: String, newpass: String) {

        viewModelScope.launch {
            val response = repo.updateprofilePassword(userID, oldpass, newpass)
            updateprofilelivedata.postValue(response)
        }

    }

    fun updateprofileRegular(request:UpdateProfileRequestBody){

        viewModelScope.launch {
            val response=repo.updteprofileregular(request)
            updateprofileregular.postValue(response)
        }

    }

    fun getVichelinformationResponse(
        userID: Double,
        LmID: Double,
        VechileRegistrationno: String
    ): MutableLiveData<GetVechileInformationResponse?> {
        val responseLiveData = MutableLiveData<GetVechileInformationResponse?>()

        viewModelScope.launch {
            val response = repo.getVechileinformation(userID, LmID, VechileRegistrationno)
            responseLiveData.postValue(response)
        }

        return responseLiveData
    }

    fun getDriverSignatureInfo(userID: Double): MutableLiveData<GetsignatureInformation?> {
        val responseLiveData = MutableLiveData<GetsignatureInformation?>()

        viewModelScope.launch {
            val response = repo.getDriverSignatureInfo(userID)
            responseLiveData.postValue(response)
        }

        return responseLiveData

    }


    fun Logout(): MutableLiveData<logoutModel?> {
        val responseLiveData = MutableLiveData<logoutModel?>()

        viewModelScope.launch {
            val response = repo.logout()
            responseLiveData.postValue(response)
        }

        return responseLiveData

    }

    fun GetDriversBasicInformation(userID: Double): MutableLiveData<DriversBasicInformationModel?> {
        val responseLiveData = MutableLiveData<DriversBasicInformationModel?>()

        viewModelScope.launch {
            val response = repo.GetDriversBasicInfo(userID)
            responseLiveData.postValue(response)
        }

        return responseLiveData

    }

    fun CheckIFTodayCheckIsDone(): MutableLiveData<CheckIFTodayCheckIsDone?> {
        val responseLiveData = MutableLiveData<CheckIFTodayCheckIsDone?>()

        viewModelScope.launch {
            val response = repo.CheckIFTodayCheckIsDone()
            responseLiveData.postValue(response)
        }

        return responseLiveData

    }


    fun UseEmailasUsername(userID: Double, Email: String): MutableLiveData<BaseResponseTwo?> {
        val responseLiveData = MutableLiveData<BaseResponseTwo?>()

        viewModelScope.launch {
            val response = repo.UseEmailAsUsername(userID, Email)
            responseLiveData.postValue(response)
        }

        return responseLiveData

    }

    fun UpdateDAprofileninetydays(
        userID: Double,
        Email: String,
        phone: String
    ): MutableLiveData<BaseResponseTwo?> {
        val responseLiveData = MutableLiveData<BaseResponseTwo?>()

        viewModelScope.launch {
            val response = repo.UpdateDAprofileninetydays(userID, Email, phone)
            responseLiveData.postValue(response)
        }

        return responseLiveData

    }

    fun setLastVisitedScreenId(Context: Context, screenId: Int) {
        Prefs.getInstance(App.instance).setLastVisitedScreenId(Context, screenId)
    }

    fun getLastVisitedScreenId(Context: Context): Int {
        return Prefs.getInstance(App.instance).getLastVisitedScreenId(Context)
    }

    fun GetVehicleDefectSheetInfo(userID: Int) {
        viewModelScope.launch {
            getVehicleDefectSheetInfoLiveData.postValue(repo.GetVehicleDefectSheetInfo(userID))
        }
    }

    fun SaveVehDefectSheet(vehicleDefectSheetInfoResponse: SaveVechileDefectSheetRequest) {
        viewModelScope.launch {
            SaveVehDefectSheetResponseLiveData.postValue(
                repo.SaveVehDefectSheet(
                    vehicleDefectSheetInfoResponse
                )
            )
        }
    }

    fun GetVehicleInformation(userID: Int, vehRegNo: String) {
        viewModelScope.launch {
            vechileInformationLiveData.postValue(repo.GetVehicleInformation(userID, vehRegNo))
        }
    }

    fun GetVehicleImageUploadInfo(userID: Int) {
        viewModelScope.launch {
            vehicleImageUploadInfoLiveData.postValue(repo.GetVehicleImageUploadInfo(userID))
        }
    }

    fun uploadVehicleImage(userID: Int, image: MultipartBody.Part, type: Int) {
        viewModelScope.launch {
            uploadVehicleImageLiveData.postValue(repo.uploadVehicleImage(userID, image, type))
        }
    }

    fun GetDailyWorkInfoById(userID: Int) {
        viewModelScope.launch {
            livedataDailyWorkInfoByIdResponse.postValue(repo.GetDailyWorkInfobyId(userID))
        }
    }

    fun GetRouteLocationInfo(locID: Int) {
        viewModelScope.launch {
            liveDataRouteLocationResponse.postValue(repo.GetRouteLocationInfo(locID))
        }
    }

    fun GetRideAlongRouteTypeInfo(userID: Int) {
        viewModelScope.launch {
            liveDataRideAlongRouteTypeInfo.postValue(repo.GetRideAlongRouteTypeInfo(userID))
        }
    }


    fun GetDriverSignatureInformation(userID: Int) {
        viewModelScope.launch {
            val response = repo.GetDriverSignatureInformation(userID)
            liveDataGetDriverSignatureInformation.postValue(response)
        }
    }

    fun UpdateDriverAgreementSignature(updateDriverAgreementSignature: UpdateDriverAgreementSignatureRequest) {
        viewModelScope.launch {
            val response = repo.UpdateDriverAgreementSignature(updateDriverAgreementSignature)
            livedataupdateDriverAgreementSignature.postValue(response)
        }
    }

    fun AddOnRouteInfo(addOnRouteInfoRequest: AddOnRouteInfoRequest) {
        viewModelScope.launch {
            val response = repo.AddOnRouteInfo(addOnRouteInfoRequest)
            livedataAddOnRouteInfo.postValue(response)
        }
    }

    fun SaveBreakTime(saveBreakTimeRequest: SaveBreakTimeRequest) {
        viewModelScope.launch {
            val response = repo.SaveBreakTime(saveBreakTimeRequest)
            livedataSaveBreakTime.postValue(response)
        }
    }

    fun GetDriverBreakTimeInfo(driverId: Int) {
        viewModelScope.launch {
            val response = repo.GetDriverBreakInfo(driverId)
            livedataDriverBreakInfo.postValue(response)
        }
    }

    fun UpdateClockInTime(driverId: Int) {
        viewModelScope.launch {
            val response = repo.UpdateClockInTime(driverId)
            livedataClockInTime.postValue(response)
        }
    }

    fun UpdateClockOutTime(driverId: Int) {
        viewModelScope.launch {
            val response = repo.UpdateClockOutTime(driverId)
            livedataUpdateClockOutTime.postValue(response)
        }
    }

    fun GetRideAlongDriversList() {
        viewModelScope.launch {
            val response = repo.GetRideAlongDriversList()
            livedataGetRideAlongDriversList.postValue(response)
        }
    }

    fun GetRideAlongVehicleLists() {
        viewModelScope.launch {
            val response = repo.GetRideAlongVehicleLists()
            livedataGetRideAlongVehicleLists.postValue(response)
        }
    }

    fun GetRouteInfoById(routeID: Int) {
        viewModelScope.launch {
            val response = repo.GetRouteInfoById(routeID)
            livedataGetRouteInfoById.postValue(response)
        }
    }

    fun AddOnRideAlongRouteInfo(addOnRideAlongRouteInfoRequest: AddOnRideAlongRouteInfoRequest) {
        viewModelScope.launch {
            val result = runCatching {
                repo.AddOnRideAlongRouteInfo(addOnRideAlongRouteInfoRequest)
            }
            result.onSuccess { response ->
                livedataRideAlongSubmitApiRes.postValue(response)
            }
            result.onFailure { exception ->
                Log.e("AddOnRideAlongRouteInfo", "Error: ${exception.message}")
            }
        }
    }

    fun GetRideAlongRouteInfoById(routeID: Int, leadDriverId: Int) {
        viewModelScope.launch {
            val result = runCatching {
                repo.GetRideAlongRouteInfoById(routeID, leadDriverId)
            }
            result.onSuccess { response ->
                livedataRideAlongRouteInfoById.postValue(response)
            }
            result.onFailure { ex ->
                Log.e("GetRideAlongRouteInfoById Exception", "Error: ${ex.message}")
            }
        }
    }


}