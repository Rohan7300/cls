package com.clebs.celerity.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.QuestionAdapter
import com.clebs.celerity.databinding.FragmentPrepardnessBinding
import com.clebs.celerity.models.QuestionWithOption
import com.clebs.celerity.models.requests.SaveQuestionairePreparednessRequest
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.showToast

class Prepardness : Fragment() {
    lateinit var binding: FragmentPrepardnessBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var pref:Prefs
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setRetainInstance(true);
        binding = FragmentPrepardnessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val questions = arrayListOf(
            QuestionWithOption("Fit for work(PPE/ID Badge)*"),
            QuestionWithOption("Vehicle readiness*"),
            QuestionWithOption("Device requirement (Android preferably)*")
        )
        pref = Prefs.getInstance(requireContext())
        pref.submittedPrepardness = false
        viewModel = (activity as HomeActivity).viewModel
        loadingDialog = (activity as HomeActivity).loadingDialog

        val adapter = QuestionAdapter(questions,requireContext())
        binding.prepRV.adapter = adapter
        binding.prepRV.layoutManager = LinearLayoutManager(requireContext())


        viewModel.liveDataQuestionairePreparedness.observe(viewLifecycleOwner){
            loadingDialog.cancel()
            if(it!=null){
                if(pref.submittedPrepardness){
                    Log.d("Preparedness",it.toString())
                    viewModel.currentViewPage.postValue(1)
                    pref.quesID = it.QuestionId
                    pref.qStage = 1
                }

            }
        }
        binding.prepCancel.setOnClickListener {
            findNavController().navigate(R.id.completeTaskFragment)
            findNavController().clearBackStack(R.id.completeTaskFragment)
        }
        binding.prepardnessSave.setOnClickListener {
            val allQuestionsSelected = adapter.areAllQuestionsSelected()
            val comment =
                if (binding.prepComment.text.isNullOrEmpty()) " " else binding.prepComment.text
            if (allQuestionsSelected) {
                val selectedOptions = questions.map { it.selectedOption }

                savePrepardnessApi(selectedOptions,comment)

            } else {
                showToast("Please select answer to all questions.", requireContext())
            }
        }

    }

    private fun savePrepardnessApi(selectedOptions: List<String>, comment: CharSequence?) {
        loadingDialog.show()
        pref.submittedPrepardness = true
        viewModel.SaveQuestionairePreparedness(SaveQuestionairePreparednessRequest(
            DaDailyWorkId = pref.daWID,
            LeadDriverId = pref.clebUserId.toInt(),
            QuestionId = 0,
            RideAlongDriverId = pref.currRideAlongID,
            RoutetId = pref.currRtId,
            RaPreparednessFitForWork = selectedOptions[0],
            RaPreparednessVehicleReadiness = selectedOptions[1],
            RaPreparednessDeviceReq =  selectedOptions[2]
        ))

    }

}
