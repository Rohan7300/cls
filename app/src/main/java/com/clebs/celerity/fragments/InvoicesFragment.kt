package com.clebs.celerity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentInvoicesBinding
import com.clebs.celerity.ui.HomeActivity


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
        showDialog()
        observers()
        binding.clsinvoices.setOnClickListener {
            findNavController().navigate(R.id.CLSInvoicesFragment)
        }
        binding.otherinvoices.setOnClickListener {
            findNavController().navigate(R.id.CLSThirdPartyFragment)
        }

        return binding.root
    }

    private fun observers() {
        viewModel.vechileInformationLiveData.observe(viewLifecycleOwner) {

            hideDialog()

            if (it != null) {
                binding.headerTop.dxLoc.text = it.locationName ?: ""
                binding.headerTop.dxReg.text = it.vmRegNo ?: ""
            }

            "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}".also { name ->
                binding.headerTop.anaCarolin.text = name
            }
            binding.headerTop.dxm5.text = (activity as HomeActivity).date
        }
    }


}