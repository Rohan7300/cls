package com.clebs.celerity.ViewModel

import android.text.format.DateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity.database.ImagesRepo
import com.clebs.celerity.ui.App
import com.clebs.celerity.utils.dateToday

import java.util.Date

class ImageViewModelProviderFactory(
    val imagesRepo: ImagesRepo
) :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val todayDate = dateToday()
        return ImageViewModel(imagesRepo,todayDate) as T
    }
}