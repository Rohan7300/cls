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
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.TicketAdapter
import com.clebs.celerity.databinding.DialogSortFiltersBinding
import com.clebs.celerity.databinding.FragmentUserTicketsBinding
import com.clebs.celerity.models.response.GetUserTicketsResponse
import com.clebs.celerity.ui.CreateTicketsActivity
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.DependencyProvider.brkEnd
import com.clebs.celerity.utils.DependencyProvider.brkStart
import com.clebs.celerity.utils.DependencyProvider.comingFromViewTickets
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.convertDateFormat
import com.clebs.celerity.utils.showDatePickerDialog


class UserTicketsFragment : Fragment() {
    lateinit var mbinding: FragmentUserTicketsBinding
    lateinit var viewModel: MainViewModel
    lateinit var prefs: Prefs
    lateinit var homeActivity: HomeActivity
    private lateinit var loadingDialog: LoadingDialog
    var d1: Boolean = false
    var d2: Boolean = false
    lateinit var deleteDialog: AlertDialog
    lateinit var deleteDailogBinding: DialogSortFiltersBinding
    var includeCompleted = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mbinding = FragmentUserTicketsBinding.inflate(inflater, container, false)
        homeActivity = (activity as HomeActivity)
        viewModel = homeActivity.viewModel
        prefs = Prefs.getInstance(requireContext())
        loadingDialog = homeActivity.loadingDialog
        comingFromViewTickets = false
        homeActivity.showDialog()
        viewModel.GetUserTickets(prefs.clebUserId.toInt())

        deleteDialog = AlertDialog.Builder(requireContext()).create()

        deleteDailogBinding =
            DialogSortFiltersBinding.inflate(LayoutInflater.from(requireContext()))

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
        viewModel.liveDataGetUserTickets.observe(viewLifecycleOwner) {
            if (loadingDialog.isShowing) {
                loadingDialog.dismiss()
            }
            if (it != null) {
                if (it.Docs.size > 0) {
                    mbinding.noticketLayout.visibility = View.GONE
                    mbinding.rvTickets.visibility = View.VISIBLE
                } else {
                    mbinding.noticketLayout.visibility = View.VISIBLE
                    mbinding.rvTickets.visibility = View.GONE
                }
                val ticketAdapter =
                    TicketAdapter(GetUserTicketsResponse(ArrayList()), requireContext(), prefs)
                mbinding.rvTickets.adapter = ticketAdapter
                mbinding.rvTickets.layoutManager = LinearLayoutManager(requireContext())
                ticketAdapter.ticketList.Docs.clear()
                val reversedList = it.Docs.reversed()
                ticketAdapter.ticketList.Docs.addAll(reversedList)
                ticketAdapter.notifyItemInserted(it.Docs.size)
            } else {
                mbinding.noticketLayout.visibility = View.VISIBLE
                mbinding.rvTickets.visibility = View.GONE
            }
        }
    }

    fun showAlert() {
        /*        val factory = LayoutInflater.from(requireActivity())
                //val view: View = factory.inflate(R.layout.dialog_sort_filters, null)
                val deleteDialog: AlertDialog = AlertDialog.Builder(requireContext()).create()

                val deleteDailogBinding =
                    DialogSortFiltersBinding.inflate(LayoutInflater.from(requireContext()))*/

        deleteDailogBinding.tvNext.isClickable = false
        deleteDailogBinding.icCrossOrange.setOnClickListener {
            deleteDialog.cancel()
        }
        deleteDailogBinding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            includeCompleted = isChecked
            if (isChecked) {
                enableDates(true, deleteDailogBinding)
            } else {
                enableDates(false, deleteDailogBinding)
            }
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
        deleteDailogBinding.edtBreakstart.doAfterTextChanged {
            d1 = deleteDailogBinding.edtBreakstart.text != "DD-MM-YYYY"
            if (d1 && d2)
                deleteDailogBinding.tvNext.isEnabled = true
        }
        deleteDailogBinding.edtBreakend.doAfterTextChanged {
            d2 = deleteDailogBinding.edtBreakend.text != "DD-MM-YYYY"
            if (d1 && d2)
                deleteDailogBinding.tvNext.isEnabled = true
        }

        deleteDailogBinding.tvNext.setOnClickListener {
            loadingDialog.show()
            val tDate1 = deleteDailogBinding.edtBreakstart.text.toString()
            var tDate2 = deleteDailogBinding.edtBreakend.text.toString()
            val inputFormat = "yyyy-MM-dd"
            val outputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS"


            viewModel.GetUserTickets(
                userID = prefs.clebUserId.toInt(),
                startDate = brkStart,
                endDate = brkEnd,
                includeCompleted = includeCompleted
            )
            deleteDialog.cancel()
        }

        deleteDialog.setView(deleteDailogBinding.root)
        deleteDialog.setCanceledOnTouchOutside(true);
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        deleteDialog.show();
    }

    private fun enableDates(b: Boolean, dialogBinding: DialogSortFiltersBinding) {
        if (b) {
            dialogBinding.edtBreakstart.isClickable = true
            dialogBinding.edtBreakstart.isEnabled = true
            dialogBinding.edtBreakend.isClickable = true
            dialogBinding.edtBreakend.isEnabled = true
            dialogBinding.edtBreakstart.setBackgroundResource(R.drawable.shape_edittext_onroad)
            dialogBinding.edtBreakend.setBackgroundResource(R.drawable.shape_edittext_onroad)
        } else {
            dialogBinding.edtBreakstart.isClickable = false
            dialogBinding.edtBreakstart.isEnabled = false
            dialogBinding.edtBreakend.isClickable = false
            dialogBinding.edtBreakend.isEnabled = false
            dialogBinding.edtBreakstart.setBackgroundResource(R.drawable.shape_edittext_onroad_gray)
            dialogBinding.edtBreakend.setBackgroundResource(R.drawable.shape_edittext_onroad_gray)
        }
    }

    override fun onResume() {
        super.onResume()
        if(!comingFromViewTickets){
            loadingDialog.show()
            viewModel.GetUserTickets(prefs.clebUserId.toInt())
            comingFromViewTickets = false
        }
    }
}