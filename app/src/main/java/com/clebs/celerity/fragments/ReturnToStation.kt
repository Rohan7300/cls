package com.clebs.celerity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.adapters.QuestionAdapter
import com.clebs.celerity.databinding.FragmentReturnToStationBinding
import com.clebs.celerity.models.QuestionWithOption

class ReturnToStation : Fragment() {
    private lateinit var binding: FragmentReturnToStationBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentReturnToStationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val questions = arrayListOf(
            QuestionWithOption("Unload bags and parcels *"),
            QuestionWithOption("Hand returned packages to AMZL *")
        )

        val adapter = QuestionAdapter(questions)
        binding.ReturnRV.adapter = adapter
        binding.ReturnRV.layoutManager = LinearLayoutManager(requireContext())
    }

}