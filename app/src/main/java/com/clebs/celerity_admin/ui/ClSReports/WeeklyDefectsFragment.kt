package com.clebs.celerity_admin.ui.ClSReports

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity_admin.adapters.WeeklyDefectAdapter
import com.clebs.celerity_admin.databinding.FragmentSlideshowBinding
import com.clebs.celerity_admin.factory.MyViewModelFactory

import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.viewModels.MainViewModel

class WeeklyDefectsFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    lateinit var mainViewModel: MainViewModel
    var week : Int?=null
    var year : Int?=null
    private lateinit var WeeklyDefectAdapter: WeeklyDefectAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)


        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]
        WeeklyDefectAdapter = WeeklyDefectAdapter(ArrayList())
binding.rvList.adapter=WeeklyDefectAdapter

        Observers()

        return root
    }

    fun Observers() {
        mainViewModel.GetCurrentWeekYear().observe(viewLifecycleOwner, Observer {
            if (it != null) {

                week = it.weekNO
                year = it.year
            }
            mainViewModel.GetWeeklyDefectChecks(24.00,year!!.toDouble(),0.0,0.0,true).observe(viewLifecycleOwner,
                Observer {
                    if (it != null) {
                        Log.e("dataass", "Observers: "+it )
                        WeeklyDefectAdapter.data.addAll(it)
                        WeeklyDefectAdapter.notifyDataSetChanged()

                    }


                })

        })

//        mainViewModel.GetWeeklyDefectChecks(week!!.toDouble(), year!!.toDouble(), 0.0, 0.0, true)
//            .observe(viewLifecycleOwner,
//                Observer {
//                    if (it != null) {
//                        Log.e("dataass", "Observers: "+it )
//                        WeeklyDefectAdapter.data.addAll(it)
//                        WeeklyDefectAdapter.notifyDataSetChanged()
//
//                    }
//
//
//                })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}