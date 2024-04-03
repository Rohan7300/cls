package com.clebs.celerity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R

/**
 * A simple [Fragment] subclass.
 * Use the [InvoicesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InvoicesFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View= inflater.inflate(R.layout.fragment_invoices, container, false)
        val clsinvoices=view.findViewById<TextView>(R.id.clsinvoices)
        val thirdparty=view.findViewById<TextView>(R.id.otherinvoices)
        clsinvoices.setOnClickListener {
            findNavController().navigate(R.id.CLSInvoicesFragment)
        }
        thirdparty.setOnClickListener {
            findNavController().navigate(R.id.CLSThirdPartyFragment)
        }
        return view
    }


}