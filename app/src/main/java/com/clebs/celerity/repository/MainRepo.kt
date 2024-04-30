package com.clebs.celerity.repository

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.clebs.celerity.models.CashFlowPieChartResponse
import com.clebs.celerity.models.CashFlowPieChartResponseItem
import com.clebs.celerity.models.GetLastWeekScore
import com.clebs.celerity.models.GetWeekYear
import com.clebs.celerity.models.SimpleNetworkResponse
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
import com.clebs.celerity.models.requests.SaveVehicleInspectionInfo
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
import com.clebs.celerity.models.response.GetRideAlongVehicleListsItem
import com.clebs.celerity.models.response.GetRouteInfoByIdRes
import com.clebs.celerity.models.response.GetRouteLocationInfoResponse
import com.clebs.celerity.models.response.GetTicketCommentListNewResponse
import com.clebs.celerity.models.response.GetTicketCommentListResponse
import com.clebs.celerity.models.response.GetUserTicketDocumentsResponse
import com.clebs.celerity.models.response.GetUserTicketsResponse
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
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.utils.NoInternetDialog
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import java.lang.IllegalArgumentException

class MainRepo(private val ApiService: ApiService) {

    private inline fun <T> safeApiCall(apiCall: () -> Response<T>): SimpleNetworkResponse<T> {
        return try {
            SimpleNetworkResponse.success(apiCall.invoke())
        } catch (e: Exception) {
            Log.d("SafeException","$e")
            SimpleNetworkResponse.failure(e)
        }
    }

    suspend fun loginUser(requestModel: LoginRequest): SimpleNetworkResponse<LoginResponse> {
        /*        val response = ApiService.login(requestModel)

                if (response.isSuccessful) {
                    return safeApiCall {
                        response.body()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response body: $errorBody")

                }
                return null*/
        return safeApiCall {
            ApiService.login(requestModel)
        }
    }

    suspend fun updateprofilePassword(
        userID: Double,
        oldpass: String,
        newpass: String
    ): SimpleNetworkResponse<SimpleStatusMsgResponse> {

        /*        val response = ApiService.updateprofilepassword(userID, oldpass, newpass)

                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response body: $errorBody")

                }
                return null*/
        return safeApiCall {
            ApiService.updateprofilepassword(userID, oldpass, newpass)
        }
    }

    suspend fun updteprofileregular(request: UpdateProfileRequestBody): SimpleNetworkResponse<SimpleStatusMsgResponse> {
        /*        val response = ApiService.updateprofileregular(request)
                if (response.isSuccessful) {

                    return response.body()
                }
                return null*/
        return safeApiCall {
            ApiService.updateprofileregular(request)
        }
    }

    suspend fun getVechileinformation(
        userID: Double,
        LmID: Double,
        VechileRegistrationno: String
    ): SimpleNetworkResponse<GetVechileInformationResponse> {
        /*val response = ApiService.getVehicleInformation(userID, LmID, VechileRegistrationno)
        if (response.isSuccessful) {
            return response.body()
        }*/
        return safeApiCall {
            ApiService.getVehicleInformation(userID, LmID, VechileRegistrationno)
        }
    }

    suspend fun getDriverSignatureInfo(userID: Double): SimpleNetworkResponse<GetsignatureInformation> {
        /*val response = ApiService.getDriverSignatureInfoforPolicy(userID)
        if (response.isSuccessful) {
            return response.body()
        }*/
        return safeApiCall {
            ApiService.getDriverSignatureInfoforPolicy(userID)
        }
    }

    suspend fun logout(): logoutModel? {
        val response = ApiService.Logout()
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }

    suspend fun GetDriversBasicInfo(userID: Double): SimpleNetworkResponse<DriversBasicInformationModel> {
        /*val response = ApiService.GetDriversBasicInfo(userID)
        if (response.isSuccessful) {
            return response.body()
        }*/
        return safeApiCall {
            ApiService.GetDriversBasicInfo(userID)
        }
    }

    suspend fun CheckIFTodayCheckIsDone(): SimpleNetworkResponse<CheckIFTodayCheckIsDone> {
        /*val response = ApiService.CheckifTodayCheckIsDone()
        if (response.isSuccessful) {
            return response.body()
        }*/
        return safeApiCall {
            ApiService.CheckifTodayCheckIsDone()
        }
    }


    suspend fun UseEmailAsUsername(
        userID: Double,
        emailAdddress: String
    ): SimpleNetworkResponse<BaseResponseTwo> {
        /*val response = ApiService.UseEmailAsUsername(userID, emailAdddress)
        if (response.isSuccessful) {
            return response.body()
        }*/
        return safeApiCall {
            ApiService.UseEmailAsUsername(userID, emailAdddress)
        }
    }

    suspend fun UpdateDAprofileninetydays(
        userID: Double,
        emailAdddress: String,
        phonenumber: String
    ): SimpleNetworkResponse<BaseResponseTwo> {
        /*val response = ApiService.updateDAProfile90days(userID, emailAdddress, phonenumber)
        if (response.isSuccessful) {
            return response.body()
        }*/
        return safeApiCall {
            ApiService.updateDAProfile90days(userID, emailAdddress, phonenumber)
        }
    }

    suspend fun GetVehicleDefectSheetInfo(userID: Int): SimpleNetworkResponse<GetVehicleDefectSheetInfoResponse> {
        /*        val response = ApiService.GetVehicleDefectSheetInfo(userID)
                if (response.isSuccessful)
                    return response.body()*/
        return safeApiCall {
            ApiService.GetVehicleDefectSheetInfo(userID)
        }
    }

    suspend fun SaveVehDefectSheet(vehicleDefectSheetInfoResponse: SaveVechileDefectSheetRequest): SimpleNetworkResponse<SaveVehDefectSheetResponse> {
        /*val response = ApiService.SaveVehDefectSheet(vehicleDefectSheetInfoResponse)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }*/
        return safeApiCall {
            ApiService.SaveVehDefectSheet(vehicleDefectSheetInfoResponse)
        }
    }

    suspend fun GetVehicleInformation(
        userID: Int,
        vehRegNo: String
    ): SimpleNetworkResponse<GetVechileInformationResponse> {
        /*      val response = ApiService.GetVehicleInformation(userID, vehRegNo)
              if (response.isSuccessful)
                  return response.body()*/
        return safeApiCall {
            ApiService.GetVehicleInformation(userID, vehRegNo)
        }
    }

    suspend fun GetVehicleImageUploadInfo(userID: Int): SimpleNetworkResponse<GetVehicleImageUploadInfoResponse> {
        /*        val response = ApiService.GetVehicleImageUploadInfo(userID)
                Log.d("GetVehicleImageUploadInfoRes : ", "$response")
                if (response.isSuccessful)
                    return response.body()
                else if (response.code() == 404) {
                    val res = response.errorBody()?.string()
                    return Gson().fromJson(res, GetVehicleImageUploadInfoResponse::class.java)
                }*/
        return safeApiCall {
            ApiService.GetVehicleImageUploadInfo(userID)
        }
    }

    suspend fun uploadVehicleImage(
        userID: Int,
        image: MultipartBody.Part,
        type: Int
    ): SimpleNetworkResponse<SimpleStatusMsgResponse> {
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
        /*        if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error response body: $errorBody")
                }
                return null*/
        return safeApiCall {
            response
        }
    }

    suspend fun GetDailyWorkInfobyId(userID: Int): SimpleNetworkResponse<DailyWorkInfoByIdResponse> {
        /*        val response = ApiService.GetDailyWorkInfobyId(userID)
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Error response body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.GetDailyWorkInfobyId(userID)
        }
    }

    suspend fun GetRouteLocationInfo(locID: Int): SimpleNetworkResponse<GetRouteLocationInfoResponse> {
        /*    val response = ApiService.GetRouteLocationInfo(locID)
            if (response.isSuccessful) {
                return response.body()
            } else {
                val errorBody = response.errorBody()?.string()
                println("Error response body: $errorBody")
            }*/
        return safeApiCall {
            ApiService.GetRouteLocationInfo(locID)
        }
    }

    suspend fun GetRideAlongRouteTypeInfo(userID: Int): SimpleNetworkResponse<GetRideAlongRouteTypeInfoResponse> {
        /*        val response = ApiService.GetRideAlongRouteTypeInfo(userID)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error response body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.GetRideAlongRouteTypeInfo(userID)
        }
    }

    suspend fun GetDriverRouteTypeInfo(userID: Int): SimpleNetworkResponse<GetRideAlongRouteTypeInfoResponse> {
        /*        val response = ApiService.GetDriverRouteTypeInfo(userID)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error response body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.GetDriverRouteTypeInfo(userID)
        }
    }

    suspend fun GetDriverSignatureInformation(userID: Int): SimpleNetworkResponse<GetDriverSignatureInformationResponse> {
        /*        val response = ApiService.GetDriverSignatureInformation(userID)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error response body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.GetDriverSignatureInformation(userID)
        }
    }

    suspend fun UpdateDriverAgreementSignature(updateDriverSignatureRequest: UpdateDriverAgreementSignatureRequest):
            SimpleNetworkResponse<SimpleStatusMsgResponse> {
        /*        val response = ApiService.UpdateDriverAgreementSignature(updateDriverSignatureRequest)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error response body: $errorBody")
                }
                       return safeApiCall {

                }*/
        return safeApiCall {
            ApiService.UpdateDriverAgreementSignature(updateDriverSignatureRequest)
        }
    }

    suspend fun AddOnRouteInfo(addOnRouteInfoRequest: AddOnRouteInfoRequest): SimpleNetworkResponse<SimpleStatusMsgResponse> {
        /*        val response = ApiService.AddOnRouteInfo(addOnRouteInfoRequest)
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Error response body: $errorBody")
                }
                       return safeApiCall {

                }*/
        return safeApiCall {
            ApiService.AddOnRouteInfo(addOnRouteInfoRequest)
        }
    }

    suspend fun SaveBreakTime(saveBreakTimeRequest: SaveBreakTimeRequest): SimpleNetworkResponse<SimpleStatusMsgResponse> {
        /*val response = ApiService.SaveBreakTime(saveBreakTimeRequest)
        if (response.isSuccessful) {
            return response.body()
        } else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }
               return safeApiCall {

        }*/
        return safeApiCall {
            ApiService.SaveBreakTime(saveBreakTimeRequest)
        }
    }

    suspend fun GetDriverBreakInfo(driverId: Int): SimpleNetworkResponse<GetDriverBreakTimeInfoResponse> {
        /*val response = ApiService.GetDriverBreakInfo(driverId)
        if (response.isSuccessful)
            return response.body()
        else {
            val errorBody = response.errorBody()?.string()
            println("Error Response body: $errorBody")
        }*/
        return safeApiCall {
            ApiService.GetDriverBreakInfo(driverId)
        }
    }

    suspend fun UpdateClockInTime(driverId: Int): SimpleNetworkResponse<SimpleStatusMsgResponse> {
        /*        val response = ApiService.UpdateClockInTime(driverId)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response body: $errorBody")
                }
                return null*/
        return safeApiCall {
            ApiService.UpdateClockInTime(driverId)
        }
    }

    suspend fun UpdateClockOutTime(driverId: Int): SimpleNetworkResponse<SimpleStatusMsgResponse> {
        /*        val response = ApiService.UpdateClockOutTime(driverId)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response body: $errorBody")
                }
                return null*/
        return safeApiCall {
            ApiService.UpdateClockOutTime(driverId)
        }
    }

    suspend fun GetRideAlongDriversList(): SimpleNetworkResponse<GetRideAlongDriversListResponse> {
        /*        val response = ApiService.GetRideAlongDriversList()
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.GetRideAlongDriversList()
        }
    }

    suspend fun GetRideAlongVehicleLists(): SimpleNetworkResponse<GetRideAlongVehicleLists> {
        /*        val response = ApiService.GetRideAlongVehicleLists()
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response body: $errorBody")
                }
                return null*/
        return safeApiCall {
            ApiService.GetRideAlongVehicleLists()
        }
    }

    suspend fun GetRouteInfoById(routeID: Int): SimpleNetworkResponse<GetRouteInfoByIdRes> {
        /*        val response = ApiService.GetRouteInfoById(routeID)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.GetRouteInfoById(routeID)
        }
    }

    suspend fun AddOnRideAlongRouteInfo(addOnRideAlongRouteInfoRequest: AddOnRideAlongRouteInfoRequest):
            SimpleNetworkResponse<SimpleStatusMsgResponse> {
        /*        val response = ApiService.AddOnRideAlongRouteInfo(addOnRideAlongRouteInfoRequest)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response body: $errorBody")
                }
                return null*/
        return safeApiCall {
            ApiService.AddOnRideAlongRouteInfo(addOnRideAlongRouteInfoRequest)
        }
    }

    suspend fun GetRideAlongRouteInfoById(
        routeID: Int,
        leadDriverId: Int
    ): SimpleNetworkResponse<GetRideAlongRouteInfoByIdRes> {
        /*        val response = ApiService.GetRideAlongRouteInfoById(routeID, leadDriverId)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.GetRideAlongRouteInfoById(routeID, leadDriverId)
        }
    }

    suspend fun GetDriverRouteInfoByDate(
        driverId: Int
    ): SimpleNetworkResponse<GetDriverRouteInfoByDateResponse> {
        /*        val response = ApiService.GetDriverRouteInfoByDate(driverId)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.GetDriverRouteInfoByDate(driverId)
        }
    }

    suspend fun SaveQuestionairePreparedness(
        request: SaveQuestionairePreparednessRequest
    ): SimpleNetworkResponse<SimpleQuestionResponse> {
        /*        val response = ApiService.SaveQuestionairePreparedness(request)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.SaveQuestionairePreparedness(request)
        }
    }

    suspend fun SaveQuestionaireStartup(
        request: SaveQuestionaireStartupRequest
    ): SimpleNetworkResponse<SimpleQuestionResponse> {
        /* val response = ApiService.SaveQuestionaireStartup(request)
         if (response.isSuccessful)
             return response.body()
         else {
             val errorBody = response.errorBody()?.string()
             println("Error Response body: $errorBody")
         }*/
        return safeApiCall {
            ApiService.SaveQuestionaireStartup(request)
        }
    }

    suspend fun SaveQuestionaireOnGoingActivities(
        request: SaveQuestionaireOnGoingActivitiesRequest
    ): SimpleNetworkResponse<SimpleQuestionResponse> {
        /*        val response = ApiService.SaveQuestionaireOnGoingActivities(request)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.SaveQuestionaireOnGoingActivities(request)
        }
    }

    suspend fun SaveQuestionaireDeliverProcedures(
        request: SaveQuestionaireDeliverProceduresRequest
    ): SimpleNetworkResponse<SimpleQuestionResponse> {
        /*        val response = ApiService.SaveQuestionaireDeliverProcedures(request)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.SaveQuestionaireDeliverProcedures(request)
        }
    }

    suspend fun SaveQuestionaireReturnToDeliveryStation(
        request: SaveQuestionaireReturnToDeliveryStationRequest
    ): SimpleNetworkResponse<SimpleQuestionResponse> {
        /*        val response = ApiService.SaveQuestionaireReturnToDeliveryStation(request)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.SaveQuestionaireReturnToDeliveryStation(request)
        }
    }

    suspend fun SubmitFinalQuestionairebyLeadDriver(
        request: SubmitFinalQuestionairebyLeadDriverRequest
    ): SimpleNetworkResponse<SimpleQuestionResponse> {
        /*        val response = ApiService.SubmitFinalQuestionairebyLeadDriver(request)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.SubmitFinalQuestionairebyLeadDriver(request)
        }
    }

    suspend fun GetRideAlongDriverInfoByDate(
        driverId: Int
    ): SimpleNetworkResponse<RideAlongDriverInfoByDateResponse> {
        /*        val response = ApiService.GetRideAlongDriverInfoByDate((driverId))
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.GetRideAlongDriverInfoByDate((driverId))
        }
    }

    suspend fun DeleteOnRideAlongRouteInfo(
        routeID: Int
    ): SimpleNetworkResponse<SimpleStatusMsgResponse> {
        /*        val response = ApiService.DeleteOnRideAlongRouteInfo(routeID)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response body: $errorBody")
                }
                return null*/
        return safeApiCall {
            ApiService.DeleteOnRideAlongRouteInfo(routeID)
        }
    }

    suspend fun DeleteOnRouteDetails(
        routeID: Int
    ): SimpleNetworkResponse<SimpleStatusMsgResponse> {
        /*        val response = ApiService.DeleteOnRouteDetails(routeID)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response body: $errorBody")
                }
                return null*/
        return safeApiCall {
            ApiService.DeleteOnRouteDetails(routeID)
        }
    }

    suspend fun SubmitRideAlongDriverFeedback(
        request: SubmitRideAlongDriverFeedbackRequest
    ): SimpleNetworkResponse<SimpleStatusMsgResponse> {
        /*        val response = ApiService.SubmitRideAlongDriverFeedback(request)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response SubmitRideAlongDriverFeedback : $errorBody")
                }
                return null*/
        return safeApiCall {
            ApiService.SubmitRideAlongDriverFeedback(request)
        }
    }

    suspend fun GetRideAlongLeadDriverQuestion(
        driverId: Int,
        routeID: Int,
        leadDriverId: Int,
        daDailyWorkId: Int
    ): SimpleNetworkResponse<GetRideAlongLeadDriverQuestionResponse> {
        /*        val response = ApiService.GetRideAlongLeadDriverQuestion(
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
                }*/
        return safeApiCall {
            ApiService.GetRideAlongLeadDriverQuestion(
                driverId,
                routeID,
                leadDriverId,
                daDailyWorkId
            )
        }
    }

    suspend fun DeleteBreakTime(
        dawDriverBreakId: Int
    ): SimpleNetworkResponse<SimpleStatusMsgResponse> {
        /*        val response = ApiService.DeleteBreakTime(dawDriverBreakId)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response DeleteBreakTime : $errorBody")
                }
                return null*/
        return safeApiCall {
            ApiService.DeleteBreakTime(dawDriverBreakId)
        }
    }

    suspend fun UpdateOnRouteInfo(
        request: GetDriverRouteInfoByDateResponseItem
    ): SimpleNetworkResponse<SimpleStatusMsgResponse> {
        /*        val response = ApiService.UpdateOnRouteInfo(request)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body : $errorBody")
                }
                return null*/
        return safeApiCall {
            ApiService.UpdateOnRouteInfo(request)
        }
    }

    suspend fun GetUserTickets(
        userID: Int,
        departmentId: Int?,
        startDate: String?,
        endDate: String?,
        includeCompleted: Boolean?
    ): SimpleNetworkResponse<GetUserTicketsResponse> {
        /*        val response =
                    ApiService.GetUserTickets(userID, departmentId, startDate, endDate, includeCompleted)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body : $errorBody")
                }*/
        return safeApiCall {
            ApiService.GetUserTickets(userID, departmentId, startDate, endDate, includeCompleted)
        }
    }

    suspend fun GetUserDepartmentList(): SimpleNetworkResponse<TicketDepartmentsResponse> {
        /*        val response = ApiService.GetUserDepartmentList()
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error response Body : $errorBody")
                }*/
        return safeApiCall {
            ApiService.GetUserDepartmentList()
        }
    }

    suspend fun GetTicketRequestType(depID: Int): SimpleNetworkResponse<DepartmentRequestResponse> {
        /*        val response = ApiService.GetTicketRequestType(depID)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body : $errorBody")
                }*/
        return safeApiCall {
            ApiService.GetTicketRequestType(depID)
        }
    }

    suspend fun SaveTicketData(
        userID: Int,
        request: SaveTicketDataRequestBody
    ): SimpleNetworkResponse<SaveTicketResponse> {
        /*        val response = ApiService.SaveTicketData(userID, request)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.SaveTicketData(userID, request)
        }
    }

    suspend fun GetTicketCommentList(
        userID: Int,
        ticketID: Int
    ): SimpleNetworkResponse<GetTicketCommentListNewResponse> {
        /*        val response = ApiService.GetTicketCommentList(userID, ticketID)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body $errorBody")
                }*/
        return safeApiCall {
            ApiService.GetTicketCommentList(userID, ticketID)
        }
    }

    suspend fun UploadTicketAttachmentDoc(
        userID: Int,
        ticketID: Int,
        file: MultipartBody.Part
    ): SimpleNetworkResponse<SimpleStatusMsgResponse> {
        /*        val response = ApiService.UploadTicketAttachmentDoc(userID, ticketID, file)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body $errorBody")
                }
                       return safeApiCall {

                }*/
        return safeApiCall {
            ApiService.UploadTicketAttachmentDoc(userID, ticketID, file)
        }
    }

    suspend fun SaveTicketComment(
        userID: Int,
        ticketID: Int,
        comment: String
    ): SimpleNetworkResponse<SaveCommentResponse> {
        /*        val response = ApiService.SaveTicketComment(userID, ticketID, comment)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body $errorBody")
                }*/
        return safeApiCall {
            ApiService.SaveTicketComment(userID, ticketID, comment)
        }
    }

    suspend fun GetAvgWeekScore(
        userID: Int,
        lmid: Int
    ): SimpleNetworkResponse<GetAvgScoreResponse> {
        /*        val response = ApiService.GetAvgScore(userID, lmid)
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.GetAvgScore(userID, lmid)
        }
    }

    suspend fun GetLastWeekScrore(
        userID: Int,
        Weekno: Int,
        Year: Int
    ): SimpleNetworkResponse<GetLastWeekScore> {
        /*        val response = ApiService.GetLastWeekScore(userID, Weekno, Year)
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.GetLastWeekScore(userID, Weekno, Year)
        }
    }

    suspend fun GetCashFlowWeek(
        userID: Int,
        companyFilter: Int,
        selyear: Int,
        selweek: Int
    ): SimpleNetworkResponse<CashFlowPieChartResponse> {
        /*        val response = ApiService.CashFLowData(
                    userID,
                    //companyFilter,
                    selyear,
                    selweek
                )
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.CashFLowData(
                userID,
                //companyFilter,
                selyear,
                selweek
            )
        }
    }

    suspend fun GetWeekYear(): SimpleNetworkResponse<GetWeekYear> {
        /*        val response = ApiService.GetWeekAndYear()
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.GetWeekAndYear()
        }
    }

    suspend fun GetVechileSchedule(
        userID: Int,
        lmid: Int,
        year: Int,
        week: Int
    ): SimpleNetworkResponse<ViewFullScheduleResponse> {
        /*        val response = ApiService.GetVechileScheduleInfo(userID, lmid, year, week)
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.GetVechileScheduleInfo(userID, lmid, year, week)
        }
    }

    suspend fun UploadTicketCommentAttachmentDoc(
        userID: Int,
        ticketCommentId: Int,
        file: MultipartBody.Part
    ): SimpleNetworkResponse<SimpleStatusMsgResponse> {
        /*        val response = ApiService.UploadTicketCommentAttachmentDoc(userID, ticketCommentId, file)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body: $errorBody")
                }
                return null*/
        return safeApiCall {
            ApiService.UploadTicketCommentAttachmentDoc(userID, ticketCommentId, file)
        }
    }

    suspend fun GetUserTicketDocuments(
        userID: Int,
        ticketID: Int
    ): SimpleNetworkResponse<GetUserTicketDocumentsResponse> {
        /*        val response = ApiService.GetUserTicketDocuments(userID, ticketID)
                if (response.isSuccessful)
                    return response.body()
                else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.GetUserTicketDocuments(userID, ticketID)
        }
    }

    suspend fun GetThirdPartyAccess(userID: Int): SimpleNetworkResponse<SimpleStatusMsgResponse> {
        /*        val response = ApiService.GetThirdPartyAccess(userID)
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body: $errorBody")
                }
                return null*/
        return safeApiCall {
            ApiService.GetThirdPartyAccess(userID)
        }
    }

    suspend fun RemoveThirdPartyAccess(userID: Int): SimpleNetworkResponse<SimpleStatusMsgResponse> {
        /*
                val response = ApiService.RemoveThirdPartyAccess(userID)
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body: $errorBody")
                }
                return null*/
        return safeApiCall {
            ApiService.RemoveThirdPartyAccess(userID)
        }
    }

    suspend fun DownloadInvoicePDF(
        userID: Int, selyear: Int
    ): SimpleNetworkResponse<DownloadInvoicePDFResponse> {
        /*        val response = ApiService.DownloadInvoicePDF(userID, selyear)
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.DownloadInvoicePDF(userID, selyear)
        }
    }

    suspend fun DownloadThirdPartyInvoicePDF(
        userID: Int, selyear: Int
    ): SimpleNetworkResponse<DownloadThirdPartyInvoicePDFResponse> {
        /*        val response = ApiService.DownloadThirdPartyInvoicePDF(userID, selyear)
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.DownloadThirdPartyInvoicePDF(userID, selyear)
        }
    }

    suspend fun DownloadSignedDAHandbook(
        handbookId: Int
    ): SimpleNetworkResponse<ResponseBody> {
        /*        val response = ApiService.DownloadSignedDAHandbook(handbookId)
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.DownloadSignedDAHandbook(handbookId)
        }
    }

    suspend fun DownloadSignedGDPRPOLICY(
        handbookId: Int
    ): SimpleNetworkResponse<ResponseBody> {
        /*        val response = ApiService.DownloadSignedGDPRPOLICY(handbookId)
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.DownloadSignedGDPRPOLICY(handbookId)
        }
    }

    suspend fun DownloadSignedServiceLevelAgreement(
        handbookId: Int
    ): SimpleNetworkResponse<ResponseBody> {
        /*        val response = ApiService.DownloadSignedServiceLevelAgreement(handbookId)
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.DownloadSignedServiceLevelAgreement(handbookId)
        }
    }

    suspend fun DownloadSignedPrivacyPolicy(
        handbookId: Int
    ): SimpleNetworkResponse<ResponseBody> {
        /*        val response = ApiService.DownloadSignedPrivacyPolicy(handbookId)
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.DownloadSignedPrivacyPolicy(handbookId)
        }
    }

    suspend fun DownloadSignedDAEngagement(
        handbookId: Int
    ): SimpleNetworkResponse<ResponseBody> {
        /*        val response = ApiService.DownloadSignedDAEngagement(handbookId)
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.DownloadSignedDAEngagement(handbookId)
        }
    }

    suspend fun GetRideAlongDriverFeedbackQuestion(
        driverId: Int,
        routeID: Int,
        leadDriverId: Int,
        daDailyWorkId: Int
    ): SimpleNetworkResponse<GetRideAlongDriverFeedbackQuestionResponse> {
        /*        val response = ApiService.GetRideAlongDriverFeedbackQuestion(
                    driverId,
                    routeID,
                    leadDriverId,
                    daDailyWorkId
                )
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Error Response Body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.GetRideAlongDriverFeedbackQuestion(
                driverId,
                routeID,
                leadDriverId,
                daDailyWorkId
            )
        }
    }

    suspend fun SaveDeviceInformation(
        body: SaveDeviceInformationRequest
    ): SimpleNetworkResponse<SimpleStatusMsgResponse> {
        /*        val response = ApiService.SaveDeviceInformation(body)
                if (response.isSuccessful) {
                    println("MainRepo SaveDeviceInformationApi Success")
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("SaveDeviceInformation: Error Response Bpdy: $errorBody")
                }
                return null*/
        return safeApiCall {
            ApiService.SaveDeviceInformation(body)
        }
    }

    suspend fun GetNotificationListByUserId(
        userID: Int
    ): SimpleNetworkResponse<NotificationResponse> {
        /*        val response = ApiService.GetNotificationListByUserId(userID)
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("GetNotificationListByUserIId: Error Response Body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.GetNotificationListByUserId(userID)
        }
    }

    suspend fun SaveVehicleInspectionInfo(
        body: SaveVehicleInspectionInfo
    ): SimpleNetworkResponse<SimpleStatusMsgResponse> {
        /*        val response = ApiService.SaveVehicleInspectionInformation(body)
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("GetNotificationListByUserIId: Error Response Body: $errorBody")
                }*/
        return safeApiCall {
            ApiService.SaveVehicleInspectionInformation(body)
        }
    }

    suspend fun GetVehicleInfobyDriverId(
        userID: Int, date: String
    ): SimpleNetworkResponse<GetvehicleInfoByDriverId> {
        /*        val response = ApiService.GetVehicleInfobyDriverId(userID, date)
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("GetNotificationListByUserIId: Error Response Body: $errorBody")
                }
                return null*/
        return safeApiCall {
            ApiService.GetVehicleInfobyDriverId(userID, date)
        }
    }

    suspend fun uploadVehicleImages(
        userID: Int,
        imageList: List<MultipartBody.Part>
    ): SimpleNetworkResponse<SimpleStatusMsgResponse> {
        return safeApiCall {
            ApiService.uploadVehicleImages(userID, imageList)
        }
    }


}