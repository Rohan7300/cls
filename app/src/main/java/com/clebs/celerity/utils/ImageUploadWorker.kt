package com.clebs.celerity.utils

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.util.UUID

class ImageUploadWorker(
    var appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val imagePartsList = inputData.getStringArray("imagePartsList") ?: return Result.failure()
        val userId = inputData.getInt("userId", 0)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        val uniqueFileName = "image_${UUID.randomUUID()}.jpg"
        for ((index, imagePart) in imagePartsList.withIndex()) {
            var partName = when (index) {
                0 -> "uploadVehicleDashBoardImage"
                1 -> "uploadVehicleFrontImage"
                2 -> "uploadVehicleNearSideImage"
                3 -> "uploadVehicleRearImage"
                4 -> "uploadVehicleOffSideImage"
                else -> "Invalid"
            }
            var partImage = MultipartBody.Part.createFormData(
                partName,
                uniqueFileName,
                decodeBase64Image(imagePart).toRequestBody()
            )
            GlobalScope.launch(Dispatchers.IO) {

                mainRepo.uploadVehicleImage(userId, partImage, index + 1)
            }
        }
        return Result.success()
    }
}
