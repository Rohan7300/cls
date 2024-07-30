package com.clebs.celerity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.QuestionAdapter
import com.clebs.celerity.databinding.FragmentReturnToStationBinding
import com.clebs.celerity.models.QuestionWithOption
import com.clebs.celerity.models.requests.SaveQuestionaireReturnToDeliveryStationRequest
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.showToast

class ReturnToStation : Fragment() {
    private lateinit var binding: FragmentReturnToStationBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var pref: Prefs
    private lateinit var loadingDialog: LoadingDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setRetainInstance(true);
        binding = FragmentReturnToStationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val questions = arrayListOf(
            QuestionWithOption("Unload bags and parcels *"),
            QuestionWithOption("Hand returned packages to AMZL *")
        )


        pref = Prefs.getInstance(requireContext())
        viewModel = (activity as HomeActivity).viewModel
        loadingDialog = (activity as HomeActivity).loadingDialog
        pref.submittedReturnToStation = false
        val adapter = QuestionAdapter(questions,requireContext())
        binding.ReturnRV.adapter = adapter
        binding.ReturnRV.layoutManager = LinearLayoutManager(requireContext())

        viewModel.liveDataQuestionareReturn.observe(viewLifecycleOwner){
            loadingDialog.cancel()
            if(it!=null){
                if(pref.submittedReturnToStation){
                    viewModel.currentViewPage.postValue(3)
                    pref.quesID = it.QuestionId
                    pref.qStage = 3
                }
            }
        }

        binding.cancelBtn.setOnClickListener {
            findNavController().navigate(R.id.newCompleteTaskFragment)
            findNavController().clearBackStack(R.id.newCompleteTaskFragment)
        }

        binding.returnSaveBtn.setOnClickListener {
            if(pref.qStage<4||pref.quesID==0){
                showToast("Please complete previous assessment first", requireContext())
            }else{
                val allQuestionsSelected = adapter.areAllQuestionsSelected()
                val comment =
                    if (binding.etReturnComment.text.isNullOrEmpty()) " " else binding.etReturnComment.text
                if (allQuestionsSelected&&!binding.etReturnComment.text.isNullOrEmpty()) {
                    val selectedOptions = questions.map { it.selectedOption }
                    saveReturnQuesApi(selectedOptions, comment)

                } else {
                    if(binding.etReturnComment.text.isNullOrEmpty())
                    showToast("Please add comment before submitting", requireContext())
                    else
                    showToast("Please select answer to all questions.", requireContext())
                }
            }
        }

    }

    private fun saveReturnQuesApi(selectedOptions: List<String>, comment: CharSequence?) {
        loadingDialog.show()
        pref.submittedReturnToStation = true
        viewModel.SaveQuestionaireReturnToDeliveryStation(
            SaveQuestionaireReturnToDeliveryStationRequest(
                QuestionId = pref.quesID,
                RaRetToDeliveryStattionOnloadBags = selectedOptions[0],
                RaRetToDeliveryStattionHandPackedToAmzl = selectedOptions[1],
                RaRetToDeliveryStattionComments = comment.toString()
            )
        )
    }

}