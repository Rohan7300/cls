package com.clebs.celerity.ViewModel

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clebs.celerity.models.CashFlowPieChartResponse
import com.clebs.celerity.models.CashFlowPieChartResponseItem
import com.clebs.celerity.models.GetLastWeekScore
import com.clebs.celerity.models.GetWeekYear
import com.clebs.celerity.models.TicketDepartmentsResponse
import com.clebs.celerity.models.ViewFullScheduleResponse
import com.clebs.celerity.models.requests.AddOnRideAlongRouteInfoRequest
import com.clebs.celerity.models.requests.AddOnRouteInfoRequest
import com.clebs.celerity.models.response.DriversBasicInformationModel
import com.clebs.celerity.models.response.GetVechileInformationResponse
import com.clebs.celerity.models.response.GetsignatureInformation
import com.clebs.celerity.models.requests.LoginRequest
import com.clebs.celerity.models.requests.SaveBreakTimeRequest
import com.clebs.celerity.models.requests.SaveQuestionaireDeliverProceduresRequest
import com.clebs.celerity.models.requests.SaveQuestionaireOnGoingActivitiesRequest
import com.clebs.celerity.models.requests.SaveQuestionairePreparednessRequest
import com.clebs.celerity.models.requests.SaveQuestionaireReturnToDeliveryStationRequest
import com.clebs.celerity.models.requests.SaveQuestionaireStartupRequest
import com.clebs.celerity.models.requests.SaveTicketDataRequestBody
import com.clebs.celerity.models.requests.SaveVechileDefectSheetRequest
import com.clebs.celerity.models.requests.SubmitFinalQuestionairebyLeadDriverRequest
import com.clebs.celerity.models.requests.SubmitRideAlongDriverFeedbackRequest
import com.clebs.celerity.models.requests.UpdateDriverAgreementSignatureRequest
import com.clebs.celerity.models.requests.UpdateProfileRequestBody
import com.clebs.celerity.models.response.LoginResponse
import com.clebs.celerity.models.requests.logoutModel
import com.clebs.celerity.models.response.BaseResponseTwo
import com.clebs.celerity.models.response.CheckIFTodayCheckIsDone
import com.clebs.celerity.models.response.DailyWorkInfoByIdResponse
import com.clebs.celerity.models.response.DepartmentRequestResponse
import com.clebs.celerity.models.response.DownloadInvoicePDFResponse
import com.clebs.celerity.models.response.DownloadThirdPartyInvoicePDFResponse
import com.clebs.celerity.models.response.GetAvgScoreResponse
import com.clebs.celerity.models.response.GetDriverBreakTimeInfoResponse
import com.clebs.celerity.models.response.GetDriverRouteInfoByDateResponse
import com.clebs.celerity.models.response.GetDriverRouteInfoByDateResponseItem
import com.clebs.celerity.models.response.GetDriverSignatureInformationResponse
import com.clebs.celerity.models.response.GetRideAlongDriverFeedbackQuestionResponse
import com.clebs.celerity.models.response.GetRideAlongDriversListResponse
import com.clebs.celerity.models.response.GetRideAlongLeadDriverQuestionResponse
import com.clebs.celerity.models.response.GetRideAlongRouteInfoByIdRes
import com.clebs.celerity.models.response.GetRideAlongRouteTypeInfoResponse
import com.clebs.celerity.models.response.GetRideAlongVehicleLists
import com.clebs.celerity.models.response.GetRouteInfoByIdRes
import com.clebs.celerity.models.response.GetRouteLocationInfoResponse
import com.clebs.celerity.models.response.GetTicketCommentListNewResponse
import com.clebs.celerity.models.response.GetTicketCommentListResponse
import com.clebs.celerity.models.response.GetUserTicketDocumentsResponse
import com.clebs.celerity.models.response.GetUserTicketsResponse
import com.clebs.celerity.models.response.GetVehicleDefectSheetInfoResponse
import com.clebs.celerity.models.response.GetVehicleImageUploadInfoResponse
import com.clebs.celerity.models.response.RideAlongDriverInfoByDateResponse
import com.clebs.celerity.models.response.SaveCommentResponse
import com.clebs.celerity.models.response.SaveTicketResponse
import com.clebs.celerity.models.response.SaveVehDefectSheetResponse
import com.clebs.celerity.models.response.SimpleQuestionResponse
import com.clebs.celerity.models.response.SimpleStatusMsgResponse
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.ui.App
import com.clebs.celerity.utils.Prefs
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.ResponseBody

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
    val liveDatadriverInfobyRouteDate = MutableLiveData<GetDriverRouteInfoByDateResponse?>()
    val liveDataRideAlongDriverInfoByDateResponse =
        MutableLiveData<RideAlongDriverInfoByDateResponse?>()
    val liveDataQuestionairePreparedness = MutableLiveData<SimpleQuestionResponse?>()
    val liveDataQuestionaireStartup = MutableLiveData<SimpleQuestionResponse?>()
    val liveDataQuestionaireGoingOn = MutableLiveData<SimpleQuestionResponse?>()
    val liveDataQuestionareDeliveryProcedures = MutableLiveData<SimpleQuestionResponse?>()
    val liveDataQuestionareReturn = MutableLiveData<SimpleQuestionResponse?>()
    val liveDataFinalAssesment = MutableLiveData<SimpleQuestionResponse?>()
    val liveDataDeleteOnRideAlongRouteInfo = MutableLiveData<SimpleStatusMsgResponse?>()
    val liveDataDeleteOnRouteDetails = MutableLiveData<SimpleStatusMsgResponse?>()
    val liveDataSubmitRideAlongDriverFeedbackRequest = MutableLiveData<SimpleStatusMsgResponse?>()
    val liveDataGetRideAlongLeadDriverQuestion =
        MutableLiveData<GetRideAlongLeadDriverQuestionResponse?>()
    val liveDataDeleteBreakTime = MutableLiveData<SimpleStatusMsgResponse?>()
    val liveDataUpdateOnRouteInfo = MutableLiveData<SimpleStatusMsgResponse?>()
    val liveDataGetUserTickets = MutableLiveData<GetUserTicketsResponse?>()
    val liveDataTicketDepartmentsResponse = MutableLiveData<TicketDepartmentsResponse?>()
    val liveDataGetTicketRequestType = MutableLiveData<DepartmentRequestResponse?>()
    val liveDataSaveTicketResponse = MutableLiveData<SaveTicketResponse?>()
    val liveDataUploadTicketCommentAttachmentDoc = MutableLiveData<SimpleStatusMsgResponse?>()
    val liveDataGetUserTicketDocuments = MutableLiveData<GetUserTicketDocumentsResponse?>()

    val livedataAvgScoreResponse = MutableLiveData<GetAvgScoreResponse?>()
    val livedatalastweekresponse = MutableLiveData<GetLastWeekScore?>()
    val livedataCashFlowWeek = MutableLiveData<CashFlowPieChartResponse?>()
    val livedatagetweekyear = MutableLiveData<GetWeekYear?>()
    val livedatagetvechilescheduleinfo = MutableLiveData<ViewFullScheduleResponse?>()
    val liveDataGetTicketCommentList = MutableLiveData<GetTicketCommentListNewResponse?>()
    val liveDataUploadTicketAttachmentDoc = MutableLiveData<SimpleStatusMsgResponse?>()
    val liveDataSaveTicketComment = MutableLiveData<SaveCommentResponse?>()
    val livedatathirdpartyaccess = MutableLiveData<SimpleStatusMsgResponse?>()
    val livedataremovethirdpartyaccess = MutableLiveData<SimpleStatusMsgResponse?>()
    val liveDataDownloadInvoicePDF = MutableLiveData<DownloadInvoicePDFResponse?>()
    val liveDataDownloadThirdPartyInvoicePDF= MutableLiveData<DownloadThirdPartyInvoicePDFResponse?>()
    val liveDataDownloadSignedDAHandbook = MutableLiveData<ResponseBody?>()
    val liveDataDownloadSignedGDPRPOLICY = MutableLiveData<ResponseBody?>()
    val liveDataDownloadSignedServiceLevelAgreement = MutableLiveData<ResponseBody?>()
    val liveDataDownloadSignedPrivacyPolicy = MutableLiveData<ResponseBody?>()
    val liveDataDownloadSignedDAEngagement = MutableLiveData<ResponseBody?>()

    val liveDataGetRideAlongDriverFeedbackQuestion = MutableLiveData<GetRideAlongDriverFeedbackQuestionResponse?>()


    private val _navigateToSecondPage = MutableLiveData<Boolean>()
    val currentViewPage: MutableLiveData<Int> = MutableLiveData<Int>().apply {
        postValue(0)
    }


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

    fun updateprofileRegular(request: UpdateProfileRequestBody) {

        viewModelScope.launch {
            val response = repo.updteprofileregular(request)
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

    fun GetDriverRouteInfoByDate(driverId: Int) {
        viewModelScope.launch {
            val result = runCatching {
                repo.GetDriverRouteInfoByDate(driverId)
            }
            result.onSuccess { response ->
                liveDatadriverInfobyRouteDate.postValue(response)
            }
            result.onFailure { ex ->
                Log.e("GetDriverRouteInfoByDate Exception", "Error: ${ex.message}")
            }
        }
    }

    fun GetRideAlongDriverInfoByDate(driverId: Int) {
        viewModelScope.launch {
            val result = runCatching {
                repo.GetRideAlongDriverInfoByDate(driverId)
            }
            result.onSuccess { response ->
                liveDataRideAlongDriverInfoByDateResponse.postValue(response)
            }
            result.onFailure { ex ->
                Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
            }
        }
    }

    fun SaveQuestionairePreparedness(request: SaveQuestionairePreparednessRequest) {
        viewModelScope.launch {
            val result = runCatching {
                repo.SaveQuestionairePreparedness(request)
            }
            result.onSuccess { response ->
                liveDataQuestionairePreparedness.postValue(response)
            }
            result.onFailure { ex ->
                Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
            }
        }
    }

    fun SaveQuestionaireStartup(request: SaveQuestionaireStartupRequest) {
        viewModelScope.launch {
            val result = runCatching {
                repo.SaveQuestionaireStartup(request)
            }
            result.onSuccess { response ->
                liveDataQuestionaireStartup.postValue(response)
            }
            result.onFailure { ex ->
                Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
            }
        }
    }

    fun SaveQuestionaireGoingOn(request: SaveQuestionaireOnGoingActivitiesRequest) {
        viewModelScope.launch {
            val result = runCatching {
                repo.SaveQuestionaireOnGoingActivities(request)
            }
            result.onSuccess { response ->
                liveDataQuestionaireGoingOn.postValue(response)
            }
            result.onFailure { ex ->
                Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
            }
        }
    }

    fun SaveQuestionaireDelivery(request: SaveQuestionaireDeliverProceduresRequest) {
        viewModelScope.launch {
            val result = runCatching {
                repo.SaveQuestionaireDeliverProcedures(request)
            }
            result.onSuccess { response ->
                liveDataQuestionareDeliveryProcedures.postValue(response)
            }
            result.onFailure { ex ->
                Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
            }
        }
    }

    fun SaveQuestionaireReturnToDeliveryStation(request: SaveQuestionaireReturnToDeliveryStationRequest) {
        viewModelScope.launch {
            val result = runCatching {
                repo.SaveQuestionaireReturnToDeliveryStation(request)
            }
            result.onSuccess { response ->
                liveDataQuestionareReturn.postValue(response)
            }
            result.onFailure { ex ->
                Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
            }
        }
    }

    fun SaveQuestionaireFinalAssesment(request: SubmitFinalQuestionairebyLeadDriverRequest) {
        viewModelScope.launch {
            val result = runCatching {
                repo.SubmitFinalQuestionairebyLeadDriver(request)
            }
            result.onSuccess { response ->
                liveDataFinalAssesment.postValue(response)
            }
            result.onFailure { ex ->
                Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
            }
        }
    }

    fun DeleteOnRideAlongRouteInfo(routeID: Int) {
        viewModelScope.launch {
            val result = runCatching {
                repo.DeleteOnRideAlongRouteInfo(routeID)
            }
            result.onSuccess { response ->
                liveDataDeleteOnRideAlongRouteInfo.postValue(response)
            }
            result.onFailure { ex ->
                Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
            }
        }
    }

    fun DeleteOnRouteDetails(routeID: Int) {
        viewModelScope.launch {
            val result = runCatching {
                repo.DeleteOnRouteDetails(routeID)
            }
            result.onSuccess { response ->
                liveDataDeleteOnRouteDetails.postValue(response)
            }
            result.onFailure { ex ->
                Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
            }
        }
    }

    fun SubmitRideAlongDriverFeedback(request: SubmitRideAlongDriverFeedbackRequest) {
        viewModelScope.launch {
            val result = runCatching {
                repo.SubmitRideAlongDriverFeedback(request)
            }
            result.onSuccess { response ->
                liveDataSubmitRideAlongDriverFeedbackRequest.postValue(response)
            }
            result.onFailure { ex ->
                Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
            }
        }
    }

    fun GetRideAlongLeadDriverQuestion(
        driverId: Int,
        routeID: Int,
        leadDriverId: Int,
        daDailyWorkId: Int
    ) {
        viewModelScope.launch {
            val result = runCatching {
                repo.GetRideAlongLeadDriverQuestion(driverId, routeID, leadDriverId, daDailyWorkId)
            }
            result.onSuccess { response ->
                liveDataGetRideAlongLeadDriverQuestion.postValue(response)
            }
            result.onFailure { ex ->
                Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
            }
        }
    }

    fun DeleteBreakTime(
        dawDriverBreakId: Int
    ) {
        viewModelScope.launch {
            val result = runCatching {
                repo.DeleteBreakTime(dawDriverBreakId)
            }
            result.onSuccess { response ->
                liveDataDeleteBreakTime.postValue(response)
            }
            result.onFailure { ex ->
                Log.e("DeleteBreakTime Exception", "Error ${ex.message}")
            }
        }
    }

    fun UpdateOnRouteInfo(
        request: GetDriverRouteInfoByDateResponseItem
    ) {
        viewModelScope.launch {
            val result = runCatching {
                repo.UpdateOnRouteInfo(request)
            }
            result.onSuccess { res ->
                liveDataUpdateOnRouteInfo.postValue(res)
            }
            result.onFailure { ex ->
                Log.e("DeleteBreakTime Exception", "Error ${ex.message}")
            }
        }
    }

    fun GetUserTickets(
        userID: Int,
        department: Int? = null,
        startDate: String? = null,
        endDate: String? = null
    ) {
        viewModelScope.launch {
            val result = runCatching {
                repo.GetUserTickets(userID, department, startDate, endDate)
            }
            result.onSuccess { res ->
                liveDataGetUserTickets.postValue(res)
            }
            result.onFailure { ex ->
                Log.e("GetUserTickets", "Error ${ex.message}")
            }
        }
    }

    fun GetUserDepartmentList() {
        viewModelScope.launch {
            val result = runCatching {
                repo.GetUserDepartmentList()
            }
            result.onSuccess { res ->
                liveDataTicketDepartmentsResponse.postValue(res)
            }
            result.onFailure { ex ->
                Log.e("GetUserTickets", "Error ${ex.message}")
            }
        }
    }

    fun GetTicketRequestType(deptID: Int) {
        viewModelScope.launch {
            val result = runCatching {
                repo.GetTicketRequestType(deptID)
            }
            result.onSuccess {
                liveDataGetTicketRequestType.postValue(it)
            }
            result.onFailure { ex ->
                Log.e("GetTicketUserType", "Error ${ex.message}")
            }
        }
    }

    fun SaveTicketData(userID: Int, request: SaveTicketDataRequestBody) {
        viewModelScope.launch {
            val result = runCatching {
                repo.SaveTicketData(userID, request)
            }
            result.onSuccess { res ->
                liveDataSaveTicketResponse.postValue(res)
            }
            result.onFailure { ex ->
                Log.e("GetTicketUserType Ex", "Error ${ex.message}")
            }
        }
    }

    fun GetTicketCommentList(userID: Int, ticketId: Int) {
        viewModelScope.launch {
            val result = runCatching {
                repo.GetTicketCommentList(userID, ticketId)
            }
            result.onSuccess { res ->
                liveDataGetTicketCommentList.postValue(res)
            }
            result.onFailure { ex ->
                Log.e("GetTicketCommentList Ex", "Error ${ex.message}")
            }
        }
    }

    fun UploadTicketAttachmentDoc(
        userID: Int, ticketId: Int, file: MultipartBody.Part
    ) {
        viewModelScope.launch {
            val result = runCatching {
                repo.UploadTicketAttachmentDoc(userID, ticketId, file)
            }
            result.onSuccess { res ->
                liveDataUploadTicketAttachmentDoc.postValue(res)
            }
            result.onFailure { ex ->
                Log.e("UploadTicketAttachmentDoc Ex", "Error ${ex.message}")
            }
        }
    }

    fun SaveTicketComment(
        userID: Int,
        ticketId: Int,
        comment: String
    ) {
        viewModelScope.launch {
            val result = runCatching {
                repo.SaveTicketComment(userID, ticketId, comment)
            }
            result.onSuccess { res ->
                liveDataSaveTicketComment.postValue(res)
            }
            result.onFailure { ex ->
                Log.e("SaveTicketComment Ex", "Error ${ex.message}")
            }
        }
    }

    fun GetAVGscore(userID: Int, lmid: Int) {
        viewModelScope.launch {
            val result = runCatching {
                repo.GetAvgWeekScore(userID, lmid)
            }
            result.onSuccess { res ->
                livedataAvgScoreResponse.postValue(res)
            }
            result.onFailure { ex ->
                Log.e("GetTicketUserType", "Error ${ex.message}")
            }
        }
    }

    fun GetLastWeekSCore(userID: Int, lmid: Int, year: Int) {
        viewModelScope.launch {
            val result = runCatching {
                repo.GetLastWeekScrore(userID, lmid, year)
            }
            result.onSuccess { res ->
                livedatalastweekresponse.postValue(res)
            }
            result.onFailure { ex ->
                Log.e("GetTicketUserType", "Error ${ex.message}")
            }
        }
    }

    fun GetcashFlowWeek(userID: Int, companyFilter: Int, selYear: Int, selWeek: Int) {
        viewModelScope.launch {
            val result = runCatching {
                repo.GetCashFlowWeek(userID, companyFilter, selYear, selWeek)
            }
            result.onSuccess { res ->
                livedataCashFlowWeek.postValue(res)
            }
            result.onFailure { ex ->
                Log.e("GetTicketUserType", "Error ${ex.message}")
            }
        }
    }

    fun GetWeekAndYear() {
        viewModelScope.launch {
            val result = runCatching {
                repo.GetWeekYear()
            }
            result.onSuccess { res ->
                livedatagetweekyear.postValue(res)
            }
            result.onFailure { ex ->
                Log.e("GetTicketUserType", "Error ${ex.message}")
            }
        }
    }

    fun GetViewFullScheduleInfo(userID: Int, lmid: Int, Year: Int, Week: Int) {
        viewModelScope.launch {
            val result = runCatching {
                repo.GetVechileSchedule(userID, lmid, Year, Week)
            }
            result.onSuccess { res ->
                livedatagetvechilescheduleinfo.postValue(res)
            }
            result.onFailure { ex ->
                Log.e("GetTicketUserType", "Error ${ex.message}")
            }
        }
    }

    fun UploadTicketCommentAttachmentDoc(
        userID: Int,
        ticketCommentID: Int,
        file: MultipartBody.Part
    ) {
        viewModelScope.launch {
            val result = runCatching {
                repo.UploadTicketCommentAttachmentDoc(userID, ticketCommentID, file)
            }
            result.onSuccess { res ->
                liveDataUploadTicketCommentAttachmentDoc.postValue(res)
            }
            result.onFailure { ex ->
                Log.e("UploadTicketCommentAttachmentDoc", "Error ${ex.message}")
            }
        }
    }

    fun GetUserTicketDocuments(
        userID: Int,
        ticketId: Int
    ) {
        viewModelScope.launch {
            val result = runCatching {
                repo.GetUserTicketDocuments(userID, ticketId)
            }
            result.onSuccess { res ->
                liveDataGetUserTicketDocuments.postValue(res)
            }
            result.onFailure { ex ->
                Log.e("GetUserTicketDocument", "Error ${ex.message}")
            }
        }
    }

    fun GetThirdPartyAccess(userID: Int) {
        viewModelScope.launch {
            val result = runCatching {
                repo.GetThirdPartyAccess(userID)
            }
            result.onSuccess { res ->
                livedatathirdpartyaccess.postValue(res)
            }
            result.onFailure { ex ->
                Log.e("GetTicketUserType", "Error ${ex.message}")
            }
        }
    }

    fun RemoveThirdPartyAccess(userID: Int) {
        viewModelScope.launch {
            val result = runCatching {
                repo.RemoveThirdPartyAccess(userID)
            }
            result.onSuccess { res ->
                livedataremovethirdpartyaccess.postValue(res)
            }
            result.onFailure { ex ->
                Log.e("GetTicketUserType", "Error ${ex.message}")
            }
        }
    }

    fun DownloadInvoicePDF(
        userID: Int, selYear: Int
    ) {
        viewModelScope.launch {
            val result = runCatching {
                repo.DownloadInvoicePDF(userID, selYear)
            }
            result.onSuccess { res ->
                liveDataDownloadInvoicePDF.postValue(res)
            }
            result.onFailure { ex ->
                Log.e("DownloadInvoicePDF ", "Error ${ex.message}")
            }
        }
    }
    fun DownloadThirdPartyInvoicePDF(
        userID: Int, selYear: Int
    ) {
        viewModelScope.launch {
            val result = runCatching {
                repo.DownloadThirdPartyInvoicePDF(userID, selYear)
            }
            result.onSuccess { res ->
                liveDataDownloadThirdPartyInvoicePDF.postValue(res)
            }
            result.onFailure { ex ->
                Log.e("DownloadInvoicePDF ", "Error ${ex.message}")
            }
        }
    }

    fun DownloadSignedDAHandbook(
        handbookId:Int
    ){
        viewModelScope.launch {
            val result = runCatching {
                repo.DownloadSignedDAHandbook(handbookId)
            }
            result.onSuccess { res->
                liveDataDownloadSignedDAHandbook.postValue(res)
            }
            result.onFailure { ex->
                Log.e("Download Signed Da Handbook","Error ${ex.message}")
            }
        }
    }
    fun DownloadSignedGDPRPOLICY(
        handbookId:Int
    ){
        viewModelScope.launch {
            val result = runCatching {
                repo.DownloadSignedGDPRPOLICY(handbookId)
            }
            result.onSuccess { res->
                liveDataDownloadSignedGDPRPOLICY.postValue(res)
            }
            result.onFailure { ex->
                Log.e("Download Signed Da Handbook","Error ${ex.message}")
            }
        }
    }
    fun DownloadSignedServiceLevelAgreement(
        handbookId:Int
    ){
        viewModelScope.launch {
            val result = runCatching {
                repo.DownloadSignedServiceLevelAgreement(handbookId)
            }
            result.onSuccess { res->
                liveDataDownloadSignedServiceLevelAgreement.postValue(res)
            }
            result.onFailure { ex->
                Log.e("Download Signed Da Handbook","Error ${ex.message}")
            }
        }
    }
    fun DownloadSignedPrivacyPolicy(
        handbookId:Int
    ){
        viewModelScope.launch {
            val result = runCatching {
                repo.DownloadSignedPrivacyPolicy(handbookId)
            }
            result.onSuccess { res->
                liveDataDownloadSignedPrivacyPolicy.postValue(res)
            }
            result.onFailure { ex->
                Log.e("Download Signed Da Handbook","Error ${ex.message}")
            }
        }
    }
    fun DownloadSignedDAEngagement(
        handbookId:Int
    ){
        viewModelScope.launch {
            val result = runCatching {
                repo.DownloadSignedDAEngagement(handbookId)
            }
            result.onSuccess { res->
                liveDataDownloadSignedDAEngagement.postValue(res)
            }
            result.onFailure { ex->
                Log.e("Download Signed Da Handbook","Error ${ex.message}")
            }
        }
    }

    fun GetRideAlongDriverFeedbackQuestion(
        driverId: Int,
        routeID: Int,
        leadDriverId: Int,
        daDailyWorkId: Int
    ){
        viewModelScope.launch {
            val result = runCatching {
                repo.GetRideAlongDriverFeedbackQuestion(driverId,routeID,leadDriverId,daDailyWorkId)
            }
            result.onSuccess {res->
                liveDataGetRideAlongDriverFeedbackQuestion.postValue(res)
            }
            result.onFailure { ex->
                Log.e("GetRideAlongDriverFeedBackQuestionRes ","${ex.message}")
            }

        }
    }

}