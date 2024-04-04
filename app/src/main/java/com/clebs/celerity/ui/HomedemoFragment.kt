package com.clebs.celerity.ui


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentHomedemoBinding
import com.clebs.celerity.utils.Prefs
import ir.mahozad.android.PieChart
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class HomedemoFragment : Fragment() {
    lateinit var mbinding: FragmentHomedemoBinding
    private lateinit var viewModel: MainViewModel
    var avprofit: Float = 0.0f
    var avdeductions: Float = 0.0f
    private var isclicked: Boolean = true
    var thirdpartydeductions: Float = 0.0f
    var week: Int = 0
    var year: Int = 0
    var totalearning: String = ""
    var totaldedecutions: String = ""
    var thirdparty: String = ""
    val showDialog: () -> Unit = {
        (activity as HomeActivity).showDialog()
    }
    val hideDialog: () -> Unit = {
        (activity as HomeActivity).hideDialog()
    }
    lateinit var pieChart: PieChart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (!this::mbinding.isInitialized) {
            mbinding = FragmentHomedemoBinding.inflate(inflater, container, false)

        }

        viewModel = (activity as HomeActivity).viewModel
        pieChart = mbinding.pieChart.findViewById<PieChart>(R.id.pieChart)
//        pieChart.isAnimationEnabled=true

        showDialog()
        viewModel.GetWeekAndYear()

        val currentDate = Calendar.getInstance()

        currentDate.set(Calendar.HOUR_OF_DAY, 0)
        currentDate.set(Calendar.MINUTE, 0)
        currentDate.set(Calendar.SECOND, 0)
        currentDate.set(Calendar.MILLISECOND, 0)
        currentDate.add(Calendar.DAY_OF_YEAR, 1)

// Get tomorrow's date
        val tomorrowDate = currentDate.time
        val dateFormat = SimpleDateFormat("EEE MMM dd yyyy", Locale.US)

// Format the date

// Format the date
        val formattedDate: String = dateFormat.format(tomorrowDate)
        mbinding.textView5.text=formattedDate
        Log.e("tomotmoit", "onCreateView: "+formattedDate )

        mbinding.viewfullschedule.setOnClickListener {
            if (isclicked) {
                mbinding.viewfulldatalayout.visibility = View.VISIBLE
            } else {
                mbinding.viewfulldatalayout.visibility = View.GONE
            }
            isclicked = !isclicked


        }

        viewModel.GetAVGscore(
            Prefs.getInstance(requireContext()).userID.toInt(),
            Prefs.getInstance(requireContext()).lmid
        )

        viewModel.GetLastWeekSCore(
            Prefs.getInstance(requireContext()).userID.toInt(),
            Prefs.getInstance(requireContext()).lmid
        )



        Observers()
        mbinding.btPrev.setOnClickListener {
            mbinding.btPrev.visibility = View.GONE
            mbinding.btThisWeek.visibility = View.VISIBLE
            showDialog()
            viewModel.GetViewFullScheduleInfo(
                Prefs.getInstance(requireContext()).userID.toInt(), 0, year, week - 1
            )
            showDialog()
            viewModel.GetcashFlowWeek(
                Prefs.getInstance(requireContext()).userID.toInt(), 0, year, week - 3
            )
        }



        mbinding.btThisWeek.setOnClickListener {
            mbinding.btThisWeek.visibility = View.GONE
            mbinding.btPrev.visibility = View.VISIBLE
            showDialog()
            viewModel.GetViewFullScheduleInfo(
                Prefs.getInstance(requireContext()).userID.toInt(), 0, year, week
            )
            showDialog()
            viewModel.GetcashFlowWeek(
                Prefs.getInstance(requireContext()).userID.toInt(), 0, year, week - 2
            )

        }


        return mbinding.root

    }

    private fun Observers() {
        viewModel.livedatagetweekyear.observe(viewLifecycleOwner) {

            if (it != null) {
                week = it.weekNO
                year = it.year
                showDialog()
                viewModel.GetcashFlowWeek(
                    Prefs.getInstance(requireContext()).userID.toInt(), 0, year, week - 2
                )
                showDialog()
                viewModel.GetViewFullScheduleInfo(
                    Prefs.getInstance(requireContext()).userID.toInt(), 0, year, week
                )
                val bt_text = (week - 3).toString()
                mbinding.btPrev.text = "Week: $bt_text"


            }


        }

        viewModel.livedataAvgScoreResponse.observe(viewLifecycleOwner) {

            if (it != null) {

                if (it.status.equals("200")) {
                    Log.e("hreheyey", "Observers: " + it.avgTotalScore)
                    mbinding.ProgressBar.setProgress(it.avgTotalScore.toDouble().toInt())
                    mbinding.tvPbone.text = it.avgTotalScore.toDouble().toInt().toString() + "%"

                    mbinding.ProgressBar.tooltipText = it.avgTotalScore.toDouble().toString() + "%"
                } else {
                    mbinding.ProgressBar.setProgress(0)
                }
            } else {
                mbinding.ProgressBar.setProgress(0)
            }
        }
        viewModel.livedatalastweekresponse.observe(viewLifecycleOwner) {

            if (it != null) {
                if (it.status.equals("200")) {
                    Log.e("hreheyey", "Observers: " + it.avgTotalScore)
                    mbinding.ProgressBartwo.setProgress(it.avgTotalScore.toDouble().toInt())
                    mbinding.tvPbTwo.text = it.avgTotalScore.toDouble().toInt().toString() + "%"
                    mbinding.ProgressBartwo.tooltipText =
                        it.avgTotalScore.toDouble().toString() + "%"
                } else {
                    mbinding.ProgressBartwo.setProgress(0)
                }
            } else {
                mbinding.ProgressBartwo.setProgress(0)
            }
        }
        viewModel.livedataCashFlowWeek.observe(viewLifecycleOwner) { depts ->
hideDialog()
            mbinding.consttwo.visibility=View.VISIBLE
            if (depts != null) {
                mbinding.pieChart.visibility = View.VISIBLE
                mbinding.nodata.visibility = View.GONE
                depts.map {


                    val average = it.totalEarning + it.totalDeduction + it.charterHireDeduction
                    totalearning = it.totalEarning.toInt().toString()
                    totaldedecutions = it.totalDeduction.toDouble().toString()
                    thirdparty = it.charterHireDeduction.toDouble().toString()

                    avprofit = (it.totalEarning / average).toFloat()
                    avdeductions = it.totalDeduction / average.toFloat()
                    Log.e("djhfdfhhdhjfdjearning", "Observers: " + it.totalEarning)
                    if (it.charterHireDeduction != 0) {
                        thirdpartydeductions = it.charterHireDeduction / average.toFloat()
                    } else {
                        thirdpartydeductions = 0f
                    }
                }

                val slices = mutableListOf<PieChart.Slice>()

                slices.add(
                    PieChart.Slice(
                        thirdpartydeductions,
                        resources.getColor(R.color.red_light),
                        resources.getColor(R.color.red_light),
                        "Third Party Deductions " + thirdparty,
                        labelSize = 30f

                    )
                )
                slices.add(
                    PieChart.Slice(
                        avprofit,
                        resources.getColor(R.color.blue_hex),
                        resources.getColor(R.color.blue_hex),
                        "Profits " + totalearning,
                        labelSize = 30f
                    )
                )


                slices.add(
                    PieChart.Slice(
                        avdeductions,
                        resources.getColor(R.color.peek_orange),
                        resources.getColor(R.color.peek_orange),
                        legend = "12",
                        legendColor = resources.getColor(R.color.black),
                        label = "Deductions " + totaldedecutions,
                        labelSize = 30f,
                    )
                )

                pieChart.slices = slices

                if (thirdpartydeductions.equals(0.0f)) {
                    slices.remove(pieChart.slices[0])
                } else if (avprofit.equals(0.0f)) {
                    slices.remove(pieChart.slices[1])
                } else if (avdeductions.equals(0.0f)) {
                    slices.remove(pieChart.slices[2])
                }

                pieChart.slices = slices
                pieChart.labelType = PieChart.LabelType.OUTSIDE_CIRCULAR_OUTWARD
                pieChart.labelsColor = resources.getColor(R.color.black)
                pieChart.holeRatio = 0f
                pieChart.overlayRatio = 0f

            } else {
                hideDialog()
                mbinding.pieChart.visibility = View.GONE
                mbinding.nodata.visibility = View.VISIBLE
            }


        }

        viewModel.livedatagetvechilescheduleinfo.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {

                mbinding.viewfullschedule.isClickable = true
                mbinding.viewfullschedule.isEnabled = true
                mbinding.llnodata.visibility = View.GONE
                mbinding.rlicons.visibility=View.VISIBLE
                it.map {
                    mbinding.tvDateShow1.text = it.sundayDate
                    mbinding.tvDateShow2.text = it.mondayDate
                    mbinding.tvDateShow3.text = it.tuesdayDate
                    mbinding.tvDateShow4.text = it.wednesdayDate
                    mbinding.tvDateShow5.text = it.thursdayDate
                    mbinding.tvDateShow6.text = it.fridayDate
                    mbinding.tvDateShow7.text = it.saturdayDate
                    mbinding.tvIsWorkingShowSunday.text = it.sundayLocation
                    mbinding.tvIsWorkingShowTuesday.text = it.tuesdayLocation
                    mbinding.tvIsWorkingShowWed.text = it.wednesdayLocation
                    mbinding.tvIsWorkingShowThu.text = it.thursdayLocation
                    mbinding.tvIsWorkingShowFri.text = it.fridayLocation
                    mbinding.tvIsWorkingShowMonday.text = it.mondayLocation
                    mbinding.tvIsWorkingShowSat.text = it.saturdayLocation

                }
            } else {
                mbinding.viewfullschedule.isClickable = false
                mbinding.viewfullschedule.isEnabled = false
                mbinding.viewfulldatalayout.visibility = View.GONE
                mbinding.llnodata.visibility = View.VISIBLE
                mbinding.rlicons.visibility=View.GONE
            }

        }
    }


}