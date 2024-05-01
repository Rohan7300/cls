package com.clebs.celerity.utils

import android.content.Context
import android.util.Log
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
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val userId = inputData.getInt("clebUserId", 0)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        var todayDate = dateFormat.format(Date())
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        val osRepo = OSyncRepo(OfflineSyncDB.invoke(applicationContext))
        var currentDateTime = getCurrentDateTime()
        GlobalScope.launch {


            val data = osRepo.getData(userId, todayDate)
            val changedPartsList = mutableListOf<MultipartBody.Part>()
            val indexes = mutableListOf<Int>()

            if (data.dashboardImage != null) {
                changedPartsList.add(
                    createMultipartPart(
                        data.dashboardImage!!,
                        "uploadVehicleDashBoardImage"
                    )
                )
                indexes.add(1)
            }

            if (data.frontImage != null) {
                changedPartsList.add(
                    createMultipartPart(
                        data.frontImage!!,
                        "uploadVehicleFrontImage"
                    )
                )
                indexes.add(2)
            }

            if (data.nearSideImage != null) {
                changedPartsList.add(
                    createMultipartPart(
                        data.nearSideImage!!,
                        "uploadVehicleNearSideImage"
                    )
                )
                indexes.add(3)
            }

            if (data.rearSideImage != null) {
                changedPartsList.add(
                    createMultipartPart(
                        data.rearSideImage!!,
                        "uploadVehicleRearImage"
                    )
                )
                indexes.add(4)
            }

            if (data.offSideImage != null) {
                changedPartsList.add(
                    createMultipartPart(
                        data.offSideImage!!,
                        "uploadVehicleOffSideImage"
                    )
                )
                indexes.add(6)
            }

            if (changedPartsList.isNotEmpty()) {
                /*changedPartsList.forEachIndexed { index, part ->
                    mainRepo.uploadVehicleImage(userId, part, indexes[index])
                }*/

                try {

                    val dashresponse =
                        mainRepo.uploadVehicleImage(userId, changedPartsList[0], 1, currentDateTime)
                    if (!dashresponse.isSuccessful) {
                        data.isdashboardUploadedFailed = true
//                        return@launch
                    }

                    val frontresponse =
                        mainRepo.uploadVehicleImage(userId, changedPartsList[1], 2, currentDateTime)
                    if (!frontresponse.isSuccessful) {
                        data.isfrontImageFailed = true
                        // return@launch
                    }

                    val nearResponse =
                        mainRepo.uploadVehicleImage(userId, changedPartsList[2], 3, currentDateTime)
                    if (!nearResponse.isSuccessful) {
                        data.isnearSideFailed = true
                        //return@launch
                    }

                    val rearResponse =
                        mainRepo.uploadVehicleImage(userId, changedPartsList[3], 4, currentDateTime)
                    if (!rearResponse.isSuccessful) {
                        data.isrearSideFailed = true
                        //return@launch
                    }

                    val offsideResponse =
                        mainRepo.uploadVehicleImage(userId, changedPartsList[4], 6, currentDateTime)
                    if (!offsideResponse.isSuccessful) {
                        data.isoffSideFailed = true
                        //return@launch
                    }

                } catch (e: Exception) {
                    Log.e("ImageWorker", "ImageUploadException e.printStackTrace()")
                }
            }

        }

        return Result.success()
    }

    private fun createMultipartPart(image: String, partName: String): MultipartBody.Part {
        val uniqueFileName = "image_${UUID.randomUUID()}.jpg"
        val requestBody = decodeBase64Image(image).toRequestBody()
        return MultipartBody.Part.createFormData(partName, uniqueFileName, requestBody)
    }
}
