package com.clebs.celerity.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clebs.celerity.database.ImageEntity
import com.clebs.celerity.database.ImagesRepo
import com.clebs.celerity.utils.DBImages
import com.clebs.celerity.utils.DBNames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageViewModel(val imageRepo: ImagesRepo) : ViewModel() {


    var images: MutableLiveData<ImageEntity> = MutableLiveData(ImageEntity())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val imageEntity = imageRepo.getImagesbyUser()
                imageEntity?.let {
                    images.postValue(it)
                }
            } catch (e: Exception) {
                // Handle error
                Log.e("ViewModel", "Error fetching image data: ${e.message}")
            }
        }
    }

    fun insertImage(image: ImageEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                imageRepo.insertImage(image)
                val imageEntity = imageRepo.getImagesbyUser()
                imageEntity?.let {
                    images.postValue(it)
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Error inserting image: ${e.message}")
            }
        }
    }

    fun insertDefectName(name: ImageEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                imageRepo.insertDefectName(name)
                val imageEntity = imageRepo.getImagesbyUser()
                imageEntity?.let {
                    images.postValue(it)
                }
            } catch (e: Exception) {
                // Handle error
                Log.e("ViewModel", "Error inserting defect name: ${e.message}")
            }
        }
    }

}