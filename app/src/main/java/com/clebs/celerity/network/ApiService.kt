package com.clebs.celerity.network

import com.clebs.celerity.models.CashFlowPieChartResponse
import com.clebs.celerity.models.DownloadDriverOtherCompaniesPolicyResponse
import com.clebs.celerity.models.GetLastWeekScore
import com.clebs.celerity.models.GetWeekYear
import com.clebs.celerity.models.TicketDepartmentsResponse
import com.clebs.celerity.models.ViewFullScheduleResponse
import com.clebs.celerity.models.requests.AddOnRideAlongRouteInfoRequest
import com.clebs.celerity.models.requests.AddOnRouteInfoRequest
import com.clebs.celerity.models.requests.ApproveDaDailyRotaRequest
import com.clebs.celerity.models.requests.CreateDaikyworkRequestBody
import com.clebs.celerity.models.requests.GetDefectSheetBasicInfoRequestModel
import com.clebs.celerity.models.response.DriversBasicInformationModel
import com.clebs.celerity.models.response.GetVechileInformationResponse
import com.clebs.celerity.models.response.GetsignatureInformation

import com.clebs.celerity.models.requests.LoginRequest
import com.clebs.celerity.models.response.LoginResponse
import com.clebs.celerity.models.requests.SaveBreakStartEndTImeRequestModel
import com.clebs.celerity.models.requests.SaveBreakTimeRequest
import com.clebs.celerity.models.requests.SaveDriverDocumentSignatureRequest
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
import com.clebs.celerity.models.requests.UpdateDeductioRequest
import com.clebs.celerity.models.requests.UpdateDriverAgreementSignatureRequest
import com.clebs.celerity.models.requests.UpdateProfileRequestBody
import com.clebs.celerity.models.requests.logoutModel
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
import com.clebs.celerity.models.response.GetDAVehicleExpiredDocumentsResponse
import com.clebs.celerity.models.response.GetDailyWorkDetailsResponse
import com.clebs.celerity.models.response.GetDefectSheetBasicInfoResponse
import com.clebs.celerity.models.response.GetDriverBreakTimeInfoResponse
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
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("api/Authentication/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @GET("/api/Vehicle/GetVehicleInformation")
    suspend fun getVehicleInformation(
        @Query("userId") userId: Double,
        @Query("lmId") lmId: Double,
        @Query("vehRegNo") vehRegNo: String
    ): Response<GetVechileInformationResponse>


    @GET("/api/Drivers/GetDriverSignatureInformation/{userId}")
    suspend fun getDriverSignatureInfoforPolicy(@Path("userId") userId: Double): Response<GetsignatureInformation>

    @GET("/api/Home/Logout")
    suspend fun Logout(): Response<logoutModel>

    @GET("/api/Drivers/GetDriverBasicInformation/{userId}")
    suspend fun GetDriversBasicInfo(@Path("userId") userId: Double): Response<DriversBasicInformationModel>

    @POST("/api/DaDailyWorks/SaveBreakStartAndEndTime")
    suspend fun SaveBreakStartEndTime(@Body body: SaveBreakStartEndTImeRequestModel): Response<BaseResponseTwo>

    @POST("/api/DaDailyWorks/DeleteBreakTime/{dawDriverBreakId}")
    suspend fun deleteBreakTime(@Path("dawDriverBreakId") dawDriverBreakId: Int): Response<BaseResponseTwo>

    @POST("/api/DailyWorks/SaveVehDefectSheet")
    suspend fun SaveVichileDeffectSheet(@Body body: SaveVechileDefectSheetRequest): Response<BaseResponseTwo>

    @POST("/api/DailyWorks/GetDefectSheetBasicInfo")
    suspend fun GetDefectSheetBasicInfo(@Body body: GetDefectSheetBasicInfoRequestModel): Response<GetDefectSheetBasicInfoResponse>


    @PUT("/api/Drivers/UpdateUsernameFromEmail")
    suspend fun UseEmailAsUsername(
        @Query("userId") userId: Double,
        @Query("emailAddress") emailAddress: String
    ): Response<BaseResponseTwo>

    @PUT("/api/Drivers/UpdateDAProfileIn90Days")
    suspend fun updateDAProfile90days(
        @Query("userId") userId: Double,
        @Query("emailId") emailAddress: String,
        @Query("phonenumber") phonenumber: String
    ): Response<BaseResponseTwo>


    @POST("/api/DailyWorks/CheckIfTodayDefecChecktIsDone")
    suspend fun CheckifTodayCheckIsDone(): Response<CheckIFTodayCheckIsDone>

    @GET("/api/DailyWorks/GetDetailsDailyWork/{dwId}")
    suspend fun GetDailyworkDetails(@Path("dwid") dwid: Double): Response<GetDailyWorkDetailsResponse>

    @POST("/api/DailyWorks/CreateDailyWork")
    suspend fun createDailyWork(@Body body: CreateDaikyworkRequestBody): Response<BaseResponseTwo>

    @POST("/api/Drivers/SaveDriverDocumentSingature")
    suspend fun saveDriversDocumentSignature(@Body body: SaveDriverDocumentSignatureRequest): Response<BaseResponseTwo>

    @GET("/api/DailyWorks/GetVehicleDefectSheetInfo/{userId}")
    suspend fun GetVehicleDefectSheetInfo(@Path("userId") userId: Int): Response<GetVehicleDefectSheetInfoResponse>

    @POST("/api/Vehicle/SaveVehDefectSheet")
    suspend fun SaveVehDefectSheet(@Body body: SaveVechileDefectSheetRequest): Response<SaveVehDefectSheetResponse>

    @GET("/api/Vehicle/GetVehicleInformation")
    suspend fun GetVehicleInformation(
        @Query("userId") userId: Int,
        @Query("vehRegNo") vehRegNo: String = ""
    ): Response<GetVechileInformationResponse>

    @GET("/api/DailyWorks/GetVehicleImageUploadedInfo/{userId}")
    suspend fun GetVehicleImageUploadInfo(
        @Path("userId") userId: Int,
        @Query("vmId") vmId:Int,
        @Query("date") date: String
    ): Response<GetVehicleImageUploadInfoResponse>

    @Multipart
    @POST("/api/Vehicle/UploadFaceMaskFile")
    suspend fun UploadFaceMaskFile(
        @Query("userId") userId: Int,
        @Part image: MultipartBody.Part,
        @Query("date") dateTime: String
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleDashBoardPictureFile")
    suspend fun uploadVehicleDashboardImage(
        @Query("userId") userId: Int,
        @Part image: MultipartBody.Part,
        @Query("date") dateTime: String
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleFrontPictureFile")
    suspend fun uploadVehFrontImage(
        @Query("userId") userId: Int,
        @Part image: MultipartBody.Part,
        @Query("date") dateTime: String
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleNearSidePictureFile")
    suspend fun uploadVehNearSideImage(
        @Query("userId") userId: Int,
        @Part image: MultipartBody.Part,
        @Query("date") dateTime: String
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleRearPictureFile")
    suspend fun uploadVehRearImage(
        @Query("userId") userId: Int,
        @Part image: MultipartBody.Part,
        @Query("date") dateTime: String
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleOilLevelFile")
    suspend fun UploadVehicleOilLevelFile(
        @Query("userId") userId: Int,
        @Part image: MultipartBody.Part,
        @Query("date") dateTime: String
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleOffSidePictureFile")
    suspend fun uploadVehOffSideImage(
        @Query("userId") userId: Int,
        @Part image: MultipartBody.Part,
        @Query("date") dateTime: String
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleAddBlueFile")
    suspend fun UploadVehicleAddBlueFile(
        @Query("userId") userId: Int,
        @Part image: MultipartBody.Part,
        @Query("date") dateTime: String
    ): Response<SimpleStatusMsgResponse>

    @GET("/api/DailyWorks/GetDailyWorkInfobyId/{userId}")
    suspend fun GetDailyWorkInfobyId(
        @Path("userId") userId: Int
    ): Response<DailyWorkInfoByIdResponse>

    @GET("/api/RouteUpdate/GetRouteLocationInfo/{locationId}")
    suspend fun GetRouteLocationInfo(
        @Path("locationId") locationId: Int
    ): Response<GetRouteLocationInfoResponse>

    @GET("/api/RouteUpdate/GetRideAlongRouteTypeInfo/{driverId}")
    suspend fun GetRideAlongRouteTypeInfo(
        @Path("driverId") userId: Int
    ): Response<GetRideAlongRouteTypeInfoResponse>

    @GET("/api/RouteUpdate/GetDriverRouteTypeInfo/{driverId}")
    suspend fun GetDriverRouteTypeInfo(
        @Path("driverId") userId: Int
    ): Response<GetRideAlongRouteTypeInfoResponse>

    @GET("/api/Drivers/GetDriverSignatureInformation/{userId}")
    suspend fun GetDriverSignatureInformation(@Path("userId") userId: Int): Response<GetDriverSignatureInformationResponse>

    @POST("/api/Drivers/UpdateDriverAgreementSignature")
    suspend fun UpdateDriverAgreementSignature(@Body updateDriverAgreementSignatureRequest: UpdateDriverAgreementSignatureRequest): Response<SimpleStatusMsgResponse>

    @POST("/api/RouteUpdate/AddOnRouteInfo")
    suspend fun AddOnRouteInfo(@Body addOnRouteInfoRequest: AddOnRouteInfoRequest): Response<SimpleStatusMsgResponse>

    @POST("/api/DaDailyWorks/SaveBreakStartAndEndTime")
    suspend fun SaveBreakTime(@Body saveBreakTimeRequest: SaveBreakTimeRequest): Response<SimpleStatusMsgResponse>

    @GET("/api/DaDailyWorks/GetDriverBreakTimeInfoByDate/{driverId}")
    suspend fun GetDriverBreakInfo(@Path("driverId") driverId: Int): Response<GetDriverBreakTimeInfoResponse>

    @POST("/api/DailyWorks/UpdateClockInTime/{dwUsrID}")
    suspend fun UpdateClockInTime(@Path("dwUsrID") dwid: Int): Response<SimpleStatusMsgResponse>

    @POST("/api/DailyWorks/UpdateClockOutTime/{dwUsrID}")
    suspend fun UpdateClockOutTime(@Path("dwUsrID") dwid: Int): Response<SimpleStatusMsgResponse>

    @GET("/api/RouteUpdate/GetRideAlongDriversList")
    suspend fun GetRideAlongDriversList(): Response<GetRideAlongDriversListResponse>

    @GET("/api/RouteUpdate/GetRideAlongVehicleLists")
    suspend fun GetRideAlongVehicleLists(): Response<GetRideAlongVehicleLists>

    @GET("/api/RouteUpdate/GetRouteInfoById/{routeId}")
    suspend fun GetRouteInfoById(@Path("routeId") routeId: Int): Response<GetRouteInfoByIdRes>

    @POST("/api/RouteUpdate/AddOnRideAlongRouteInfo")
    suspend fun AddOnRideAlongRouteInfo(@Body addOnRideAlongRouteInfoRequest: AddOnRideAlongRouteInfoRequest)
            : Response<SimpleStatusMsgResponse>

    @GET("/api/RouteUpdate/GetRideAlongRouteInfoById")
    suspend fun GetRideAlongRouteInfoById(
        @Query("routeId") routeId: Int,
        @Query("LeadDriverId") LeadDriverId: Int
    ): Response<GetRideAlongRouteInfoByIdRes>

    @PUT("/api/Drivers/UpdatePassword")
    suspend fun updateprofilepassword(
        @Query("userId") userId: Double,
        @Query("oldPassword") oldpassword: String,
        @Query("newPassword") newpassworfd: String
    ): Response<SimpleStatusMsgResponse>

    @PUT("/api/Drivers/UpdateProfile")
    suspend fun updateprofileregular(@Body request: UpdateProfileRequestBody): Response<SimpleStatusMsgResponse>

    @GET("/api/RouteUpdate/GetDriverRouteInfoByDate/{driverId}")
    suspend fun GetDriverRouteInfoByDate(
        @Path("driverId") driverId: Int
    ): Response<GetDriverRouteInfoByDateResponse>

    @POST("/api/DriverQuestionnaire/SaveQuestionairePreparedness")
    suspend fun SaveQuestionairePreparedness(
        @Body request: SaveQuestionairePreparednessRequest
    ): Response<SimpleQuestionResponse>

    @POST("/api/DriverQuestionnaire/SaveQuestionaireStartup")
    suspend fun SaveQuestionaireStartup(
        @Body request: SaveQuestionaireStartupRequest
    ): Response<SimpleQuestionResponse>

    @POST("/api/DriverQuestionnaire/SaveQuestionaireOnGoingActivities")
    suspend fun SaveQuestionaireOnGoingActivities(
        @Body request: SaveQuestionaireOnGoingActivitiesRequest
    ): Response<SimpleQuestionResponse>

    @POST("/api/DriverQuestionnaire/SaveQuestionaireDeliverProcedures")
    suspend fun SaveQuestionaireDeliverProcedures(
        @Body request: SaveQuestionaireDeliverProceduresRequest
    ): Response<SimpleQuestionResponse>

    @POST("/api/DriverQuestionnaire/SaveQuestionaireReturnToDeliveryStation")
    suspend fun SaveQuestionaireReturnToDeliveryStation(
        @Body request: SaveQuestionaireReturnToDeliveryStationRequest
    ): Response<SimpleQuestionResponse>

    @POST("/api/DriverQuestionnaire/SubmitFinalQuestionairebyLeadDriver")
    suspend fun SubmitFinalQuestionairebyLeadDriver(
        @Body request: SubmitFinalQuestionairebyLeadDriverRequest
    ): Response<SimpleQuestionResponse>

    @GET("/api/RouteUpdate/GetRideAlongDriverInfoByDate/{leadDriverId}")
    suspend fun GetRideAlongDriverInfoByDate(
        @Path("leadDriverId") driverID: Int
    ): Response<RideAlongDriverInfoByDateResponse>

    @DELETE("/api/RouteUpdate/DeleteOnRideAlongRouteInfo/{routeId}")
    suspend fun DeleteOnRideAlongRouteInfo(@Path("routeId") routeId: Int): Response<SimpleStatusMsgResponse>

    @DELETE("/api/RouteUpdate/DeleteOnRouteDetails/{routeId}")
    suspend fun DeleteOnRouteDetails(@Path("routeId") routeId: Int): Response<SimpleStatusMsgResponse>

    @POST("/api/DriverQuestionnaire/SubmitRideAlongDriverFeedback")
    suspend fun SubmitRideAlongDriverFeedback(@Body request: SubmitRideAlongDriverFeedbackRequest): Response<SimpleStatusMsgResponse>

    @GET("/api/DriverQuestionnaire/GetRideAlongLeadDriverQuestion")
    suspend fun GetRideAlongLeadDriverQuestion(
        @Query("driverId") driverId: Int,
        @Query("routetId") routetId: Int,
        @Query("leadDriverId") leadDriverId: Int,
        @Query("daDailyWorkId") daDailyWorkId: Int
    ): Response<GetRideAlongLeadDriverQuestionResponse>

    @POST("/api/DaDailyWorks/DeleteBreakTime/{dawDriverBreakId}")
    suspend fun DeleteBreakTime(
        @Path("dawDriverBreakId") dawDriverBreakId: Int
    ): Response<SimpleStatusMsgResponse>

    @PUT("/api/RouteUpdate/UpdateOnRouteInfo")
    suspend fun UpdateOnRouteInfo(
        @Body request: GetDriverRouteInfoByDateResponseItem
    ): Response<SimpleStatusMsgResponse>

    @GET("/api/Ticket/GetUserTickets")
    suspend fun GetUserTickets(
        @Query("userId") userId: Int,
        @Query("departmentId") departmentId: Int?,
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?,
        @Query("includeCompleted") includeCompleted: Boolean?
    ): Response<GetUserTicketsResponse>

    @GET("/api/Ticket/GetUserDepartmentList")
    suspend fun GetUserDepartmentList(): Response<TicketDepartmentsResponse>

    @GET("/api/Ticket/GetTicketRequestType")
    suspend fun GetTicketRequestType(
        @Query("departmentId") departmentId: Int
    ): Response<DepartmentRequestResponse>

    @POST("/api/Ticket/CreateUserTicket")
    suspend fun SaveTicketData(
        @Query("userId") userId: Int,
        @Query("daDedAggrId") daDedAggrId: Int,
        @Body request: SaveTicketDataRequestBody
    ): Response<SaveTicketResponse>


    @GET("/api/Dashboard/GetAverageTotalScorebyId")
    suspend fun GetAvgScore(
        @Query("userId") userId: Int,
        @Query("LmId") lmID: Int
    ): Response<GetAvgScoreResponse>


    suspend fun GetLastWeekScore(
        @Query("userId") userId: Int,
        @Query("LmId") lmID: Int
    ): Response<GetLastWeekScore>

    @GET("/api/Dashboard/GetLastWeekScorebyId")
    suspend fun GetLastWeekScore(
        @Query("userId") userId: Int,
        @Query("WeekNo") WeekNo: Int,
        @Query("Year") Year: Int
    ): Response<GetLastWeekScore>

    @GET("/api/Dashboard/GetDriverWeeklyInvoice")
    suspend fun CashFLowData(
        @Query("userId") userId: Int,
        //@Query("companyFilter") companyFilter: Int,
        @Query("selYear") selYear: Int,
        @Query("selWeek") selWeek: Int
    ): Response<CashFlowPieChartResponse>

    @GET("/api/Dashboard/GetISO8601WeekandYear")
    suspend fun GetWeekAndYear(): Response<GetWeekYear>


    @GET("/api/Dashboard/GetWeeklyLocationRotaList")
    suspend fun GetVechileScheduleInfo(
        @Query("userId") userId: Int,
        @Query("LmId") lmID: Int,
        @Query("year") year: Int,
        @Query("weekNo") week: Int
    ): Response<ViewFullScheduleResponse>

    @GET("/api/Ticket/GetTicketCommentList")
    suspend fun GetTicketCommentList(
        @Query("userId") userId: Int,
        @Query("ticketId") ticketId: Int
    ): Response<GetTicketCommentListNewResponse>

    @Multipart
    @POST("/api/Ticket/UploadTicketAttachmentDoc")
    suspend fun UploadTicketAttachmentDoc(
        @Query("userId") userId: Int,
        @Query("ticketId") ticketId: Int,
        @Part file: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @POST("/api/Ticket/SaveTicketComment")
    suspend fun SaveTicketComment(
        @Query("userId") userId: Int,
        @Query("ticketId") ticketId: Int,
        @Query("comment") comment: String
    ): Response<SaveCommentResponse>

    @Multipart
    @POST("/api/Ticket/UploadTicketCommentAttachmentDoc")
    suspend fun UploadTicketCommentAttachmentDoc(
        @Query("userId") userId: Int,
        @Query("ticketCommentId") ticketCommentId: Int,
        @Part file: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @GET("/api/Ticket/GetUserTicketDocuments")
    suspend fun GetUserTicketDocuments(
        @Query("userId") userId: Int,
        @Query("ticketId") ticketId: Int
    ): Response<GetUserTicketDocumentsResponse>

    @PUT("/api/Dashboard/CreateThirdPartyAccess")
    suspend fun GetThirdPartyAccess(@Query("userId") userId: Int): Response<SimpleStatusMsgResponse>

    @PUT("/api/Dashboard/RemoveThirdPartyAccess")
    suspend fun RemoveThirdPartyAccess(@Query("userId") userId: Int): Response<SimpleStatusMsgResponse>


    @GET("/api/HtmlToPDF/DownloadSignedDAHandbook/{handBookId}")
    suspend fun DownloadSignedDAHandbook(
        @Path("handBookId") handbookId: Int
    ): Response<ResponseBody>

    @GET("/api/HtmlToPDF/DownloadSignedDAEngagement/{handBookId}")
    suspend fun DownloadSignedDAEngagement(
        @Path("handBookId") handbookId: Int
    ): Response<ResponseBody>

    @GET("/api/HtmlToPDF/DownloadSignedGDPRPOLICY/{handBookId}")
    suspend fun DownloadSignedGDPRPOLICY(
        @Path("handBookId") handbookId: Int
    ): Response<ResponseBody>

    @GET("/api/HtmlToPDF/DownloadSignedServiceLevelAgreement/{handBookId}")
    suspend fun DownloadSignedServiceLevelAgreement(
        @Path("handBookId") handbookId: Int
    ): Response<ResponseBody>

    @GET("/api/HtmlToPDF/DownloadSignedPrivacyPolicy/{handBookId}")
    suspend fun DownloadSignedPrivacyPolicy(
        @Path("handBookId") handbookId: Int
    ): Response<ResponseBody>

    @GET("/api/DriverQuestionnaire/GetRideAlongDriverFeedbackQuestion")
    suspend fun GetRideAlongDriverFeedbackQuestion(
        @Query("driverId") driverId: Int,
        @Query("routetId") routeId: Int,
        @Query("leadDriverId") leadDriverId: Int,
        @Query("daDailyWorkId") daDailyWorkId: Int
    ): Response<GetRideAlongDriverFeedbackQuestionResponse>

    @POST("/api/Authentication/SaveDeviceInformation")
    suspend fun SaveDeviceInformation(
        @Body body: SaveDeviceInformationRequest
    ): Response<SimpleStatusMsgResponse>

    @GET("/api/Notification/GetNotificationsListByUserId/{userId}")
    suspend fun GetNotificationListByUserId(
        @Path("userId") userId: Int
    ): Response<NotificationResponse>

    @POST("/api/Vehicle/SaveVehInspectionInfo")
    suspend fun SaveVehicleInspectionInformation(
        @Body body: SaveVehicleInspectionInfo
    ): Response<SimpleStatusMsgResponse>

    @GET("/api/Drivers/GetVehicleInfobyDriverId")
    suspend fun GetVehicleInfobyDriverId(@Query("userId") userId: Int, @Query("date") date: String)
            : Response<GetvehicleInfoByDriverId>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleImages")
    suspend fun uploadVehicleImages(
        @Query("userId") userId: Int,
        @Part image: List<MultipartBody.Part>
    ): Response<SimpleStatusMsgResponse>

    @GET("/api/Drivers/GetVehicleAdvancePaymentAgreement/{userId}")
    suspend fun GetVehicleAdvancePaymentAgreement(
        @Path("userId") userId: Int
    ): Response<GetVehicleAdvancePaymentAgreementResponse>

    @GET("/api/Drivers/GetDadeductionSignAgreement")
    suspend fun GetDeductionAgreement(
        @Query("userId") userId: Int,
        @Query("aggrId") aggrId: Int
    ): Response<DeductionAgreementResponse>

    @POST("/api/Drivers/UpdateDadeductionSignAgreement")
    suspend fun UpdateDaDeductionSignAgreement(
        @Body requestBody: UpdateDeductioRequest
    ): Response<SimpleStatusMsgResponse>

    @GET("/api/Drivers/GetDAVehicleExpiredDocuments/{userId}")
    suspend fun GetDaVehicleExpiredDocuments(
        @Path("userId") userId: Int
    ): Response<GetDAVehicleExpiredDocumentsResponse>

    @GET("/api/Drivers/GetDAExpiringDocuments/{userId}")
    suspend fun GetDAExpiringDocuments(
        @Path("userId") userId: Int
    ): Response<ExpiringDocumentsResponse>

    @PUT("/api/Drivers/ApproveWeeklyRotabyDA")
    suspend fun ApproveWeeklyRotabyDA(
        @Query("userId") userId: Int,
        @Query("lrnId") lrnID: Int
    ): Response<SimpleStatusMsgResponse>


    @Multipart
    @POST("/api/Vehicle/UploadUserDocumentFileByDriver")
    suspend fun UploadExpiringDocs(
        @Query("userId") userId: Int,
        @Query("docTypeId") docTypeID: Int,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @PUT("/api/Drivers/ApproveVehicleAdvancePaymentAgreement")
    suspend fun ApproveVehicleAdvancePaymentAgreement(
        @Query("userId") userId: Int,
        @Query("isApproved") isApproved: Boolean
    ): Response<SimpleStatusMsgResponse>

    @GET("/api/Drivers/WeeklyRotaExistForDAApproval/{userId}")
    suspend fun WeeklyRotaExistForDAApproval(
        @Path("userId") userId: Int
    ): Response<SimpleStatusMsgResponse>

    @GET("/api/Drivers/GetWeeklyLocationRotabyId/{lrnId}")
    suspend fun GetWeeklyLocationRotabyId(
        @Path("lrnId") lrnID: Int
    ): Response<WeeklyLocationRotabyIdResponse>

    @POST("/api/Notification/MarkNotificationAsRead/{notificationId}")
    suspend fun MarkNotificationAsRead(
        @Path("notificationId") notificationId: Int
    ): Response<SimpleStatusMsgResponse>

    @GET("/api/Drivers/GetDaDailyLocationRota")
    suspend fun GetDaDailyLocationRota(
        @Query("userId") userId: Int,
        @Query("token") token: String
    ): Response<DaDailyLocationRotaResponse>

    @POST("/api/Drivers/ApproveDailyRotabyDA")
    suspend fun ApproveDailyRotabyDA(
        @Body body: ApproveDaDailyRotaRequest
    ): Response<SimpleStatusMsgResponse>


    @GET("/api/HtmlToPDF/DownloadDAHandbookPolicy")
    suspend fun DownloadDAHandbookPolicy(): Response<ResponseBody>

    @GET("/api/HtmlToPDF/DownloadDAEngagementPolicy")
    suspend fun DownloadDAEngagementPolicy(): Response<ResponseBody>

    @GET("/api/HtmlToPDF/DownloadGDPRPolicy")
    suspend fun DownloadGDPRPolicy(): Response<ResponseBody>

    @GET("/api/HtmlToPDF/DownloadServiceLevelAgreementPolicy")
    suspend fun DownloadServiceLevelAgreementPolicy(): Response<ResponseBody>

    @GET("/api/HtmlToPDF/DownloadPrivacyPolicy")
    suspend fun DownloadPrivacyPolicy(): Response<ResponseBody>

    @GET("/api/HtmlToPDF/DownloadTrucksServiceLevelAgreementPolicy")
    suspend fun DownloadTrucksServiceLevelAgreementPolicy(): Response<ResponseBody>

    @GET("/api/HtmlToPDF/GetDriverInvoiceList")
    suspend fun GetDriverInvoiceList(
        @Query("UserId") UserId: Int,
        @Query("selYear") selYear: Int,
        @Query("selWeek") selWeek: Int
    ): Response<GetDriverInvoiceListResponse>

    @GET("/api/HtmlToPDF/GetThirdPartyInvoiceList")
    suspend fun GetThirdPartyInvoiceList(
        @Query("UserId") UserId: Int,
        @Query("selYear") setYear: Int,
        @Query("selWeek") selWeek: Int
    ): Response<GetDriverInvoiceListResponse>

    @GET("/api/HtmlToPDF/DownloadInvoicePDF")
    suspend fun DownloadInvoicePDF(
        @Query("UserId") userId: Int,
        @Query("InvoiceId") invoiceId: Int
    ): Response<DownloadInvoicePDFResponseX>

    @GET("/api/HtmlToPDF/DownloadThirdPartyInvoicePDF")
    suspend fun DownloadThirdPartyInvoicePDF(
        @Query("UserId") userId: Int,
        @Query("InvoiceId") invoiceId: Int
    ): Response<DownloadInvoicePDFResponseX>

    @GET("/api/HtmlToPDF/GetDriverOtherCompaniesPolicy/{userId}")
    suspend fun GetDriverOtherCompaniesPolicy(
        @Path("userId") userId: Int
    ): Response<GetDriverOtherCompaniesPolicyResponse>

    @GET("/api/HtmlToPDF/DownloadDriverOtherCompaniesPolicy")
    suspend fun DownloadDriverOtherCompaniesPolicy(
        @Query("userId") userId: Int,
        @Query("companyId") companyId: Int,
        @Query("companyDocId") companyDocId: Int
    ): Response<DownloadDriverOtherCompaniesPolicyResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleWindscreenDefect")
    suspend fun UploadVehicleWindscreenDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleWindowsOrGlassVisibilityDefect")
    suspend fun UploadVehicleWindowsOrGlassVisibilityDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleWipersOrWashersDefect")
    suspend fun UploadVehicleWipersOrWashersDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleMirrorDefect")
    suspend fun UploadVehicleMirrorDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleCabSecurityOrInteriorDefect")
    suspend fun UploadVehicleCabSecurityOrInteriorDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleSeatBeltDefect")
    suspend fun UploadVehicleSeatBeltDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleWarningOrServiceLightDefect")
    suspend fun UploadVehicleWarningOrServiceLightDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleFuelOrAdBlueLevelDefect")
    suspend fun UploadVehicleFuelOrAdBlueLevelDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleOilOrCoolantLeaksDefect")
    suspend fun UploadVehicleOilOrCoolantLeaksDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleLightsDefect")
    suspend fun UploadVehicleLightsDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleIndicatorsOrSideRepeatersDefect")
    suspend fun UploadVehicleIndicatorsOrSideRepeatersDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleHornOrReverseBeeperDefect")
    suspend fun UploadVehicleHornOrReverseBeeperDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleSteeringDefect")
    suspend fun UploadVehicleSteeringDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleBrakesDefect")
    suspend fun UploadVehicleBrakesDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleLockingSystemDefect")
    suspend fun UploadVehicleLockingSystemDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleFrontDefect")
    suspend fun UploadVehicleFrontDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleNearSideDefect")
    suspend fun UploadVehicleNearSideDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleRearDefect")
    suspend fun UploadVehicleRearDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleOffSideDefect")
    suspend fun UploadVehicleOffSideDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleRegistrationNumberPlateDefect")
    suspend fun UploadVehicleRegistrationNumberPlateDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleReflectorOrMarkerDefect")
    suspend fun UploadVehicleReflectorOrMarkerDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleWheelsOrWheelFixingDefect")
    suspend fun UploadVehicleWheelsOrWheelFixingDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleTyresDefect")
    suspend fun UploadVehicleTyresDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleOilOrFuelOrCoolantLeaksDefect")
    suspend fun UploadVehicleOilOrFuelOrCoolantLeaksDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleExcessiveEngineExhaustSmokeDefect")
    suspend fun UploadVehicleExcessiveEngineExhaustSmokeDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleSpareWheelDefect")
    suspend fun UploadVehicleSpareWheelDefect(
        @Query("userId") userId: Int,
        @Query("vmId") vmId: Int,
        @Query("lmId") lmID: Int,
        @Query("date") date: String,
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @GET("/api/Drivers/GetDAVehicleExpiringDocuments/{userId}")
    suspend fun GetDAVehicleExpiringDocuments(
        @Path("userId") userId: Int
    ): Response<VehicleExpiringDocumentsResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehDocumentFileByDriver")
    suspend fun UploadVehDocumentFileByDriver(
        @Query("VehId") VehId: Int,
        @Query("docTypeId") docTypeId: Int,
        @Query("expiredDocId") expiredDocId: Int,
        @Query("userId") userId: Int,
        /*@Query("uploadedByUser") uploadedByUser:Int,*/
        @Part multipartBody: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

}

