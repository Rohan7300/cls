package com.clebs.celerity_admin.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.clebs.celerity_admin.database.User
import com.clebs.celerity_admin.models.CollectVehicleFromSupplierRequest
import com.clebs.celerity_admin.models.CompanyListResponse
import com.clebs.celerity_admin.models.DDAMandateModel
import com.clebs.celerity_admin.models.DriverListResponseModel
import com.clebs.celerity_admin.models.GetAllDriversInspectionListResponse
import com.clebs.celerity_admin.models.GetAllVehicleInspectionListResponse
import com.clebs.celerity_admin.models.GetCurrentAllocatedDaResponse
import com.clebs.celerity_admin.models.GetCurrentInsuranceInfo
import com.clebs.celerity_admin.models.GetReturnVehicleListResponse
import com.clebs.celerity_admin.models.GetReturnVmID
import com.clebs.celerity_admin.models.GetVehOilLevelListResponse
import com.clebs.celerity_admin.models.GetVehWindScreenConditionStatusResponse
import com.clebs.celerity_admin.models.GetVehicleCollectionHistoryResponse
import com.clebs.celerity_admin.models.GetVehicleDamageWorkingStatusResponse
import com.clebs.celerity_admin.models.GetVehicleFuelLevelList
import com.clebs.celerity_admin.models.GetVehicleIdOnCollectVehicleOptionResponse
import com.clebs.celerity_admin.models.GetVehicleLocation
import com.clebs.celerity_admin.models.GetVehicleRequestType
import com.clebs.celerity_admin.models.GetVehicleReturnHistoryResponse
import com.clebs.celerity_admin.models.GetWeeklyDefectCheckImagesResponse
import com.clebs.celerity_admin.models.GetvehicleOilLevelList
import com.clebs.celerity_admin.models.LastMileageInfo
import com.clebs.celerity_admin.models.LoginRequest
import com.clebs.celerity_admin.models.LoginResponse
import com.clebs.celerity_admin.models.OtherDefectCheckImagesInDropBoxResponse
import com.clebs.celerity_admin.models.RepoInfoModel
import com.clebs.celerity_admin.models.SaveDefectSheetWeeklyOSMCheckRequest
import com.clebs.celerity_admin.models.ResponseInspectionDone
import com.clebs.celerity_admin.models.ReturnVehicleToDepoRequest
import com.clebs.celerity_admin.models.SaveInspectionRequestBody
import com.clebs.celerity_admin.models.SaveVehicleBreakDownInspectionRequest
import com.clebs.celerity_admin.models.SucessStatusMsgResponse
import com.clebs.celerity_admin.models.VehicleAllocateTODARequestBody
import com.clebs.celerity_admin.models.VehicleReturnModelList
import com.clebs.celerity_admin.models.WeekYearModel
import com.clebs.celerity_admin.models.WeeklyDefectChecksModel
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.utils.DefectFileType
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.ResponseBody

class MainViewModel(private val repo: MainRepo) : ViewModel() {

    val lDGetWeeklyDefectChecks: MutableLiveData<WeeklyDefectChecksModel?> = MutableLiveData()
    val lDGetWeeklyDefectCheckImages: MutableLiveData<GetWeeklyDefectCheckImagesResponse?> =
        MutableLiveData()
    val lDGetVehOilLevelList: MutableLiveData<GetVehOilLevelListResponse?> = MutableLiveData()
    val lDGetVehWindScreenConditionStatus: MutableLiveData<GetVehWindScreenConditionStatusResponse?> =
        MutableLiveData()
    val lDUploadVehOSMDefectChkFile: MutableLiveData<SucessStatusMsgResponse?> = MutableLiveData()
    val lDSaveDefectSheetWeeklyOSMCheck: MutableLiveData<SucessStatusMsgResponse?> =
        MutableLiveData()
    var mileageApiLiveData:MutableLiveData<LastMileageInfo?> = MutableLiveData()
    val saveinspectionlivedata: MutableLiveData<SucessStatusMsgResponse?> = MutableLiveData()
    val isinspectiondonelivedata: MutableLiveData<ResponseInspectionDone?> = MutableLiveData()
    val otherImagesListLiveData: MutableLiveData<OtherDefectCheckImagesInDropBoxResponse?> =
        MutableLiveData()
    val GetLocationListbyUserIdLiveData: MutableLiveData<GetVehicleLocation?> =
        MutableLiveData()
    val VehicleInspectionListLiveData: MutableLiveData<GetAllVehicleInspectionListResponse?> =
        MutableLiveData()
    val DriverInspectionListLiveData: MutableLiveData<GetAllDriversInspectionListResponse?> =
        MutableLiveData()
    val VehicleDamageWorkingStatusLD: MutableLiveData<GetVehicleDamageWorkingStatusResponse?> =
        MutableLiveData()
    val SaveVehicleBreakDownInspectionLD: MutableLiveData<SucessStatusMsgResponse?> =
        MutableLiveData()
    val GetCurrentAllocatedDaLD: MutableLiveData<GetCurrentAllocatedDaResponse?> = MutableLiveData()
    val LDGetReturnVehicleList: MutableLiveData<GetReturnVehicleListResponse?> = MutableLiveData()
    val LDDownloadVehicleHireAgreementPDF: MutableLiveData<ResponseBody?> = MutableLiveData()
    val LDDownloadVehicleSignOutHireAgreementPDF: MutableLiveData<ResponseBody?> = MutableLiveData()
    val LDReturnVehicleToDepo: MutableLiveData<SucessStatusMsgResponse?> = MutableLiveData()
    val GetVehicleCurrentInsuranceInfo: MutableLiveData<GetCurrentInsuranceInfo?> =
        MutableLiveData()
    val CreateVehicleReleaseReqlivedata: MutableLiveData<SucessStatusMsgResponse?> =
        MutableLiveData()
    val CHangeAllocatedDAVehicle: MutableLiveData<SucessStatusMsgResponse?> = MutableLiveData()
    private val _clickEvent = MutableLiveData<Boolean>()
    val clickEvent: LiveData<Boolean> = _clickEvent

    fun setClickEvent(clicked: Boolean) {
        _clickEvent.value = clicked
    }

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
    ) {
        viewModelScope.launch {
            val response = repo.GetWeeklyDefectCheckList(weekno, year, driverid, lmid, showdefects)
            if (response.failed || !response.isSuccessful) {
                lDGetWeeklyDefectChecks.postValue(null)
            } else {
                lDGetWeeklyDefectChecks.postValue(response.body)
            }
        }
    }

    fun GetWeeklyDefectCheckImages(vdhCheckId: Int) {
        viewModelScope.launch {
            val response = repo.GetWeeklyDefectCheckImages(vdhCheckId)
            if (response.failed || !response.isSuccessful) {
                lDGetWeeklyDefectCheckImages.postValue(null)
            } else {
                lDGetWeeklyDefectCheckImages.postValue(response.body)
            }
        }
    }

    fun GetVehOilLevelList() {
        viewModelScope.launch {
            val response = repo.GetVehOilLevelList()
            if (response.failed || !response.isSuccessful)
                lDGetVehOilLevelList.postValue(null)
            else
                lDGetVehOilLevelList.postValue(response.body)
        }
    }

    fun GetVehWindScreenConditionStatus() {
        viewModelScope.launch {
            val response = repo.GetVehWindScreenConditionStatus()
            if (response.failed || !response.isSuccessful)
                lDGetVehWindScreenConditionStatus.postValue(null)
            else
                lDGetVehWindScreenConditionStatus.postValue(response.body)
        }
    }

    fun UploadVehOSMDefectChkFile(
        vdhDefectCheckId: Int,
        fileType: DefectFileType,
        date: String,
        image: MultipartBody.Part
    ) {
        viewModelScope.launch {
            val response =
                repo.UploadVehOSMDefectChkFile(vdhDefectCheckId, fileType, date, image)
            if (response.failed || !response.isSuccessful)
                lDUploadVehOSMDefectChkFile.postValue(null)
            else
                lDUploadVehOSMDefectChkFile.postValue(response.body)
        }
    }

    fun SaveVehWeeklyDefectSheetInspectionInfo(
        saveInspectionRequestBody: SaveInspectionRequestBody
    ) {
        viewModelScope.launch {
            val response =
                repo.SaveVehWeeklyDefectSheetInspectionInfo(saveInspectionRequestBody)
            if (response.failed || !response.isSuccessful)
                saveinspectionlivedata.postValue(null)
            else
                saveinspectionlivedata.postValue(response.body)
        }
    }

    fun GetVehWeeklyDefectSheetInspectionInfo(
        vdhCheckId: Int
    ) {
        viewModelScope.launch {
            val response =
                repo.GetVehWeeklyDefectSheetInspectionInfo(vdhCheckId)
            if (response.failed || !response.isSuccessful)
                isinspectiondonelivedata.postValue(null)
            else
                isinspectiondonelivedata.postValue(response.body)
        }
    }


    fun GetOtherDefectCheckImagesInDropBox(
        vdhDefectCheckId: Int,
        fileType: String
    ) {
        viewModelScope.launch {
            val response = repo.GetOtherDefectCheckImagesInDropBox(vdhDefectCheckId, fileType)
            if (!response.isSuccessful || response.failed)
                otherImagesListLiveData.postValue(null)
            else
                otherImagesListLiveData.postValue(response.body)
        }
    }

    fun GetLocationListbyUserId(
        userid: Double
    ) {
        viewModelScope.launch {
            val response = repo.GetLocationListbyUserId(userid)
            if (!response.isSuccessful || response.failed)
                GetLocationListbyUserIdLiveData.postValue(null)
            else
                GetLocationListbyUserIdLiveData.postValue(response.body)
        }
    }

    fun GetAllVehicleInspectionList() {
        viewModelScope.launch {
            val response = repo.GetAllVehicleInspectionList()
            if (!response.isSuccessful || response.failed)
                VehicleInspectionListLiveData.postValue(null)
            else
                VehicleInspectionListLiveData.postValue(response.body)
        }
    }

    fun GetAllDriversInspectionList() {
        viewModelScope.launch {
            val response = repo.GetAllDriversInspectionList()
            if (!response.isSuccessful || response.failed)
                DriverInspectionListLiveData.postValue(null)
            else
                DriverInspectionListLiveData.postValue(response.body)
        }
    }

    fun GetVehicleDamageWorkingStatus() {
        viewModelScope.launch {
            val response = repo.GetVehicleDamageWorkingStatus()
            if (!response.isSuccessful || response.failed)
                VehicleDamageWorkingStatusLD.postValue(null)
            else
                VehicleDamageWorkingStatusLD.postValue(response.body)
        }
    }

    fun SaveVehicleBreakDownInspectionInfo(request: SaveVehicleBreakDownInspectionRequest) {
        viewModelScope.launch {
            val response = repo.SaveVehicleBreakDownInspectionInfo(request)
            if (!response.isSuccessful || response.failed)
                SaveVehicleBreakDownInspectionLD.postValue(null)
            else
                SaveVehicleBreakDownInspectionLD.postValue(response.body)
        }
    }

    fun GetCurrentAllocatedDa(vmId: String, isVehReturned: Boolean) {
        viewModelScope.launch {
            val response = repo.GetCurrentAllocatedDa(vmId, isVehReturned)
            if (!response.isSuccessful || response.failed)
                GetCurrentAllocatedDaLD.postValue(null)
            else
                GetCurrentAllocatedDaLD.postValue(response.body)
        }
    }

    fun GetReturnVehicleList() {
        viewModelScope.launch {
            val response = repo.GetReturnVehicleList()
            if (!response.isSuccessful || response.failed)
                LDGetReturnVehicleList.postValue(null)
            else
                LDGetReturnVehicleList.postValue(response.body)
        }
    }

    fun DownloadVehicleHireAgreementPDF() {
        viewModelScope.launch {
            val response = repo.DownloadVehicleHireAgreementPDF()
            if (!response.isSuccessful || response.failed)
                LDDownloadVehicleHireAgreementPDF.postValue(null)
            else
                LDDownloadVehicleHireAgreementPDF.postValue(response.body)
        }
    }

    fun DownloadVehicleSignOutHireAgreementPDF() {
        viewModelScope.launch {
            val response = repo.DownloadVehicleSignOutHireAgreementPDF()
            if (!response.isSuccessful || response.failed)
                LDDownloadVehicleSignOutHireAgreementPDF.postValue(null)
            else
                LDDownloadVehicleSignOutHireAgreementPDF.postValue(response.body)
        }
    }

    fun ReturnVehicleToDepo(request: ReturnVehicleToDepoRequest) {
        viewModelScope.launch {
            val response = repo.ReturnVehicleToDepo(request)
            if (!response.isSuccessful || response.failed)
                LDReturnVehicleToDepo.postValue(null)
            else
                LDReturnVehicleToDepo.postValue(response.body)
        }
    }

    fun GetVehicleCurrentInsuranceInfo(
        vmID: Int
    ) {
        viewModelScope.launch {
            val response = repo.GetVehicleCurrentInsuranceInfo(vmID)
            if (!response.isSuccessful || response.failed)
                GetVehicleCurrentInsuranceInfo.postValue(null)
            else
                GetVehicleCurrentInsuranceInfo.postValue(response.body)
        }
    }

    fun CreateVehicleReleaseReq(
        vmID: Double, supervisor: Double
    ) {
        viewModelScope.launch {
            val response = repo.CreateVehicleReleaseReq(vmID, supervisor)
            if (!response.isSuccessful || response.failed)
                CreateVehicleReleaseReqlivedata.postValue(null)
            else
                CreateVehicleReleaseReqlivedata.postValue(response.body)
        }
    }

    fun GetVehicleReturnHistory(
        superVisorId: Int,
        includeReturned: Boolean
    ): MutableLiveData<GetVehicleReturnHistoryResponse?> {
        return liveData {
            val response = repo.GetVehicleReturnHistory(superVisorId, includeReturned)
            if (!response.isSuccessful || response.failed)
                emit(null)
            else
                emit(response.body)
        } as MutableLiveData<GetVehicleReturnHistoryResponse?>
    }

    fun GetVehicleCollectionHistory(
        superVisorId: Int,
        includeReturned: Boolean
    ): MutableLiveData<GetVehicleCollectionHistoryResponse> {
        return liveData {
            val response = repo.GetVehicleCollectionHistory(superVisorId, includeReturned)
            if (!response.isSuccessful || response.failed)
                emit(null)
            else
                emit(response.body)
        } as MutableLiveData<GetVehicleCollectionHistoryResponse>
    }

    fun ChangeALlocatedDAvehicle(request: VehicleAllocateTODARequestBody) {
        viewModelScope.launch {
            val response = repo.CreateVehicleReleaseReq(request)
            if (!response.isSuccessful || response.failed)
                CHangeAllocatedDAVehicle.postValue(null)
            else
                CHangeAllocatedDAVehicle.postValue(response.body)
        }
    }

    fun SaveVehicleCollectionComment(
        supervisorId: Int,
        vehCollectionId: Int,
        comment: String
    ): MutableLiveData<SucessStatusMsgResponse> {
        return liveData {
            val response = repo.SaveVehicleCollectionComment(supervisorId, vehCollectionId, comment)
            if (!response.isSuccessful || response.failed)
                emit(null)
            else
                emit(response.body)
        } as MutableLiveData<SucessStatusMsgResponse>
    }

    fun GetExistingRegIds(
        vmRegNo:String
    ):MutableLiveData<SucessStatusMsgResponse>{
        return liveData {
            val response = repo.GetExistingRegIds(vmRegNo)
            if(!response.isSuccessful || response.failed)
                emit(null)
            else
                emit(response.body)
        } as MutableLiveData<SucessStatusMsgResponse>
    }
    fun GetVehicleIdOnCollectVehicleOption(
        vmRegNo:String
    ):MutableLiveData<GetVehicleIdOnCollectVehicleOptionResponse>{
        return liveData {
            val response = repo.GetVehicleIdOnCollectVehicleOption(vmRegNo)
            if(!response.isSuccessful || response.failed)
                emit(null)
            else
                emit(response.body)
        } as MutableLiveData<GetVehicleIdOnCollectVehicleOptionResponse>
    }

    fun GetVehicleLastMileageInfo(vmID: String){
        viewModelScope.launch {
            val response = repo.GetLastMileageInfo(vmID)
            if (response.failed) {
                mileageApiLiveData.postValue(null)
            }
            if (!response.isSuccessful) {
                mileageApiLiveData.postValue(null)
            } else {
                mileageApiLiveData.postValue(response.body)
            }
        }
    }

    fun CollectVehicelFromSupplier(request: CollectVehicleFromSupplierRequest):MutableLiveData<SucessStatusMsgResponse?>{
        return liveData {
            val response = repo.CollectVehicelFromSupplier(request)
            if(response.failed||!response.isSuccessful)
                emit(null)
            else
                emit(response.body)
        } as MutableLiveData<SucessStatusMsgResponse?>
    }


}