package com.clebs.celerity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentVechileMileageBinding
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
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
            mbinding = FragmentVechileMileageBinding.inflate(inflater, container, false)
        }
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        viewModel =(activity as HomeActivity).viewModel
        viewModel.setLastVisitedScreenId(requireActivity(), R.id.vechileMileageFragment)

        mbinding.miles.setText(
            Prefs.getInstance(App.instance).get("vehicleLastMillage") + " Miles"
        )


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

//        mbinding.run {
           mbinding. edtMil.doAfterTextChanged {

                mbinding.tvNext.isEnabled = (mbinding.edtMil.text?.length!! > 0)
                if (mbinding.tvNext.isEnabled) {
                    mbinding.tvNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                } else {
                    mbinding.  tvNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                }
//            }
        }
        mbinding.tvNext.setOnClickListener {
            val bundle= Bundle()
            bundle.putString("vm_mileage",mbinding.edtMil.text.toString())
            navigateTo(R.id.windScreenFragment,requireContext(),findNavController())
        }
        return mbinding.root
    }
}