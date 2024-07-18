package com.clebs.celerity_admin.utils

import android.content.ContentResolver
import android.content.Context
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
import com.clebs.celerity_admin.utils.DependencyClass.currentWeeklyDefectItem
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
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

                Log.d("DbDefectSheet","Worker $dbDefectSheet")
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
                        Log.e("kfdfhdjfdj", "doWork: ", )
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
                        Log.e("URIIIIIIIIIIIIIIIIIIBACKGROUND", "::::: "+dbDefectSheet.threeSixtyVideo, )
                        val path = getFilePathFromURI(appContext, dbDefectSheet.threeSixtyVideo!!.toUri())
                        val file: File = File(path!!)
                        // Parsing any Media type file
                        // Parsing any Media type file

                        val fileToUpload: MultipartBody.Part =
                           createFormData("filename", file.name,file.asRequestBody() )
                     mainRepo.Uploadvideo360(currentWeeklyDefectItem!!.vdhCheckId, dateToday(),fileToUpload)

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
    fun uriToByteArray(uri: Uri,context:Context): ByteArray? {
        var inputStream: InputStream? = null
        var byteArray: ByteArray? = null

        try {
            // Open an InputStream for the URI using the ContentResolver
            val contentResolver: ContentResolver = context.contentResolver
            inputStream = contentResolver.openInputStream(uri)

            // Create a byte array to hold the data
            byteArray = if (inputStream != null) {
                inputStream.readBytes()
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            // Close the InputStream
            inputStream?.close()
        }

        return byteArray
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