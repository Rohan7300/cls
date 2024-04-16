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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInvoicesBinding.inflate(layoutInflater)
        viewModel = (activity as HomeActivity).viewModel

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
                    viewModel.GetVehicleInformation(Prefs.getInstance(requireContext()).userID.toInt(),
                        it1
                    )
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
            if (Prefs.getInstance(requireContext()).currLocationName != null) {
                binding.headerTop.dxLoc.text =
                    Prefs.getInstance(requireContext()).currLocationName ?: ""
            } else if (Prefs.getInstance(requireContext()).workLocationName != null) {
                binding.headerTop.dxLoc.text =
                    Prefs.getInstance(requireContext()).workLocationName ?: ""
            } else {
                if (it != null) {
                    binding.headerTop.dxLoc.text = it.locationName ?: ""
                }
            }
            if (it != null) {
                binding.headerTop.dxReg.text = it.vmRegNo ?: ""
            }
        }
    }


}