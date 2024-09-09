package com.clebs.celerity.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.TableViewAdapter
import com.clebs.celerity.databinding.ActivityWeeklyPerformanceBinding
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.models.MovieModel
import com.clebs.celerity.models.RewardsModel
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.Prefs
import java.time.Year
import kotlin.math.abs

class WeeklyPerformanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeeklyPerformanceBinding
    private lateinit var tableViewAdapter: TableViewAdapter
    private lateinit var vm: MainViewModel
    lateinit var prefs: Prefs
    private var crrWeek = 0
    private var crrYear = 0
    var week = 0
    private var isLoaded: Boolean = false
    var i = 0
    lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(
                this@WeeklyPerformanceActivity,
                R.layout.activity_weekly_performance
            )
        vm = DependencyProvider.getMainVM(this)
        prefs = Prefs.getInstance(this)
        var dataList = arrayListOf<RewardsModel>()
        loadingDialog = LoadingDialog(this)
        binding.placeHolderViewLeft.visibility = View.GONE
        binding.placeHolderViewRight.visibility = View.GONE
        tableViewAdapter = TableViewAdapter(this, dataList)
        binding.rewardsRV.adapter = tableViewAdapter
        binding.rewardsRV.layoutManager = LinearLayoutManager(this)

        binding.back.setOnClickListener {
            finish()
        }
        binding.prev.setOnClickListener {
            if (isLoaded) {
                if (crrWeek - i > 0) {
                    i++
                    loadingDialog.show()
                    isLoaded = false
                    vm.GetDriverWeeklyRewardsInfo(prefs.clebUserId.toInt(), 0, crrYear, crrWeek - i)
                    if (crrWeek - i <= 0) {
                        binding.prev.visibility = View.GONE
                        binding.placeHolderViewLeft.visibility = View.VISIBLE
                        binding.placeHolderViewRight.visibility = View.GONE
                        binding.next.visibility = View.VISIBLE
                    } else {
                        binding.prev.visibility = View.VISIBLE
                        binding.next.visibility = View.VISIBLE
                        binding.placeHolderViewLeft.visibility = View.GONE
                        binding.placeHolderViewRight.visibility = View.GONE
                    }
                }
                binding.weekNoTV.text = "Week  ${crrWeek - i}"
            }
        }
        binding.next.setOnClickListener {
            if (isLoaded) {
                if (crrWeek - i < week) {
                    if (i > 0)
                        i--
                    isLoaded = false
                    loadingDialog.show()
                    vm.GetDriverWeeklyRewardsInfo(prefs.clebUserId.toInt(), 0, crrYear, crrWeek - i)

                    if (crrWeek - i >= week) {
                        binding.prev.visibility = View.VISIBLE
                        binding.next.visibility = View.GONE
                        binding.placeHolderViewLeft.visibility = View.GONE
                        binding.placeHolderViewRight.visibility = View.VISIBLE
                    } else {
                        binding.prev.visibility = View.VISIBLE
                        binding.next.visibility = View.VISIBLE
                        binding.placeHolderViewLeft.visibility = View.GONE
                        binding.placeHolderViewRight.visibility = View.GONE
                    }
                } else {
                    binding.prev.visibility = View.VISIBLE
                    binding.next.visibility = View.GONE
                    binding.placeHolderViewLeft.visibility = View.GONE
                    binding.placeHolderViewRight.visibility = View.VISIBLE
                }
                binding.weekNoTV.text = "Week  ${crrWeek - i}"
            }
        }
        loadingDialog.show()
        vm.GetWeekAndYear()
        vm.livedatagetweekyear.observe(this) {
            isLoaded = false
            if (it != null) {
                week = it.weekNO
                crrWeek = it.weekNO
                showWeekPickerNew()
                if (crrWeek > 0) {
                    binding.prev.visibility = View.VISIBLE
                    binding.next.visibility = View.GONE
                    binding.placeHolderViewLeft.visibility = View.GONE
                    binding.placeHolderViewRight.visibility = View.VISIBLE
                } else {
                    binding.prev.visibility = View.GONE
                    binding.next.visibility = View.GONE
                    binding.placeHolderViewLeft.visibility = View.VISIBLE
                    binding.placeHolderViewRight.visibility = View.VISIBLE
                }
                crrYear = it.year
                binding.weekNoTV.text = "Week  $crrWeek"
                binding.weekNoTV.text = "Week  $crrWeek"
                vm.GetDriverWeeklyRewardsInfo(prefs.clebUserId.toInt(), 0, crrYear, crrWeek)
            }
        }

        vm.liveDataDriverWeeklyRewardsInfo.observe(this) {
            loadingDialog.dismiss()
            isLoaded = true
            if (it != null) {
                binding.nodataLayout.visibility = View.GONE
                binding.mainLayout.visibility = View.VISIBLE
                dataList.clear()
                dataList.add(RewardsModel("Week", it[0].RewardWeek.toString()))
                dataList.add(RewardsModel("Transporter ID", it[0].TransporterID.toString()))

                dataList.add(RewardsModel("Driver", it[0].DriverName.toString()))
                dataList.add(RewardsModel("Location", it[0].LocationName.toString()))
                dataList.add(RewardsModel("Status", it[0].StatusName.toString()))
                dataList.add(RewardsModel("Total Score", it[0].RewardTotalScore.toString()))
                dataList.add(RewardsModel("Delivered", it[0].RewardDelivered.toString()))
                dataList.add(RewardsModel("DCR", it[0].RewardDCR.toString()+"%"))
                dataList.add(
                    RewardsModel(
                        "Concessions (DNR DPMO)",
                        it[0].RewardConcessions.toString()
                    )
                )
                dataList.add(RewardsModel("POD", it[0].RewardPOD.toString()+"%"))
                dataList.add(RewardsModel("CC", it[0].RewardCC.toString()+"%"))
                if (it[0].RewardSC != null)
                    dataList.add(RewardsModel("Reward SC", it[0].RewardSC.toString()))
                else
                    dataList.add(RewardsModel("Reward SC", "-"))
                dataList.add(RewardsModel("PHR", it[0].RewardPHR.toString()+"%"))
                dataList.add(RewardsModel("CE", it[0].RewardCE.toString()))
                if (it[0].RewardDEX != null)
                    dataList.add(RewardsModel("DEX", it[0].RewardDEX.toString()+"%"))
                else
                    dataList.add(RewardsModel("DEX", "-"))
                dataList.add(RewardsModel("Focus Area", it[0].RewardFocusArea.toString()))
                tableViewAdapter.rewardList = dataList
                tableViewAdapter.notifyDataSetChanged()
            } else {
                binding.nodataLayout.visibility = View.VISIBLE
                binding.mainLayout.visibility = View.GONE
            }
        }
    }

    private fun showWeekPickerNew() {
        val itemsList = mutableListOf<Int>()
        val currentYear = week
        for (wk in 1..currentYear)
            itemsList.add(wk)
        val adapter =
            ArrayAdapter(this, R.layout.dropdown_menu_popup_item, itemsList.reversed())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.selectWeekET.setAdapter(adapter)
        binding.selectWeekET.setOnItemClickListener { parent, view, position, id ->
            run {

                parent?.let { nonNullParent ->
                    var selectedWeek = 1
                     selectedWeek = nonNullParent.getItemAtPosition(position) as Int

                    i = abs(selectedWeek - crrWeek)
                    if (crrWeek - i >= week) {
                        binding.prev.visibility = View.VISIBLE
                        binding.next.visibility = View.GONE
                        binding.placeHolderViewLeft.visibility = View.GONE
                        binding.placeHolderViewRight.visibility = View.VISIBLE
                    }
                    else if (crrWeek - i <= 0) {
                        binding.prev.visibility = View.GONE
                        binding.placeHolderViewLeft.visibility = View.VISIBLE
                        binding.placeHolderViewRight.visibility = View.GONE
                        binding.next.visibility = View.VISIBLE
                    }else {
                        binding.prev.visibility = View.VISIBLE
                        binding.next.visibility = View.VISIBLE
                        binding.placeHolderViewLeft.visibility = View.GONE
                        binding.placeHolderViewRight.visibility = View.GONE
                    }
                    binding.weekNoTV.text = "Week  $selectedWeek"
                    loadingDialog.show()
                    isLoaded = false
                    vm.GetDriverWeeklyRewardsInfo(prefs.clebUserId.toInt(), 0, crrYear, crrWeek-i)
                }
            }
        }
    }
}