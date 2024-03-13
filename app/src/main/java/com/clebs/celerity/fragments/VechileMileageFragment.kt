package com.clebs.celerity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
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


        mbinding.edtMilvm.doAfterTextChanged { edtMilText ->
            edtMilText.let {
                mbinding.tvNext.isEnabled = edtMilText?.isNotEmpty() == true
                mbinding.tvNext.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (mbinding.tvNext.isEnabled) R.color.white else R.color.orange
                    )
                )
            }
        }

        viewModel.vechileInformationLiveData.observe(viewLifecycleOwner){
            mbinding.dxLoc.text = it?.locationName?:""
            mbinding.dxReg.text = it?.vmRegNo?:""
            "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}"
                .also { name -> mbinding.anaCarolin.text = name }
            mbinding.dxm5.text = (activity as HomeActivity).date
        }

        mbinding.headings.setOnClickListener {
            navigateTo(R.id.profileFragment,requireContext(),findNavController())
        }


        mbinding.tvNext.setOnClickListener {
            val bundle= Bundle()
            bundle.putString("vm_mileage",mbinding.edtMilvm.text.toString())
            navigateTo(R.id.windScreenFragment,requireContext(),findNavController())
        }
    }
}