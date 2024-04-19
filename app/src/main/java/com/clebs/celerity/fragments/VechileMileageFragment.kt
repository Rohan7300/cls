package com.clebs.celerity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentVechileMileageBinding
import com.clebs.celerity.ui.App
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.navigateTo


class VechileMileageFragment : Fragment() {
    lateinit var mbinding: FragmentVechileMileageBinding
    private lateinit var viewModel: MainViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::mbinding.isInitialized) {
            mbinding =DataBindingUtil.inflate(inflater,R.layout.fragment_vechile_mileage,container, false)
        }
              return mbinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel =(activity as HomeActivity).viewModel
        viewModel.setLastVisitedScreenId(requireActivity(), R.id.vechileMileageFragment)

        mbinding.miles.text = buildString {

            append("${Prefs.getInstance(App.instance).vehicleLastMileage} ")
            append("Miles")
        }
        if (Prefs.getInstance(requireContext()).currLocationName != null) {
            mbinding.headerTop.dxLoc.text =
                Prefs.getInstance(requireContext()).currLocationName ?: ""
        } else if (Prefs.getInstance(requireContext()).workLocationName != null) {
            mbinding.headerTop.dxLoc.text =
                Prefs.getInstance(requireContext()).workLocationName ?: ""
        }
        "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}"
            .also { name -> mbinding.headerTop.anaCarolin.text = name }
        mbinding.headerTop.dxm5.text = (activity as HomeActivity).date
        if(mbinding.headerTop.dxReg.text.isEmpty()||mbinding.headerTop.dxReg.text=="")
            mbinding.headerTop.strikedxRegNo.visibility = View.VISIBLE
        else
            mbinding.headerTop.strikedxRegNo.visibility = View.GONE
        if(mbinding.headerTop.dxLoc.text.isEmpty()||mbinding.headerTop.dxLoc.text==""||mbinding.headerTop.dxLoc.text=="Not Allocated")
            mbinding.headerTop.strikedxLoc.visibility = View.VISIBLE
        else
            mbinding.headerTop.strikedxLoc.visibility = View.GONE

        viewModel.vechileInformationLiveData.observe(viewLifecycleOwner) { it ->
            it?.let {
                Prefs.getInstance(App.instance).vehicleLastMileage = it.vehicleLastMillage
                mbinding.miles.text = buildString {

                    append("${it.vehicleLastMillage} ")
                    append("Miles")
                }
                mbinding.headerTop.dxReg.text = it.vmRegNo ?: ""
            }
            if (Prefs.getInstance(requireContext()).currLocationName != null) {
                mbinding.headerTop.dxLoc.text =
                    Prefs.getInstance(requireContext()).currLocationName ?: ""
            } else if (Prefs.getInstance(requireContext()).workLocationName != null) {
                mbinding.headerTop.dxLoc.text =
                    Prefs.getInstance(requireContext()).workLocationName ?: ""
            } else {
                if (it != null) {
                    mbinding.headerTop.dxLoc.text = it.locationName ?: ""
                }
            }
            if(mbinding.headerTop.dxReg.text.isEmpty()||mbinding.headerTop.dxReg.text=="")
                mbinding.headerTop.strikedxRegNo.visibility = View.VISIBLE
            else
                mbinding.headerTop.strikedxRegNo.visibility = View.GONE
            if(mbinding.headerTop.dxLoc.text.isEmpty()||mbinding.headerTop.dxLoc.text==""||mbinding.headerTop.dxLoc.text=="Not Allocated")
                mbinding.headerTop.strikedxLoc.visibility = View.VISIBLE
            else
                mbinding.headerTop.strikedxLoc.visibility = View.GONE


        }

        mbinding.edtMilvm.doAfterTextChanged { edtMilText ->
            edtMilText.let {
                if(edtMilText?.isNotEmpty() == true){
                    Prefs.getInstance(App.instance).vehicleLastMileage = edtMilText.toString().toInt()
                    mbinding.tvNext.isEnabled = true
                }
                mbinding.tvNext.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (mbinding.tvNext.isEnabled) R.color.white else R.color.orange
                    )
                )
            }
        }



        mbinding.headerTop.headings.setOnClickListener {
            navigateTo(R.id.profileFragment,requireContext(),findNavController())
        }


        mbinding.tvNext.setOnClickListener {
            val bundle= Bundle()
            bundle.putString("vm_mileage",mbinding.edtMilvm.text.toString())
            navigateTo(R.id.windScreenFragment,requireContext(),findNavController())
        }
    }
}