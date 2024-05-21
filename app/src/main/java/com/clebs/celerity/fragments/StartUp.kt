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
import com.clebs.celerity.databinding.FragmentStartUpBinding
import com.clebs.celerity.models.QuestionWithOption
import com.clebs.celerity.models.requests.SaveQuestionaireStartupRequest
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.showToast


class StartUp : Fragment() {
    private lateinit var binding: FragmentStartUpBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var pref: Prefs
    lateinit var loadingDialog: LoadingDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setRetainInstance(true);
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
        pref = Prefs.getInstance(requireContext())
        viewModel = (activity as HomeActivity).viewModel
        loadingDialog = (activity as HomeActivity).loadingDialog
        pref.submittedStartUp = false

        val adapter = QuestionAdapter(questions,requireContext())
        binding.startUpRv.adapter = adapter
        binding.startUpRv.layoutManager = LinearLayoutManager(requireContext())

        viewModel.liveDataQuestionaireStartup.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            if(pref.submittedStartUp){
                if (it != null) {
                    viewModel.currentViewPage.postValue(2)
                    pref.quesID = it.QuestionId
                    pref.qStage = 2
                }
            }
        }
        binding.cancel.setOnClickListener {
            findNavController().navigate(R.id.completeTaskFragment)
            findNavController().clearBackStack(R.id.completeTaskFragment)
        }

        binding.saveBTNStartup.setOnClickListener {
            if(pref.qStage<1||pref.quesID==0){
                showToast("Please complete previous assessment first", requireContext())
            }else{
                val allQuestionsSelected = adapter.areAllQuestionsSelected()
                val comment =
                    if (binding.startupComment.text.isNullOrEmpty()) " " else binding.startupComment.text
                if (allQuestionsSelected&&!binding.startupComment.text.isNullOrEmpty()) {
                    val selectedOptions = questions.map { it.selectedOption }
                    if (comment != null) {
                        saveStartupApi(selectedOptions, comment)
                    }
                } else {
                    showToast("Please select answer to all questions.", requireContext())
                }
            }
        }
    }

    private fun saveStartupApi(selectedOptions: List<String>, comment: CharSequence) {
        loadingDialog.show()
        pref.submittedStartUp = true
        viewModel.SaveQuestionaireStartup(
            SaveQuestionaireStartupRequest(
                QuestionId = pref.quesID,
                RaStartupClsDaSystem = selectedOptions[0],
                RaStartupComments = comment.toString(),
                RaStartupEmentor = selectedOptions[1],
                RaStartupDvic = selectedOptions[2],
                RaStartupUseOfTrollies = selectedOptions[3],
                RaStartupYardSafty = selectedOptions[4],
                RaStartupLoadingVehicle = selectedOptions[5]
            )
        )
    }


}