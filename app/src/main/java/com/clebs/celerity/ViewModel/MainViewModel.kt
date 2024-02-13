package com.clebs.celerity.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clebs.celerity.models.requests.GetDriverBasicInfoRequest
import com.clebs.celerity.models.response.DriversBasicInformationModel
import com.clebs.celerity.models.response.GetVechileInformationResponse
import com.clebs.celerity.models.response.GetsignatureInformation
import com.clebs.celerity.models.requests.LoginRequest
import com.clebs.celerity.models.response.LoginResponse
import com.clebs.celerity.models.requests.logoutModel
import com.clebs.celerity.models.response.CheckIFTodayCheckIsDone
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.ui.App
import com.clebs.celerity.utils.Prefs
import kotlinx.coroutines.launch

class MainViewModel(private val repo: MainRepo) : ViewModel() {
    fun loginUser(requestModel: LoginRequest): MutableLiveData<LoginResponse?> {
        val responseLiveData = MutableLiveData<LoginResponse?>()

        viewModelScope.launch {
            val response = repo.loginUser(requestModel)
            responseLiveData.postValue(response)
        }

        return responseLiveData
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

    fun GetDriversBasicInformation(GetdriverBasicInforequest: GetDriverBasicInfoRequest): MutableLiveData<DriversBasicInformationModel?> {
        val responseLiveData = MutableLiveData<DriversBasicInformationModel?>()

        viewModelScope.launch {
            val response = repo.GetDriversBasicInfo(GetdriverBasicInforequest)
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
    fun setLastVisitedScreenId(Context: Context, screenId: Int) {
        Prefs.getInstance(App.instance).setLastVisitedScreenId(Context, screenId)
    }

    fun getLastVisitedScreenId(Context: Context): Int {
        return Prefs.getInstance(App.instance).getLastVisitedScreenId(Context)
    }
}