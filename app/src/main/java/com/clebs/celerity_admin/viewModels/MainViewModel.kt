package com.clebs.celerity_admin.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clebs.celerity_admin.database.User
import com.clebs.celerity_admin.models.CompanyListResponse
import com.clebs.celerity_admin.models.DDAMandateModel
import com.clebs.celerity_admin.models.DriverListResponseModel
import com.clebs.celerity_admin.models.GetReturnVmID
import com.clebs.celerity_admin.models.GetVehicleFuelLevelList
import com.clebs.celerity_admin.models.GetVehicleLocation
import com.clebs.celerity_admin.models.GetVehicleRequestType
import com.clebs.celerity_admin.models.GetvehicleOilLevelList
import com.clebs.celerity_admin.models.LastMileageInfo
import com.clebs.celerity_admin.models.LoginRequest
import com.clebs.celerity_admin.models.LoginResponse
import com.clebs.celerity_admin.models.RepoInfoModel
import com.clebs.celerity_admin.models.VehicleReturnModelList
import com.clebs.celerity_admin.models.WeekYearModel
import com.clebs.celerity_admin.models.WeeklyDefectChecksModel
import com.clebs.celerity_admin.repo.MainRepo
import kotlinx.coroutines.launch

class MainViewModel(private val repo: MainRepo) : ViewModel() {


    fun loginUser(requestModel: LoginRequest): MutableLiveData<LoginResponse?> {
        val responseLiveData = MutableLiveData<LoginResponse?>()

        viewModelScope.launch {
            val response = repo.loginUser(requestModel)
            if (response.failed) {
                responseLiveData.postValue(null)
            }
            if (!response.isSuccessful) {
                responseLiveData.postValue(null)
            } else {
                responseLiveData.postValue(response.body)
            }
        }

        return responseLiveData
    }

    fun GetCompanyListing(): MutableLiveData<CompanyListResponse?> {
        val responseLiveData = MutableLiveData<CompanyListResponse?>()

        viewModelScope.launch {
            val response = repo.Getcompanylist()
            if (response.failed) {
                responseLiveData.postValue(null)
            }
            if (!response.isSuccessful) {
                responseLiveData.postValue(null)
            } else {
                responseLiveData.postValue(response.body)
            }
        }
        return responseLiveData
    }

    fun GetDriverListing(): MutableLiveData<DriverListResponseModel?> {
        val responseLiveData = MutableLiveData<DriverListResponseModel?>()

        viewModelScope.launch {
            val response = repo.GetDriverlist()
            if (response.failed) {
                responseLiveData.postValue(null)
            }
            if (!response.isSuccessful) {
                responseLiveData.postValue(null)
            } else {
                responseLiveData.postValue(response.body)
            }
        }
        return responseLiveData
    }

    fun GetVehicleListing(): MutableLiveData<VehicleReturnModelList?> {
        val responseLiveData = MutableLiveData<VehicleReturnModelList?>()

        viewModelScope.launch {
            val response = repo.GetVehiclelist()
            if (response.failed) {
                responseLiveData.postValue(null)
            }
            if (!response.isSuccessful) {
                responseLiveData.postValue(null)
            } else {
                responseLiveData.postValue(response.body)
            }
        }
        return responseLiveData
    }

    fun GetVehicleLocationListing(): MutableLiveData<GetVehicleLocation?> {
        val responseLiveData = MutableLiveData<GetVehicleLocation?>()

        viewModelScope.launch {
            val response = repo.GetVehiclelocationList()
            if (response.failed) {
                responseLiveData.postValue(null)
            }
            if (!response.isSuccessful) {
                responseLiveData.postValue(null)
            } else {
                responseLiveData.postValue(response.body)
            }
        }
        return responseLiveData
    }

    fun GetVehiclefuelListing(): MutableLiveData<GetVehicleFuelLevelList?> {
        val responseLiveData = MutableLiveData<GetVehicleFuelLevelList?>()

        viewModelScope.launch {
            val response = repo.GetVehicleFuelList()
            if (response.failed) {
                responseLiveData.postValue(null)
            }
            if (!response.isSuccessful) {
                responseLiveData.postValue(null)
            } else {
                responseLiveData.postValue(response.body)
            }
        }
        return responseLiveData
    }

    fun GetVehicleOilListing(): MutableLiveData<GetvehicleOilLevelList?> {
        val responseLiveData = MutableLiveData<GetvehicleOilLevelList?>()

        viewModelScope.launch {
            val response = repo.GetVehicleOilList()
            if (response.failed) {
                responseLiveData.postValue(null)
            }
            if (!response.isSuccessful) {
                responseLiveData.postValue(null)
            } else {
                responseLiveData.postValue(response.body)
            }
        }
        return responseLiveData
    }

    fun GetDDAmandate(ddaid: String): MutableLiveData<DDAMandateModel?> {
        val responseLiveData = MutableLiveData<DDAMandateModel?>()

        viewModelScope.launch {
            val response = repo.GetVehicleDDAMandate(ddaid)
            if (response.failed) {
                responseLiveData.postValue(null)
            }
            if (!response.isSuccessful) {
                responseLiveData.postValue(null)
            } else {
                responseLiveData.postValue(response.body)
            }
        }
        return responseLiveData
    }

    fun GetDDAmandateReturn(ddaid: String): MutableLiveData<GetReturnVmID?> {
        val responseLiveData = MutableLiveData<GetReturnVmID?>()

        viewModelScope.launch {
            val response = repo.GetVehicleDDAMandateReturn(ddaid)
            if (response.failed) {
                responseLiveData.postValue(null)
            }
            if (!response.isSuccessful) {
                responseLiveData.postValue(null)
            } else {
                responseLiveData.postValue(response.body)
            }
        }
        return responseLiveData
    }

    fun GetRepoInfoModel(ddaid: String): MutableLiveData<RepoInfoModel?> {
        val responseLiveData = MutableLiveData<RepoInfoModel?>()

        viewModelScope.launch {
            val response = repo.GetRepoInfoModel(ddaid)
            if (response.failed) {
                responseLiveData.postValue(null)
            }
            if (!response.isSuccessful) {
                responseLiveData.postValue(null)
            } else {
                responseLiveData.postValue(response.body)
            }
        }
        return responseLiveData
    }

    fun GetVehicleRequestTypeList(): MutableLiveData<GetVehicleRequestType?> {
        val responseLiveData = MutableLiveData<GetVehicleRequestType?>()

        viewModelScope.launch {
            val response = repo.GetVehicleRequestType()
            if (response.failed) {
                responseLiveData.postValue(null)
            }
            if (!response.isSuccessful) {
                responseLiveData.postValue(null)
            } else {
                responseLiveData.postValue(response.body)
            }
        }
        return responseLiveData
    }

    fun GetemergencyContact(userid: Int): MutableLiveData<String?> {
        val responseLiveData = MutableLiveData<String?>()

        viewModelScope.launch {
            val response = repo.GetDAEmergencyContact(userid)
            if (response.failed) {
                responseLiveData.postValue(null)
            }
            if (!response.isSuccessful) {
                responseLiveData.postValue(null)
            } else {
                responseLiveData.postValue(response.body)
            }
        }
        return responseLiveData
    }

    fun GetlastMileageInfo(vmID: String): MutableLiveData<LastMileageInfo?> {
        val responseLiveData = MutableLiveData<LastMileageInfo?>()

        viewModelScope.launch {
            val response = repo.GetLastMileageInfo(vmID)
            if (response.failed) {
                responseLiveData.postValue(null)
            }
            if (!response.isSuccessful) {
                responseLiveData.postValue(null)
            } else {
                responseLiveData.postValue(response.body)
            }
        }
        return responseLiveData
    }

    fun GetCurrentWeekYear(): MutableLiveData<WeekYearModel?> {
        val responseLiveData = MutableLiveData<WeekYearModel?>()

        viewModelScope.launch {
            val response = repo.GetCurrentWeakAndYear()
            if (response.failed) {
                responseLiveData.postValue(null)
            }
            if (!response.isSuccessful) {
                responseLiveData.postValue(null)
            } else {
                responseLiveData.postValue(response.body)
            }
        }
        return responseLiveData
    }

    fun GetWeeklyDefectChecks(
        weekno: Double,
        year: Double,
        driverid: Double,
        lmid: Double,
        showdefects: Boolean
    ): MutableLiveData<WeeklyDefectChecksModel?> {
        val responseLiveData = MutableLiveData<WeeklyDefectChecksModel?>()

        viewModelScope.launch {
            val response = repo.GetWeeklyDefectCheckList(weekno, year, driverid, lmid, showdefects)
            if (response.failed) {
                responseLiveData.postValue(null)
            }
            if (!response.isSuccessful) {
                responseLiveData.postValue(null)
            } else {
                responseLiveData.postValue(response.body)
            }
        }
        return responseLiveData
    }
}