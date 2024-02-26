package com.clebs.celerity.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.databinding.FragmentHomeBinding
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


        mbinding.slideact.bumpVibration = 50
        mbinding.slideact.onSlideToActAnimationEventListener =
            (object : SlideToActView.OnSlideToActAnimationEventListener {
                override fun onSlideCompleteAnimationEnded(view: SlideToActView) {
                    mbinding.arroww.visibility = View.VISIBLE
                }

                override fun onSlideCompleteAnimationStarted(
                    view: SlideToActView,
                    threshold: Float
                ) {
                    mbinding.arroww.visibility = View.GONE
                }

                override fun onSlideResetAnimationEnded(view: SlideToActView) {
                    mbinding.arroww.visibility = View.VISIBLE
                }

                override fun onSlideResetAnimationStarted(view: SlideToActView) {
                    mbinding.arroww.visibility = View.VISIBLE
                }

            })
        mbinding.slideact.onSlideCompleteListener =
            (object : SlideToActView.OnSlideCompleteListener {
                override fun onSlideComplete(view: SlideToActView) {
                    mbinding.arroww.visibility = View.GONE
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