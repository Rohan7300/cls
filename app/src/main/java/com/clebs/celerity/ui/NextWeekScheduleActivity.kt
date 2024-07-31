package com.clebs.celerity.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.ActivityNextWeekScheduleBinding
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.convertToDate
import com.clebs.celerity.utils.convertToTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NextWeekScheduleActivity : AppCompatActivity() {
    lateinit var binding: ActivityNextWeekScheduleBinding
    lateinit var vm: MainViewModel
    lateinit var prefs: Prefs
    lateinit var loadingDialog: LoadingDialog
    var nxtWeek = 0
    var crrYear = 0
    var isLoaded: Boolean = false
    var i = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_next_week_schedule)
        vm = DependencyProvider.getMainVM(this)
        prefs = Prefs.getInstance(this)
        loadingDialog = LoadingDialog(this)
        binding.backIcon.setOnClickListener {
            finish()
        }
        binding.prev.visibility = View.GONE
        binding.prev.setOnClickListener {
            if (isLoaded) {
                if (i > 0) {
                    i--
                    loadingDialog.show()
                    isLoaded = false
                    vm.GetViewFullScheduleInfo(
                        prefs.clebUserId.toInt(), 0, crrYear, nxtWeek + i
                    )
                    binding.prev.visibility = View.GONE
                    binding.next.visibility = View.VISIBLE
                }
                binding.weekNoTV.text = "Week ${nxtWeek + i}"
            }
        }
        binding.next.setOnClickListener {
            if (isLoaded) {
                if (i < 1) {
                    i++
                    isLoaded = false
                    loadingDialog.show()
                    vm.GetViewFullScheduleInfo(
                        prefs.clebUserId.toInt(), 0, crrYear, nxtWeek + i
                    )
                    binding.prev.visibility = View.VISIBLE
                    binding.next.visibility = View.GONE
                }
                binding.weekNoTV.text = "Week ${nxtWeek + i}"
            }
        }
        vm.GetWeekAndYear()
        loadingDialog.show()
        vm.livedatagetweekyear.observe(this) {
            if (it != null) {
                nxtWeek = it.weekNO + 1
                crrYear = it.year
                binding.weekNoTV.text = "Week $nxtWeek"
                binding.yourNext.text = "Working day for week : $nxtWeek"
                vm.GetViewFullScheduleInfo(
                    prefs.clebUserId.toInt(), 0, crrYear, nxtWeek
                )
            }
        }

        vm.livedatagetvechilescheduleinfo.observe(this) {
            isLoaded = true
            loadingDialog.dismiss()
            if (it != null) {

                binding.viewfulldatalayout.visibility = View.VISIBLE
                binding.llnodata.visibility = View.GONE
                binding.rlicons.visibility = View.VISIBLE
                val currentDate = Calendar.getInstance()

                currentDate.set(Calendar.HOUR_OF_DAY, 0)
                currentDate.set(Calendar.MINUTE, 0)
                currentDate.set(Calendar.SECOND, 0)
                currentDate.set(Calendar.MILLISECOND, 0)
                currentDate.add(Calendar.DAY_OF_YEAR, 1)


                it.map {

                    val formattedDate = convertToDate(it.sundayDate)
                    binding.tvDateShow1.text = "SUN   $formattedDate"
                    val formattedDate2 = convertToDate(it.mondayDate)
                    binding.tvDateShow2.text = "MON   $formattedDate2"

                    val formattedDate3 = convertToDate(it.tuesdayDate)
                    binding.tvDateShow3.text = "TUE   " + formattedDate3


                    val formattedDate4 = convertToDate(it.wednesdayDate)
                    binding.tvDateShow4.text = "WED  " + formattedDate4

                    val formattedDate5 = convertToDate(it.thursdayDate)

                    binding.tvDateShow5.text = "THU   " + formattedDate5

                    val formattedDate6 = convertToDate(it.fridayDate)

                    binding.tvDateShow6.text = "FRI   " + formattedDate6

                    val formattedDate7 = convertToDate(it.saturdayDate)
                    binding.tvDateShow7.text = "SAT   " + formattedDate7

                    binding.yourNext.text = "Working day for week : ${nxtWeek + i}"
                    try {

                        var time = it.NextWorkingDayWaveTime ?: " "
                        var date = it.NextWorkingDate ?: " "
                        var nextLoc = it.NextWorkingLoc ?: " "



                        if (it.NextWorkingDayWaveTime != null) {
                            binding.textViewDate.visibility = View.VISIBLE
                            binding.textViewLocation.visibility = View.VISIBLE
                            binding.textViewDate.text = "${convertToDate(date)}"

                            binding.textViewTime.text =
                                "${convertToTime(it.NextWorkingDayWaveTime)}"
                            binding.textViewLocation.text = "${nextLoc}"
                        } else {
                            binding.textViewDate.visibility = View.VISIBLE
                            binding.textViewLocation.visibility = View.VISIBLE
                            binding.textViewDate.text = "${convertToDate(date)}"
                            binding.textViewTime.text = "-- : --"
                            binding.textViewLocation.text = "${nextLoc}"
                        }
                        if (it.NextWorkingDay.isNullOrEmpty() || it.NextWorkingDay.isNullOrEmpty()) {
                            binding.textViewDate.visibility = View.GONE
                            binding.textViewLocation.visibility = View.GONE
                            binding.textViewTime.text = "Not allocated"
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.d("ExceptionHomeDemo", e.printStackTrace().toString())
                        binding.textViewDate.visibility = View.GONE
                        binding.textViewLocation.visibility = View.GONE
                        binding.textViewTime.text = "Not allocated"
                    }
                    binding.tvIsWorkingShowSunday.text = it.sundayLocation
                    binding.tvIsWorkingShowTuesday.text = it.tuesdayLocation
                    binding.tvIsWorkingShowWed.text = it.wednesdayLocation
                    binding.tvIsWorkingShowThu.text = it.thursdayLocation
                    binding.tvIsWorkingShowFri.text = it.fridayLocation
                    binding.tvIsWorkingShowMonday.text = it.mondayLocation
                    binding.tvIsWorkingShowSat.text = it.saturdayLocation

                    if (it.sundayLocation.equals("OFF")||it.sundayLocation.equals("7th Day Off")) {
                        binding.constsunday.alpha = 0.4f
                    } else {
                        binding.constsunday.alpha = 1f
                    }
                    if (it.mondayLocation.equals("OFF")||it.mondayLocation.equals("7th Day Off")) {
                        binding.constmonday.alpha = 0.4f
                    } else {
                        binding.constmonday.alpha = 1f
                    }
                    if (it.tuesdayLocation.equals("OFF")||it.tuesdayLocation.equals("7th Day Off")) {
                        binding.consttuesday.alpha = 0.4f
                    } else {
                        binding.consttuesday.alpha = 1f
                    }
                    if (it.wednesdayLocation.equals("OFF")||it.wednesdayLocation.equals("7th Day Off")) {
                        binding.constwed.alpha = 0.4f
                    } else {
                        binding.constwed.alpha = 1f
                    }
                    if (it.thursdayLocation.equals("OFF")||it.thursdayLocation.equals("7th Day Off")) {
                        binding.constthu.alpha = 0.4f
                    } else {
                        binding.constthu.alpha = 1f
                    }
                    if (it.fridayLocation.equals("OFF")||it.fridayLocation.equals("7th Day Off")) {
                        binding.constfri.alpha = 0.4f
                    } else {
                        binding.constfri.alpha = 1f
                    }
                    if (it.saturdayLocation.equals("OFF")||it.saturdayLocation.equals("7th Day Off")) {
                        binding.constsat.alpha = 0.4f
                    } else {
                        binding.constsat.alpha = 1f
                    }


                }
            } else {
                binding.viewfulldatalayout.visibility = View.GONE
                binding.llnodata.visibility = View.VISIBLE
                binding.rlicons.visibility = View.GONE
                binding.textViewDate.visibility = View.GONE
                binding.textViewLocation.visibility = View.GONE
                binding.textViewTime.text = "Not allocated"
                binding.yourNext.text = "Working day for week :${nxtWeek + i}"

            }

        }
    }
}