package com.clebs.celerity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentRideAlongBinding


class RideAlongFragment : Fragment() {
    lateinit var binding: FragmentRideAlongBinding
    lateinit var viewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(!this::binding.isInitialized){
            binding = FragmentRideAlongBinding.inflate(inflater,container,false)
        }



        return binding.root
    }


}