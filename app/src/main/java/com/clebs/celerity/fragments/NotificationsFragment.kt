package com.clebs.celerity.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.opengl.Visibility
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
import com.clebs.celerity.adapters.NotificationAdapterCallback
import com.clebs.celerity.databinding.FragmentNotifficationsBinding
import com.clebs.celerity.ui.CreateTicketsActivity
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.getMimeType
import com.clebs.celerity.utils.showToast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody


class NotificationsFragment : Fragment(), NotificationAdapterCallback {

    lateinit var binding: FragmentNotifficationsBinding
    lateinit var viewModel: MainViewModel
    private lateinit var homeActivity: HomeActivity


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
        (activity as HomeActivity).ActivityHomeBinding.title.text = "Notifications"
        prefs = Prefs.getInstance(requireContext())
        notificationAdapter = NotificationAdapter(
            findNavController(),
            homeActivity.supportFragmentManager,
            homeActivity,
            homeActivity.loadingDialog,
            viewModel,
            prefs,
            viewLifecycleOwner,
            this
        )
        binding.rvNotifications.adapter = notificationAdapter
        binding.rvNotifications.layoutManager = LinearLayoutManager(requireContext())

        binding.swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }

        observers()

//        viewModel.GetNotificationListByUserId(prefs.clebUserId.toInt())
        return binding.root
    }

    private fun observers() {
        viewModel.livedataGetNotificationListByUserId.observe(viewLifecycleOwner) {
            hideDialog()
            hideDialog()
            binding.swipeRefreshLayout.isRefreshing = false
            if (it != null) {
                if(it.size>0){
                    noDataLayout(false)
                    notificationAdapter.saveData(it)
                }else{
                    noDataLayout(true)
                }
            }else{
                notificationAdapter.saveData(listOf())
                noDataLayout(true)
            }
        }

        viewModel.liveDataMarkNotificationAsRead.observe(viewLifecycleOwner) {
            if (it != null)
                refresh()
        }
    }

    private fun noDataLayout(visibility: Boolean) {
            if(visibility){
                binding.notificationNodataLayout.visibility = View.VISIBLE
                binding.rvNotifications.visibility = View.GONE
            }else{
                binding.notificationNodataLayout.visibility = View.GONE
                binding.rvNotifications.visibility = View.VISIBLE
            }
    }

    override fun onResume() {
        super.onResume()
        showDialog()
        viewModel.GetNotificationListByUserId(prefs.clebUserId.toInt())
    }

    override fun refresh() {
        showDialog()
        viewModel.GetNotificationListByUserId(prefs.clebUserId.toInt())
    }
}