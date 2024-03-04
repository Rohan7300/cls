package com.clebs.celerity.fragments

import android.app.AlertDialog
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
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentDailyWorkBinding
import com.clebs.celerity.databinding.FragmentVechileMileageBinding
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.ui.App
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.navigateTo

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VechileMileageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VechileMileageFragment : Fragment() {
    lateinit var mbinding: FragmentVechileMileageBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

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
        }


        mbinding.headings.setOnClickListener {
        //   findNavController().navigate(R.id.profileFragment)
            //navigateTo(R.id.profileFragment)
            navigateTo(R.id.profileFragment,requireContext(),findNavController())
        }

        mbinding.run {
            edtMil.doAfterTextChanged {

                mbinding.tvNext.isEnabled = (edtMil.text?.length!! > 0)
                if (tvNext.isEnabled) {
                    tvNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                } else {
                    tvNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                }

            }

        }
        mbinding.tvNext.setOnClickListener {
            val bundle= Bundle()
            bundle.putString("vm_mileage",mbinding.edtMil.text.toString())
            navigateTo(R.id.windScreenFragment,requireContext(),findNavController())
            //findNavController().navigate(R.id.windScreenFragment,bundle)
        }
        return mbinding.root
    }

/*    fun navigateTo(fragmentId: Int) {

        val prefs = Prefs.getInstance(requireContext())
        val fragmentStack = prefs.getNavigationHistory()
        fragmentStack.push(fragmentId)
        findNavController().navigate(fragmentId)
        prefs.saveNavigationHistory(fragmentStack)
    }*/
}