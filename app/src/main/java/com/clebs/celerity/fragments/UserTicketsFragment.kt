package com.clebs.celerity.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.databinding.FragmentUserTicketsBinding
import com.clebs.celerity.ui.CreateTicketsActivity
import com.clebs.celerity.ui.LoginActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserTicketsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserTicketsFragment : Fragment() {
    lateinit var mbinding: FragmentUserTicketsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mbinding = FragmentUserTicketsBinding.inflate(inflater, container, false)

        mbinding.rlreltive.setOnClickListener {
            showAlert()
        }
        mbinding.addNew.setOnClickListener {
            val intent = Intent(requireContext(), CreateTicketsActivity::class.java)
            startActivity(intent)
        }

        return mbinding.root
    }

    fun showAlert() {
        val factory = LayoutInflater.from(requireActivity())
        val view: View = factory.inflate(R.layout.dialog_sort_filters, null)
        val deleteDialog: AlertDialog = AlertDialog.Builder(requireContext()).create()

        deleteDialog.setView(view)
        deleteDialog.setCanceledOnTouchOutside(true);
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        deleteDialog.show();

    }
}