package com.clebs.celerity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.adapters.QuestionAdapter
import com.clebs.celerity.databinding.FragmentDeliveryProceduresBinding
import com.clebs.celerity.models.QuestionWithOption

class DeliveryProcedures : Fragment() {
    private lateinit var binding: FragmentDeliveryProceduresBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeliveryProceduresBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val questions = arrayListOf(
            QuestionWithOption("Age verification Deliveries *"),
            QuestionWithOption("Handle all packages with care *"),
            QuestionWithOption("Geocodes, Geo fences *"),
            QuestionWithOption("Verify Address, Street, and Houses *"),
            QuestionWithOption("Person named on shipping Label *"),
            QuestionWithOption("POD (Photo On Delivery) *"),
            QuestionWithOption("Letterbox Deliveries *"),
            QuestionWithOption("Package Left 'as Instructed'; PHR (Preference Honor Rate) *"),
            QuestionWithOption("Delivered to a neighbor *"),
            QuestionWithOption("Front Desk, Mail room *"),
            QuestionWithOption("Locker Deliveries/Collections *"),
            QuestionWithOption("Contact Compliance *")
        )


        val adapter = QuestionAdapter(questions)
        binding.DeliveryRV.adapter = adapter
        binding.DeliveryRV.layoutManager = LinearLayoutManager(requireContext())
    }

}