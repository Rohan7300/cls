package com.clebs.celerity_admin.ui.ClSReports

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity_admin.MainActivityTwo
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.SubmitWeeklyDefectActivity
import com.clebs.celerity_admin.adapters.SelectVehicleLocationAdapter
import com.clebs.celerity_admin.adapters.SelectVehicleLocationAdapterTwo
import com.clebs.celerity_admin.adapters.WeeklyDefectAdapter
import com.clebs.celerity_admin.databinding.FragmentSlideshowBinding
import com.clebs.celerity_admin.dialogs.LoadingDialog
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.models.WeeklyDefectChecksModelItem
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.utils.DependencyClass.currentWeeklyDefectItem
import com.clebs.celerity_admin.utils.OnItemClickRecyclerView
import com.clebs.celerity_admin.utils.Prefs
import com.clebs.celerity_admin.viewModels.MainViewModel

class WeeklyDefectsFragment : Fragment(), WeeklyDefectAdapter.WeeklyDefectsClickListener,
    OnItemClickRecyclerView {

    private var _binding: FragmentSlideshowBinding? = null
    private lateinit var mainViewModel: MainViewModel
    var week: Int? = null
    var isLoaded = false
    var j = 0
    private var rv_locatio: RecyclerView? = null
    lateinit var selectVehcilelocationadapter: SelectVehicleLocationAdapterTwo
    var filter: Boolean = true
    var showDefectCheckboxValue:Boolean = false
    private var year: Int? = null
    lateinit var deleteDialogthree: AlertDialog
    private lateinit var WeeklyDefectAdapter: WeeklyDefectAdapter
    private lateinit var loadingDialog: LoadingDialog
    private val binding get() = _binding!!
    var currentWeek = 0
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
        WeeklyDefectAdapter = WeeklyDefectAdapter(requireContext(), ArrayList(), this)
        binding.rvList.adapter = WeeklyDefectAdapter
        selectVehcilelocationadapter = SelectVehicleLocationAdapterTwo(ArrayList(), this)
        mainViewModel.GetVehicleLocationListing().observe(viewLifecycleOwner, Observer {
            if (it != null) {

                selectVehcilelocationadapter.data.addAll(it)
                selectVehcilelocationadapter.notifyDataSetChanged()
            } else {

            }
        })
        binding.showDefectCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            showDefectCheckboxValue = isChecked
            if (isLoaded) {
                loadingDialog.show()
                isLoaded=false
                mainViewModel.GetWeeklyDefectChecks(
                    currentWeek.toDouble(),
                    year!!.toDouble(),
                    0.0,
                    0.0,
                    showDefectCheckboxValue
                )
            }
        }
        loadingDialog = (activity as MainActivityTwo).loadingDialog
        setPrevNextButton()
        val activity = requireActivity() as MainActivityTwo
        val view = activity.findViewById<View>(R.id.filter)
        view.setOnClickListener {
            if (filter) {
                binding.llfilter.visibility = View.GONE
                filter = false
            } else {
                binding.llfilter.visibility = View.VISIBLE
                filter = true
            }

        }
        binding.rltwo.setOnClickListener {
            ShowReturnVehicleList()
        }
        binding.prev.setOnClickListener {

            if (isLoaded) {
                loadingDialog.show()
                j -= 1
                val y = week!! + j
                binding.weekNoTV.text = "Week No. $y"
                isLoaded = false
                currentWeek = y
                mainViewModel.GetWeeklyDefectChecks(
                    y.toDouble(),
                    year!!.toDouble(),
                    0.0,
                    0.0,
                    showDefectCheckboxValue
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
                currentWeek = x
                mainViewModel.GetWeeklyDefectChecks(
                    x.toDouble(),
                    year!!.toDouble(),
                    0.0,
                    0.0,
                    showDefectCheckboxValue
                )
                setPrevNextButton()
            }
        }
        Observers()

        return root
    }

    private fun Observers() {
        loadingDialog.show()
        mainViewModel.GetCurrentWeekYear().observe(viewLifecycleOwner, Observer {
            loadingDialog.dismiss()
            if (it != null) {
                isLoaded = true
                week = it.weekNO
                year = it.year
                binding.weekNoTV.text = "Week No. $week"
                loadingDialog.show()
                currentWeek = week!!.toInt()
                mainViewModel.GetWeeklyDefectChecks(
                    week!!.toDouble(),
                    year!!.toDouble(),
                    0.0,
                    0.0,
                    showDefectCheckboxValue
                )
            }
        })
        mainViewModel.lDGetWeeklyDefectChecks.observe(viewLifecycleOwner) {
            loadingDialog.dismiss()
            isLoaded = true
            if (it != null) {
                Log.e("dataass", "Observers: " + it)
                WeeklyDefectAdapter.data.clear()
                if (it.size > 0) {
                    binding.nodataLayout.visibility = View.GONE
                    binding.rvList.visibility = View.VISIBLE
                    WeeklyDefectAdapter.data.addAll(it)
                    WeeklyDefectAdapter.notifyDataSetChanged()
                } else {
                    binding.nodataLayout.visibility = View.VISIBLE
                    binding.rvList.visibility = View.GONE
                }
            } else {
                binding.nodataLayout.visibility = View.VISIBLE
                binding.rvList.visibility = View.GONE
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

        (activity as MainActivityTwo).binding.appBarMainActivityTwo.bottomBar.visibility =
            View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivityTwo).binding.appBarMainActivityTwo.bottomBar.visibility =
            View.VISIBLE
    }

    override fun docClickAction(item: WeeklyDefectChecksModelItem) {
        val intent = Intent(requireContext(), SubmitWeeklyDefectActivity::class.java)
        currentWeeklyDefectItem = item
        Prefs.getInstance(requireContext()).currentWeeklyDefectItemVehRegNo = item.vehRegNo
        startActivity(intent)
    }

    private fun ShowReturnVehicleList() {
        val factory = LayoutInflater.from(requireContext())
        val view: View = factory.inflate(R.layout.dialog_location_list, null)
        deleteDialogthree = AlertDialog.Builder(requireContext()).create()
        deleteDialogthree.setView(view)
        rv_locatio = view.findViewById(R.id.tvcompany)
        rv_locatio?.adapter = selectVehcilelocationadapter
        deleteDialogthree.setCancelable(true)
        deleteDialogthree.setCanceledOnTouchOutside(true);
        deleteDialogthree.show()
    }

    override fun OnItemClickRecyclerViewClicks(
        recyclerViewId: Int,
        position: Int,
        itemclicked: String
    ) {
        deleteDialogthree.dismiss()
        binding.tvlocname.setText(itemclicked)

        mainViewModel.GetWeeklyDefectChecks(
            currentWeek!!.toDouble(),
            year!!.toDouble(),
            0.0,
            position.toDouble(),
            showDefectCheckboxValue
        )
        mainViewModel.lDGetWeeklyDefectChecks.observe(viewLifecycleOwner) {
            loadingDialog.dismiss()
            isLoaded = true
            if (it != null) {
                Log.e("dataass", "Observers: " + it)
                WeeklyDefectAdapter.data.clear()
                if (it.size > 0) {
                    binding.nodataLayout.visibility = View.GONE
                    binding.rvList.visibility = View.VISIBLE
                    WeeklyDefectAdapter.data.addAll(it)
                    WeeklyDefectAdapter.notifyDataSetChanged()
                } else {
                    binding.nodataLayout.visibility = View.VISIBLE
                    binding.rvList.visibility = View.GONE
                }
            } else {
                binding.nodataLayout.visibility = View.VISIBLE
                binding.rvList.visibility = View.GONE
            }
        }

    }

}