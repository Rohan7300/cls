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
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.TicketAdapter
import com.clebs.celerity.databinding.FragmentUserTicketsBinding
import com.clebs.celerity.models.response.Doc
import com.clebs.celerity.models.response.GetUserTicketsResponse
import com.clebs.celerity.ui.CreateTicketsActivity
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.ui.LoginActivity
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.Prefs


class UserTicketsFragment : Fragment() {
    lateinit var mbinding: FragmentUserTicketsBinding
    lateinit var viewModel: MainViewModel
    lateinit var prefs: Prefs
    lateinit var homeActivity:HomeActivity
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mbinding = FragmentUserTicketsBinding.inflate(inflater, container, false)
        homeActivity = (activity as HomeActivity)
        viewModel = homeActivity.viewModel
        prefs = Prefs.getInstance(requireContext())
        loadingDialog = homeActivity.loadingDialog

        homeActivity.showDialog()
        viewModel.GetUserTickets(prefs.userID.toInt())


        observers()
        mbinding.rlreltive.setOnClickListener {
            showAlert()
        }

        mbinding.addNew.setOnClickListener {
            val intent = Intent(requireContext(), CreateTicketsActivity::class.java)
            startActivity(intent)
        }

        return mbinding.root
    }

    private fun observers() {
        val ticketAdapter = TicketAdapter(GetUserTicketsResponse(ArrayList()))
        mbinding.rvTickets.adapter = ticketAdapter
        mbinding.rvTickets.layoutManager = LinearLayoutManager(requireContext())
        viewModel.liveDataGetUserTickets.observe(viewLifecycleOwner){
            homeActivity.hideDialog()
            if(it!=null){
                ticketAdapter.ticketList.Docs.clear()
                ticketAdapter.ticketList.Docs.addAll(it.Docs)
                ticketAdapter.notifyDataSetChanged()
            }
        }
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