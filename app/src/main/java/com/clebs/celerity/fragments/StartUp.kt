package com.clebs.celerity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.adapters.QuestionAdapter
import com.clebs.celerity.databinding.FragmentStartUpBinding
import com.clebs.celerity.models.QuestionWithOption


class StartUp : Fragment() {
    private lateinit var binding: FragmentStartUpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val questions = arrayListOf(
            QuestionWithOption("CLS DA System Log in and out(payment and vehicle check)*"),
            QuestionWithOption("eMeter(every working day log in:start trip and end trip;speeding;FICO score)*"),
            QuestionWithOption("DVIC(pre and post trip checks)*"),
            QuestionWithOption("Use Of trollies,cages,bags *"),
            QuestionWithOption("Yard Safety *"),
            QuestionWithOption("loading Vehicle *")
        )


        val adapter = QuestionAdapter(questions)
        binding.startUpRv.adapter = adapter
        binding.startUpRv.layoutManager = LinearLayoutManager(requireContext())
    }


}