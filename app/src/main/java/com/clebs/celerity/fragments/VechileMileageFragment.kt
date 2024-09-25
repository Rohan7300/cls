package com.clebs.celerity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.getLoc
import com.clebs.celerity.utils.getVRegNo
import com.clebs.celerity.utils.navigateTo
import com.clebs.celerity.utils.showToast


class VechileMileageFragment : Fragment() {
    lateinit var mbinding: FragmentVechileMileageBinding
    private lateinit var viewModel: MainViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::mbinding.isInitialized) {
            mbinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_vechile_mileage,
                container,
                false
            )
        }
        return mbinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as HomeActivity).viewModel
        viewModel.setLastVisitedScreenId(requireActivity(), R.id.vechileMileageFragment)

        mbinding.miles.text = buildString {
            append("${Prefs.getInstance(App.instance).vehicleLastMileage} ")
            append("Miles")
        }

        /* setDxLoc()

         mbinding.headerTop.dxReg.text = getVRegNo(prefs = Prefs.getInstance(requireContext()))

         "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}"
             .also { name -> mbinding.headerTop.anaCarolin.text = name }
         mbinding.headerTop.dxm5.text = (activity as HomeActivity).date

         if (mbinding.headerTop.dxReg.text.isEmpty() || mbinding.headerTop.dxReg.text == "")
             mbinding.headerTop.strikedxRegNo.visibility = View.VISIBLE
         else
             mbinding.headerTop.strikedxRegNo.visibility = View.GONE*/

        setHeader()
/*        val dispatcher = requireActivity().onBackPressedDispatcher
        dispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (activity as HomeActivity).onBackPressed()
            }
        })*/
        mbinding.miles.text = buildString {
            append("${Prefs.getInstance(App.instance).VdhOdoMeterReading} ")
            append("Miles")
        }

        /*        mbinding.headerTop.dxReg.text = getVRegNo(prefs = Prefs.getInstance(requireContext()))
                setDxLoc()
                if (mbinding.headerTop.dxReg.text.isEmpty() || mbinding.headerTop.dxReg.text == "")
                    mbinding.headerTop.strikedxRegNo.visibility = View.VISIBLE
                else
                    mbinding.headerTop.strikedxRegNo.visibility = View.GONE*/

        mbinding.edtMilvm.doAfterTextChanged { edtMilText ->
            edtMilText.let {
                try {
                    if (edtMilText?.isNotEmpty() == true) {
                        Prefs.getInstance(App.instance).vehicleLastMileage =
                            edtMilText.toString().toInt()
                        mbinding.tvNext.isEnabled = true
                    } else {
                        mbinding.tvNext.isEnabled = false
                    }
                    mbinding.tvNext.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (mbinding.tvNext.isEnabled) R.color.white else R.color.orange
                        )
                    )
                } catch (_: Exception) {
                    showToast("Value too Long!!", requireContext())
                }

            }
        }



        mbinding.headerTop.headings.setOnClickListener {
            navigateTo(R.id.profileFragment, requireContext(), findNavController())
        }


        mbinding.tvNext.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("vm_mileage", mbinding.edtMilvm.text.toString())
            navigateTo(R.id.windScreenFragment, requireContext(), findNavController())
        }
    }

    private fun setHeader() {
        val prefs = Prefs.getInstance(requireContext())

        setDxLoc(getLoc(prefs))
        mbinding.headerTop.anaCarolin.text = prefs.userName
        mbinding.headerTop.dxm5.text = prefs.headerDate
        setDxLoc(getLoc(prefs))
        mbinding.headerTop.dxReg.text = getVRegNo(prefs = prefs)

        if (mbinding.headerTop.dxReg.text.isEmpty() || mbinding.headerTop.dxReg.text == "")
            mbinding.headerTop.strikedxRegNo.visibility = View.VISIBLE
        else
            mbinding.headerTop.strikedxRegNo.visibility = View.GONE

    }

    private fun setDxLoc(loc: String) {
        mbinding.headerTop.dxLoc.text = loc
        if (mbinding.headerTop.dxLoc.text.isEmpty() || mbinding.headerTop.dxLoc.text == "")
            mbinding.headerTop.strikedxLoc.visibility = View.VISIBLE
        else
            mbinding.headerTop.strikedxLoc.visibility = View.GONE
    }
}