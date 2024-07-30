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
import com.clebs.celerity.models.DownloadDriverOtherCompaniesPolicyResponse
import com.clebs.celerity.models.GetLastWeekScore
import com.clebs.celerity.models.GetWeekYear
import com.clebs.celerity.models.TicketDepartmentsResponse
import com.clebs.celerity.models.ViewFullScheduleResponse
import com.clebs.celerity.models.requests.AddOnRideAlongRouteInfoRequest
import com.clebs.celerity.models.requests.AddOnRouteInfoRequest
import com.clebs.celerity.models.requests.ApproveDaDailyRotaRequest
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
import com.clebs.celerity.models.requests.SaveQuestionareDrivingabilityassessment
import com.clebs.celerity.models.requests.SaveQuestionareStartupRequestNew
import com.clebs.celerity.models.requests.SaveTicketDataRequestBody
import com.clebs.celerity.models.requests.SaveVechileDefectSheetRequest
import com.clebs.celerity.models.requests.SaveVehicleInspectionInfo
import com.clebs.celerity.models.requests.SubmitFinalQuestionairebyLeadDriverRequest
import com.clebs.celerity.models.requests.SubmitRideAlongDriverFeedbackRequest
import com.clebs.celerity.models.requests.UpdateDeductioRequest
import com.clebs.celerity.models.requests.UpdateDriverAgreementSignatureRequest
import com.clebs.celerity.models.requests.UpdateProfileRequestBody
import com.clebs.celerity.models.response.LoginResponse
import com.clebs.celerity.models.requests.logoutModel
import com.clebs.celerity.models.response.AppVersionResponse
import com.clebs.celerity.models.response.BaseResponseTwo
import com.clebs.celerity.models.response.CheckIFTodayCheckIsDone
import com.clebs.celerity.models.response.DaDailyLocationRotaResponse
import com.clebs.celerity.models.response.DailyWorkInfoByIdResponse
import com.clebs.celerity.models.response.DeductionAgreementResponse
import com.clebs.celerity.models.response.DepartmentRequestResponse
import com.clebs.celerity.models.response.DownloadInvoicePDFResponse
import com.clebs.celerity.models.response.DownloadInvoicePDFResponseX
import com.clebs.celerity.models.response.DownloadThirdPartyInvoicePDFResponse
import com.clebs.celerity.models.response.ExpiringDocumentsResponse
import com.clebs.celerity.models.response.GetAvgScoreResponse
import com.clebs.celerity.models.response.GetCompanySignedDocumentListResponse
import com.clebs.celerity.models.response.GetDAOutStandingDeductionListResponse
import com.clebs.celerity.models.response.GetDAOutStandingDeductionListResponseItem
import com.clebs.celerity.models.response.GetDAVehicleExpiredDocumentsResponse
import com.clebs.celerity.models.response.GetDriverBreakTimeInfoResponse
import com.clebs.celerity.models.response.GetDriverDeductionHistoryResponse
import com.clebs.celerity.models.response.GetDriverInvoiceListResponse
import com.clebs.celerity.models.response.GetDriverOtherCompaniesPolicyResponse
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
import com.clebs.celerity.models.response.GetVehicleAdvancePaymentAgreementResponse
import com.clebs.celerity.models.response.GetVehicleDefectSheetInfoResponse
import com.clebs.celerity.models.response.GetVehicleImageUploadInfoResponse
import com.clebs.celerity.models.response.GetvehicleInfoByDriverId
import com.clebs.celerity.models.response.NotificationResponse
import com.clebs.celerity.models.response.RideAlongDriverInfoByDateResponse
import com.clebs.celerity.models.response.SaveCommentResponse
import com.clebs.celerity.models.response.SaveDeviceInformationRequest
import com.clebs.celerity.models.response.SaveTicketResponse
import com.clebs.celerity.models.response.SaveVehDefectSheetResponse
import com.clebs.celerity.models.response.SimpleQuestionResponse
import com.clebs.celerity.models.response.SimpleStatusMsgResponse
import com.clebs.celerity.models.response.VehicleExpiringDocumentsResponse
import com.clebs.celerity.models.response.WeeklyLocationRotabyIdResponse
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.ui.App
import com.clebs.celerity.utils.DBImages
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
    val liveDataRouteLocationResponse = MutableLiveData<GetRouteLocationInfoResponse?>()
    val liveDataRideAlongRouteTypeInfo = MutableLiveData<GetRideAlongRouteTypeInfoResponse?>()
    val liveDataRouteTypeInfo = MutableLiveData<GetRideAlongRouteTypeInfoResponse?>()
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
    val liveDataDownloadInvoicePDF = MutableLiveData<DownloadInvoicePDFResponseX?>()
    val liveDataDownloadThirdPartyInvoicePDF =
        MutableLiveData<DownloadInvoicePDFResponseX?>()
    val liveDataDownloadSignedDAHandbook = MutableLiveData<ResponseBody?>()
    val liveDataDownloadSignedGDPRPOLICY = MutableLiveData<ResponseBody?>()
    val liveDataDownloadSignedServiceLevelAgreement = MutableLiveData<ResponseBody?>()
    val liveDataDownloadSignedPrivacyPolicy = MutableLiveData<ResponseBody?>()
    val liveDataDownloadSignedDAEngagement = MutableLiveData<ResponseBody?>()
    val liveDataSaveDeviceInformation = MutableLiveData<SimpleStatusMsgResponse?>()
    val liveDataGetRideAlongDriverFeedbackQuestion =
        MutableLiveData<GetRideAlongDriverFeedbackQuestionResponse?>()
    val ldcompleteTaskLayoutObserver = MutableLiveData<Int>().apply {
        postValue(0)
    }
    val livedataGetNotificationListByUserId = MutableLiveData<NotificationResponse?>()
    val livedataSavevehicleinspectioninfo = MutableLiveData<SimpleStatusMsgResponse?>()
    val livedataGetVehicleInfobyDriverId = MutableLiveData<GetvehicleInfoByDriverId?>()
    val liveDatauploadVehicleImages = MutableLiveData<SimpleStatusMsgResponse?>()
    val liveDataGetAdvancePaymentAgreement =
        MutableLiveData<GetVehicleAdvancePaymentAgreementResponse?>()
    val liveDataDeductionAgreement = MutableLiveData<DeductionAgreementResponse?>()
    val liveDataUpdateDeducton = MutableLiveData<SimpleStatusMsgResponse?>()
    val liveDataGetDAVehicleExpiredDocuments =
        MutableLiveData<GetDAVehicleExpiredDocumentsResponse?>()
    val liveDataGetDAExpiringDocuments = MutableLiveData<ExpiringDocumentsResponse?>()
    val liveDataApproveWeeklyRota = MutableLiveData<SimpleStatusMsgResponse?>()
    val liveDataUploadExpiringDocs = MutableLiveData<SimpleStatusMsgResponse?>()
    val liveDataApproveVehicleAdvancePaymentAgreement = MutableLiveData<SimpleStatusMsgResponse?>()
    val liveDataWeeklyRotaExistForDAApproval = MutableLiveData<SimpleStatusMsgResponse?>()
    val liveDataWeeklyLocationRotabyId = MutableLiveData<WeeklyLocationRotabyIdResponse?>()
    val liveDataMarkNotificationAsRead = MutableLiveData<SimpleStatusMsgResponse?>()
    val liveDataDaDailyLocationRota = MutableLiveData<DaDailyLocationRotaResponse?>()
    val liveDataApproveDailyRotabyDA = MutableLiveData<SimpleStatusMsgResponse?>()
    val liveDataDownloadDAHandbookPolicy = MutableLiveData<ResponseBody?>()
    val liveDataDownloadDAEngagementPolicy = MutableLiveData<ResponseBody?>()
    val liveDataDownloadGDPRPolicy = MutableLiveData<ResponseBody?>()
    val liveDataDownloadServiceLevelAgreementPolicy = MutableLiveData<ResponseBody?>()
    val liveDataDownloadPrivacyPolicy = MutableLiveData<ResponseBody?>()
    val liveDataDownloadTrucksServiceLevelAgreementPolicy = MutableLiveData<ResponseBody?>()
    val liveDataGetDriverInvoiceList = MutableLiveData<GetDriverInvoiceListResponse?>()
    val liveDataGetThirdPartyInvoiceList = MutableLiveData<GetDriverInvoiceListResponse?>()

    val liveDataGetDriverOtherCompaniesPolicy =
        MutableLiveData<GetDriverOtherCompaniesPolicyResponse?>()
    val liveDataDownloadDriverOtherCompaniesPolicy =
        MutableLiveData<DownloadDriverOtherCompaniesPolicyResponse?>()
    val liveDataGetDAEmergencyContact = MutableLiveData<String?>()
    val liveDataUploadVehicleDefectImages = MutableLiveData<SimpleStatusMsgResponse?>()
    val liveDataGetCompanySignedDocumentList =
        MutableLiveData<GetCompanySignedDocumentListResponse?>()
    val liveDataGetDAOutStandingDeductionList =
        MutableLiveData<GetDAOutStandingDeductionListResponseItem?>()
    val liveDataGetDriverDeductionHistory = MutableLiveData<GetDriverDeductionHistoryResponse?>()
    val liveDataGetLatestAppVersion = MutableLiveData<AppVersionResponse?>()
    private val _navigateToSecondPage = MutableLiveData<Boolean>()
    val currentViewPage: MutableLiveData<Int> = MutableLiveData<Int>().apply {
        postValue(0)
    }
    val liveDataVehicleExpiringDocumentsResponse =
        MutableLiveData<VehicleExpiringDocumentsResponse?>()

    val liveDataUploadVehDocumentFileByDriverResponse = MutableLiveData<SimpleStatusMsgResponse?>()

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

    fun updateProfilepassword(userID: Double, oldpass: String, newpass: String) {
        viewModelScope.launch {
            val response = repo.updateprofilePassword(userID, oldpass, newpass)
            updateprofilelivedata.postValue(response)
        }
    }

    fun updateprofileRegular(request: UpdateProfileRequestBody) {
        viewModelScope.launch {
            val response = repo.updteprofileregular(request)
            if (response.failed)
                updateprofileregular.postValue(null)
            if (!response.isSuccessful)
                updateprofileregular.postValue(null)
            else
                updateprofileregular.postValue(response.body)
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
            if (response.failed)
                responseLiveData.postValue(null)
            if (!response.isSuccessful)
                responseLiveData.postValue(null)
            else
                responseLiveData.postValue(response.body)
        }
        return responseLiveData
    }

    fun getDriverSignatureInfo(userID: Double): MutableLiveData<GetsignatureInformation?> {
        val responseLiveData = MutableLiveData<GetsignatureInformation?>()

        viewModelScope.launch {
            val response = repo.getDriverSignatureInfo(userID)
            if (response.failed)
                responseLiveData.postValue(null)
            if (!response.isSuccessful)
                responseLiveData.postValue(null)
            else
                responseLiveData.postValue(response.body)
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
            if (response.failed)
                responseLiveData.postValue(null)
            if (!response.isSuccessful)
                responseLiveData.postValue(null)
            else
                responseLiveData.postValue(response.body)
        }

        return responseLiveData

    }

    fun CheckIFTodayCheckIsDone(): MutableLiveData<CheckIFTodayCheckIsDone?> {
        val responseLiveData = MutableLiveData<CheckIFTodayCheckIsDone?>()

        viewModelScope.launch {
            val response = repo.CheckIFTodayCheckIsDone()
            if (response.failed)
                responseLiveData.postValue(null)
            if (!response.isSuccessful)
                responseLiveData.postValue(null)
            else
                responseLiveData.postValue(response.body)
        }

        return responseLiveData

    }


    fun UseEmailasUsername(userID: Double, Email: String): MutableLiveData<BaseResponseTwo?> {
        val responseLiveData = MutableLiveData<BaseResponseTwo?>()

        viewModelScope.launch {
            val response = repo.UseEmailAsUsername(userID, Email)
            if (response.failed)
                responseLiveData.postValue(null)
            if (!response.isSuccessful)
                responseLiveData.postValue(null)
            else
                responseLiveData.postValue(response.body)
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
            if (response.failed)
                responseLiveData.postValue(null)
            if (!response.isSuccessful)
                responseLiveData.postValue(null)
            else
                responseLiveData.postValue(response.body)
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
            val response = repo.GetVehicleDefectSheetInfo(userID)
            if (response.failed)
                getVehicleDefectSheetInfoLiveData.postValue(null)
            if (!response.isSuccessful)
                getVehicleDefectSheetInfoLiveData.postValue(null)
            else
                getVehicleDefectSheetInfoLiveData.postValue(response.body)
        }
    }

    fun SaveVehDefectSheet(vehicleDefectSheetInfoResponse: SaveVechileDefectSheetRequest) {
        viewModelScope.launch {
            var response = repo.SaveVehDefectSheet(
                vehicleDefectSheetInfoResponse
            )
            if (response.failed)
                SaveVehDefectSheetResponseLiveData.postValue(null)
            if (!response.isSuccessful)
                SaveVehDefectSheetResponseLiveData.postValue(null)
            else
                SaveVehDefectSheetResponseLiveData.postValue(response.body)
        }
    }

    fun GetVehicleInformation(userID: Int, vehRegNo: String) {
        viewModelScope.launch {
            var response = repo.GetVehicleInformation(userID, vehRegNo)
            if (response.failed)
                vechileInformationLiveData.postValue(null)
            if (!response.isSuccessful)
                vechileInformationLiveData.postValue(null)
            else
                vechileInformationLiveData.postValue(response.body)
        }
    }

    fun GetVehicleImageUploadInfo(userID: Int, vmId: Int, date: String) {
        viewModelScope.launch {
            val response = repo.GetVehicleImageUploadInfo(userID, vmId, date)
            if (response.failed)
                vehicleImageUploadInfoLiveData.postValue(null)
            if (!response.isSuccessful)
                vehicleImageUploadInfoLiveData.postValue(null)
            else
                vehicleImageUploadInfoLiveData.postValue(response.body)
        }
    }

    fun uploadVehicleImage(userID: Int, image: MultipartBody.Part, type: Int, dateTime: String) {
        viewModelScope.launch {
            var response = repo.uploadVehicleImage(userID, image, type, dateTime)
            if (response.failed)
                uploadVehicleImageLiveData.postValue(null)
            if (!response.isSuccessful)
                uploadVehicleImageLiveData.postValue(null)
            else
                uploadVehicleImageLiveData.postValue(response.body)
        }
    }

    fun GetDailyWorkInfoById(userID: Int) {
        viewModelScope.launch {
            var response = repo.GetDailyWorkInfobyId(userID)
            if (response.failed)
                livedataDailyWorkInfoByIdResponse.postValue(null)
            if (!response.isSuccessful)
                livedataDailyWorkInfoByIdResponse.postValue(null)
            else
                livedataDailyWorkInfoByIdResponse.postValue(response.body)
        }
    }

    fun GetRouteLocationInfo(locID: Int) {
        viewModelScope.launch {
            var response = repo.GetRouteLocationInfo(locID)
            if (response.failed)
                liveDataRouteLocationResponse.postValue(null)
            if (!response.isSuccessful)
                liveDataRouteLocationResponse.postValue(null)
            else
                liveDataRouteLocationResponse.postValue(response.body)
        }
    }

    fun GetRideAlongRouteTypeInfo(userID: Int) {
        viewModelScope.launch {
            var response = repo.GetRideAlongRouteTypeInfo(userID)
            if (response.failed)
                liveDataRideAlongRouteTypeInfo.postValue(null)
            if (!response.isSuccessful)
                liveDataRideAlongRouteTypeInfo.postValue(null)
            else
                liveDataRideAlongRouteTypeInfo.postValue(response.body)
        }
    }

    fun GetDriverRouteTypeInfo(userID: Int) {
        viewModelScope.launch {
            var response = repo.GetDriverRouteTypeInfo(userID)
            if (response.failed)
                liveDataRouteTypeInfo.postValue(null)
            if (!response.isSuccessful)
                liveDataRouteTypeInfo.postValue(null)
            else
                liveDataRouteTypeInfo.postValue(response.body)

        }
    }


    fun GetDriverSignatureInformation(userID: Int) {
        viewModelScope.launch {
            val response = repo.GetDriverSignatureInformation(userID)
            if (response.failed)
                liveDataGetDriverSignatureInformation.postValue(null)
            if (!response.isSuccessful)
                liveDataGetDriverSignatureInformation.postValue(null)
            else
                liveDataGetDriverSignatureInformation.postValue(response.body)
            /*liveDataGetDriverSignatureInformation.postValue(response)*/
        }
    }

    fun UpdateDriverAgreementSignature(updateDriverAgreementSignature: UpdateDriverAgreementSignatureRequest) {
        viewModelScope.launch {
            val response = repo.UpdateDriverAgreementSignature(updateDriverAgreementSignature)
            if (response.failed)
                livedataupdateDriverAgreementSignature.postValue(null)
            if (!response.isSuccessful)
                livedataupdateDriverAgreementSignature.postValue(null)
            else
                livedataupdateDriverAgreementSignature.postValue(response.body)
            /*livedataupdateDriverAgreementSignature.postValue(response)*/
        }
    }

    fun AddOnRouteInfo(addOnRouteInfoRequest: AddOnRouteInfoRequest) {
        viewModelScope.launch {
            val response = repo.AddOnRouteInfo(addOnRouteInfoRequest)
            if (response.failed)
                livedataAddOnRouteInfo.postValue(null)
            if (!response.isSuccessful)
                livedataAddOnRouteInfo.postValue(null)
            else
                livedataAddOnRouteInfo.postValue(response.body)
            /*livedataAddOnRouteInfo.postValue(response)*/
        }
    }

    fun SaveBreakTime(saveBreakTimeRequest: SaveBreakTimeRequest) {
        viewModelScope.launch {
            val response = repo.SaveBreakTime(saveBreakTimeRequest)
            if (response.failed)
                livedataSaveBreakTime.postValue(null)
            if (!response.isSuccessful)
                livedataSaveBreakTime.postValue(null)
            else
                livedataSaveBreakTime.postValue(response.body)
//            livedataSaveBreakTime.postValue(response)
        }
    }

    fun GetDriverBreakTimeInfo(driverId: Int) {
        viewModelScope.launch {
            val response = repo.GetDriverBreakInfo(driverId)
            if (response.failed)
                livedataDriverBreakInfo.postValue(null)
            if (!response.isSuccessful)
                livedataDriverBreakInfo.postValue(null)
            else
                livedataDriverBreakInfo.postValue(response.body)
            //livedataDriverBreakInfo.postValue(response)
        }
    }

    fun UpdateClockInTime(driverId: Int) {
        viewModelScope.launch {
            val response = repo.UpdateClockInTime(driverId)
            if (response.failed)
                livedataClockInTime.postValue(null)
            if (!response.isSuccessful)
                livedataClockInTime.postValue(null)
            else
                livedataClockInTime.postValue(response.body)
            //  livedataClockInTime.postValue(response)
        }
    }

    fun UpdateClockOutTime(driverId: Int) {
        viewModelScope.launch {
            val response = repo.UpdateClockOutTime(driverId)
            if (response.failed)
                livedataUpdateClockOutTime.postValue(null)
            if (!response.isSuccessful)
                livedataUpdateClockOutTime.postValue(null)
            else
                livedataUpdateClockOutTime.postValue(response.body)
            //  livedataUpdateClockOutTime.postValue(response)
        }
    }

    fun GetRideAlongDriversList() {
        viewModelScope.launch {
            val response = repo.GetRideAlongDriversList()
            if (response.failed)
                livedataGetRideAlongDriversList.postValue(null)
            if (!response.isSuccessful)
                livedataGetRideAlongDriversList.postValue(null)
            else
                livedataGetRideAlongDriversList.postValue(response.body)
            // livedataGetRideAlongDriversList.postValue(response)
        }
    }

    fun GetRideAlongVehicleLists() {
        viewModelScope.launch {
            val response = repo.GetRideAlongVehicleLists()
            if (response.failed)
                livedataGetRideAlongVehicleLists.postValue(null)
            if (!response.isSuccessful)
                livedataGetRideAlongVehicleLists.postValue(null)
            else
                livedataGetRideAlongVehicleLists.postValue(response.body)
            // livedataGetRideAlongVehicleLists.postValue(response)
        }
    }

    fun GetRouteInfoById(routeID: Int) {
        viewModelScope.launch {
            val response = repo.GetRouteInfoById(routeID)
            if (response.failed)
                livedataGetRouteInfoById.postValue(null)
            if (!response.isSuccessful)
                livedataGetRouteInfoById.postValue(null)
            else
                livedataGetRouteInfoById.postValue(response.body)
            // livedataGetRouteInfoById.postValue(response)
        }
    }

    fun AddOnRideAlongRouteInfo(addOnRideAlongRouteInfoRequest: AddOnRideAlongRouteInfoRequest) {
        viewModelScope.launch {
            var response = repo.AddOnRideAlongRouteInfo(addOnRideAlongRouteInfoRequest)
            if (response.failed)
                livedataRideAlongSubmitApiRes.postValue(null)
            if (!response.isSuccessful)
                livedataRideAlongSubmitApiRes.postValue(null)
            else
                livedataRideAlongSubmitApiRes.postValue(response.body)

            /*            val result = runCatching {
                            repo.AddOnRideAlongRouteInfo(addOnRideAlongRouteInfoRequest)
                        }
                        result.onSuccess { response ->
                            livedataRideAlongSubmitApiRes.postValue(response)
                        }
                        result.onFailure { exception ->
                            Log.e("AddOnRideAlongRouteInfo", "Error: ${exception.message}")
                        }*/
        }
    }

    fun GetRideAlongRouteInfoById(routeID: Int, leadDriverId: Int) {
        viewModelScope.launch {
            var response = repo.GetRideAlongRouteInfoById(routeID, leadDriverId)
            if (response.failed)
                livedataRideAlongRouteInfoById.postValue(null)
            if (!response.isSuccessful)
                livedataRideAlongRouteInfoById.postValue(null)
            else
                livedataRideAlongRouteInfoById.postValue(response.body)

            /*   val result = runCatching {
                   repo.GetRideAlongRouteInfoById(routeID, leadDriverId)
               }
               result.onSuccess { response ->
                   livedataRideAlongRouteInfoById.postValue(response)
               }
               result.onFailure { ex ->
                   Log.e("GetRideAlongRouteInfoById Exception", "Error: ${ex.message}")
               }*/
        }
    }

    fun GetDriverRouteInfoByDate(driverId: Int) {
        viewModelScope.launch {
            var response = repo.GetDriverRouteInfoByDate(driverId)
            if (response.failed)
                liveDatadriverInfobyRouteDate.postValue(null)
            if (!response.isSuccessful)
                liveDatadriverInfobyRouteDate.postValue(null)
            else
                liveDatadriverInfobyRouteDate.postValue(response.body)

            /*            val result = runCatching {
                            repo.GetDriverRouteInfoByDate(driverId)
                        }
                        result.onSuccess { response ->
                            liveDatadriverInfobyRouteDate.postValue(response)
                        }
                        result.onFailure { ex ->
                            Log.e("GetDriverRouteInfoByDate Exception", "Error: ${ex.message}")
                        }*/
        }
    }

    fun GetRideAlongDriverInfoByDate(driverId: Int) {
        viewModelScope.launch {
            var response = repo.GetRideAlongDriverInfoByDate(driverId)
            if (response.failed)
                liveDataRideAlongDriverInfoByDateResponse.postValue(null)
            if (!response.isSuccessful)
                liveDataRideAlongDriverInfoByDateResponse.postValue(null)
            else
                liveDataRideAlongDriverInfoByDateResponse.postValue(response.body)

            /*            val result = runCatching {
                            repo.GetRideAlongDriverInfoByDate(driverId)
                        }
                        result.onSuccess { response ->
                            liveDataRideAlongDriverInfoByDateResponse.postValue(response)
                        }
                        result.onFailure { ex ->
                            Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
                        }*/
        }
    }

    fun SaveQuestionairePreparedness(request: SaveQuestionairePreparednessRequest) {
        viewModelScope.launch {
            var response = repo.SaveQuestionairePreparedness(request)
            if (response.failed)
                liveDataQuestionairePreparedness.postValue(null)
            if (!response.isSuccessful)
                liveDataQuestionairePreparedness.postValue(null)
            else
                liveDataQuestionairePreparedness.postValue(response.body)
            /*
                     val result = runCatching {
                         repo.SaveQuestionairePreparedness(request)
                     }
                     result.onSuccess { response ->
                         liveDataQuestionairePreparedness.postValue(response)
                     }
                     result.onFailure { ex ->
                         Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
                     }*/
        }
    }

    fun SaveQuestionaireStartup(request: SaveQuestionareStartupRequestNew) {
        viewModelScope.launch {
            var response = repo.SaveQuestionaireStartup(request)
            if (response.failed)
                liveDataQuestionaireStartup.postValue(null)
            if (!response.isSuccessful)
                liveDataQuestionaireStartup.postValue(null)
            else
                liveDataQuestionaireStartup.postValue(response.body)
            /*
                        val result = runCatching {
                            repo.SaveQuestionaireStartup(request)
                        }
                        result.onSuccess { response ->
                            liveDataQuestionaireStartup.postValue(response)
                        }
                        result.onFailure { ex ->
                            Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
                        }*/
        }
    }

    fun SaveQuestionaireGoingOn(request: SaveQuestionaireOnGoingActivitiesRequest) {
        viewModelScope.launch {
            var response = repo.SaveQuestionaireOnGoingActivities(request)
            if (response.failed)
                liveDataQuestionaireGoingOn.postValue(null)
            if (!response.isSuccessful)
                liveDataQuestionaireGoingOn.postValue(null)
            else
                liveDataQuestionaireGoingOn.postValue(response.body)

            /*            val result = runCatching {
                            repo.SaveQuestionaireOnGoingActivities(request)
                        }
                        result.onSuccess { response ->
                            liveDataQuestionaireGoingOn.postValue(response)
                        }
                        result.onFailure { ex ->
                            Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
                        }*/
        }
    }

    fun SaveQuestionaireDelivery(request: SaveQuestionareDrivingabilityassessment) {
        viewModelScope.launch {
            var response = repo.SaveQuestionaireDeliverProcedures(request)
            if (response.failed)
                liveDataQuestionareDeliveryProcedures.postValue(null)
            if (!response.isSuccessful)
                liveDataQuestionareDeliveryProcedures.postValue(null)
            else
                liveDataQuestionareDeliveryProcedures.postValue(response.body)

            /*          val result = runCatching {
                          repo.SaveQuestionaireDeliverProcedures(request)
                      }
                      result.onSuccess { response ->
                          liveDataQuestionareDeliveryProcedures.postValue(response)
                      }
                      result.onFailure { ex ->
                          Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
                      }*/
        }
    }

    fun SaveQuestionaireReturnToDeliveryStation(request: SaveQuestionaireReturnToDeliveryStationRequest) {
        viewModelScope.launch {
            var response = repo.SaveQuestionaireReturnToDeliveryStation(request)
            if (response.failed)
                liveDataQuestionareReturn.postValue(null)
            if (!response.isSuccessful)
                liveDataQuestionareReturn.postValue(null)
            else
                liveDataQuestionareReturn.postValue(response.body)

            /* val result = runCatching {
                 repo.SaveQuestionaireReturnToDeliveryStation(request)
             }
             result.onSuccess { response ->
                 liveDataQuestionareReturn.postValue(response)
             }
             result.onFailure { ex ->
                 Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
             }*/
        }
    }

    fun SaveQuestionaireFinalAssesment(request: SubmitFinalQuestionairebyLeadDriverRequest) {
        viewModelScope.launch {
            var response = repo.SubmitFinalQuestionairebyLeadDriver(request)
            if (response.failed)
                liveDataFinalAssesment.postValue(null)
            if (!response.isSuccessful)
                liveDataFinalAssesment.postValue(null)
            else
                liveDataFinalAssesment.postValue(response.body)

            /*            val result = runCatching {
                            repo.SubmitFinalQuestionairebyLeadDriver(request)
                        }
                        result.onSuccess { response ->
                            liveDataFinalAssesment.postValue(response)
                        }
                        result.onFailure { ex ->
                            Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
                        }*/
        }
    }

    fun DeleteOnRideAlongRouteInfo(routeID: Int) {
        viewModelScope.launch {
            var response = repo.DeleteOnRideAlongRouteInfo(routeID)
            if (response.failed)
                liveDataDeleteOnRideAlongRouteInfo.postValue(null)
            if (!response.isSuccessful)
                liveDataDeleteOnRideAlongRouteInfo.postValue(null)
            else
                liveDataDeleteOnRideAlongRouteInfo.postValue(response.body)
            /*
                        val result = runCatching {
                            repo.DeleteOnRideAlongRouteInfo(routeID)
                        }
                        result.onSuccess { response ->
                            liveDataDeleteOnRideAlongRouteInfo.postValue(response)
                        }
                        result.onFailure { ex ->
                            Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
                        }*/
        }
    }

    fun DeleteOnRouteDetails(routeID: Int) {
        viewModelScope.launch {
            var response = repo.DeleteOnRouteDetails(routeID)
            if (response.failed)
                liveDataDeleteOnRouteDetails.postValue(null)
            if (!response.isSuccessful)
                liveDataDeleteOnRouteDetails.postValue(null)
            else
                liveDataDeleteOnRouteDetails.postValue(response.body)

            /*            val result = runCatching {
                            repo.DeleteOnRouteDetails(routeID)
                        }
                        result.onSuccess { response ->
                            liveDataDeleteOnRouteDetails.postValue(response)
                        }
                        result.onFailure { ex ->
                            Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
                        }*/
        }
    }

    fun SubmitRideAlongDriverFeedback(request: SubmitRideAlongDriverFeedbackRequest) {
        viewModelScope.launch {
            var response = repo.SubmitRideAlongDriverFeedback(request)
            if (response.failed)
                liveDataSubmitRideAlongDriverFeedbackRequest.postValue(null)
            if (!response.isSuccessful)
                liveDataSubmitRideAlongDriverFeedbackRequest.postValue(null)
            else
                liveDataSubmitRideAlongDriverFeedbackRequest.postValue(response.body)

            /*            val result = runCatching {
                            repo.SubmitRideAlongDriverFeedback(request)
                        }
                        result.onSuccess { response ->
                            liveDataSubmitRideAlongDriverFeedbackRequest.postValue(response)
                        }
                        result.onFailure { ex ->
                            Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
                        }*/
        }
    }

    fun GetRideAlongLeadDriverQuestion(
        driverId: Int,
        routeID: Int,
        leadDriverId: Int,
        daDailyWorkId: Int
    ) {
        viewModelScope.launch {
            var response =
                repo.GetRideAlongLeadDriverQuestion(driverId, routeID, leadDriverId, daDailyWorkId)
            if (response.failed)
                liveDataGetRideAlongLeadDriverQuestion.postValue(null)
            if (!response.isSuccessful)
                liveDataGetRideAlongLeadDriverQuestion.postValue(null)
            else
                liveDataGetRideAlongLeadDriverQuestion.postValue(response.body)

            /*            val result = runCatching {
                            repo.GetRideAlongLeadDriverQuestion(driverId, routeID, leadDriverId, daDailyWorkId)
                        }
                        result.onSuccess { response ->
                            liveDataGetRideAlongLeadDriverQuestion.postValue(response)
                        }
                        result.onFailure { ex ->
                            Log.e("RideAlongDriverInfo Exception", "Error ${ex.message}")
                        }*/
        }
    }

    fun DeleteBreakTime(
        dawDriverBreakId: Int
    ) {
        viewModelScope.launch {
            var response = repo.DeleteBreakTime(dawDriverBreakId)
            if (response.failed)
                liveDataDeleteBreakTime.postValue(null)
            if (!response.isSuccessful)
                liveDataDeleteBreakTime.postValue(null)
            else
                liveDataDeleteBreakTime.postValue(response.body)

            /*
                        val result = runCatching {
                            repo.DeleteBreakTime(dawDriverBreakId)
                        }
                        result.onSuccess { response ->
                            liveDataDeleteBreakTime.postValue(response)
                        }
                        result.onFailure { ex ->
                            Log.e("DeleteBreakTime Exception", "Error ${ex.message}")
                        }*/
        }
    }

    fun UpdateOnRouteInfo(
        request: GetDriverRouteInfoByDateResponseItem
    ) {
        viewModelScope.launch {

            var response = repo.UpdateOnRouteInfo(request)
            if (response.failed)
                liveDataUpdateOnRouteInfo.postValue(null)
            if (!response.isSuccessful)
                liveDataUpdateOnRouteInfo.postValue(null)
            else
                liveDataUpdateOnRouteInfo.postValue(response.body)

            /*            val result = runCatching {
                            repo.UpdateOnRouteInfo(request)
                        }
                        result.onSuccess { res ->
                            liveDataUpdateOnRouteInfo.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.e("DeleteBreakTime Exception", "Error ${ex.message}")
                        }*/
        }
    }

    fun GetUserTickets(
        userID: Int,
        department: Int? = null,
        startDate: String? = null,
        endDate: String? = null,
        includeCompleted: Boolean? = null
    ) {
        viewModelScope.launch {
            var response =
                repo.GetUserTickets(userID, department, startDate, endDate, includeCompleted)
            if (response.failed)
                liveDataGetUserTickets.postValue(null)
            if (!response.isSuccessful)
                liveDataGetUserTickets.postValue(null)
            else
                liveDataGetUserTickets.postValue(response.body)

            /*          val result = runCatching {
                          repo.GetUserTickets(userID, department, startDate, endDate,includeCompleted)
                      }
                      result.onSuccess { res ->
                          liveDataGetUserTickets.postValue(res)
                      }
                      result.onFailure { ex ->
                          Log.e("GetUserTickets", "Error ${ex.message}")
                      }*/
        }
    }

    fun GetUserDepartmentList() {
        viewModelScope.launch {
            var response = repo.GetUserDepartmentList()
            if (response.failed)
                liveDataTicketDepartmentsResponse.postValue(null)
            if (!response.isSuccessful)
                liveDataTicketDepartmentsResponse.postValue(null)
            else
                liveDataTicketDepartmentsResponse.postValue(response.body)
            /*
                        val result = runCatching {
                            repo.GetUserDepartmentList()
                        }
                        result.onSuccess { res ->
                            liveDataTicketDepartmentsResponse.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.e("GetUserTickets", "Error ${ex.message}")
                        }*/
        }
    }

    fun GetTicketRequestType(deptID: Int) {
        viewModelScope.launch {
            var response = repo.GetTicketRequestType(deptID)
            if (response.failed)
                liveDataGetTicketRequestType.postValue(null)
            if (!response.isSuccessful)
                liveDataGetTicketRequestType.postValue(null)
            else
                liveDataGetTicketRequestType.postValue(response.body)

            /*            val result = runCatching {
                            repo.GetTicketRequestType(deptID)
                        }
                        result.onSuccess {
                            liveDataGetTicketRequestType.postValue(it)
                        }
                        result.onFailure { ex ->
                            Log.e("GetTicketUserType", "Error ${ex.message}")
                        }*/
        }
    }

    fun SaveTicketData(userID: Int, daDedAggrId: Int, request: SaveTicketDataRequestBody) {
        viewModelScope.launch {
            var response = repo.SaveTicketData(userID, daDedAggrId, request)
            if (response.failed)
                liveDataSaveTicketResponse.postValue(null)
            if (!response.isSuccessful)
                liveDataSaveTicketResponse.postValue(null)
            else
                liveDataSaveTicketResponse.postValue(response.body)

            /*            val result = runCatching {
                            repo.SaveTicketData(userID, request)
                        }
                        result.onSuccess { res ->
                            liveDataSaveTicketResponse.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.e("GetTicketUserType Ex", "Error ${ex.message}")
                        }*/
        }
    }

    fun GetTicketCommentList(userID: Int, ticketId: Int) {
        viewModelScope.launch {
            var response = repo.GetTicketCommentList(userID, ticketId)
            if (response.failed)
                liveDataGetTicketCommentList.postValue(null)
            if (!response.isSuccessful)
                liveDataGetTicketCommentList.postValue(null)
            else
                liveDataGetTicketCommentList.postValue(response.body)

            /*            val result = runCatching {
                            repo.GetTicketCommentList(userID, ticketId)
                        }
                        result.onSuccess { res ->
                            liveDataGetTicketCommentList.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.e("GetTicketCommentList Ex", "Error ${ex.message}")
                        }*/
        }
    }

    fun UploadTicketAttachmentDoc(
        userID: Int, ticketId: Int, file: MultipartBody.Part
    ) {
        viewModelScope.launch {
            var response = repo.UploadTicketAttachmentDoc(userID, ticketId, file)
            if (response.failed)
                liveDataUploadTicketAttachmentDoc.postValue(null)
            if (!response.isSuccessful)
                liveDataUploadTicketAttachmentDoc.postValue(null)
            else
                liveDataUploadTicketAttachmentDoc.postValue(response.body)
            /*
                        val result = runCatching {
                            repo.UploadTicketAttachmentDoc(userID, ticketId, file)
                        }
                        result.onSuccess { res ->
                            liveDataUploadTicketAttachmentDoc.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.e("UploadTicketAttachmentDoc Ex", "Error ${ex.message}")
                        }*/
        }
    }

    fun SaveTicketComment(
        userID: Int,
        ticketId: Int,
        comment: String
    ) {
        viewModelScope.launch {
            var response = repo.SaveTicketComment(userID, ticketId, comment)
            if (response.failed)
                liveDataSaveTicketComment.postValue(null)
            if (!response.isSuccessful)
                liveDataSaveTicketComment.postValue(null)
            else
                liveDataSaveTicketComment.postValue(response.body)
            /*
                        val result = runCatching {
                            repo.SaveTicketComment(userID, ticketId, comment)
                        }
                        result.onSuccess { res ->
                            liveDataSaveTicketComment.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.e("SaveTicketComment Ex", "Error ${ex.message}")
                        }*/
        }
    }

    fun GetAVGscore(userID: Int, lmid: Int) {
        viewModelScope.launch {
            var response = repo.GetAvgWeekScore(userID, lmid)
            if (response.failed)
                livedataAvgScoreResponse.postValue(null)
            if (!response.isSuccessful)
                livedataAvgScoreResponse.postValue(null)
            else
                livedataAvgScoreResponse.postValue(response.body)

            /*            val result = runCatching {
                            repo.GetAvgWeekScore(userID, lmid)
                        }
                        result.onSuccess { res ->
                            livedataAvgScoreResponse.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.e("GetTicketUserType", "Error ${ex.message}")
                        }*/
        }
    }

    fun GetLastWeekSCore(userID: Int, lmid: Int, year: Int) {
        viewModelScope.launch {
            var response = repo.GetLastWeekScrore(userID, lmid, year)
            if (response.failed)
                livedatalastweekresponse.postValue(null)
            if (!response.isSuccessful)
                livedatalastweekresponse.postValue(null)
            else
                livedatalastweekresponse.postValue(response.body)
            /*
                        val result = runCatching {
                            repo.GetLastWeekScrore(userID, lmid, year)
                        }
                        result.onSuccess { res ->
                            livedatalastweekresponse.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.e("GetTicketUserType", "Error ${ex.message}")
                        }*/
        }
    }

    fun GetcashFlowWeek(userID: Int, companyFilter: Int, selYear: Int, selWeek: Int) {
        viewModelScope.launch {
            var response = repo.GetCashFlowWeek(userID, companyFilter, selYear, selWeek)
            if (response.failed)
                livedataCashFlowWeek.postValue(null)
            if (!response.isSuccessful)
                livedataCashFlowWeek.postValue(null)
            else
                livedataCashFlowWeek.postValue(response.body)

            /*           val result = runCatching {
                           repo.GetCashFlowWeek(userID, companyFilter, selYear, selWeek)
                       }
                       result.onSuccess { res ->
                           livedataCashFlowWeek.postValue(res)
                       }
                       result.onFailure { ex ->
                           Log.e("GetTicketUserType", "Error ${ex.message}")
                       }*/
        }
    }

    fun GetWeekAndYear() {
        viewModelScope.launch {
            var response = repo.GetWeekYear()
            if (response.failed)
                livedatagetweekyear.postValue(null)
            if (!response.isSuccessful)
                livedatagetweekyear.postValue(null)
            else
                livedatagetweekyear.postValue(response.body)

            /*            val result = runCatching {
                            repo.GetWeekYear()
                        }
                        result.onSuccess { res ->
                            livedatagetweekyear.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.e("GetTicketUserType", "Error ${ex.message}")
                        }*/
        }
    }

    fun GetViewFullScheduleInfo(userID: Int, lmid: Int, Year: Int, Week: Int) {
        viewModelScope.launch {
            var response = repo.GetVechileSchedule(userID, lmid, Year, Week)
            if (response.failed)
                livedatagetvechilescheduleinfo.postValue(null)
            if (!response.isSuccessful)
                livedatagetvechilescheduleinfo.postValue(null)
            else
                livedatagetvechilescheduleinfo.postValue(response.body)

            /*            val result = runCatching {
                            repo.GetVechileSchedule(userID, lmid, Year, Week)
                        }
                        result.onSuccess { res ->
                            livedatagetvechilescheduleinfo.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.e("GetTicketUserType", "Error ${ex.message}")
                        }*/
        }
    }

    fun UploadTicketCommentAttachmentDoc(
        userID: Int,
        ticketCommentID: Int,
        file: MultipartBody.Part
    ) {
        viewModelScope.launch {
            var response = repo.UploadTicketCommentAttachmentDoc(userID, ticketCommentID, file)
            if (response.failed)
                liveDataUploadTicketCommentAttachmentDoc.postValue(null)
            if (!response.isSuccessful)
                liveDataUploadTicketCommentAttachmentDoc.postValue(null)
            else
                liveDataUploadTicketCommentAttachmentDoc.postValue(response.body)

            /*            val result = runCatching {
                            repo.UploadTicketCommentAttachmentDoc(userID, ticketCommentID, file)
                        }
                        result.onSuccess { res ->
                            liveDataUploadTicketCommentAttachmentDoc.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.e("UploadTicketCommentAttachmentDoc", "Error ${ex.message}")
                        }*/
        }
    }

    fun GetUserTicketDocuments(
        userID: Int,
        ticketId: Int
    ) {
        viewModelScope.launch {
            var response = repo.GetUserTicketDocuments(userID, ticketId)
            if (response.failed)
                liveDataGetUserTicketDocuments.postValue(null)
            if (!response.isSuccessful)
                liveDataGetUserTicketDocuments.postValue(null)
            else
                liveDataGetUserTicketDocuments.postValue(response.body)

            /*    val result = runCatching {
                    repo.GetUserTicketDocuments(userID, ticketId)
                }
                result.onSuccess { res ->
                    liveDataGetUserTicketDocuments.postValue(res)
                }
                result.onFailure { ex ->
                    Log.e("GetUserTicketDocument", "Error ${ex.message}")
                }*/
        }
    }

    fun GetThirdPartyAccess(userID: Int) {
        viewModelScope.launch {
            var response = repo.GetThirdPartyAccess(userID)
            if (response.failed)
                livedatathirdpartyaccess.postValue(null)
            if (!response.isSuccessful)
                livedatathirdpartyaccess.postValue(null)
            else
                livedatathirdpartyaccess.postValue(response.body)

            /*            val result = runCatching {

                            repo.GetThirdPartyAccess(userID)
                        }
                        result.onSuccess { res ->
                            livedatathirdpartyaccess.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.e("GetTicketUserType", "Error ${ex.message}")
                        }*/
        }
    }

    fun RemoveThirdPartyAccess(userID: Int) {
        viewModelScope.launch {
            var response = repo.RemoveThirdPartyAccess(userID)
            if (response.failed)
                livedataremovethirdpartyaccess.postValue(null)
            if (!response.isSuccessful)
                livedataremovethirdpartyaccess.postValue(null)
            else
                livedataremovethirdpartyaccess.postValue(response.body)
            /*
                        val result = runCatching {
                            repo.RemoveThirdPartyAccess(userID)
                        }
                        result.onSuccess { res ->
                            livedataremovethirdpartyaccess.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.e("GetTicketUserType", "Error ${ex.message}")
                        }*/
        }
    }

    fun DownloadThirdPartyInvoicePDF(
        userID: Int, invoiceId: Int
    ) {
        viewModelScope.launch {
            var response = repo.DownloadThirdPartyInvoicePDF(userID, invoiceId)
            if (response.failed)
                liveDataDownloadThirdPartyInvoicePDF.postValue(null)
            if (!response.isSuccessful)
                liveDataDownloadThirdPartyInvoicePDF.postValue(null)
            else
                liveDataDownloadThirdPartyInvoicePDF.postValue(response.body)
        }
    }

    fun DownloadSignedDAHandbook(
        handbookId: Int
    ) {
        viewModelScope.launch {
            var response = repo.DownloadSignedDAHandbook(handbookId)
            if (response.failed)
                liveDataDownloadSignedDAHandbook.postValue(null)
            if (!response.isSuccessful)
                liveDataDownloadSignedDAHandbook.postValue(null)
            else
                liveDataDownloadSignedDAHandbook.postValue(response.body)

            /*            val result = runCatching {
                            repo.DownloadSignedDAHandbook(handbookId)
                        }
                        result.onSuccess { res ->
                            liveDataDownloadSignedDAHandbook.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.e("Download Signed Da Handbook", "Error ${ex.message}")
                        }*/
        }
    }

    fun DownloadSignedGDPRPOLICY(
        handbookId: Int
    ) {
        viewModelScope.launch {
            var response = repo.DownloadSignedGDPRPOLICY(handbookId)
            if (response.failed)
                liveDataDownloadSignedGDPRPOLICY.postValue(null)
            if (!response.isSuccessful)
                liveDataDownloadSignedGDPRPOLICY.postValue(null)
            else
                liveDataDownloadSignedGDPRPOLICY.postValue(response.body)

            /*            val result = runCatching {
                            repo.DownloadSignedGDPRPOLICY(handbookId)
                        }
                        result.onSuccess { res ->
                            liveDataDownloadSignedGDPRPOLICY.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.e("Download Signed Da Handbook", "Error ${ex.message}")
                        }*/
        }
    }

    fun DownloadSignedServiceLevelAgreement(
        handbookId: Int
    ) {
        viewModelScope.launch {
            var response = repo.DownloadSignedServiceLevelAgreement(handbookId)
            if (response.failed)
                liveDataDownloadSignedServiceLevelAgreement.postValue(null)
            if (!response.isSuccessful)
                liveDataDownloadSignedServiceLevelAgreement.postValue(null)
            else
                liveDataDownloadSignedServiceLevelAgreement.postValue(response.body)

            /*           val result = runCatching {
                           repo.DownloadSignedServiceLevelAgreement(handbookId)
                       }
                       result.onSuccess { res ->
                           liveDataDownloadSignedServiceLevelAgreement.postValue(res)
                       }
                       result.onFailure { ex ->
                           Log.e("Download Signed Da Handbook", "Error ${ex.message}")
                       }*/
        }
    }

    fun DownloadSignedPrivacyPolicy(
        handbookId: Int
    ) {
        viewModelScope.launch {
            var response = repo.DownloadSignedPrivacyPolicy(handbookId)
            if (response.failed)
                liveDataDownloadSignedPrivacyPolicy.postValue(null)
            if (!response.isSuccessful)
                liveDataDownloadSignedPrivacyPolicy.postValue(null)
            else
                liveDataDownloadSignedPrivacyPolicy.postValue(response.body)
            /*
                        val result = runCatching {
                            repo.DownloadSignedPrivacyPolicy(handbookId)
                        }
                        result.onSuccess { res ->
                            liveDataDownloadSignedPrivacyPolicy.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.e("Download Signed Da Handbook", "Error ${ex.message}")
                        }*/
        }
    }

    fun DownloadSignedDAEngagement(
        handbookId: Int
    ) {
        viewModelScope.launch {
            var response = repo.DownloadSignedDAEngagement(handbookId)
            if (response.failed)
                liveDataDownloadSignedDAEngagement.postValue(null)
            if (!response.isSuccessful)
                liveDataDownloadSignedDAEngagement.postValue(null)
            else
                liveDataDownloadSignedDAEngagement.postValue(response.body)

            /*            val result = runCatching {
                            repo.DownloadSignedDAEngagement(handbookId)
                        }
                        result.onSuccess { res ->
                            liveDataDownloadSignedDAEngagement.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.e("Download Signed Da Handbook", "Error ${ex.message}")
                        }*/
        }
    }

    fun GetRideAlongDriverFeedbackQuestion(
        driverId: Int,
        routeID: Int,
        leadDriverId: Int,
        daDailyWorkId: Int
    ) {
        viewModelScope.launch {
            var response = repo.GetRideAlongDriverFeedbackQuestion(
                driverId,
                routeID,
                leadDriverId,
                daDailyWorkId
            )
            if (response.failed)
                liveDataGetRideAlongDriverFeedbackQuestion.postValue(null)
            if (!response.isSuccessful)
                liveDataGetRideAlongDriverFeedbackQuestion.postValue(null)
            else
                liveDataGetRideAlongDriverFeedbackQuestion.postValue(response.body)

            /*            val result = runCatching {
                            repo.GetRideAlongDriverFeedbackQuestion(
                                driverId,
                                routeID,
                                leadDriverId,
                                daDailyWorkId
                            )
                        }
                        result.onSuccess { res ->
                            liveDataGetRideAlongDriverFeedbackQuestion.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.e("GetRideAlongDriverFeedBackQuestionRes ", "${ex.message}")
                        }*/

        }
    }

    fun SaveDeviceInformation(body: SaveDeviceInformationRequest) {
        viewModelScope.launch {
            var response = repo.SaveDeviceInformation(body)
            if (response.failed)
                liveDataSaveDeviceInformation.postValue(null)
            if (!response.isSuccessful)
                liveDataSaveDeviceInformation.postValue(null)
            else
                liveDataSaveDeviceInformation.postValue(response.body)

            /*            val result = runCatching {
                            repo.SaveDeviceInformation(body)
                        }
                        result.onSuccess { res ->
                            liveDataSaveDeviceInformation.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.e("SaveDeviceInformation ", "${ex.message}")
                        }*/
        }
    }

    fun GetNotificationListByUserId(userId: Int) {
        viewModelScope.launch {
            var response = repo.GetNotificationListByUserId(userId)
            if (response.failed)
                livedataGetNotificationListByUserId.postValue(null)
            if (!response.isSuccessful)
                livedataGetNotificationListByUserId.postValue(null)
            else
                livedataGetNotificationListByUserId.postValue(response.body)


            /*            val result = runCatching {
                            repo.GetNotificationListByUserId(userId)
                        }
                        result.onSuccess { res ->
                            livedataGetNotificationListByUserId.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.d("NotificationList ", "${ex.message}")
                        }*/
        }
    }

    fun SaveVehicleInspectionInfo(body: SaveVehicleInspectionInfo) {
        viewModelScope.launch {
            val response = repo.SaveVehicleInspectionInfo(body)
            if (response.failed)
                livedataSavevehicleinspectioninfo.postValue(null)
            if (!response.isSuccessful)
                livedataSavevehicleinspectioninfo.postValue(null)
            else
                livedataSavevehicleinspectioninfo.postValue(response.body)


            /*            val result = runCatching {
                            repo.SaveVehicleInspectionInfo(body)
                        }
                        result.onSuccess { res ->
                            livedataSavevehicleinspectioninfo.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.d("NotificationList ", "${ex.message}")
                        }*/
        }
    }

    fun GetVehicleInfobyDriverId(userID: Int, date: String) {
        viewModelScope.launch {
            var response = repo.GetVehicleInfobyDriverId(userID, date)
            if (response.failed)
                livedataGetVehicleInfobyDriverId.postValue(null)
            if (!response.isSuccessful)
                livedataGetVehicleInfobyDriverId.postValue(null)
            else
                livedataGetVehicleInfobyDriverId.postValue(response.body)

            /*            val result = runCatching {
                            repo.GetVehicleInfobyDriverId(userID,date)
                        }
                        result.onSuccess { res ->
                            livedataGetVehicleInfobyDriverId.postValue(res)
                        }
                        result.onFailure { ex ->
                            Log.d("NotificationList ", "${ex.message}")
                        }*/
        }
    }

    fun uploadVehicleImages(userID: Int, imageList: List<MultipartBody.Part>) {
        viewModelScope.launch {
            val response = repo.uploadVehicleImages(userID, imageList)
            if (response.failed || !response.isSuccessful) {
                liveDatauploadVehicleImages.postValue(null)
            } else {
                liveDatauploadVehicleImages.postValue(response.body)
            }
        }
    }

    fun GetVehicleAdvancePaymentAgreement(
        userID: Int
    ) {
        viewModelScope.launch {
            val response = repo.GetVehicleAdvancePaymentAgreement(userID)
            if (response.failed || !response.isSuccessful)
                liveDataGetAdvancePaymentAgreement.postValue(null)
            else
                liveDataGetAdvancePaymentAgreement.postValue(response.body)
        }
    }

    fun GetDeductionAgreement(userID: Int, aggrId: Int) {
        viewModelScope.launch {
            val response = repo.GetDeductionAgreement(userID, aggrId)
            if (response.failed || !response.isSuccessful)
                liveDataDeductionAgreement.postValue(null)
            else
                liveDataDeductionAgreement.postValue(response.body)
        }
    }

    fun UpdateDaDeduction(
        body: UpdateDeductioRequest
    ) {
        viewModelScope.launch {
            val response = repo.UpdateDaDeductionSignAgreement(body)
            if (response.failed || !response.isSuccessful)
                liveDataUpdateDeducton.postValue(null)
            else
                liveDataUpdateDeducton.postValue(response.body)
        }
    }

    fun GetDAVehicleExpiredDocuments(userID: Int) {
        viewModelScope.launch {
            val response = repo.GetDaVehicleExpiredDocuments(userID)
            if (!response.isSuccessful || response.failed)
                liveDataGetDAVehicleExpiredDocuments.postValue(null)
            else
                liveDataGetDAVehicleExpiredDocuments.postValue(response.body)
        }
    }

    fun GetDAExpiringDocuments(userID: Int) {
        viewModelScope.launch {
            val response = repo.GetDAExpiringDocuments(userID)
            if (!response.isSuccessful || response.failed)
                liveDataGetDAExpiringDocuments.postValue(null)
            else
                liveDataGetDAExpiringDocuments.postValue(response.body)
        }
    }

    fun ApproveWeeklyRotabyDA(
        userID: Int,
        lrnID: Int
    ) {
        viewModelScope.launch {
            val response = repo.ApproveWeeklyRotabyDA(userID, lrnID)
            if (!response.isSuccessful || response.failed)
                liveDataApproveWeeklyRota.postValue(null)
            else
                liveDataApproveWeeklyRota.postValue(response.body)
        }
    }

    fun UploadExpiringDocs(
        userID: Int,
        docTypeID: Int,
        multipartBody: MultipartBody.Part
    ) {
        viewModelScope.launch {
            val response = repo.UploadExpiringDocs(userID, docTypeID, multipartBody)
            if (!response.isSuccessful || response.failed)
                liveDataUploadExpiringDocs.postValue(null)
            else
                liveDataUploadExpiringDocs.postValue(response.body)
        }
    }

    fun ApproveVehicleAdvancePaymentAgreement(
        userID: Int,
        isApproved: Boolean
    ) {
        viewModelScope.launch {
            val response = repo.ApproveVehicleAdvancePaymentAgreement(userID, isApproved)
            if (!response.isSuccessful || response.failed)
                liveDataApproveVehicleAdvancePaymentAgreement.postValue(null)
            else
                liveDataApproveVehicleAdvancePaymentAgreement.postValue(response.body)
        }
    }

    fun WeeklyRotaExistForDAApproval(
        userID: Int
    ) {
        viewModelScope.launch {
            val response = repo.WeeklyRotaExistForDAApproval(userID)
            if (!response.isSuccessful || response.failed)
                liveDataWeeklyRotaExistForDAApproval.postValue(null)
            else
                liveDataWeeklyRotaExistForDAApproval.postValue(response.body)
        }
    }

    fun GetWeeklyLocationRotabyId(
        lrnID: Int
    ) {
        viewModelScope.launch {
            val response = repo.GetWeeklyLocationRotabyId(lrnID)
            if (!response.isSuccessful || response.failed)
                liveDataWeeklyLocationRotabyId.postValue(null)
            else
                liveDataWeeklyLocationRotabyId.postValue(response.body)
        }
    }

    fun MarkNotificationAsRead(
        notificationId: Int
    ) {
        viewModelScope.launch {
            val response = repo.MarkNotificationAsRead(notificationId)
            if (!response.isSuccessful || response.failed)
                liveDataMarkNotificationAsRead.postValue(null)
            else
                liveDataMarkNotificationAsRead.postValue(response.body)
        }
    }

    fun GetDaDailyLocationRota(
        userID: Int,
        tokenxx: String
    ) {
        viewModelScope.launch {
            val response = repo.GetDaDailyLocationRota(userID, tokenxx)
            if (!response.isSuccessful || response.failed)
                liveDataDaDailyLocationRota.postValue(null)
            else
                liveDataDaDailyLocationRota.postValue(response.body)
        }
    }

    fun ApproveDailyRotabyDA(body: ApproveDaDailyRotaRequest) {
        viewModelScope.launch {
            val response = repo.ApproveDailyRotabyDA(body)
            if (!response.isSuccessful || response.failed)
                liveDataApproveDailyRotabyDA.postValue(null)
            else
                liveDataApproveDailyRotabyDA.postValue(response.body)
        }
    }

    fun DownloadDAHandbookPolicy() {
        viewModelScope.launch {
            val response = repo.DownloadDAHandbookPolicy()
            if (!response.isSuccessful || response.failed)
                liveDataDownloadDAHandbookPolicy.postValue(null)
            else
                liveDataDownloadDAHandbookPolicy.postValue(response.body)
        }
    }

    fun DownloadDAEngagementPolicy() {
        viewModelScope.launch {
            val response = repo.DownloadDAEngagementPolicy()
            if (!response.isSuccessful || response.failed)
                liveDataDownloadDAEngagementPolicy.postValue(null)
            else
                liveDataDownloadDAEngagementPolicy.postValue(response.body)
        }
    }

    fun DownloadGDPRPolicy() {
        viewModelScope.launch {
            val response = repo.DownloadGDPRPolicy()
            if (!response.isSuccessful || response.failed)
                liveDataDownloadGDPRPolicy.postValue(null)
            else
                liveDataDownloadGDPRPolicy.postValue(response.body)
        }
    }

    fun DownloadServiceLevelAgreementPolicy() {
        viewModelScope.launch {
            val response = repo.DownloadServiceLevelAgreementPolicy()
            if (!response.isSuccessful || response.failed)
                liveDataDownloadServiceLevelAgreementPolicy.postValue(null)
            else
                liveDataDownloadServiceLevelAgreementPolicy.postValue(response.body)
        }
    }

    fun DownloadPrivacyPolicy() {
        viewModelScope.launch {
            val response = repo.DownloadPrivacyPolicy()
            if (!response.isSuccessful || response.failed)
                liveDataDownloadPrivacyPolicy.postValue(null)
            else
                liveDataDownloadPrivacyPolicy.postValue(response.body)
        }
    }

    fun DownloadTrucksServiceLevelAgreementPolicy() {
        viewModelScope.launch {
            val response = repo.DownloadTrucksServiceLevelAgreementPolicy()
            if (!response.isSuccessful || response.failed)
                liveDataDownloadTrucksServiceLevelAgreementPolicy.postValue(null)
            else
                liveDataDownloadTrucksServiceLevelAgreementPolicy.postValue(response.body)
        }
    }

    fun GetDriverInvoiceList(userID: Int, selYear: Int, selWeek: Int) {
        viewModelScope.launch {
            val response = repo.GetDriverInvoiceList(userID, selYear, selWeek)
            if (!response.isSuccessful || response.failed)
                liveDataGetDriverInvoiceList.postValue(null)
            else
                liveDataGetDriverInvoiceList.postValue(response.body)
        }
    }

    fun GetThirdPartyInvoiceList(userID: Int, selYear: Int, selWeek: Int) {
        viewModelScope.launch {
            val response = repo.GetThirdPartyInvoiceList(userID, selYear, selWeek)
            if (!response.isSuccessful || response.failed)
                liveDataGetThirdPartyInvoiceList.postValue(null)
            else
                liveDataGetThirdPartyInvoiceList.postValue(response.body)
        }
    }

    fun DownloadInvoicePDF(
        userID: Int, invoiceId: Int
    ) {
        viewModelScope.launch {
            var response = repo.DownloadInvoicePDF(userID, invoiceId)
            if (response.failed)
                liveDataDownloadInvoicePDF.postValue(null)
            if (!response.isSuccessful)
                liveDataDownloadInvoicePDF.postValue(null)
            else
                liveDataDownloadInvoicePDF.postValue(response.body)
        }
    }

    fun GetDriverOtherCompaniesPolicy(userID: Int) {
        viewModelScope.launch {
            var response = repo.GetDriverOtherCompaniesPolicy(userID)
            if (response.failed || !response.isSuccessful)
                liveDataGetDriverOtherCompaniesPolicy.postValue(null)
            else
                liveDataGetDriverOtherCompaniesPolicy.postValue(response.body)
        }
    }

    fun DownloadDriverOtherCompaniesPolicy(
        userID: Int,
        companyId: Int,
        companyDocID: Int
    ) {
        viewModelScope.launch {
            val response = repo.DownloadDriverOtherCompaniesPolicy(userID, companyId, companyDocID)
            if (response.failed || !response.isSuccessful)
                liveDataDownloadDriverOtherCompaniesPolicy.postValue(null)
            else
                liveDataDownloadDriverOtherCompaniesPolicy.postValue(response.body)
        }
    }

    fun GetDAVehicleExpiringDocuments(userID: Int) {
        viewModelScope.launch {
            val response = repo.GetDAVehicleExpiringDocuments(userID)
            if (response.failed || !response.isSuccessful)
                liveDataVehicleExpiringDocumentsResponse.postValue(null)
            else
                liveDataVehicleExpiringDocumentsResponse.postValue(response.body)
        }
    }

    fun UploadVehDocumentFileByDriver(
        VehId: Int,
        docTypeID: Int,
        expiredDocID: Int,
        userID: Int,
        filepart: MultipartBody.Part
    ) {
        viewModelScope.launch {
            val response =
                repo.UploadVehDocumentFileByDriver(
                    VehId,
                    docTypeID,
                    expiredDocID,
                    userID,
                    filepart
                )
            if (response.failed || !response.isSuccessful)
                liveDataUploadVehDocumentFileByDriverResponse.postValue(null)
            else
                liveDataUploadVehDocumentFileByDriverResponse.postValue(response.body)
        }
    }

    fun GetDAEmergencyContact(
        userID: Int
    ) {
        viewModelScope.launch {
            val response = repo.GetDAEmergencyContact(userID)
            if (response.failed || !response.isSuccessful)
                liveDataGetDAEmergencyContact.postValue(null)
            else
                liveDataGetDAEmergencyContact.postValue(response.body)
        }

    }

    fun GetCompanySignedDocumentList(userID: Int) {
        viewModelScope.launch {
            val response = repo.GetCompanySignedDocumentList(userID)
            if (response.failed || !response.isSuccessful)
                liveDataGetCompanySignedDocumentList.postValue(null)
            else
                liveDataGetCompanySignedDocumentList.postValue(response.body)
        }
    }

    fun GetDAOutStandingDeductionList(userID: Int, companyId: Int) {
        viewModelScope.launch {
            val response = repo.GetDAOutStandingDeductionList(userID, companyId)
            if (response.failed || !response.isSuccessful)
                liveDataGetDAOutStandingDeductionList.postValue(null)
            else
                liveDataGetDAOutStandingDeductionList.postValue(response.body)
        }
    }

    fun GetDriverDeductionHistory(userID: Int, companyId: Int) {
        viewModelScope.launch {
            val response = repo.GetDriverDeductionHistory(userID, companyId)
            if (response.failed || !response.isSuccessful)
                liveDataGetDriverDeductionHistory.postValue(null)
            else {
                liveDataGetDriverDeductionHistory.postValue(response.body)
            }
        }
    }

    fun GetLatestAppVersion() {
        viewModelScope.launch {
            val response = repo.GetLatestAppVersion()
            if (response.failed || !response.isSuccessful)
                liveDataGetLatestAppVersion.postValue(null)
            else
                liveDataGetLatestAppVersion.postValue(response.body)
        }
    }

    /*fun UploadVehicleDefectImages(
        userID: Int,
        vmId: Int,
        lmid: Int,
        date: Int,
        type: Enum<DBImages>
    ) {
        viewModelScope.launch {
            val response = repo.UploadVehicleDefectImages(userID, vmId, lmid, date, type)
            if (response.failed || !response.isSuccessful)
                liveDataUploadVehicleDefectImages.postValue(null)
            else
                liveDataUploadVehicleDefectImages.postValue(response.body)
        }
    }*/

}