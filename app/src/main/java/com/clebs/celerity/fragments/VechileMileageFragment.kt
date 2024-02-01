package com.clebs.celerity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.databinding.FragmentDailyWorkBinding
import com.clebs.celerity.databinding.FragmentVechileMileageBinding
import com.clebs.celerity.ui.App
import com.clebs.celerity.utils.Prefs

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VechileMileageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VechileMileageFragment : Fragment() {
    lateinit var mbinding: FragmentVechileMileageBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::mbinding.isInitialized) {
            mbinding = FragmentVechileMileageBinding.inflate(inflater, container, false)
        }
        mbinding.run {
            edtMil.doAfterTextChanged {
                mbinding.miles.setText(
                    Prefs.getInstance(App.instance).getSaveStrings("vehicleLastMillage").toString()
                )
                mbinding.tvNext.isEnabled = (edtMil.text?.length!! > 0)
                if (tvNext.isEnabled) {
                    tvNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                } else {
                    tvNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                }

            }

        }
        mbinding.tvNext.setOnClickListener {
            findNavController().navigate(R.id.windScreenFragment)
        }
        return mbinding.root
    }


}