package com.clebs.celerity.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentInvoicesBinding
import com.clebs.celerity.ui.App
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.Prefs


class InvoicesFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    val showDialog: () -> Unit = {
        (activity as HomeActivity).showDialog()
    }
    val hideDialog: () -> Unit = {
        (activity as HomeActivity).hideDialog()
    }
    lateinit var binding: FragmentInvoicesBinding
    lateinit var prefs: Prefs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInvoicesBinding.inflate(layoutInflater)
        viewModel = (activity as HomeActivity).viewModel
        prefs = Prefs.getInstance(requireContext())
        observers()
        binding.clsinvoices.setOnClickListener {
            findNavController().navigate(R.id.CLSInvoicesFragment)
        }
        binding.otherinvoices.setOnClickListener {
            findNavController().navigate(R.id.CLSThirdPartyFragment)
        }
        binding.otherinvoices.visibility = View.GONE
        GetDriversBasicInformation()
        return binding.root
    }

    private fun GetDriversBasicInformation() {

        showDialog()

        viewModel.GetDriversBasicInformation(
            Prefs.getInstance(App.instance).userID.toDouble()
        ).observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                it.vmRegNo?.let { it1 ->
                    binding.headerTop.dxReg.text = it1?:"Not Assigned"
                    viewModel.GetVehicleInformation(Prefs.getInstance(requireContext()).userID.toInt(),
                        it1
                    )
                }
                if(it.workinglocation!=null){
                    prefs.workLocationName = it.workinglocation
                }
                if(it.currentlocation!=null){
                    prefs.currLocationName = it.currentlocation
                }


                if (prefs.currLocationName.isNotEmpty()) {
                    binding.headerTop.dxLoc.text = prefs.currLocationName ?: ""
                } else if (prefs.workLocationName.isNotEmpty()) {
                    binding.headerTop.dxLoc.text =
                        prefs.workLocationName ?: ""
                }
                if (it.IsThirdPartyChargeAccessAllowed) {
                    binding.otherinvoices.visibility = View.VISIBLE
                } else {
                    binding.otherinvoices.visibility = View.GONE
                }
            }
        }
    }

    private fun observers() {
        "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}".also { name ->
            binding.headerTop.anaCarolin.text = name
        }
        binding.headerTop.dxm5.text = (activity as HomeActivity).date
        viewModel.vechileInformationLiveData.observe(viewLifecycleOwner) {
            hideDialog()
            if (prefs.currLocationName.isNotEmpty()) {
                binding.headerTop.dxLoc.text = prefs.currLocationName ?: ""
            } else if (prefs.workLocationName.isNotEmpty()) {
                binding.headerTop.dxLoc.text =
                   prefs.workLocationName ?: ""
            } else {
                if (it != null) {
                    binding.headerTop.dxLoc.text = it.locationName ?: ""
                }
            }
            "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}".also { name ->
                binding.headerTop.anaCarolin.text = name
            }
            binding.headerTop.dxm5.text = (activity as HomeActivity).date
        }
    }


}