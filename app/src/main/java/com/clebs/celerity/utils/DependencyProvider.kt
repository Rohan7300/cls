package com.clebs.celerity.utils

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.database.OSyncRepo
import com.clebs.celerity.database.OfflineSyncDB
import com.clebs.celerity.database.OfflineSyncEntity
import com.clebs.celerity.models.response.GetCompanySignedDocumentListResponseItem
import com.clebs.celerity.models.response.GetDriverDeductionHistoryResponse
import com.clebs.celerity.models.response.GetVehBreakDownInspectionInfobyDriverResponseItem
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo

object DependencyProvider {
    private var viewModelInstance: MainViewModel? = null
    private var apiService: ApiService? = null
    private var mainRepo: MainRepo? = null
    private var oSyncRepo: OSyncRepo? = null
    var dailyRotaNotificationShowing: Boolean = false
    var isComingBackFromCLSCapture: Boolean = false
    var isComingBackFromFaceScan: Boolean = false
    var isComingFromPolicyNotification: Boolean = false
    var currentUri: Uri? = null
    var insLevel: Int = 0
    var policyDocPDFURI: Uri? = null
    var notificationWatcher: MutableLiveData<Int> = MutableLiveData<Int>().apply {
        postValue(0)
    }
    var breakDownInspectionImageStage:Int = 0

    lateinit var currentBreakDownItemforInspection: GetVehBreakDownInspectionInfobyDriverResponseItem
    fun isBreakDownItemInitialize():Boolean{
        return ::currentBreakDownItemforInspection.isInitialized
    }

    var handlingDeductionNotification: Boolean = false
    var handlingRotaNotification: Boolean = false
    var isComingBackFromBreakDownActivity = false
    var handlingExpiredDialogNotification: Boolean = false
    var headerName = MutableLiveData<String>().apply{
        postValue("")
    }

    var osData: OfflineSyncEntity = OfflineSyncEntity()
    var getCompanySignedDocs: GetCompanySignedDocumentListResponseItem? = null
    var getCompanySignedDocsClicked: Boolean = false
    var currentDeductionHistory: GetDriverDeductionHistoryResponse? = null
    var brkStart: String = ""
    var brkEnd: String = ""
    var isComingToRaiseTicketforExpiredDocs: Boolean = false
    var blockCreateTicket: Boolean = false
    var notify: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply {
        postValue(false)
    }
    var comingFromViewTickets: Boolean = false
    var brkStartTime: String = ""
    var brkEndTime: String = ""

    fun getMainVM(owner: ViewModelStoreOwner): MainViewModel {

        viewModelInstance = ViewModelProvider(
            owner,
            MyViewModelFactory(getMainRepo())
        )[MainViewModel::class.java]

        return viewModelInstance!!
    }

    private fun getApiService(): ApiService {
        if (apiService == null)
            apiService = RetrofitService.getInstance().create(ApiService::class.java)
        return apiService!!
    }

    private fun getMainRepo(): MainRepo {
        if (mainRepo == null)
            mainRepo = MainRepo(getApiService())
        return mainRepo!!
    }

    fun offlineSyncRepo(context: Context): OSyncRepo {
        if (oSyncRepo == null)
            oSyncRepo = OSyncRepo(OfflineSyncDB.invoke(context))
        return oSyncRepo!!
    }

    //fun getPrefInstance()
}