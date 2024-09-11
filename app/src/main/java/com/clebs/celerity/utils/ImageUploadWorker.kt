package com.clebs.celerity.utils

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.database.ImageDatabase
import com.clebs.celerity.database.ImageEntity
import com.clebs.celerity.database.ImagesRepo
import com.clebs.celerity.database.OSyncRepo
import com.clebs.celerity.database.OfflineSyncDB
import com.clebs.celerity.models.requests.SaveVehicleInspectionInfo
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.ui.App
import com.clebs.celerity.ui.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class ImageUploadWorker(
    var appContext: Context, workerParams: WorkerParameters,
) : Worker(appContext, workerParams) {
    private var lmId: Int = 0
    private var vmId: Int = 0
    override fun doWork(): Result {
        val clebUserId = inputData.getInt("clebUserId", 0)
        val uploadtype = inputData.getInt("uploadtype", 0)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val todayDate = dateFormat.format(Date())
        val prefs = Prefs.getInstance(appContext)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        val osRepo = OSyncRepo(OfflineSyncDB.invoke(appContext))
        val currentDateTime = getCurrentDateTime()
        val imagesRepo =
            ImagesRepo(ImageDatabase.invoke(appContext), Prefs.getInstance(appContext))

        GlobalScope.launch {


            try {
                when (uploadtype) {

                    0 -> {
                        val data = osRepo.getData(clebUserId, todayDate)
                        logOSEntity("ImageWorker", data)

/*                        if (data.dashboardImage != null&&data.isDashboardImageRequired) {
                            val partBody = createMultipartPart(
                                data.dashboardImage!!, "uploadVehicleDashBoardImage",
                                appContext
                            )
                            val dashresponse =
                                mainRepo.uploadVehicleImage(
                                    clebUserId,
                                    partBody,
                                    1,
                                    currentDateTime
                                )
                            if (!dashresponse.isSuccessful) {
                                data.isdashboardUploadedFailed = true
                            }
                        }

                        if (data.frontImage != null&&data.isFrontImageRequired) {
                            val partBody = createMultipartPart(
                                data.frontImage!!, "uploadVehicleFrontImage",
                                appContext
                            )
                            val frontresponse =
                                mainRepo.uploadVehicleImage(
                                    clebUserId,
                                    partBody,
                                    2,
                                    currentDateTime
                                )
                            if (!frontresponse.isSuccessful) {
                                data.isfrontImageFailed = true
                            }
                        }

                        if (data.nearSideImage != null&&data.isnearImageRequired) {
                            val partBody = createMultipartPart(
                                data.nearSideImage!!, "uploadVehicleNearSideImage",
                                appContext
                            )

                            val nearResponse =
                                mainRepo.uploadVehicleImage(
                                    clebUserId,
                                    partBody,
                                    3,
                                    currentDateTime
                                )
                            if (!nearResponse.isSuccessful) {
                                data.isnearSideFailed = true
                            }
                        }

                        if (data.rearSideImage != null&&data.isRearImageRequired) {
                            val partBody = createMultipartPart(
                                data.rearSideImage!!, "uploadVehicleRearImage",
                                appContext
                            )
                            val rearResponse =
                                mainRepo.uploadVehicleImage(
                                    clebUserId,
                                    partBody,
                                    4,
                                    currentDateTime
                                )
                            if (!rearResponse.isSuccessful) {
                                data.isrearSideFailed = true
                            }
                        }

                        if (data.offSideImage != null&&data.isOffsideImageRequired) {
                            val partBody = createMultipartPart(
                                data.offSideImage!!, "uploadVehicleOffSideImage",
                                appContext
                            )

                            val offsideResponse =
                                mainRepo.uploadVehicleImage(
                                    clebUserId,
                                    partBody,
                                    6,
                                    currentDateTime
                                )
                            if (!offsideResponse.isSuccessful) {
                                data.isoffSideFailed = true
                            }
                        }*/

                        //if (data.addblueImage != null&&data.isaddBlueImageRequired) {
                        if (prefs.addBlueUri!=null) {
                            val partBody = createMultipartPart(
                                prefs.addBlueUri!!, "uploadVehicleAddBlueImage",
                                appContext
                            )
                            val addBlueResponse = mainRepo.uploadVehicleImage(
                                clebUserId, partBody, 7, currentDateTime
                            )
                            if (!addBlueResponse.isSuccessful)
                                data.isaddblueImageFailed = true
                        }

                        //if (data.oillevelImage != null&&data.isoilLevelImageRequired) {
                        if (prefs.oilLevelUri != null) {
                            val partBody = createMultipartPart(
                                prefs.oilLevelUri!!, "uploadVehicleOilLevelImage",
                                appContext
                            )
                            val oilLevelResponse = mainRepo.uploadVehicleImage(
                                clebUserId, partBody, 5, currentDateTime
                            )
                            if (!oilLevelResponse.isSuccessful)
                                data.isoillevelImageFailed = true
                        }

                        //if (data.faceMaskImage != null&&data.isfaceMaskImageRequired) {
                        if (prefs.faceMaskUri!=null) {
                            val partBody = createMultipartPart(
                                prefs.faceMaskUri!!, "uploadFaceMaskImage",
                                appContext
                            )
                            val selfieeRes = mainRepo.uploadVehicleImage(
                                clebUserId,
                                partBody,
                                0,
                                currentDateTime
                            )
                            if (!selfieeRes.isSuccessful)
                                data.isfaceMaskImageFailed = true
                        }
                    }

                    1 -> {
                        val data = osRepo.getData(clebUserId, todayDate)
                        logOSEntity("ImageWorker", data)

                        if (prefs.faceMaskUri!=null) {
                            val partBody = createMultipartPart(
                                prefs.faceMaskUri!!, "uploadFaceMaskImage",
                                appContext
                            )
                            val selfieeRes = mainRepo.uploadVehicleImage(
                                clebUserId,
                                partBody,
                                0,
                                currentDateTime
                            )
                            if (!selfieeRes.isSuccessful)
                                data.isfaceMaskImageFailed = true
                        }
                    }

                    2 -> {
                        vmId = Prefs.getInstance(appContext).vmId
                        lmId = Prefs.getInstance(appContext).getLocationID().toInt()
                        val todayDate = dateFormat.format(Date())
                        val imageEntity = imagesRepo.getImagesbyUser(todayDate)
                        Log.d("IMWorker","2 ")
                        if (imageEntity != null) {
                            if (checkNullorEmpty(imageEntity.exBodyDamageFront)) {
                                val partBody = createMultipartPart(
                                    imageEntity.exBodyDamageFront!!, "uploadVehicleFrontDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 FRONT")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.BODY_DAMAGE_FRONT,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.inWindScreen)) {
                                val partBody = createMultipartPart(
                                    imageEntity.inWindScreen!!, "uploadWindscreenDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 WindScreen")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.WIND_SCREEN,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.inWindowGlass)) {
                                val partBody = createMultipartPart(
                                    imageEntity.inWindowGlass!!,
                                    "uploadWindowsOrGlassVisibilityDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 WindowGlass")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.WINDOW_GLASS,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.inWipersWashers)) {
                                val partBody = createMultipartPart(
                                    imageEntity.inWipersWashers!!,
                                    "uploadVehicleWipersOrWashersDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 WIPERS_WASHERS")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.WIPERS_WASHERS,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.inMirrors)) {
                                val partBody = createMultipartPart(
                                    imageEntity.inMirrors!!, "uploadMirrorDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 MIRRORS")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.MIRRORS,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.inCabSecurityInterior)) {
                                val partBody = createMultipartPart(
                                    imageEntity.inCabSecurityInterior!!,
                                    "uploadVehicleCabSecurityOrInteriorDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 CAB_SECURITY_INTERIOR")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.CAB_SECURITY_INTERIOR,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.inSeatBelt)) {
                                val partBody = createMultipartPart(
                                    imageEntity.inSeatBelt!!, "uploadVehicleSeatBeltDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 CAB_SECURITY_INTERIOR")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.SEAT_BELT,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.inWarningServiceLights)) {
                                val partBody = createMultipartPart(
                                    imageEntity.inWarningServiceLights!!,
                                    "uploadVehicleWarningOrServiceLightDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 WARNING_SERVICE_LIGHTS")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.WARNING_SERVICE_LIGHTS,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.inFuelAdBlueLevel)) {
                                val partBody = createMultipartPart(
                                    imageEntity.inFuelAdBlueLevel!!,
                                    "uploadVehicleFuelOrAdBlueLevelDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 ADD_BLUE")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.FUEL_ADBLUE_LEVEL,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.inOilCoolantLevel)) {
                                val partBody = createMultipartPart(
                                    imageEntity.inOilCoolantLevel!!,
                                    "uploadVehicleOilOrCoolantLevel",
                                    appContext
                                )
                                Log.d("IMWorker","2 OIL_COOLANT_LEVEL")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.OIL_COOLANT_LEVEL,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.inFogLights)) {
                                val partBody = createMultipartPart(
                                    imageEntity.inFogLights!!, "uploadVehicleLightsDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 FOG_LIGHTS")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.FOG_LIGHTS,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.inIndicatorsSideRepeaters)) {
                                val partBody = createMultipartPart(
                                    imageEntity.inIndicatorsSideRepeaters!!,
                                    "uploadVehicleIndicatorsOrSideRepeatersDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 INDICATORS_SIDE_REPEATERS")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.INDICATORS_SIDE_REPEATERS,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.inHornReverseBeeper)) {
                                val partBody = createMultipartPart(
                                    imageEntity.inHornReverseBeeper!!,
                                    "uploadVehicleHornOrReverseBeeperDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 HORN_REVERSE_BEEPER")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.HORN_REVERSE_BEEPER,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.inSteeringControl)) {
                                val partBody = createMultipartPart(
                                    imageEntity.inSteeringControl!!, "uploadVehicleSteeringDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 STEERING_CONTROL")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.STEERING_CONTROL,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.inBrakedEbsAbs)) {
                                val partBody = createMultipartPart(
                                    imageEntity.inBrakedEbsAbs!!, "uploadVehicleBrakesDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 BRAKED_EBS_ABS")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.BRAKED_EBS_ABS,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.exVehicleLockingSystem)) {
                                val partBody = createMultipartPart(
                                    imageEntity.exVehicleLockingSystem!!,
                                    "uploadVehicleLockingSystemDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 VEHICLE_LOCKING_SYSTEM")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.VEHICLE_LOCKING_SYSTEM,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.exBodyDamageNearSide)) {
                                val partBody = createMultipartPart(
                                    imageEntity.exBodyDamageNearSide!!, "uploadVehicleNearSideDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 NEAR_SIDE")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.NEAR_SIDE,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.exBodyDamageRear)) {
                                val partBody = createMultipartPart(
                                    imageEntity.exBodyDamageRear!!, "uploadVehicleRearDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 REAR")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.REAR,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.exBodyDamageOffside)) {
                                val partBody = createMultipartPart(
                                    imageEntity.exBodyDamageOffside!!, "uploadVehicleOffSideDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 OFF_SIDE")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.OFF_SIDE,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.exRegistrationNumberPlates)) {
                                val partBody = createMultipartPart(
                                    imageEntity.exRegistrationNumberPlates!!,
                                    "uploadVehicleRegistrationNumberPlateDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 REGISTRATION_NUMBER_PLATES")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.REGISTRATION_NUMBER_PLATES,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.exReflectorsMarkers)) {
                                val partBody = createMultipartPart(
                                    imageEntity.exReflectorsMarkers!!,
                                    "uploadVehicleReflectorOrMarkerDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 REFLECTORS_MARKERS")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.REFLECTORS_MARKERS,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.exWheelFixings)) {
                                val partBody = createMultipartPart(
                                    imageEntity.exWheelFixings!!,
                                    "uploadVehicleWheelsOrWheelFixingDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 WHEEL_FIXINGS")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.WHEEL_FIXINGS,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.exTyreConditionThreadDepth)) {
                                val partBody = createMultipartPart(
                                    imageEntity.exTyreConditionThreadDepth!!,
                                    "uploadVehicleTyresDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 TYRE_CONDITION_THREAD_DEPTH")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.TYRE_CONDITION_THREAD_DEPTH,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.exOilFuelCoolantLeaks)) {
                                val partBody = createMultipartPart(
                                    imageEntity.exOilFuelCoolantLeaks!!,
                                    "uploadVehicleOilOrFuelOrCoolantLeaksDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 OIL_FUEL_COOLANT_LEAKS")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.OIL_FUEL_COOLANT_LEAKS,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.exExcessiveEngExhaustSmoke)) {
                                val partBody = createMultipartPart(
                                    imageEntity.exExcessiveEngExhaustSmoke!!,
                                    "uploadVehExcessiveEngineExhaustSmokeDef",
                                    appContext
                                )
                                Log.d("IMWorker","2 EXCESSIVE_ENG_EXHAUST_SMOKE")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.EXCESSIVE_ENG_EXHAUST_SMOKE,
                                    partBody
                                )
                            }
                            if (checkNullorEmpty(imageEntity.exSpareWheel)) {
                                val partBody = createMultipartPart(
                                    imageEntity.exSpareWheel!!, "uploadSpareWheelDefect",
                                    appContext
                                )
                                Log.d("IMWorker","2 SPARE_WHEEL")
                                mainRepo.UploadVehicleDefectImages(
                                    clebUserId,
                                    lmId,
                                    vmId,
                                    currentDateTime,
                                    DBImages.SPARE_WHEEL,
                                    partBody
                                )
                            }

                        }

                    }

                    3->{


                        val currentLoction = Prefs.getInstance(App.instance).currLocationId
                        val workingLocation = Prefs.getInstance(App.instance).workLocationId
                        val locationID: Int = if (workingLocation != 0) {
                            workingLocation
                        } else {
                            currentLoction
                        }
                        val response = mainRepo.SaveVehicleInspectionInfo(SaveVehicleInspectionInfo(
                            prefs.clebUserId.toInt(),
                            prefs.inspectionDateTime?:"Date error",
                            prefs.inspectionID.replace(" ",""),
                            locationID,
                            prefs.vmId
                        ))

                        if(!response.isSuccessful||response.failed){
                            prefs.isInspectionIDFailedToUpload = true
                        }else{
                            prefs.isInspectionIDFailedToUpload = false
                            prefs.updateInspectionStatus(true)
                        }
                    }
                    4->{
                        prefs.isBreakDownImagesAreUploading = true
                        if (!prefs.breakDownSpareWheelUri.isNullOrBlank()&& prefs.currBreakDownInspectionId!=0) {
                            val partBody = createMultipartPart(
                                prefs.breakDownSpareWheelUri!!, "uploadDAVehBreakDownInpectionSpearWheelPictureFile",
                                appContext
                            )
                            Log.d("breakDownSpareWheelUri","Uploading>> ${prefs.breakDownSpareWheelUri}")
                            val response = withContext(Dispatchers.IO){
                                mainRepo.UploadDAVehBreakDownInpectionSpearWheelPictureFile(
                                    prefs.currBreakDownInspectionId,
                                    0,
                                    partBody
                                )
                            }
                        }
                        if (!prefs.breakDownVehicleInteriorUri.isNullOrBlank()&& prefs.currBreakDownInspectionId!=0) {
                            val partBody = createMultipartPart(
                                prefs.breakDownVehicleInteriorUri!!, "uploadDAVehBreakDownInpectionInteriorPictureFile",
                                appContext
                            )
                            Log.d("breakDownVehicleInteriorUri","Uploading>> ${prefs.breakDownVehicleInteriorUri}")
                            val response = withContext(Dispatchers.IO){
                                mainRepo.UploadDAVehBreakDownInpectionInteriorPictureFile(
                                    prefs.currBreakDownInspectionId,
                                    0,
                                    partBody
                                )
                            }
                        }
                        if (!prefs.breakDownLoadingInteriorUri.isNullOrBlank()&& prefs.currBreakDownInspectionId!=0) {
                            val partBody = createMultipartPart(
                                prefs.breakDownLoadingInteriorUri!!, "uploadDAVehBreakDownInpectionLoadingInteriorPictureFile",
                                appContext
                            )
                            Log.d("breakDownLoadingInteriorUri","Uploading>> ${prefs.breakDownLoadingInteriorUri}")
                            val response = withContext(Dispatchers.IO){
                                mainRepo.UploadDAVehBreakDownInpectionLoadingInteriorPictureFile(
                                    prefs.currBreakDownInspectionId,
                                    0,
                                    partBody
                                )
                            }
                        }
                        if (!prefs.breakDownToolsPictureUri.isNullOrBlank()&& prefs.currBreakDownInspectionId!=0) {
                            val partBody = createMultipartPart(
                                prefs.breakDownToolsPictureUri!!, "uploadDAVehBreakDownInpectionToolsPictureFile",
                                appContext
                            )
                            Log.d("breakDownToolsPictureUri","Uploading>> ${prefs.breakDownToolsPictureUri}")
                            val response = withContext(Dispatchers.IO){
                                mainRepo.UploadDAVehBreakDownInpectionToolsPictureFile(
                                    prefs.currBreakDownInspectionId,
                                    0,
                                    partBody
                                )
                            }
                        }
                        if (!prefs.breakDownVinNumberPictureUri.isNullOrBlank()&& prefs.currBreakDownInspectionId!=0) {
                            val partBody = createMultipartPart(
                                prefs.breakDownVinNumberPictureUri!!, "uploadDAVehBreakDownInpectionVinNoPictureFile",
                                appContext
                            )
                            Log.d("breakDownToolsPictureUri","Uploading>> ${prefs.breakDownVinNumberPictureUri}")
                            val response = withContext(Dispatchers.IO){
                                mainRepo.UploadDAVehBreakDownInpectionVinNoPictureFile(
                                    prefs.currBreakDownInspectionId,
                                    0,
                                    partBody
                                )
                            }
                        }
                        prefs.isBreakDownImagesAreUploading = false
                    }
                }

            } catch (e: Exception) {
                Log.e("ImageWorker", "ImageUploadException e.printStackTrace()")
            }
        }

        return Result.success()
    }

    private fun createMultipartPart(
        image: String,
        partName: String,
        context: Context
    ): MultipartBody.Part {
/*        val uniqueFileName = "image_${UUID.randomUUID()}.jpg"
        var bs64ImageString = getImageBitmapFromUri(context, image.toUri())
        val requestBody = bs64ImageString!!.toRequestBody()
        return MultipartBody.Part.createFormData(partName, uniqueFileName, requestBody)*/
        return try {
            val uniqueFileName = "image_${UUID.randomUUID()}.jpg"
            val bs64ImageString = getImageBitmapFromUri(context, image.toUri())
            val requestBody = bs64ImageString!!.toRequestBody()
            MultipartBody.Part.createFormData(partName, uniqueFileName, requestBody)
        } catch (e: Exception) {
            e.printStackTrace()
            val defaultRequestBody = "".toRequestBody("text/plain".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, "default.jpg", defaultRequestBody)
        }
    }

    private fun checkNullorEmpty(value: String?): Boolean {
        return !(value.isNullOrEmpty() || value == "empty")
    }
}
