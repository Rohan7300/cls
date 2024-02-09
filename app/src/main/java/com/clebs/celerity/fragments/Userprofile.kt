package com.clebs.celerity.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.databinding.FragmentDailyWorkBinding
import com.clebs.celerity.databinding.FragmentHomeBinding
import com.clebs.celerity.databinding.FragmentUserprofileBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Userprofile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Userprofile : Fragment() {
    lateinit var mbinding: FragmentUserprofileBinding
    private var isedit: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        if (!this::mbinding.isInitialized) {
            mbinding = FragmentUserprofileBinding.inflate(inflater, container, false)
        }
        mbinding.editImg.setOnClickListener {
            if (isedit) {
                mbinding.save.visibility = View.VISIBLE
                mbinding.emailtext.isEnabled = true
                mbinding.emailtext.isFocusable = true
                mbinding.emailtext.isFocusableInTouchMode = true


                mbinding.usertext.isEnabled = true
                mbinding.usertext.isFocusable = true
                mbinding.usertext.isFocusableInTouchMode = true

                mbinding.passtext.isEnabled = true
                mbinding.passtext.isFocusable = true
                mbinding.passtext.isFocusableInTouchMode = true

                mbinding.phonetext.isEnabled = true
                mbinding.phonetext.isFocusable = true
                mbinding.phonetext.isFocusableInTouchMode = true

                mbinding.addresstext.isEnabled = true
                mbinding.addresstext.isFocusable = true
                mbinding.addresstext.isFocusableInTouchMode = true

                mbinding.editImg.alpha = 0.5f

                mbinding.icInfos.visibility = View.VISIBLE
                mbinding.useEmailas.visibility = View.VISIBLE
            } else {
                mbinding.save.visibility = View.GONE
                mbinding.emailtext.isEnabled = false
                mbinding.emailtext.isFocusable = false
                mbinding.emailtext.isFocusableInTouchMode = false


                mbinding.usertext.isEnabled = false
                mbinding.usertext.isFocusable = false
                mbinding.usertext.isFocusableInTouchMode = false

                mbinding.passtext.isEnabled = false
                mbinding.passtext.isFocusable = false
                mbinding.passtext.isFocusableInTouchMode = false

                mbinding.phonetext.isEnabled = false
                mbinding.phonetext.isFocusable = false
                mbinding.phonetext.isFocusableInTouchMode = false

                mbinding.addresstext.isEnabled = false
                mbinding.addresstext.isFocusable = false
                mbinding.addresstext.isFocusableInTouchMode = false

                mbinding.editImg.alpha = 1f
                mbinding.icInfos.visibility = View.GONE
                mbinding.useEmailas.visibility = View.GONE
            }
            isedit = !isedit

        }
        mbinding.useEmailas.setOnClickListener {
            showAlert()
        }
        return mbinding.root
    }

    fun showAlert() {
        val factory = LayoutInflater.from(requireActivity())
        val view: View = factory.inflate(R.layout.profile_popup, null)
        val deleteDialog: AlertDialog = AlertDialog.Builder(requireContext()).create()
        val textview_yes: TextView = view.findViewById<TextView>(R.id.tv_next_yes)
        val textview_no: TextView = view.findViewById<TextView>(R.id.tv_next)
        deleteDialog.setView(view)
        textview_yes.setOnClickListener {

            deleteDialog.dismiss()
        }
        textview_no.setOnClickListener {

            deleteDialog.dismiss()
        }

        deleteDialog.setCancelable(false)
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        deleteDialog.show();

    }

}