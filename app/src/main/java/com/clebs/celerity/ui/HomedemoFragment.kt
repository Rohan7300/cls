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
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
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

    protected val months = arrayOf(
        "Jan", "Feb", "Mar"
    )

    protected val parties = arrayOf(
        "Party A", "Party B", "Party C"

    )


    var totalearning: String = ""
    var totaldedecutions: String = ""
    var thirdparty: String = ""
    val showDialog: () -> Unit = {
        (activity as HomeActivity).showDialog()
    }
    val hideDialog: () -> Unit = {
        (activity as HomeActivity).hideDialog()
    }

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

//        pieChart.isAnimationEnabled=true

        showDialog()
//            mbinding.constmains.visibility=View.GONE


        viewModel.GetWeekAndYear()

        val currentDate = Calendar.getInstance()

        currentDate.set(Calendar.HOUR_OF_DAY, 0)
        currentDate.set(Calendar.MINUTE, 0)
        currentDate.set(Calendar.SECOND, 0)
        currentDate.set(Calendar.MILLISECOND, 0)
        currentDate.add(Calendar.DAY_OF_YEAR, 1)

        val tomorrowDate = currentDate.time
        val dateFormat = SimpleDateFormat("EEE MMM dd yyyy", Locale.US)

        val formattedDate: String = dateFormat.format(tomorrowDate)
        mbinding.textView5.text = formattedDate
        Log.e("tomotmoit", "onCreateView: " + formattedDate)
//        mbinding.pieChart.setUsePercentValues(true);
        mbinding.pieChart.getDescription().setEnabled(false);


        mbinding.pieChart.setDragDecelerationFrictionCoef(0.95f);

//
//
        mbinding.pieChart.setDrawCenterText(false);

        mbinding.pieChart.setRotationEnabled(true);
        mbinding.pieChart.setHighlightPerTapEnabled(false);
        mbinding.pieChart.isDrawHoleEnabled = false
        mbinding.pieChart.setHoleRadius(0f);
        mbinding.pieChart.setRotationAngle(0f);
        mbinding.pieChart.animateY(1400, Easing.EaseInOutQuad)
        mbinding.pieChart.setEntryLabelColor(resources.getColor(R.color.black))

        mbinding.pieChart.setEntryLabelTextSize(12f)
        val l: Legend = mbinding.pieChart.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f




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
            viewModel.GetLastWeekSCore(
                Prefs.getInstance(requireContext()).userID.toInt(), week - 3, year
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
            viewModel.GetLastWeekSCore(
                Prefs.getInstance(requireContext()).userID.toInt(), week - 2, year
            )

        }


        return mbinding.root

    }

    private fun Observers() {
        viewModel.livedatagetweekyear.observe(viewLifecycleOwner) {
            hideDialog()
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

                viewModel.GetLastWeekSCore(
                    Prefs.getInstance(requireContext()).userID.toInt(), week - 2, year
                )


                val bt_text = (week - 2).toString()
                mbinding.btPrev.text = "Load Week: $bt_text" + " data"


            }


        }

        viewModel.livedataAvgScoreResponse.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {

                if (it.status.equals("200")) {
                    Log.e("hreheyey", "Observers: " + it.avgTotalScore)
                    mbinding.ProgressBar.setProgress(it.avgTotalScore.toDouble().toInt())
                    mbinding.tvPbone.text = it.avgTotalScore.toDouble().toInt().toString() + "%"

                    mbinding.ProgressBar.tooltipText = it.avgTotalScore.toDouble().toString() + "%"
                } else {
                    mbinding.ProgressBar.setProgress(0)
                    mbinding.tvPbone.setText("0%")
                }
            } else {
                mbinding.ProgressBar.setProgress(0)
                mbinding.tvPbone.setText("0%")
            }
        }
        viewModel.livedatalastweekresponse.observe(viewLifecycleOwner) {
            hideDialog()
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
                mbinding.tvPbTwo.setText("0%")
            }
        }
        viewModel.livedataCashFlowWeek.observe(viewLifecycleOwner) { depts ->
            hideDialog()
            mbinding.consttwo.visibility = View.VISIBLE
            if (depts != null) {
                mbinding.demo.visibility = View.GONE
                mbinding.pieChart.visibility = View.VISIBLE
//                mbinding.nodata.visibility = View.GONE
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
                val entries = ArrayList<PieEntry>()

                // NOTE: The order of the entries when being added to the entries array determines their position around the center of
                // the chart.
                entries.add(PieEntry(avprofit, "Profits" + "\n" + totalearning))

                entries.add(PieEntry(avdeductions, "Deductions" + "\n" + totaldedecutions))
                entries.add(
                    PieEntry(
                        thirdpartydeductions,
                        "3rd party Deductions" + "\n" + thirdparty
                    )
                )
                val dataSet = PieDataSet(entries, "")
//
                val colors = ArrayList<Int>()
                colors.add(resources.getColor(R.color.blue_hex))
                colors.add(resources.getColor(R.color.peek_orange))
                colors.add(resources.getColor(R.color.red_light))

                dataSet.colors = colors
                dataSet.sliceSpace = 3f
                dataSet.iconsOffset = MPPointF(0f, 40f)
                dataSet.selectionShift = 5f
                val data = PieData(dataSet)


                data.setValueFormatter(PercentFormatter())
                data.setValueTextSize(11f)
                data.setValueTextColor(resources.getColor(io.clearquote.assessment.cq_sdk.R.color.transparent))

                mbinding.pieChart.setData(data)

                // undo all highlights
                mbinding.pieChart.highlightValues(null)
                mbinding.pieChart.invalidate()
//                val slices = mutableListOf<PieChart.Slice>()
//
//                slices.add(
//                    PieChart.Slice(
//                        thirdpartydeductions,
//                        resources.getColor(R.color.red_light),
//                        resources.getColor(R.color.red_light),
//                        "Third Party Deductions " + thirdparty,
//                        labelSize = 30f
//
//                    )
//                )
//                slices.add(
//                    PieChart.Slice(
//                        avprofit,
//                        resources.getColor(R.color.blue_hex),
//                        resources.getColor(R.color.blue_hex),
//                        "Profits " + totalearning,
//                        labelSize = 30f
//                    )
//                )
//
//
//                slices.add(
//                    PieChart.Slice(
//                        avdeductions,
//                        resources.getColor(R.color.peek_orange),
//                        resources.getColor(R.color.peek_orange),
//                        legend = "12",
//                        legendColor = resources.getColor(R.color.black),
//                        label = "Deductions " + totaldedecutions,
//                        labelSize = 30f,
//                    )
//                )
//
//                pieChart.slices = slices

                if (thirdpartydeductions.equals(0.0f)) {
                    entries.removeAt(2)
//                    slices.remove(pieChart.slices[0])
                } else if (avprofit.equals(0.0f)) {
                    entries.removeAt(0)
//                    slices.remove(pieChart.slices[1])
                } else if (avdeductions.equals(0.0f)) {
                    entries.removeAt(1)
//                    slices.remove(pieChart.slices[2])
                }

//                pieChart.slices = slices
//                pieChart.labelType = PieChart.LabelType.INSIDE
//                pieChart.labelsColor = resources.getColor(R.color.black)
//                pieChart.holeRatio = 0f
//                pieChart.overlayRatio = 0f

            } else {
                hideDialog()
                mbinding.demo.visibility = View.VISIBLE
                mbinding.pieChart.setNoDataText("No cash flow data found!")
                mbinding.pieChart.setNoDataTextColor(resources.getColor(R.color.red))
                mbinding.pieChart.setCenterTextSize(1.0f)
                mbinding.pieChart.visibility = View.VISIBLE

            }


        }

        viewModel.livedatagetvechilescheduleinfo.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {

                mbinding.viewfullschedule.isClickable = true
                mbinding.viewfullschedule.isEnabled = true
                mbinding.llnodata.visibility = View.GONE
                mbinding.rlicons.visibility = View.VISIBLE
                it.map {


                    val formattedDate = convertDateFormat(it.sundayDate)
                    mbinding.tvDateShow1.text = "SUN $formattedDate"
                    val formattedDate2 = convertDateFormat(it.mondayDate)
                    mbinding.tvDateShow2.text = "MON $formattedDate2"

                    val formattedDate3 = convertDateFormat(it.tuesdayDate)
                    mbinding.tvDateShow3.text = "TUE " + formattedDate3


                    val formattedDate4 = convertDateFormat(it.wednesdayDate)
                    mbinding.tvDateShow4.text = "WED " + formattedDate4

                    val formattedDate5 = convertDateFormat(it.thursdayDate)

                    mbinding.tvDateShow5.text = "THU " + formattedDate5

                    val formattedDate6 = convertDateFormat(it.fridayDate)

                    mbinding.tvDateShow6.text = "FRI " + formattedDate6

                    val formattedDate7 = convertDateFormat(it.saturdayDate)
                    mbinding.tvDateShow7.text = "SAT " + formattedDate7
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
                mbinding.rlicons.visibility = View.GONE
            }

        }
    }

    fun convertDateFormat(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    }

}