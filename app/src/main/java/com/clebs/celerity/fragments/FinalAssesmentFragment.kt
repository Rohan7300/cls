package com.clebs.celerity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentDeliveryProceduresBinding
import com.clebs.celerity.databinding.FragmentFinalAssesmentBinding
import com.clebs.celerity.models.requests.SubmitFinalQuestionairebyLeadDriverRequest
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.showToast


class FinalAssesmentFragment : Fragment() {
    private lateinit var binding: FragmentFinalAssesmentBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var pref: Prefs
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFinalAssesmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = Prefs.getInstance(requireContext())
        viewModel = (activity as HomeActivity).viewModel
        loadingDialog = (activity as HomeActivity).loadingDialog

        viewModel.liveDataFinalAssesment.observe(viewLifecycleOwner){
            if(it!=null){
                findNavController().navigate(R.id.completeTaskFragment)
            }
        }

        binding.finalAssesmentSubmit.setOnClickListener {
            val assesment =
                if (binding.etFinalAssesmentComment.text.isNullOrEmpty()) "" else binding.etFinalAssesmentComment.text

            if(assesment.isNotEmpty()){
                viewModel.SaveQuestionaireFinalAssesment(SubmitFinalQuestionairebyLeadDriverRequest(
                    QuestionId = pref.quesID,
                    DaDailyWorkId = pref.daWID,
                    LeadDriverId = pref.userID.toInt(),
                    RoutetId = pref.currRtId,
                    Assessment = assesment.toString()
                ))
            }else{
                showToast("Please enter the assesment",requireContext())
            }

        }


    }

}