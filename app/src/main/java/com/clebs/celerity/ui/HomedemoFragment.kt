package com.clebs.celerity.ui

import android.os.Bundle
import android.transition.Fade
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.clebs.celerity.R
import com.clebs.celerity.databinding.FragmentDailyWorkBinding
import com.clebs.celerity.databinding.FragmentHomedemoBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomedemoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomedemoFragment : Fragment() {
  lateinit var mbinding:FragmentHomedemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::mbinding.isInitialized) {
            mbinding = FragmentHomedemoBinding.inflate(inflater, container, false)
            val rotateAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.anam)
            mbinding.imgCircleLogo.startAnimation(rotateAnimation)
//            mbinding.imgCircleLogo.animate()
//                .rotationBy(360f)
//                .setDuration(2000)
//
//                .setInterpolator(AccelerateInterpolator())
//                .setListener(null)


            mbinding.icLoh.animate()
                .rotationBy(360f)
                .setDuration(2000)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setListener(null)

        }
        return mbinding.root

    }


}