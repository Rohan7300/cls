package com.clebs.celerity.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentQuestinareBinding
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.ViewAdaptor
import com.google.android.material.tabs.TabLayout

class QuestinareFragment : Fragment() {
    lateinit var binding: FragmentQuestinareBinding
    private val TAG = "QuestinareFrag"
    private var rideAlongID = 0
    private var leadDriverID = 0
    lateinit var adapter: ViewAdaptor
    lateinit var viewModel: MainViewModel
    var firsttime: Boolean = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::binding.isInitialized) {
            binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_questinare, container, false)
        }
        binding.tablay.getTabAt(0)?.select()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rideAlongID = arguments?.getInt("rideAlongID", 0) ?: 0
        leadDriverID = arguments?.getInt("leadDriverID", 0) ?: 0
        viewModel = (activity as HomeActivity).viewModel

        if (rideAlongID != null && leadDriverID != null) {
            Log.d(TAG, "RIDEALONGID $rideAlongID \n LEADDRIVERID $leadDriverID")
        } else {
            Log.d(TAG, "RIDEALONGID null \n LEADDRIVERID null")
        }

        val headingList = arrayOf(
            "Preparedness",
            "Start Up",
            "Going On",
            "Delivery Procedures",
            "Return",
            "Final Assessment"
        )
        val str = "Observations and explanations must be conducted on a" +
                " Nursery Level 1 route. The new driver should make at least 50 unassisted deliveries," +
                " before being considered as fully trained. Where an individual is identified as not ready" +
                " the On Site Manager must be made aware of the concerns and the development path to competency" +
                " before the individual will be allowed to take a route out on their own. All sections must be completed."

        binding.hh1.setOnClickListener() {
            val orignalParams = binding.hiddenText.layoutParams
            if (binding.hiddenText.visibility == View.GONE) {
                binding.hiddenText.visibility = View.VISIBLE
                binding.hiddenText.text = str
                binding.arrow1.setImageResource(R.drawable.down_arrow)
            } else {
                binding.hiddenText.visibility = View.GONE
                binding.arrow1.setImageResource(R.drawable.rightarrow)
                binding.hiddenText.layoutParams =
                    orignalParams
            }
        }


        binding.tablay.setupWithViewPager(binding.viewPager)

        adapter = ViewAdaptor(
            requireContext(),
            childFragmentManager,
            headingList.size,
        )
        binding.viewPager.adapter = adapter
        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tablay))

        binding.tablay.getTabAt(0)?.select()

        for (i in headingList.indices) {
            binding.tablay.getTabAt(i)?.text = headingList[i]
        }

        binding.tablay!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager!!.currentItem = tab.position
                tab.text = headingList[tab.position]
                tab.text = tab.contentDescription
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.text = tab.contentDescription?.substring(0, 3)

            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        if (this::adapter.isInitialized && this::viewModel.isInitialized) {
            binding.viewPager.currentItem = 0
            binding.viewPager.setCurrentItem(0, true)
            viewModel.currentViewPage.postValue(0)
        }

        viewModel.currentViewPage.observe(viewLifecycleOwner) {
            if (firsttime) {
                firsttime = false
            } else {
                it.let { currentPage ->
                    binding.viewPager.currentItem = currentPage!!

                }
            }

        }

    }

    override fun onResume() {
        super.onResume()
        if (this::adapter.isInitialized && this::viewModel.isInitialized) {
            binding.viewPager.currentItem = 0
            firsttime = true
            viewModel.currentViewPage.postValue(0)
        }
    }

}