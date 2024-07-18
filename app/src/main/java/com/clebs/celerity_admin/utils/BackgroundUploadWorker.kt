package com.clebs.celerity_admin.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.clebs.celerity_admin.models.SaveDefectSheetWeeklyOSMCheckRequest
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.ui.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import okio.source
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

                        val partBody = createVideoMultipart(
                            dbDefectSheet.threeSixtyVideo!!,
                            "UploadVan360Video",
                            appContext
                        )
                        mainRepo.Uploadvideo360(
                            dbDefectSheet.id,
                            dateToday(),
                            partBody
                        )

                    }
                    if (dbDefectSheet.otherImages != null) {
                        for (imageUri in convertStringToList(dbDefectSheet.otherImages!!)) {
                            val partBody = createMultipartPart(
                                imageUri, "uploadOtherPictureOfPartsFile", appContext
                            )
                            val response = withContext(Dispatchers.IO) {
                                mainRepo.UploadOtherPictureOfPartsFile(
                                    dbDefectSheet.id, DefectFileType.OtherPicOfParts, dateToday(), partBody
                                )
                            }
                            if (response.isSuccessful) {
                                println("Upload successful for $imageUri")
                            } else {
                                println("Upload failed for $imageUri")
                            }
                        }
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

    private fun createVideoMultipart(
        uri: String, partName: String, context: Context
    ): MultipartBody.Part {
        return try {
            val uriX = uri.toUri()
            val contentResolver = context.contentResolver

            // Ensure the app has read permissions
            context.grantUriPermission(context.packageName, uriX, Intent.FLAG_GRANT_READ_URI_PERMISSION)

            val mimeType = contentResolver.getType(uriX)
            val fileExtension = when (mimeType) {
                "video/mp4" -> ".mp4"
                else -> throw IllegalArgumentException("Unsupported file type")
            }

            val uniqueFileName = "${UUID.randomUUID()}$fileExtension"
            val inputStream: InputStream? = contentResolver.openInputStream(uriX)
            val requestBody = inputStream?.let {
                object : RequestBody() {
                    override fun contentType() = mimeType?.toMediaTypeOrNull()

                    override fun writeTo(sink: BufferedSink) {
                        it.source().use { source ->
                            sink.writeAll(source)
                        }
                    }
                }
            } ?: throw IllegalArgumentException("Unable to open input stream")

            MultipartBody.Part.createFormData(partName, uniqueFileName, requestBody)
        } catch (e: SecurityException) {
            Log.d("VideoUploadEx", "SecurityException: ${e.message}")
            val defaultRequestBody = "".toRequestBody("text/plain".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, "default.jpg", defaultRequestBody)
        } catch (e: Exception) {
            Log.d("VideoUploadEx", "Exception: ${e.message}")
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