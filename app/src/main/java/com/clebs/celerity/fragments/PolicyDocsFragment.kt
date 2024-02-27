package com.clebs.celerity.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.databinding.FragmentPolicyDocsBinding
import com.clebs.celerity.ui.App
import com.clebs.celerity.utils.Prefs
//import nsmarinro.librarysignature.SignatureView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PolicyDocsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PolicyDocsFragment : Fragment() {

    lateinit var mbinding: FragmentPolicyDocsBinding
    var checkboxchecked = Int
    var checkboxchecked2 = Int
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mbinding = FragmentPolicyDocsBinding.inflate(inflater, container, false)

        if (Prefs.getInstance(App.instance).getBoolean("IsamazonSign", false).equals(false)) {
            mbinding.llAmazon.visibility = View.GONE
        } else {
            mbinding.llAmazon.visibility = View.VISIBLE
        }

        if (Prefs.getInstance(App.instance).getBoolean("isother", false).equals(false)) {
            mbinding.llTrucks.visibility = View.GONE
        } else {
            mbinding.llAmazon.visibility = View.VISIBLE
        }

        mbinding.checkbox.addOnCheckedStateChangedListener { checkBox, state ->
            if (checkBox.isChecked) {


                if (mbinding.llTrucks.visibility == View.GONE) {
//                    showAlert()

                } else {
                    if (!mbinding.checkbox2.isChecked) {
                        Toast.makeText(
                            requireContext(),
                            "Please check the trucks agreement to proceed",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
//                        showAlert()
                    }
                }

            } else {

            }


        }
        mbinding.checkbox2.addOnCheckedStateChangedListener { checkBox, state ->

            if (checkBox.isChecked) {
                if (mbinding.llAmazon.visibility == View.GONE) {
//                    showAlert()

                } else {
                    if (!mbinding.checkbox.isChecked) {
                        Toast.makeText(
                            requireContext(),
                            "Please check the amazon agreement to proceed",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
//                        showAlert()
                    }

                }

            } else {

            }
        }


        return mbinding.root

    }

//    fun showAlert() {
//        val factory = LayoutInflater.from(requireActivity())
//        val view: View = factory.inflate(R.layout.dialog_signature, null)
//        val deleteDialog: AlertDialog = AlertDialog.Builder(requireContext()).create()
//        val SignatureView = view.findViewById<SignatureView>(R.id.signatureView)
//        val LinearLayout = view.findViewById<LinearLayout>(R.id.llclear)
//        val sign = view.findViewById<TextView>(R.id.bt_sign)
//        sign.setOnClickListener {
//            findNavController().popBackStack()
//            findNavController().navigate(R.id.homedemoFragment)
//            deleteDialog.dismiss()
//        }
//        LinearLayout.setOnClickListener {
//            SignatureView.signatureClear()
//        }
//
//        deleteDialog.setView(view)
//        deleteDialog.setCanceledOnTouchOutside(false);
//        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
//        deleteDialog.show();
//
//    }

}