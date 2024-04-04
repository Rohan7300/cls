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
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.TicketAdapter
import com.clebs.celerity.databinding.DialogSortFiltersBinding
import com.clebs.celerity.databinding.FragmentUserTicketsBinding
import com.clebs.celerity.models.response.GetUserTicketsResponse
import com.clebs.celerity.ui.CreateTicketsActivity
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.convertDateFormat
import com.clebs.celerity.utils.showDatePickerDialog


class UserTicketsFragment : Fragment() {
    lateinit var mbinding: FragmentUserTicketsBinding
    lateinit var viewModel: MainViewModel
    lateinit var prefs: Prefs
    lateinit var homeActivity: HomeActivity
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
        val ticketAdapter = TicketAdapter(GetUserTicketsResponse(ArrayList()), requireContext())
        mbinding.rvTickets.adapter = ticketAdapter
        mbinding.rvTickets.layoutManager = LinearLayoutManager(requireContext())
        viewModel.liveDataGetUserTickets.observe(viewLifecycleOwner) {
            homeActivity.hideDialog()
            if (it != null) {
                if (it.Docs.size > 0) {
                    mbinding.noticketLayout.visibility = View.GONE
                    mbinding.rvTickets.visibility = View.VISIBLE
                } else {
                    mbinding.noticketLayout.visibility = View.VISIBLE
                    mbinding.rvTickets.visibility = View.GONE
                }
                ticketAdapter.ticketList.Docs.clear()
                val reversedList = it.Docs.reversed()
                ticketAdapter.ticketList.Docs.addAll(reversedList)
                ticketAdapter.notifyDataSetChanged()
            } else {
                mbinding.noticketLayout.visibility = View.VISIBLE
                mbinding.rvTickets.visibility = View.GONE
            }
        }
    }

    fun showAlert() {
        val factory = LayoutInflater.from(requireActivity())
        //val view: View = factory.inflate(R.layout.dialog_sort_filters, null)
        val deleteDialog: AlertDialog = AlertDialog.Builder(requireContext()).create()

        val deleteDailogBinding =
            DialogSortFiltersBinding.inflate(LayoutInflater.from(requireContext()))
        deleteDailogBinding.tvNext.isClickable = false
        deleteDailogBinding.icCrossOrange.setOnClickListener {
            deleteDialog.cancel()
        }

        deleteDailogBinding.edtBreakstart.setOnClickListener {
            showDatePickerDialog(
                requireContext(),
                deleteDailogBinding.edtBreakstart,
                deleteDailogBinding.edtBreakend,
                deleteDailogBinding.tvNext, 0
            )
        }

        deleteDailogBinding.edtBreakend.setOnClickListener {
            showDatePickerDialog(
                requireContext(),
                deleteDailogBinding.edtBreakstart,
                deleteDailogBinding.edtBreakend,
                deleteDailogBinding.tvNext,
                1
            )
        }

        deleteDailogBinding.tvNext.setOnClickListener {
            homeActivity.showDialog()
            val tDate1 = deleteDailogBinding.edtBreakstart.text.toString()
            var tDate2 = deleteDailogBinding.edtBreakend.text.toString()
            val inputFormat = "yyyy-MM-dd"
            val outputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS"

            val t1 = convertDateFormat(tDate1, inputFormat, outputFormat)
            val t2 = convertDateFormat(tDate2, inputFormat, outputFormat)
            viewModel.GetUserTickets(
                userID = prefs.userID.toInt(),
                startDate = t1, endDate = t2
            )
            deleteDialog.cancel()
        }

        deleteDialog.setView(deleteDailogBinding.root)
        deleteDialog.setCanceledOnTouchOutside(true);
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        deleteDialog.show();
    }

    override fun onResume() {
        super.onResume()
        loadingDialog.show()
        viewModel.GetUserTickets(prefs.userID.toInt())
    }
}