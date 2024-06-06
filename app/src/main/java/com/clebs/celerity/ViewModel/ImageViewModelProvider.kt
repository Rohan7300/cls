package com.clebs.celerity.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity.database.ImagesRepo
import com.clebs.celerity.ui.App
import io.clearquote.assessment.cq_sdk.support.dateFormat
import java.util.Date

class ImageViewModelProviderFactory(
    val imagesRepo: ImagesRepo
) :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val todayDate = dateFormat.format(Date())
        return ImageViewModel(imagesRepo,todayDate) as T
    }
}