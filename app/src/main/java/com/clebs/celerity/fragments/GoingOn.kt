package com.clebs.celerity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.QuestionAdapter
import com.clebs.celerity.databinding.FragmentGoingOnBinding
import com.clebs.celerity.models.QuestionWithOption
import com.clebs.celerity.models.requests.SaveQuestionaireOnGoingActivitiesRequest
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.showToast


class GoingOn : Fragment() {
    private lateinit var binding: FragmentGoingOnBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var pref: Prefs
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoingOnBinding.inflate(inflater, container, false)
        return binding.root
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

        pref = Prefs.getInstance(requireContext())
        viewModel = (activity as HomeActivity).viewModel
        loadingDialog = (activity as HomeActivity).loadingDialog

        val adapter = QuestionAdapter(questions)
        binding.GoingOnRV.adapter = adapter
        binding.GoingOnRV.layoutManager = LinearLayoutManager(requireContext())

        viewModel.liveDataQuestionaireGoingOn.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            if (it != null) {
                viewModel.currentViewPage.postValue(3)
                pref.quesID = it.QuestionId
            }
        }

        binding.saveBtnGoinon.setOnClickListener {
            val allQuestionsSelected = adapter.areAllQuestionsSelected()
            val comment =
                if (binding.goingOnCommentET.text.isNullOrEmpty()) "" else binding.goingOnCommentET.text
            if (allQuestionsSelected) {
                val selectedOptions = questions.map { it.selectedOption }
                saveGoingonApi(selectedOptions, comment)

            } else {
                showToast("Not all selected", requireContext())
            }
        }

    }

    private fun saveGoingonApi(selectedOptions: List<String>, comment: CharSequence?) {
        loadingDialog.show()
        viewModel.SaveQuestionaireGoingOn(
            SaveQuestionaireOnGoingActivitiesRequest(
                QuestionId = pref.quesID,
                RaOnGoingActivitiesNavigatingByZone = selectedOptions[0],
                RaOnGoingActivitiesLocatePackages = selectedOptions[1],
                RaOnGoingActivitiesParkingVehSecurity = selectedOptions[2],
                RaOnGoingActivitiesDpmoConcession = selectedOptions[3],
                RaOnGoingActivitiesDcrParcelReturned = selectedOptions[4],
                RaOnGoingActivitiesMissedYouCard = selectedOptions[5],
                RaOnGoingActivitiesDamagedParcel = selectedOptions[6],
                RaOnGoingActivitiesCustomerFeedback = selectedOptions[7]
            )
        )
    }

}