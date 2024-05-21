package com.clebs.celerity.utils

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.database.OSyncRepo
import com.clebs.celerity.database.OfflineSyncDB
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.UUID

class ImageUploadWorker(
    var appContext: Context, workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val clebUserId = inputData.getInt("clebUserId", 0)
        val uploadtype = inputData.getInt("uploadtype", 0)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        var todayDate = dateFormat.format(Date())
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        val osRepo = OSyncRepo(OfflineSyncDB.invoke(appContext))
            var currentDateTime = getCurrentDateTime()
        GlobalScope.launch {

            val data = osRepo.getData(clebUserId, todayDate)
            logOSEntity("ImageWorker",data)

            try {
                when(uploadtype){
                    0->{
                        if (data.dashboardImage != null) {
                            val partBody = createMultipartPart(
                                data.dashboardImage!!, "uploadVehicleDashBoardImage",
                                appContext
                            )
                            val dashresponse =
                                mainRepo.uploadVehicleImage(clebUserId, partBody, 1, currentDateTime)
                            if (!dashresponse.isSuccessful) {
                                data.isdashboardUploadedFailed = true
                            }
                        }

                        if (data.frontImage != null) {
                            val partBody = createMultipartPart(
                                data.frontImage!!, "uploadVehicleFrontImage",
                                appContext
                            )
                            val frontresponse =
                                mainRepo.uploadVehicleImage(clebUserId, partBody, 2, currentDateTime)
                            if (!frontresponse.isSuccessful) {
                                data.isfrontImageFailed = true
                            }
                        }

                        if (data.nearSideImage != null) {
                            val partBody = createMultipartPart(
                                data.nearSideImage!!, "uploadVehicleNearSideImage",
                                appContext
                            )

                            val nearResponse =
                                mainRepo.uploadVehicleImage(clebUserId, partBody, 3, currentDateTime)
                            if (!nearResponse.isSuccessful) {
                                data.isnearSideFailed = true
                            }
                        }

                        if (data.rearSideImage != null) {
                            val partBody = createMultipartPart(
                                data.rearSideImage!!, "uploadVehicleRearImage",
                                appContext
                            )
                            val rearResponse =
                                mainRepo.uploadVehicleImage(clebUserId, partBody, 4, currentDateTime)
                            if (!rearResponse.isSuccessful) {
                                data.isrearSideFailed = true
                            }
                        }

                        if (data.offSideImage != null) {
                            val partBody = createMultipartPart(
                                data.offSideImage!!, "uploadVehicleOffSideImage",
                                appContext
                            )

                            val offsideResponse =
                                mainRepo.uploadVehicleImage(clebUserId, partBody, 6, currentDateTime)
                            if (!offsideResponse.isSuccessful) {
                                data.isoffSideFailed = true
                            }
                        }

                        if (data.addblueImage != null) {
                            val partBody = createMultipartPart(
                                data.addblueImage!!, "uploadVehicleAddBlueImage",
                                appContext
                            )
                            val addBlueResponse = mainRepo.uploadVehicleImage(
                                clebUserId, partBody, 7, currentDateTime
                            )
                            if (!addBlueResponse.isSuccessful)
                                data.isaddblueImageFailed = true
                        }

                        if (data.oillevelImage != null) {
                            val partBody = createMultipartPart(
                                data.oillevelImage!!, "uploadVehicleOilLevelImage",
                                appContext
                            )
                            val oilLevelResponse = mainRepo.uploadVehicleImage(
                                clebUserId, partBody, 5, currentDateTime
                            )
                            if (!oilLevelResponse.isSuccessful)
                                data.isoillevelImageFailed = true
                        }
                        if(data.faceMaskImage!=null){
                            val partBody = createMultipartPart(
                                data.faceMaskImage!!,"uploadFaceMaskImage",
                                appContext
                            )
                            val selfieeRes = mainRepo.uploadVehicleImage(clebUserId,partBody,0,currentDateTime)
                            if(!selfieeRes.isSuccessful)
                                data.isfaceMaskImageFailed = true
                        }
                    }
                    1->{
                        if(data.faceMaskImage!=null){
                            val partBody = createMultipartPart(
                                data.faceMaskImage!!,"uploadFaceMaskImage",
                                appContext
                            )
                            val selfieeRes = mainRepo.uploadVehicleImage(clebUserId,partBody,0,currentDateTime)
                            if(!selfieeRes.isSuccessful)
                                data.isfaceMaskImageFailed = true
                        }
                    }
                    2->{

                    }
                }

            } catch (e: Exception) {
                Log.e("ImageWorker", "ImageUploadException e.printStackTrace()")
            }
        }

        return Result.success()
    }

    private fun createMultipartPart(image: String, partName: String,context: Context): MultipartBody.Part {
        val uniqueFileName = "image_${UUID.randomUUID()}.jpg"
        var bs64ImageString = getImageBitmapFromUri(context, image.toUri())
        val requestBody = bs64ImageString!!.toRequestBody()
        return MultipartBody.Part.createFormData(partName, uniqueFileName, requestBody)
    }
}
