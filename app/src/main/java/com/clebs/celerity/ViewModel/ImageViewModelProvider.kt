package com.clebs.celerity.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity.database.ImagesRepo
import com.clebs.celerity.ui.App

class ImageViewModelProviderFactory(
    val imagesRepo: ImagesRepo
) :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ImageViewModel(imagesRepo) as T
    }
}