package com.clebs.celerity.fragments

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.FeedbackQuestionareAdapter
import com.clebs.celerity.databinding.FragmentFeedbackBinding
import com.clebs.celerity.models.QuestionWithOption
import com.clebs.celerity.models.requests.SubmitRideAlongDriverFeedbackRequest
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.CustDialog2
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.SignatureListener
import com.clebs.celerity.utils.bitmapToBase64
import com.clebs.celerity.utils.showToast


class FeedbackFragment : Fragment() {

    lateinit var binding: FragmentFeedbackBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var pref: Prefs
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feedback, container, false)


        val questions = arrayListOf(
            QuestionWithOption("Does the new DA know where to fillup the oil/water/adblue/screenwash?*"),
            QuestionWithOption("Does the new DA know where the tool and spare wheel is?*"),
            QuestionWithOption("Does the new DA hae experience driving any type of van?*"),
            QuestionWithOption("How confident do you feel the new DA driving the van? *"),
            QuestionWithOption("Reversing?*"),
            QuestionWithOption("Parking? *"),
            QuestionWithOption("Use of mirrors? *"),
            QuestionWithOption("Spatial awareness? *"),
            QuestionWithOption("Do you feel safe with the new QA in control of the van?*"),
            QuestionWithOption("Can they perform a few safety checks or identify different parts of the vehicle.?*"),
            QuestionWithOption("Are they able to change gear,brake and steer, and park safely?*"),
            QuestionWithOption("Did they observe local traffic regulations?*"),
            QuestionWithOption("Did they react safely and appropriately to road hazards?"),
            QuestionWithOption("Do you feel they could adapt to unexpected circumstances and conditions?"),
        )
        pref = Prefs.getInstance(requireContext())
        viewModel = (activity as HomeActivity).viewModel
        loadingDialog = (activity as HomeActivity).loadingDialog
        pref.submittedFeedback = false


        val adapter = FeedbackQuestionareAdapter(questions, requireContext())
        binding.feedbackQuestionare.adapter = adapter
        binding.feedbackQuestionare.layoutManager = LinearLayoutManager(requireContext())

        viewModel.liveDataSubmitRideAlongDriverFeedbackRequest.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            if (pref.submittedFeedback) {
                if (it != null) {
                    findNavController().popBackStack()
                    findNavController().navigate(R.id.completeTaskFragment)
                }
            }
        }


        val dialog = CustDialog2()
        dialog.setSignatureListener(object : SignatureListener {
            override fun onSignatureSaved(bitmap: Bitmap) {
                val selectedOptions = questions.map { it.selectedOption }
                val bse64 = "data:image/png;base64," + bitmapToBase64(bitmap)
                saveFeedbackQuestions(selectedOptions, bse64)
            }
        })
        dialog.isCancelable = true




        binding.feedbackAddSignature.setOnClickListener {


            val areAllQuestionsSelected = adapter.areAllQuestionsSelected()
            if (areAllQuestionsSelected) {
                dialog.show((activity as HomeActivity).supportFragmentManager, "sign")

            } else {
                showToast("Please complete questionnaire first!!", requireContext())
            }
        }


        return binding.root
    }

    private fun saveFeedbackQuestions(selectedOptions: List<String>, bse64: String) {
        pref.submittedFeedback = true
        var request = SubmitRideAlongDriverFeedbackRequest(
            DaDailyWorkId = pref.daWID,
            LeadDriverId = pref.clebUserId.toInt(),
            QuestionId = 0,
            RoutetId = pref.currRtId,
            Signature = bse64,
            RideAlongDriverId = pref.currRideAlongID,
            RaDriverFillDaWater = selectedOptions[0],
            RaDriverToolSpareWheel = selectedOptions[1],
            RaDriverDrivingAnyVan = selectedOptions[2],
            RaDriverConfident = selectedOptions[3],
            RaDriverReversing = selectedOptions[4],
            RaDriverParking = selectedOptions[5],
            RaDriverUseOfMirrors = selectedOptions[6],
            RaDriverSpatialAwareness = selectedOptions[7],
            RaDriverFeelSafe = selectedOptions[8],
            RaDriverIdentifyDiffVehParts = selectedOptions[9],
            RaDriverChangeGear = selectedOptions[10],
            RaDriverObserveLocalTrafficRegulations = selectedOptions[11],
            RaDriverReactRoadHazard = selectedOptions[12],
            RaDriverAdoptCircumstances = selectedOptions[13]
        )
        loadingDialog.show()
        viewModel.SubmitRideAlongDriverFeedback(request)
    }
}