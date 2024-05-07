package com.clebs.celerity.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.ExpiringDocUploadListener
import com.clebs.celerity.adapters.NotificationAdapter
import com.clebs.celerity.databinding.FragmentNotifficationsBinding
import com.clebs.celerity.ui.CreateTicketsActivity
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.getMimeType
import com.clebs.celerity.utils.showToast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody


class NotificationsFragment : Fragment() {

    lateinit var binding: FragmentNotifficationsBinding
    lateinit var viewModel: MainViewModel
    lateinit var homeActivity: HomeActivity


    val showDialog: () -> Unit = {
        (activity as HomeActivity).showDialog()
    }
    val hideDialog: () -> Unit = {
        (activity as HomeActivity).hideDialog()
    }
    lateinit var notificationAdapter: NotificationAdapter
    lateinit var prefs: Prefs


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotifficationsBinding.inflate(inflater, container, false)
        homeActivity = (activity as HomeActivity)
        viewModel = homeActivity.viewModel
        prefs = Prefs.getInstance(requireContext())
        notificationAdapter = NotificationAdapter(
            findNavController(),
            homeActivity.supportFragmentManager,
            homeActivity,
            homeActivity.loadingDialog,
            viewModel,
            prefs,
            viewLifecycleOwner
        )
        binding.rvNotifications.adapter = notificationAdapter
        binding.rvNotifications.layoutManager = LinearLayoutManager(requireContext())


        observers()
        showDialog()
        viewModel.GetNotificationListByUserId(prefs.clebUserId.toInt())
        return binding.root
    }

    private fun observers() {
        viewModel.livedataGetNotificationListByUserId.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                if (it.size > 0) {
                    notificationAdapter.saveData(it)
                }
            }
        }
    }
}