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
import com.clebs.celerity.databinding.FragmentDeliveryProceduresBinding
import com.clebs.celerity.models.QuestionWithOption
import com.clebs.celerity.models.requests.SaveQuestionaireDeliverProceduresRequest
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.models.requests.SaveQuestionareDrivingabilityassessment
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.showToast

class DeliveryProcedures : Fragment() {
    private lateinit var binding: FragmentDeliveryProceduresBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var pref: Prefs
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setRetainInstance(true);
        binding = FragmentDeliveryProceduresBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = Prefs.getInstance(requireContext())
        pref.submittedDeliveryProcedures = false
        viewModel = (activity as HomeActivity).viewModel
        loadingDialog = (activity as HomeActivity).loadingDialog

        val questions = arrayListOf(
            QuestionWithOption("Speed awareness *"),
            QuestionWithOption("Mirrors checks *"),
            QuestionWithOption("Indicating in time *"),
            QuestionWithOption("Distance between other vehicles *"),
            QuestionWithOption("Parking correctly *"),
            QuestionWithOption("Courtesy to other road users *"),
            QuestionWithOption("Stop signs and road junction adherence *"),
            QuestionWithOption("Instructed on No reversing policy *"),
            QuestionWithOption("Vehicle security – Keys left in ignition *"),
            /*QuestionWithOption("Front Desk, Mail room *"),
            QuestionWithOption("Locker Deliveries/Collections *"),
            QuestionWithOption("Contact Compliance *")*/
        )


        val adapter = QuestionAdapter(questions, requireContext())
        binding.DeliveryRV.adapter = adapter
        binding.DeliveryRV.layoutManager = LinearLayoutManager(requireContext())

        binding.cancel.setOnClickListener {
            findNavController().navigate(R.id.newCompleteTaskFragment)
            findNavController().clearBackStack(R.id.newCompleteTaskFragment)
        }

        viewModel.liveDataQuestionareDeliveryProcedures.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            if (it != null) {
                if (pref.submittedDeliveryProcedures) {
                    viewModel.currentViewPage.postValue(2)
                    pref.quesID = it.QuestionId
                    pref.qStage = 2
                }
            } else {
                showToast("Failed to submit!!", requireContext())
            }
        }

        binding.deliverSaveBtn.setOnClickListener {
            if (pref.qStage < 1 || pref.quesID == 0) {
                showToast("Please complete previous assessment first", requireContext())
            } else {
                val allQuestionsSelected = adapter.areAllQuestionsSelected()
                val comment =
                    if (binding.etDeliveryComment.text.isNullOrEmpty()) "" else binding.etDeliveryComment.text
                if (allQuestionsSelected && !binding.etDeliveryComment.text.isNullOrEmpty()) {
                    val selectedOptions = questions.map { it.selectedOption }
                    saveDeliveryProcedureApi(selectedOptions, comment)
                } else {
                    if (binding.etDeliveryComment.text.isNullOrEmpty())
                        showToast("Please add comment before submitting.", requireContext())
                    else
                        showToast("Please select answer to all questions.", requireContext())
                }
            }
        }
    }

    private fun saveDeliveryProcedureApi(selectedOptions: List<String>, comment: CharSequence?) {
        loadingDialog.show()
        pref.submittedDeliveryProcedures = true
        viewModel.SaveQuestionaireDelivery(
            SaveQuestionareDrivingabilityassessment(
                QuestionId = pref.quesID,
                RaDeliveryProceduresAgeVerificationDelivery = selectedOptions[0],
                RaDeliveryProceduresHandleWithCare = selectedOptions[1],
                RaDeliveryProceduresGeocodes = selectedOptions[2],
                RaDeliveryProceduresVerifyAddress = selectedOptions[3],
                RaDeliveryProceduresPersonNamedOnShippingLabel = selectedOptions[4],
                RaDeliveryProceduresPod = selectedOptions[5],
                RaDeliveryProceduresLetterboxDelivery = selectedOptions[6],
                RaDeliveryProceduresPhr = selectedOptions[7],
                RaDeliveryProceduresDeliveredToNeighbour = selectedOptions[8],
                //RaDeliveryProceduresFrontDeskMailRoom = selectedOptions[9],
                //RaDeliveryProceduresLockerDeleveries = selectedOptions[10],
                //RaDeliveryProceduresContractCompliance = selectedOptions[11],
                RaDeliveryProceduresComments = comment.toString()
            )
        )
    }

}