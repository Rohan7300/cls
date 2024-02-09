package com.clebs.celerity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.databinding.FragmentHomeBinding
import com.clebs.celerity.interfaces.BottomNavigationProvider
import com.clebs.celerity.ui.HomeActivity
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

        mbinding.arroww.alpha = 1f
        mbinding.slideact.bumpVibration = 50
        mbinding.slideact.onSlideToActAnimationEventListener =
            (object : SlideToActView.OnSlideToActAnimationEventListener {
                override fun onSlideCompleteAnimationEnded(view: SlideToActView) {
                    mbinding.arroww.alpha = 0.0f
                }

                override fun onSlideCompleteAnimationStarted(
                    view: SlideToActView,
                    threshold: Float
                ) {
                    mbinding.arroww.alpha = 0.1f
                }

                override fun onSlideResetAnimationEnded(view: SlideToActView) {
                    mbinding.arroww.alpha = 1f
                }

                override fun onSlideResetAnimationStarted(view: SlideToActView) {

                }

            })
        mbinding.slideact.onSlideCompleteListener =
            (object : SlideToActView.OnSlideCompleteListener {
                override fun onSlideComplete(view: SlideToActView) {
                    findNavController().navigate(R.id.dailyWorkFragment)


                }

            })
        mbinding.slideact.onSlideResetListener = (object : SlideToActView.OnSlideResetListener {
            override fun onSlideReset(view: SlideToActView) {
                mbinding.arroww.visibility = View.VISIBLE
            }
        })


        return mbinding.root
    }


}