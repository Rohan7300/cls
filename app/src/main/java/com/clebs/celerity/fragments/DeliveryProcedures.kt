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
import com.clebs.celerity.utils.LoadingDialog
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



        val adapter = QuestionAdapter(questions,requireContext())
        binding.DeliveryRV.adapter = adapter
        binding.DeliveryRV.layoutManager = LinearLayoutManager(requireContext())

        binding.cancel.setOnClickListener {
            findNavController().navigate(R.id.completeTaskFragment)
            findNavController().clearBackStack(R.id.completeTaskFragment)
        }

        viewModel.liveDataQuestionareDeliveryProcedures.observe(viewLifecycleOwner){
            loadingDialog.cancel()
            if(it!=null){
                if(pref.submittedDeliveryProcedures){
                    viewModel.currentViewPage.postValue(4)
                    pref.quesID = it.QuestionId
                    pref.qStage = 4
                }
            }else{
                showToast("Failed to submit!!",requireContext())
            }
        }

        binding.deliverSaveBtn.setOnClickListener {
            if(pref.qStage<3||pref.quesID==0){
                showToast("Please complete previous assessment first", requireContext())
            }else{
                val allQuestionsSelected = adapter.areAllQuestionsSelected()
                val comment =
                    if (binding.etDeliveryComment.text.isNullOrEmpty()) "" else binding.etDeliveryComment.text
                if (allQuestionsSelected) {
                    val selectedOptions = questions.map { it.selectedOption }
                    saveDeliveryProcedureApi(selectedOptions, comment)
                } else {
                    showToast("Please select answer to all questions.", requireContext())
                }
            }
        }
    }

    private fun saveDeliveryProcedureApi(selectedOptions: List<String>, comment: CharSequence?) {
        loadingDialog.show()
        pref.submittedDeliveryProcedures = true
        viewModel.SaveQuestionaireDelivery(
            SaveQuestionaireDeliverProceduresRequest(
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
                RaDeliveryProceduresFrontDeskMailRoom = selectedOptions[9],
                RaDeliveryProceduresLockerDeleveries = selectedOptions[10],
                RaDeliveryProceduresContractCompliance = selectedOptions[11],
                RaDeliveryProceduresComments = comment.toString()
            )
        )
    }

}