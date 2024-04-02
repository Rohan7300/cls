package com.clebs.celerity.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.databinding.FragmentHomeBinding
import com.elconfidencial.bubbleshowcase.BubbleShowCase
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import com.elconfidencial.bubbleshowcase.BubbleShowCaseListener
import com.ncorti.slidetoact.SlideToActView


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    lateinit var mbinding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (!this::mbinding.isInitialized) {
            mbinding = FragmentHomeBinding.inflate(inflater, container, false)
        }
        BubbleShowCaseBuilder(requireActivity()) //Activity instance
            .title("Swipe") //Any title for the bubble view
            .description("Swipe right to scan vehicle Registration number") //More detailed description
            .arrowPosition(BubbleShowCase.ArrowPosition.TOP)
            //You can force the position of the arrow to change the location of the bubble.
            .backgroundColor((requireContext().getColor(R.color.very_light_orange)))
            //Bubble background color
            .textColor(requireContext().getColor(R.color.black)) //Bubble Text color
            .titleTextSize(16) //Title text size in SP (default value 16sp)
            .descriptionTextSize(12) //Subtitle text size in SP (default value 14sp)
            .image(requireContext().resources.getDrawable(R.drawable.baseline_document_scanner_24)!!) //Bubble main image
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
            .targetView(mbinding.fm).highlightMode(BubbleShowCase.HighlightMode.VIEW_SURFACE) //View to point out
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