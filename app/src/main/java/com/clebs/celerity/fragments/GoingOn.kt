package com.clebs.celerity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.adapters.QuestionAdapter
import com.clebs.celerity.databinding.FragmentGoingOnBinding
import com.clebs.celerity.models.QuestionWithOption


class GoingOn : Fragment() {
    private lateinit var binding: FragmentGoingOnBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentGoingOnBinding.inflate(inflater,container,false).root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val questions = arrayListOf(
            QuestionWithOption("Navigating By zone*"),
            QuestionWithOption("Locate packages by scanning *"),
            QuestionWithOption("Parking and vehicle security *"),
            QuestionWithOption("DPMO -concessions *"),
            QuestionWithOption("DCR -parcels returned to stations *"),
            QuestionWithOption("'We missed you' cards *"),
            QuestionWithOption("Damaged parcels *"),
            QuestionWithOption("Customer feedback : Customer escalations and Positive delivery experience rate *")
        )


        val adapter = QuestionAdapter(questions)
        binding.GoingOnRV.adapter = adapter
        binding.GoingOnRV.layoutManager = LinearLayoutManager(requireContext())
    }

}