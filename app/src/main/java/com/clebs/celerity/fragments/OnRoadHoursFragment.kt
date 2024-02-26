package com.clebs.celerity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentCompleteTaskBinding
import com.clebs.celerity.databinding.FragmentOnRoadHoursBinding
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.Prefs

class OnRoadHoursFragment : Fragment() {
    lateinit var binding: FragmentOnRoadHoursBinding
    lateinit var viewModel: MainViewModel
    lateinit var prefs: Prefs
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::binding.isInitialized) {
            binding = FragmentOnRoadHoursBinding.inflate(inflater, container, false)
        }
        init()
        return binding.root

    }

    private fun init() {
        viewModel = (activity as HomeActivity).viewModel
        prefs = Prefs.getInstance(requireContext())
        viewModel.livedataDailyWorkInfoByIdResponse.observe(viewLifecycleOwner) {
            if (it != null) {

            }
        }
        viewModel.GetDailyWorkInfoById(prefs.userID.toInt())
    }


}