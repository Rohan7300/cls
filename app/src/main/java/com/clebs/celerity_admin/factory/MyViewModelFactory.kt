package com.clebs.celerity_admin.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.viewModels.MainViewModel

class MyViewModelFactory (private val repository: MainRepo): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}