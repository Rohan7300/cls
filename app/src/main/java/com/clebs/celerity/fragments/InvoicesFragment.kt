package com.clebs.celerity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentInvoicesBinding
import com.clebs.celerity.ui.App
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.getLoc
import com.clebs.celerity.utils.getVRegNo


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
        (activity as HomeActivity).ActivityHomeBinding.title.text = "Invoices"
        prefs = Prefs.getInstance(requireContext())
        observers()
        findNavController().currentDestination!!.id = R.id.invoicesFragment
        binding.clsinvoices.setOnClickListener {
            findNavController().navigate(R.id.CLSInvoicesFragment)
        }
        binding.otherinvoices.setOnClickListener {
            findNavController().navigate(R.id.CLSThirdPartyFragment)
        }
        binding.otherinvoices.visibility = View.GONE
        showDialog()
        GetDriversBasicInformation()
        return binding.root
    }

    private fun GetDriversBasicInformation() {

        hideDialog()
        viewModel.GetDriversBasicInformation(
            Prefs.getInstance(App.instance).clebUserId.toDouble()
        ).observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                prefs.thridPartyAcess = it.IsThirdPartyChargeAccessAllowed
                if (it.IsThirdPartyChargeAccessAllowed) {
                    binding.otherinvoices.visibility = View.VISIBLE
                } else {
                    binding.otherinvoices.visibility = View.GONE
                }


                if (it.workinglocation != null) {
                    prefs.workLocationName = it.workinglocation
                }
                if (it.currentlocation != null) {
                    prefs.currLocationName = it.currentlocation
                }

                /*               setDxLoc()
                               binding.headerTop.dxReg.text =
                                   getVRegNo(prefs = Prefs.getInstance(requireContext()))

                               if (binding.headerTop.dxReg.text.isEmpty())
                                   binding.headerTop.strikedxRegNo.visibility = View.VISIBLE
                               else
                                   binding.headerTop.strikedxRegNo.visibility = View.GONE*/
            }
        }
    }

    private fun observers() {
        setHeader()
        /*        "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}".also { name ->
                    binding.headerTop.anaCarolin.text = name
                }
                binding.headerTop.dxm5.text = (activity as HomeActivity).date

                if (binding.headerTop.dxReg.text.isEmpty())
                    binding.headerTop.strikedxRegNo.visibility = View.VISIBLE
                else
                    binding.headerTop.strikedxRegNo.visibility = View.GONE

                    setDxLoc()*/

        /*        viewModel.vechileInformationLiveData.observe(viewLifecycleOwner) {
                    hideDialog()

                    if (binding.headerTop.dxReg.text.isEmpty())
                        binding.headerTop.strikedxRegNo.visibility = View.VISIBLE
                    else
                        binding.headerTop.strikedxRegNo.visibility = View.GONE
                    setDxLoc()

                    "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}".also { name ->
                        binding.headerTop.anaCarolin.text = name
                    }
                    binding.headerTop.dxm5.text = (activity as HomeActivity).date*/

    }

    private fun setDxLoc(loc: String) {
        binding.headerTop.dxLoc.text = loc
        if (binding.headerTop.dxLoc.text.isEmpty() || binding.headerTop.dxLoc.text == "")
            binding.headerTop.strikedxLoc.visibility = View.VISIBLE
        else
            binding.headerTop.strikedxLoc.visibility = View.GONE
    }

    private fun setHeader() {
        val prefs = Prefs.getInstance(requireContext())

        binding.headerTop.anaCarolin.text = prefs.userName
        binding.headerTop.dxm5.text = prefs.headerDate
        setDxLoc(getLoc(prefs))

        binding.headerTop.dxReg.text = getVRegNo(prefs = prefs)
        if (binding.headerTop.dxReg.text.isEmpty() || binding.headerTop.dxReg.text == "")
            binding.headerTop.strikedxRegNo.visibility = View.VISIBLE
        else
            binding.headerTop.strikedxRegNo.visibility = View.GONE

    }
}