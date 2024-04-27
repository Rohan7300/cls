package com.clebs.celerity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.NotificationAdapter
import com.clebs.celerity.databinding.FragmentNotifficationsBinding
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.Prefs


class NotifficationsFragment : Fragment() {

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
    lateinit var prefs:Prefs
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotifficationsBinding.inflate(inflater, container, false)
        homeActivity = (activity as HomeActivity)
        viewModel = homeActivity.viewModel

        notificationAdapter = NotificationAdapter()
        binding.rvNotifications.adapter = notificationAdapter
        binding.rvNotifications.layoutManager = LinearLayoutManager(requireContext())
        prefs = Prefs.getInstance(requireContext())

        observers()
        showDialog()
        viewModel.GetNotificationListByUserId(prefs.clebUserId.toInt())
        return binding.root
    }

    private fun observers() {
        viewModel.livedataGetNotificationListByUserId.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                if(it.size>0){
                    notificationAdapter.saveData(it)
                }
            }
        }
    }
}