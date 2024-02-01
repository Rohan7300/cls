package com.clebs.celerity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.clebs.celerity.R
import com.clebs.celerity.databinding.FragmentVechileMileageBinding
import com.clebs.celerity.databinding.FragmentWindScreenBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WindScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WindScreenFragment : Fragment() {

    lateinit var mbinding: FragmentWindScreenBinding
    private var isupload: Boolean = true
    private var isuploadtwo: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::mbinding.isInitialized) {
            mbinding = FragmentWindScreenBinding.inflate(inflater, container, false)
        }




        mbinding.edtMilsLayout.setOnClickListener {
            if (isupload) {

                mbinding.edtMil.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.shape_filled_textview
                    )
                )
                mbinding.imageRadio.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.round_circle_orange
                    )
                )

                mbinding.rlUploadDefect.visibility = View.VISIBLE
            } else {

                mbinding.edtMil.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.shape_textview_radio_button
                    )
                )
                mbinding.imageRadio.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.round_circle_white
                    )
                )

                mbinding.rlUploadDefect.visibility = View.GONE
            }
            isupload = !isupload

        }
        mbinding.edtMilsLayoutTwo.setOnClickListener {
            if (isuploadtwo) {

                mbinding.edtMilTwo.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.shape_filled_textview
                    )
                )
                mbinding.imageRadioTwo.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.round_circle_orange
                    )
                )

                mbinding.rlUploadDefectTwo.visibility = View.VISIBLE
            } else {

                mbinding.edtMilTwo.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.shape_textview_button
                    )
                )
                mbinding.imageRadioTwo.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.round_circle_white
                    )
                )

                mbinding.rlUploadDefectTwo.visibility = View.GONE
            }
            isuploadtwo = !isuploadtwo

        }
        mbinding.run {
            edtDefect.doAfterTextChanged {

                mbinding.tvNext.isEnabled  = (edtDefect.text?.length!! > 0)

                if (tvNext.isEnabled){
                    tvNext.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                }
                else{
                    tvNext.setTextColor(ContextCompat.getColor(requireContext(),R.color.orange))
                }

            }
            edtDefectTwo.doAfterTextChanged {

                mbinding.tvNext.isEnabled  = (edtDefectTwo.text?.length!! > 0)

                if (tvNext.isEnabled){
                    tvNext.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                }
                else{
                    tvNext.setTextColor(ContextCompat.getColor(requireContext(),R.color.orange))
                }

            }

        }
        return mbinding.root
    }


}