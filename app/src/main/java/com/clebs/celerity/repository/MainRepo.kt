package com.clebs.celerity.repository

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.clebs.celerity.models.CashFlowPieChartResponse
import com.clebs.celerity.models.CashFlowPieChartResponseItem
import com.clebs.celerity.models.GetLastWeekScore
import com.clebs.celerity.models.GetWeekYear
import com.clebs.celerity.models.TicketDepartmentsResponse
import com.clebs.celerity.models.ViewFullScheduleResponse
import com.clebs.celerity.models.requests.AddOnRideAlongRouteInfoRequest
import com.clebs.celerity.models.requests.AddOnRouteInfoRequest
import com.clebs.celerity.models.requests.GetDriverBasicInfoRequest
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
import com.clebs.celerity.models.response.GetRideAlongDriversListResponse
import com.clebs.celerity.models.response.GetRideAlongLeadDriverQuestionResponse
import com.clebs.celerity.models.response.GetRideAlongRouteInfoByIdRes
import com.clebs.celerity.models.response.GetRideAlongRouteTypeInfoResponse
import com.clebs.celerity.models.response.GetRideAlongVehicleLists
import com.clebs.celerity.models.response.GetRideAlongVehicleListsItem
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
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.utils.NoInternetDialog
import com.google.gson.Gson
import okhttp3.MultipartBody
import retrofit2.Response
import java.lang.IllegalArgumentException

class MainRepo(private val ApiService: ApiService) {

    suspend fun loginUser(requestModel: LoginRequest): LoginResponse? {
        val response = ApiService.login(requestModel)

        if (response.isSuccessful) {
            return response.body()
        } else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")

        }
        return null
    }

    suspend fun updateprofilePassword(
        userID: Double,
        oldpass: String,
        newpass: String
    ): SimpleStatusMsgResponse? {

        val response = ApiService.updateprofilepassword(userID, oldpass, newpass)

        if (response.isSuccessful) {
            return response.body()
        } else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")

        }
        return null
    }

    suspend fun updteprofileregular(request: UpdateProfileRequestBody): SimpleStatusMsgResponse? {
        val response = ApiService.updateprofileregular(request)
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


    suspend fun UseEmailAsUsername(userID: Double, emailAdddress: String): BaseResponseTwo? {
        val response = ApiService.UseEmailAsUsername(userID, emailAdddress)
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }

    suspend fun UpdateDAprofileninetydays(
        userID: Double,
        emailAdddress: String,
        phonenumber: String
    ): BaseResponseTwo? {
        val response = ApiService.updateDAProfile90days(userID, emailAdddress, phonenumber)
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
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
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

    suspend fun GetDailyWorkInfobyId(userID: Int): DailyWorkInfoByIdResponse? {
        val response = ApiService.GetDailyWorkInfobyId(userID)
        if (response.isSuccessful) {
            return response.body()
        } else {
            val errorBody = response.errorBody()?.string()
            println("Error response body: $errorBody")
        }
        return null
    }

    suspend fun GetRouteLocationInfo(locID: Int): GetRouteLocationInfoResponse? {
        val response = ApiService.GetRouteLocationInfo(locID)
        if (response.isSuccessful) {
            return response.body()
        } else {
            val errorBody = response.errorBody()?.string()
            println("Error response body: $errorBody")
        }
        return null
    }

    suspend fun GetRideAlongRouteTypeInfo(userID: Int): GetRideAlongRouteTypeInfoResponse? {
        val response = ApiService.GetRideAlongRouteTypeInfo(userID)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error response body: $errorBody")
        }
        return null
    }

    suspend fun GetDriverSignatureInformation(userID: Int): GetDriverSignatureInformationResponse? {
        val response = ApiService.GetDriverSignatureInformation(userID)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error response body: $errorBody")
        }
        return null
    }

    suspend fun UpdateDriverAgreementSignature(updateDriverSignatureRequest: UpdateDriverAgreementSignatureRequest): SimpleStatusMsgResponse? {
        val response = ApiService.UpdateDriverAgreementSignature(updateDriverSignatureRequest)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error response body: $errorBody")
        }
        return null
    }

    suspend fun AddOnRouteInfo(addOnRouteInfoRequest: AddOnRouteInfoRequest): SimpleStatusMsgResponse? {
        val response = ApiService.AddOnRouteInfo(addOnRouteInfoRequest)
        if (response.isSuccessful) {
            return response.body()
        } else {
            val errorBody = response.errorBody()?.string()
            println("Error response body: $errorBody")
        }
        return null
    }

    suspend fun SaveBreakTime(saveBreakTimeRequest: SaveBreakTimeRequest): SimpleStatusMsgResponse? {
        val response = ApiService.SaveBreakTime(saveBreakTimeRequest)
        if (response.isSuccessful) {
            return response.body()
        } else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }

    suspend fun GetDriverBreakInfo(driverId: Int): GetDriverBreakTimeInfoResponse? {
        val response = ApiService.GetDriverBreakInfo(driverId)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }

    suspend fun UpdateClockInTime(driverId: Int): SimpleStatusMsgResponse? {
        val response = ApiService.UpdateClockInTime(driverId)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }

    suspend fun UpdateClockOutTime(driverId: Int): SimpleStatusMsgResponse? {
        val response = ApiService.UpdateClockOutTime(driverId)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }

    suspend fun GetRideAlongDriversList(): GetRideAlongDriversListResponse? {
        val response = ApiService.GetRideAlongDriversList()
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }

    suspend fun GetRideAlongVehicleLists(): GetRideAlongVehicleLists? {
        val response = ApiService.GetRideAlongVehicleLists()
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }

    suspend fun GetRouteInfoById(routeID: Int): GetRouteInfoByIdRes? {
        val response = ApiService.GetRouteInfoById(routeID)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }

    suspend fun AddOnRideAlongRouteInfo(addOnRideAlongRouteInfoRequest: AddOnRideAlongRouteInfoRequest): SimpleStatusMsgResponse? {
        val response = ApiService.AddOnRideAlongRouteInfo(addOnRideAlongRouteInfoRequest)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }

    suspend fun GetRideAlongRouteInfoById(
        routeID: Int,
        leadDriverId: Int
    ): GetRideAlongRouteInfoByIdRes? {
        val response = ApiService.GetRideAlongRouteInfoById(routeID, leadDriverId)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }

    suspend fun GetDriverRouteInfoByDate(
        driverId: Int
    ): GetDriverRouteInfoByDateResponse? {
        val response = ApiService.GetDriverRouteInfoByDate(driverId)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }

    suspend fun SaveQuestionairePreparedness(
        request: SaveQuestionairePreparednessRequest
    ): SimpleQuestionResponse? {
        val response = ApiService.SaveQuestionairePreparedness(request)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }

    suspend fun SaveQuestionaireStartup(
        request: SaveQuestionaireStartupRequest
    ): SimpleQuestionResponse? {
        val response = ApiService.SaveQuestionaireStartup(request)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }

    suspend fun SaveQuestionaireOnGoingActivities(
        request: SaveQuestionaireOnGoingActivitiesRequest
    ): SimpleQuestionResponse? {
        val response = ApiService.SaveQuestionaireOnGoingActivities(request)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }

    suspend fun SaveQuestionaireDeliverProcedures(
        request: SaveQuestionaireDeliverProceduresRequest
    ): SimpleQuestionResponse? {
        val response = ApiService.SaveQuestionaireDeliverProcedures(request)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }

    suspend fun SaveQuestionaireReturnToDeliveryStation(
        request: SaveQuestionaireReturnToDeliveryStationRequest
    ): SimpleQuestionResponse? {
        val response = ApiService.SaveQuestionaireReturnToDeliveryStation(request)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }

    suspend fun SubmitFinalQuestionairebyLeadDriver(
        request: SubmitFinalQuestionairebyLeadDriverRequest
    ): SimpleQuestionResponse? {
        val response = ApiService.SubmitFinalQuestionairebyLeadDriver(request)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }

    suspend fun GetRideAlongDriverInfoByDate(
        driverId: Int
    ): RideAlongDriverInfoByDateResponse? {
        val response = ApiService.GetRideAlongDriverInfoByDate((driverId))
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }

    suspend fun DeleteOnRideAlongRouteInfo(
        routeID: Int
    ): SimpleStatusMsgResponse? {
        val response = ApiService.DeleteOnRideAlongRouteInfo(routeID)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }

    suspend fun DeleteOnRouteDetails(
        routeID: Int
    ): SimpleStatusMsgResponse? {
        val response = ApiService.DeleteOnRouteDetails(routeID)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
        return null
    }

    suspend fun SubmitRideAlongDriverFeedback(
        request: SubmitRideAlongDriverFeedbackRequest
    ): SimpleStatusMsgResponse? {
        val response = ApiService.SubmitRideAlongDriverFeedback(request)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response SubmitRideAlongDriverFeedback : $errorBody")
        }
        return null
    }

    suspend fun GetRideAlongLeadDriverQuestion(
        driverId: Int,
        routeID: Int,
        leadDriverId: Int,
        daDailyWorkId: Int
    ): GetRideAlongLeadDriverQuestionResponse? {
        val response = ApiService.GetRideAlongLeadDriverQuestion(
            driverId,
            routeID,
            leadDriverId,
            daDailyWorkId
        )
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response SubmitRideAlongDriverFeedback : $errorBody")
        }
        return null
    }

    suspend fun DeleteBreakTime(
        dawDriverBreakId: Int
    ): SimpleStatusMsgResponse? {
        val response = ApiService.DeleteBreakTime(dawDriverBreakId)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response DeleteBreakTime : $errorBody")
        }
        return null
    }

    suspend fun UpdateOnRouteInfo(
        request: GetDriverRouteInfoByDateResponseItem
    ): SimpleStatusMsgResponse? {
        val response = ApiService.UpdateOnRouteInfo(request)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response Body : $errorBody")
        }
        return null
    }

    suspend fun GetUserTickets(
        userID: Int,
        departmentId: Int?,
        startDate: String?,
        endDate: String?
    ): GetUserTicketsResponse? {
        val response = ApiService.GetUserTickets(userID, departmentId, startDate, endDate)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response Body : $errorBody")
        }
        return null
    }

    suspend fun GetUserDepartmentList(): TicketDepartmentsResponse? {
        val response = ApiService.GetUserDepartmentList()
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error response Body : $errorBody")
        }
        return null
    }

    suspend fun GetTicketRequestType(depID: Int): DepartmentRequestResponse? {
        val response = ApiService.GetTicketRequestType(depID)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response Body : $errorBody")
        }
        return null
    }

    suspend fun SaveTicketData(
        userID: Int,
        request: SaveTicketDataRequestBody
    ): SaveTicketResponse? {
        val response = ApiService.SaveTicketData(userID, request)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response Body: $errorBody")
        }
        return null
    }

    suspend fun GetTicketCommentList(userID: Int, ticketID: Int): GetTicketCommentListNewResponse? {
        val response = ApiService.GetTicketCommentList(userID, ticketID)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response Body $errorBody")
        }
        return null
    }

    suspend fun UploadTicketAttachmentDoc(
        userID: Int,
        ticketID: Int,
        file: MultipartBody.Part
    ): SimpleStatusMsgResponse? {
        val response = ApiService.UploadTicketAttachmentDoc(userID, ticketID, file)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response Body $errorBody")
        }
        return null
    }

    suspend fun SaveTicketComment(
        userID: Int,
        ticketID: Int,
        comment: String
    ): SaveCommentResponse? {
        val response = ApiService.SaveTicketComment(userID, ticketID, comment)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response Body $errorBody")
        }
        return null
    }

    suspend fun GetAvgWeekScore(userID: Int, lmid: Int): GetAvgScoreResponse? {
        val response = ApiService.GetAvgScore(userID, lmid)
        if (response.isSuccessful) {
            return response.body()
        } else {
            val errorBody = response.errorBody()?.string()
            println("Error Response Body: $errorBody")
        }
        return null
    }
    suspend fun GetLastWeekScrore(userID: Int, Weekno:Int,Year:Int): GetLastWeekScore? {
        val response = ApiService.GetLastWeekScore(userID, Weekno,Year)
        if (response.isSuccessful) {
            return response.body()
        } else {
            val errorBody = response.errorBody()?.string()
            println("Error Response Body: $errorBody")
        }
        return null
    }
    suspend fun GetCashFlowWeek(userID: Int, companyFilter: Int,selyear:Int,selweek:Int): CashFlowPieChartResponse? {
        val response = ApiService.CashFLowData(userID, companyFilter,selyear,selweek)
        if (response.isSuccessful) {
            return response.body()
        } else {
            val errorBody = response.errorBody()?.string()
            println("Error Response Body: $errorBody")
        }
        return null
    }

    suspend fun GetWeekYear(): GetWeekYear? {
        val response = ApiService.GetWeekAndYear()
        if (response.isSuccessful) {
            return response.body()
        } else {
            val errorBody = response.errorBody()?.string()
            println("Error Response Body: $errorBody")
        }
        return null
    }

    suspend fun GetVechileSchedule(
        userID: Int,
        lmid: Int,
        year: Int,
        week: Int
    ): ViewFullScheduleResponse? {
        val response = ApiService.GetVechileScheduleInfo(userID, lmid, year, week)
        if (response.isSuccessful) {
            return response.body()
        } else {
            val errorBody = response.errorBody()?.string()
            println("Error Response Body: $errorBody")
        }
        return null
    }

    suspend fun UploadTicketCommentAttachmentDoc(
        userID: Int,
        ticketCommentId: Int,
        file: MultipartBody.Part
    ): SimpleStatusMsgResponse? {
        val response = ApiService.UploadTicketCommentAttachmentDoc(userID, ticketCommentId, file)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response Body: $errorBody")
        }
        return null
    }

    suspend fun GetUserTicketDocuments(
        userID: Int,
        ticketID: Int
    ): GetUserTicketDocumentsResponse? {
        val response = ApiService.GetUserTicketDocuments(userID, ticketID)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response Body: $errorBody")
        }
        return null
    }

    suspend fun GetThirdPartyAccess(userID: Int): SimpleStatusMsgResponse? {
        val response = ApiService.GetThirdPartyAccess(userID)
        if (response.isSuccessful) {
            return response.body()
        } else {
            val errorBody = response.errorBody()?.string()
            println("Error Response Body: $errorBody")
        }
        return null
    }

    suspend fun RemoveThirdPartyAccess(userID: Int):SimpleStatusMsgResponse?{

        val response=ApiService.RemoveThirdPartyAccess(userID)
        if (response.isSuccessful) {
            return response.body()
        } else {
            val errorBody = response.errorBody()?.string()
            println("Error Response Body: $errorBody")
        }
        return null
    }

    suspend fun DownloadInvoicePDF(
        userID: Int,selyear: Int
    ): DownloadInvoicePDFResponse? {
        val response = ApiService.DownloadInvoicePDF(userID,selyear)
        if(response.isSuccessful){
            return response.body()
        }else{
            val errorBody = response.errorBody()?.string()
            println("Error Response Body: $errorBody")
        }
        return null
    }
    suspend fun DownloadThirdPartyInvoicePDF(
        userID: Int,selyear: Int
    ): DownloadThirdPartyInvoicePDFResponse? {
        val response = ApiService.DownloadThirdPartyInvoicePDF(userID,selyear)
        if(response.isSuccessful){
            return response.body()
        }else{
            val errorBody = response.errorBody()?.string()
            println("Error Response Body: $errorBody")
        }
        return null
    }

}