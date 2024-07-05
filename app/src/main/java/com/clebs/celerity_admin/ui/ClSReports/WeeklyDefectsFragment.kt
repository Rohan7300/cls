package com.clebs.celerity_admin.ui.ClSReports

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity_admin.MainActivityTwo
import com.clebs.celerity_admin.adapters.WeeklyDefectAdapter
import com.clebs.celerity_admin.databinding.FragmentSlideshowBinding
import com.clebs.celerity_admin.dialogs.LoadingDialog
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.viewModels.MainViewModel

class WeeklyDefectsFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    lateinit var mainViewModel: MainViewModel
    var week: Int? = null
    var isLoaded = false
    var j = 0
    var year: Int? = null
    private lateinit var WeeklyDefectAdapter: WeeklyDefectAdapter
    private lateinit var loadingDialog: LoadingDialog
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
        WeeklyDefectAdapter = WeeklyDefectAdapter(requireContext(), ArrayList())
        binding.rvList.adapter = WeeklyDefectAdapter
        loadingDialog = (activity as MainActivityTwo).loadingDialog
        setPrevNextButton()

        binding.prev.setOnClickListener {

            if (isLoaded) {
                loadingDialog.show()
                j -= 1
                val y = week!! + j
                binding.weekNoTV.text = "Week No. $y"
                isLoaded = false
                mainViewModel.GetWeeklyDefectChecks(
                    y.toDouble(),
                    year!!.toDouble(),
                    0.0,
                    0.0,
                    false
                )

                setPrevNextButton()
            }
        }

        binding.next.setOnClickListener {
            if (isLoaded) {
                loadingDialog.show()
                j += 1
                isLoaded = false
                val x = week!! + j
                binding.weekNoTV.text = "Week No. $x"
                mainViewModel.GetWeeklyDefectChecks(
                    x.toDouble(),
                    year!!.toDouble(),
                    0.0,
                    0.0,
                    false
                )
                setPrevNextButton()
            }
        }
        Observers()

        return root
    }

    fun Observers() {
        mainViewModel.GetCurrentWeekYear().observe(viewLifecycleOwner, Observer {
            loadingDialog.dismiss()
            if (it != null) {
                isLoaded = true
                week = it.weekNO
                year = it.year
                binding.weekNoTV.text = "Week No. $week"
                loadingDialog.show()
                mainViewModel.GetWeeklyDefectChecks(
                    week!!.toDouble(),
                    year!!.toDouble(),
                    0.0,
                    0.0,
                    false
                )
            }
        })
        mainViewModel.lDGetWeeklyDefectChecks.observe(viewLifecycleOwner) {
            loadingDialog.dismiss()
            isLoaded = true
            if (it != null) {
                Log.e("dataass", "Observers: " + it)
                WeeklyDefectAdapter.data.clear()
                WeeklyDefectAdapter.data.addAll(it)
                WeeklyDefectAdapter.notifyDataSetChanged()

            } else {

            }
        }

    }

    fun setPrevNextButton() {
        if (j == 0) {
            binding.prev.visibility = View.VISIBLE
            binding.next.visibility = View.GONE
            binding.placeholder1.visibility = View.GONE
            binding.placeholder2.visibility = View.VISIBLE
        } else if (j == -1) {
            binding.prev.visibility = View.VISIBLE
            binding.next.visibility = View.VISIBLE
            binding.placeholder1.visibility = View.GONE
            binding.placeholder2.visibility = View.GONE
        } else if (j == -2) {
            binding.prev.visibility = View.GONE
            binding.next.visibility = View.VISIBLE
            binding.placeholder1.visibility = View.VISIBLE
            binding.placeholder2.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        (activity as MainActivityTwo).binding.appBarMainActivityTwo.bottomBar.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivityTwo).binding.appBarMainActivityTwo.bottomBar.visibility = View.VISIBLE
    }
}