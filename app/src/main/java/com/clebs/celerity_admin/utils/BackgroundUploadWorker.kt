package com.clebs.celerity_admin.utils

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.clebs.celerity_admin.models.SaveDefectSheetWeeklyOSMCheckRequest
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.ui.App
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.UUID


class BackgroundUploadWorker(
    private var appContext: Context, workerParams: WorkerParameters,
) : Worker(appContext, workerParams) {
    override fun doWork(): ListenableWorker.Result {
        val defectSheetUserId = inputData.getInt("defectSheetUserId", 0)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        GlobalScope.launch {
            val dbDefectSheet = App.offlineSyncDB?.getDefectSheet(
                DependencyClass.currentWeeklyDefectItem!!.vdhCheckId
            )
            if (dbDefectSheet != null) {
                val response = mainRepo.SaveDefectSheetWeeklyOSMCheck(
                    SaveDefectSheetWeeklyOSMCheckRequest(
                        Comment = dbDefectSheet.comment ?: "",
                        PowerSteering = dbDefectSheet.powerSteeringCheck,
                        PowerSteeringLiquid = dbDefectSheet.powerSteeringCheck,
                        TyrePressureFrontNS = getRadioButtonState(dbDefectSheet.tyrePressureFrontNSRB),
                        TyrePressureFrontOS = getRadioButtonState(dbDefectSheet.tyrePressureFrontOSRB),
                        TyrePressureRearNS = getRadioButtonState(dbDefectSheet.tyrePressureRearNSRB),
                        TyrePressureRearOS = getRadioButtonState(dbDefectSheet.tyrePressureRearOSRB),
                        TyreThreadDepthFrontNSVal = 0,
                        TyreThreadDepthFrontOSVal = 0,
                        TyreThreadDepthRearNSVal = 0,
                        TyreThreadDepthRearOSVal = 0,
                        UserId = defectSheetUserId,
                        VdhAdminComment = "",
                        VdhBrakeFluidLevelId = dbDefectSheet.brakeFluidLevelID,
                        VdhCheckId = dbDefectSheet.id,
                        VdhDefChkImgOilLevelId = dbDefectSheet.oilLevelID,
                        VdhEngineCoolantLevelId = dbDefectSheet.engineCoolantLevelID,
                        VdhWindScreenConditionId = dbDefectSheet.windScreenConditionId,
                        VdhWindowScreenWashingLiquidId = dbDefectSheet.windScreenWashingLevelId,
                        WeeklyActionCheck = dbDefectSheet.WeeklyActionCheck,
                        WeeklyApproveCheck = dbDefectSheet.WeeklyApproveCheck,
                        WindowScreenState = false,
                        WindscreenWashingLiquid = false
                    )
                )
                if (response.isSuccessful || response.failed) {
                    if (dbDefectSheet.tyreDepthFrontNSImage != null) {

                        val partBody = createMultipartPart(
                            dbDefectSheet.tyreDepthFrontNSImage!!,
                            "uploadVehOSMDefectChkFile",
                            appContext
                        )
                        mainRepo.UploadVehOSMDefectChkFile(
                            dbDefectSheet.id,
                            DefectFileType.TyrethreaddepthFrontNS,
                            dateToday(),
                            partBody
                        )
                    }
                    if (dbDefectSheet.tyreDepthRearNSImage != null) {
                        val partBody = createMultipartPart(
                            dbDefectSheet.tyreDepthRearNSImage!!,
                            "uploadVehOSMDefectChkFile",
                            appContext
                        )
                        mainRepo.UploadVehOSMDefectChkFile(
                            dbDefectSheet.id,
                            DefectFileType.TyrethreaddepthRearNS,
                            dateToday(),
                            partBody
                        )

                    }
                    if (dbDefectSheet.tyreDepthRearOSImage != null) {
                        val partBody = createMultipartPart(
                            dbDefectSheet.tyreDepthRearOSImage!!,
                            "uploadVehOSMDefectChkFile",
                            appContext
                        )
                        mainRepo.UploadVehOSMDefectChkFile(
                            dbDefectSheet.id,
                            DefectFileType.TyrethreaddepthRearOS,
                            dateToday(),
                            partBody
                        )


                    }
                    if (dbDefectSheet.tyreDepthFrontOSImage != null) {
                        val partBody = createMultipartPart(
                            dbDefectSheet.tyreDepthFrontOSImage!!,
                            "uploadVehOSMDefectChkFile",
                            appContext
                        )
                        mainRepo.UploadVehOSMDefectChkFile(
                            dbDefectSheet.id,
                            DefectFileType.TyrethreaddepthFrontOS,
                            dateToday(),
                            partBody
                        )

                    }
                    if (dbDefectSheet.addBlueLevelImage != null) {
                        val partBody = createMultipartPart(
                            dbDefectSheet.addBlueLevelImage!!,
                            "uploadVehOSMDefectChkFile",
                            appContext
                        )
                        mainRepo.UploadVehOSMDefectChkFile(
                            dbDefectSheet.id, DefectFileType.AddBlueLevel, dateToday(), partBody
                        )

                    }

                    if (dbDefectSheet.engineLevelImage != null) {
                        val partBody = createMultipartPart(
                            dbDefectSheet.engineLevelImage!!,
                            "uploadVehOSMDefectChkFile",
                            appContext
                        )
                        mainRepo.UploadVehOSMDefectChkFile(
                            dbDefectSheet.id, DefectFileType.EngineOilLevel, dateToday(), partBody
                        )


                    }
                    if (dbDefectSheet.nsWingMirrorImage != null) {
                        val partBody = createMultipartPart(
                            dbDefectSheet.nsWingMirrorImage!!,
                            "uploadVehOSMDefectChkFile",
                            appContext
                        )
                        mainRepo.UploadVehOSMDefectChkFile(
                            dbDefectSheet.id, DefectFileType.NSWingMirror, dateToday(), partBody
                        )
                    }
                    if (dbDefectSheet.osWingMirrorImage != null) {
                        val partBody = createMultipartPart(
                            dbDefectSheet.osWingMirrorImage!!,
                            "uploadVehOSMDefectChkFile",
                            appContext
                        )
                        mainRepo.UploadVehOSMDefectChkFile(
                            dbDefectSheet.id, DefectFileType.OSWingMirror, dateToday(), partBody
                        )
                    }
                    if (dbDefectSheet.threeSixtyVideo != null) {

                        val file: File =
                            getFileFromUri(appContext, dbDefectSheet.threeSixtyVideo!!.toUri())!!

                        mainRepo.Uploadvideo360(
                            dbDefectSheet.id,
                            dateToday(),
                            MultipartBody.Part.createFormData(
                                "UploadVan360Video",
                                "video ${UUID.randomUUID()}",
                                file.asRequestBody()
                            )
                        )

                    }
                    if (dbDefectSheet.otherImages != null) {


                        val partBody = createMultipartPart(
                            dbDefectSheet.otherImages!!, "uploadVehOSMDefectChkFile", appContext
                        )
                        mainRepo.UploadVehOSMDefectChkFile(
                            dbDefectSheet.id, DefectFileType.OtherPicOfParts, dateToday(), partBody
                        )

                    }
                }
            }


        }

        return Result.success()
    }

    private fun createMultipartPart(
        image: String, partName: String, context: Context
    ): MultipartBody.Part {
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

    fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            // Check the URI scheme
            when (uri.scheme) {
                "content" -> {
                    // Content URI, use ContentResolver to get the file
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val file = createTempFile(context)
                    inputStream?.copyToFile(file)
                    file
                }

                "file" -> {
                    // File URI, get the file path directly
                    File(uri.path ?: return null)
                }

                else -> {
                    // Unsupported URI scheme
                    null
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun createTempFile(context: Context): File {
        val fileName = "temp_file_${System.currentTimeMillis()}.tmp"
        return File.createTempFile(fileName, null, context.cacheDir)
    }

    private fun InputStream.copyToFile(file: File) {
        use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
    }

}