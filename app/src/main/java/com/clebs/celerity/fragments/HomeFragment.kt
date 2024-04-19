package com.clebs.celerity.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentHomeBinding
import com.clebs.celerity.databinding.FragmentInvoicesBinding
import com.clebs.celerity.ui.App
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.Prefs
import com.elconfidencial.bubbleshowcase.BubbleShowCase
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import com.elconfidencial.bubbleshowcase.BubbleShowCaseListener
import com.ncorti.slidetoact.SlideToActView


class HomeFragment : Fragment() {
    lateinit var mbinding: FragmentHomeBinding
    private lateinit var viewModel: MainViewModel

    val showDialog: () -> Unit = {
        (activity as HomeActivity).showDialog()
    }
    val hideDialog: () -> Unit = {
        (activity as HomeActivity).hideDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (!this::mbinding.isInitialized) {
            mbinding = FragmentHomeBinding.inflate(inflater, container, false)
        }

        "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}".also { name ->
            mbinding.marianIonu.text = name
        }
        viewModel = (activity as HomeActivity).viewModel
        showDialog()
        viewModel.GetDriversBasicInformation(
            Prefs.getInstance(App.instance).userID.toInt().toDouble()
        ).observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                if (it.currentlocation != null) {
                    mbinding.location.text = it.currentlocation
                    mbinding.away.text = it.currentlocation
                } else if (it.workinglocation != null) {
                    mbinding.location.text = it.workinglocation
                    mbinding.away.text = it.workinglocation
                } else {
                    mbinding.location.setText("Not assigned")
                    mbinding.away.setText("Not assigned")
                }
                if (it.vmRegNo.isNullOrEmpty()) {
                    mbinding.striketruckImage.visibility = View.VISIBLE
                }
                if (it.userID.toString().isEmpty() || it.userID ==null||it.userID==0) {
                    mbinding.striketicketNumber.visibility = View.VISIBLE
                }
                mbinding.truckNumber.text = it.vmRegNo
                mbinding.ticketNumber.text = it.userID.toString()
            }
        }

        viewModel.vechileInformationLiveData.observe(viewLifecycleOwner) {
            hideDialog()
        }

        BubbleShowCaseBuilder(requireActivity()) //Activity instance
            .title("Swipe Right") //Any title for the bubble view
            .description("Swipe right to scan vehicle Registration number") //More detailed description
            .arrowPosition(BubbleShowCase.ArrowPosition.TOP)
            //You can force the position of the arrow to change the location of the bubble.
            .backgroundColor((requireContext().getColor(R.color.very_light_orange)))
            //Bubble background color
            .textColor(requireContext().getColor(R.color.text_color))
            //Bubble Text color
            .titleTextSize(14)
            .descriptionTextSize(10)
            .image(requireContext().resources.getDrawable(R.drawable.scanner))//Bubble main image
            .closeActionImage(requireContext().resources.getDrawable(R.drawable.cross)!!) //Custom close action image
            .showOnce("3")
            .listener(
                (object : BubbleShowCaseListener { //Listener for user actions
                    override fun onTargetClick(bubbleShowCase: BubbleShowCase) {
                        //Called when the user clicks the target
                        bubbleShowCase.dismiss()
                    }

                    override fun onCloseActionImageClick(bubbleShowCase: BubbleShowCase) {
                        //Called when the user clicks the close button
                        bubbleShowCase.dismiss()
                    }

                    override fun onBubbleClick(bubbleShowCase: BubbleShowCase) {
                        //Called when the user clicks on the bubble
                        bubbleShowCase.dismiss()
                    }

                    override fun onBackgroundDimClick(bubbleShowCase: BubbleShowCase) {
                        bubbleShowCase.dismiss()
                        //Called when the user clicks on the background dim
                    }
                })
            )
            .targetView(mbinding.fm)
            .highlightMode(BubbleShowCase.HighlightMode.VIEW_SURFACE) //View to point out
            .show()


        mbinding.slideact.bumpVibration = 50
        mbinding.slideact.onSlideToActAnimationEventListener =
            (object : SlideToActView.OnSlideToActAnimationEventListener {
                override fun onSlideCompleteAnimationEnded(view: SlideToActView) {
                    mbinding.arroww.visibility = View.GONE

                }

                override fun onSlideCompleteAnimationStarted(
                    view: SlideToActView,
                    threshold: Float
                ) {

                    mbinding.arroww.alpha = 0.0f
                    mbinding.arroww.visibility = View.GONE
                }

                override fun onSlideResetAnimationEnded(view: SlideToActView) {
                    mbinding.arroww.alpha = 0f
                }

                override fun onSlideResetAnimationStarted(view: SlideToActView) {
                    mbinding.arroww.visibility = View.VISIBLE

                }

            })
        mbinding.slideact.onSlideCompleteListener =
            (object : SlideToActView.OnSlideCompleteListener {
                override fun onSlideComplete(view: SlideToActView) {
                    mbinding.arroww.alpha = 0f
                    findNavController().navigate(R.id.dailyWorkFragment)
                }

            })
        mbinding.slideact.onSlideResetListener = (object : SlideToActView.OnSlideResetListener {
            override fun onSlideReset(view: SlideToActView) {
                mbinding.arroww.alpha = 1f
                mbinding.arroww.visibility = View.VISIBLE
            }
        })


        return mbinding.root
    }


}