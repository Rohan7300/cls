package com.clebs.celerity.ui


import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentHomedemoBinding
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.convertDateFormat
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
    var entries= ArrayList<PieEntry>()
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

    @RequiresApi(Build.VERSION_CODES.Q)
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
//        mbinding.web.settings.allowContentAccess
//        mbinding.web.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
//        mbinding.web.settings.setCacheMode(WebSettings.LOAD_NO_CACHE)
//        mbinding.web.settings.setDomStorageEnabled(true)
//       mbinding.web.settings.loadsImagesAutomatically
//        //my webview is lagging
//        mbinding.web.settings.setJavaScriptEnabled(true)
//        //my web is still lagging
//        mbinding.web.setWebViewRenderProcessClient(mbinding.web.webViewRenderProcessClient)
//        mbinding.web.settings.setSupportZoom(false)
//        mbinding.web.setMixedContentAllowed(true)
//        mbinding.web.loadHtml(
//            "<html>\n" +
//                    "  <head>\n" +
//                    "    <!--Load the AJAX API-->\n" +
//                    "    <script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>\n" +
//                    "    <script type=\"text/javascript\">\n" +
//                    "    \n" +
//                    "      // Load the Visualization API and the piechart package.\n" +
//                    "      google.load('visualization', '1.0', {'packages':['corechart']});\n" +
//                    "      \n" +
//                    "      // Set a callback to run when the Google Visualization API is loaded.\n" +
//                    "      google.setOnLoadCallback(drawChart);\n" +
//                    "\n" +
//                    "\n" +
//                    "      // Callback that creates and populates a data table, \n" +
//                    "      // instantiates the pie chart, passes in the data and\n" +
//                    "      // draws it.\n" +
//                    "      function drawChart() {\n" +
//                    "\n" +
//                    "      // Create the data table.\n" +
//                    "      var data = new google.visualization.DataTable();\n" +
//                    "      data.addColumn('string', 'ITEM');\n" +
//                    "      data.addColumn('number', 'VALUE');\n" +
//                    "      data.addRows([\n" +
//                    "        ['Item 1', 30],\n" +
//                    "        ['Item 2', 11],\n" +
//                    "        ['Item 3', 12], \n" +
//                    "        ['Item 4', 50],\n" +
//                    "        ['Item 5', 10]\n" +
//                    "      ]);\n" +
//                    "\n" +
//                    "      // Set chart options\n" +
//                    "      var options = {'title':'Android-er: Load Google Charts (pie chart) with WebView',\n" +
//                    "                     'width':600,\n" +
//                    "                     'height':600};\n" +
//
//                    "\n" +
//                    "      // Instantiate and draw our chart, passing in some options.\n" +
//                    "      var chart = new google.visualization.PieChart(document.getElementById('chart_div'));\n" +
//                    "      chart.draw(data, options);\n" +
//                    "    }\n" +
//                    "    </script>\n" +
//                    "  </head>\n" +
//                    "\n" +
//                    "  <body>\n" +
//                    " <!--Div that will hold the pie chart-->\n" +
//                    "    <div id=\"chart_div\" style=\"width:400; height:600,overflow:hidden\"></div>\n" +
//                    "  </body>\n" +
//                    "</html>"
//        )

        // Load the HTML file from the assets folder


//        mbinding.pieChart.setUsePercentValues(true);
        mbinding.pieChart.description.isEnabled = false;

        mbinding.pieChart.dragDecelerationFrictionCoef = 0.95f;

        mbinding.pieChart.setDrawCenterText(true);

        mbinding.pieChart.isRotationEnabled = true;
        mbinding.pieChart.isHighlightPerTapEnabled = false;
        mbinding.pieChart.isDrawHoleEnabled = false
        mbinding.pieChart.holeRadius = 0f;
        mbinding.pieChart.rotationAngle = 0f;
        mbinding.pieChart.animateY(1400, Easing.EaseInOutQuad)
        mbinding.pieChart.setEntryLabelColor(resources.getColor(R.color.black))

        mbinding.pieChart.setEntryLabelTextSize(12f)
        val l: Legend = mbinding.pieChart.legend
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
            Prefs.getInstance(requireContext()).clebUserId.toInt(),
            Prefs.getInstance(requireContext()).lmid
        )



        Observers()
        mbinding.btPrev.setOnClickListener {
            mbinding.btPrev.visibility = View.GONE
            mbinding.btThisWeek.visibility = View.VISIBLE
            val weekprev = week - 3
            mbinding.txtLastWeek.text = "Week " + weekprev
            val weekschedule = week - 1
            mbinding.viewfullschedule.text = "Full schedule for week $weekschedule"
            showDialog()
            viewModel.GetViewFullScheduleInfo(
                Prefs.getInstance(requireContext()).clebUserId.toInt(), 0, year, week - 1
            )
            showDialog()
            viewModel.GetcashFlowWeek(
                Prefs.getInstance(requireContext()).clebUserId.toInt(), 0, year, week - 3
            )
            viewModel.GetLastWeekSCore(
                Prefs.getInstance(requireContext()).clebUserId.toInt(), week - 3, year
            )
        }



        mbinding.btThisWeek.setOnClickListener {
            mbinding.btThisWeek.visibility = View.GONE
            mbinding.btPrev.visibility = View.VISIBLE
            showDialog()
            val weekprev = week - 2
            mbinding.txtLastWeek.text = "Week " + weekprev
            mbinding.viewfullschedule.text = "Full schedule for week " + week
            viewModel.GetViewFullScheduleInfo(
                Prefs.getInstance(requireContext()).clebUserId.toInt(), 0, year, week
            )
            showDialog()
            viewModel.GetcashFlowWeek(
                Prefs.getInstance(requireContext()).clebUserId.toInt(), 0, year, week - 2
            )
            viewModel.GetLastWeekSCore(
                Prefs.getInstance(requireContext()).clebUserId.toInt(), week - 2, year
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

                val weekprev = week - 2
                mbinding.txtLastWeek.text = "Week $weekprev"
                mbinding.viewfullschedule.text = "Full schedule for week $week"


                viewModel.GetcashFlowWeek(
                    Prefs.getInstance(requireContext()).clebUserId.toInt(), 0, year, week-2
                )
                showDialog()
                viewModel.GetViewFullScheduleInfo(
                    Prefs.getInstance(requireContext()).clebUserId.toInt(), 0, year, week
                )

                viewModel.GetLastWeekSCore(
                    Prefs.getInstance(requireContext()).clebUserId.toInt(), week - 2, year
                )
                mbinding.btPrev.text = "Previous"
            }
        }

        viewModel.livedataAvgScoreResponse.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {

                if (it.status.equals("200")) {
                    Log.e("hreheyey", "Observers: " + it.avgTotalScore)
                    mbinding.ProgressBar.setProgress(it.avgTotalScore.toDouble().toInt())
                    mbinding.tvPbone.text =
                        "${it.avgTotalScore.toDouble().toInt()}% Score"

                    mbinding.ProgressBar.tooltipText = it.avgTotalScore.toDouble().toString() + "%"
                } else {
                    mbinding.ProgressBar.setProgress(0)
                    mbinding.tvPbone.setText("0% Score")
                }
            } else {
                mbinding.ProgressBar.setProgress(0)
                mbinding.tvPbone.setText("0% Score")
            }
        }
        viewModel.livedatalastweekresponse.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                if (it.status == "200") {
                    Log.e("hreheyey", "Observers: " + it.avgTotalScore)
                    mbinding.ProgressBartwo.setProgress(it.avgTotalScore.toDouble().toInt())
                    mbinding.tvPbTwo.text =
                        "${it.avgTotalScore.toDouble().toInt()}% Score"
                    mbinding.ProgressBartwo.tooltipText =
                        it.avgTotalScore.toDouble().toString() + "%"
                } else {
                    mbinding.ProgressBartwo.setProgress(0)
                }
            } else {
                mbinding.ProgressBartwo.setProgress(0)
                mbinding.tvPbTwo.setText("0%" + " Score")
            }
        }
        viewModel.livedataCashFlowWeek.observe(viewLifecycleOwner) { depts ->
            hideDialog()

            mbinding.consttwo.visibility = View.VISIBLE
            if (depts != null) {
                mbinding.demo.visibility = View.GONE
                mbinding.pieChart.visibility = View.VISIBLE
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
                entries.clear();
                mbinding.pieChart.invalidate();
                mbinding.pieChart.clear();

                entries.add(PieEntry(avprofit, "Profits\n $totalearning"))
                entries.add(PieEntry(avdeductions, "Deductions\n $totaldedecutions"))
                entries.add(
                    PieEntry(
                        thirdpartydeductions,
                        "3rd party Deductions\n $thirdparty"
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

                mbinding.pieChart.data=data

                // undo all highlights
                mbinding.pieChart.highlightValues(null)
                mbinding.pieChart.invalidate()


                if (thirdpartydeductions.equals(0.0f)) {
                    entries.removeAt(2)
//                    slices.remove(pieChart.slices[0])
                }
                if (avprofit.equals(0.0f)) {
                    entries.removeAt(0)
//                    slices.remove(pieChart.slices[1])
                }
                if (avdeductions.equals(0.0f)) {
                    entries.removeAt(1)
//                    slices.remove(pieChart.slices[2])
                }

//                pieChart.slices = slices
//                pieChart.labelType = PieChart.LabelType.INSIDE
//                pieChart.labelsColor = resources.getColor(R.color.black)
//                pieChart.holeRatio = 0f
//                pieChart.overlayRatio = 0f

            }
            else {
                hideDialog()
                mbinding.demo.visibility = View.VISIBLE
                mbinding.pieChart.setNoDataText("No cash flow data found.")
                mbinding.pieChart.setNoDataTextColor(resources.getColor(R.color.red))
                mbinding.pieChart.setCenterTextSize(5f)
                mbinding.pieChart.visibility = View.VISIBLE

            }
        }

        viewModel.livedatagetvechilescheduleinfo.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                val currentDate = Calendar.getInstance()

                currentDate.set(Calendar.HOUR_OF_DAY, 0)
                currentDate.set(Calendar.MINUTE, 0)
                currentDate.set(Calendar.SECOND, 0)
                currentDate.set(Calendar.MILLISECOND, 0)
                currentDate.add(Calendar.DAY_OF_YEAR, 1)

                val tomorrowDate = currentDate.time
                val dateFormat = SimpleDateFormat("EEE MMM dd yyyy", Locale.US)


                val formattedDatetwo: String = dateFormat.format(tomorrowDate)


//                Log.e("tomotmoit", "onCreateView: " + formattedDate)
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


//                    if (newformatteddate.equals(formattedDate) && !it.sundayLocation.equals("OFF")) {
//
//                    }
//
                    mbinding.viewfullschedule.visibility = View.VISIBLE
                    mbinding.yourNext.text = "Your next working day"
                    try{
                        /*         mbinding.textView5.text =
                                     "${convertDateFormat(it.NextWorkingDate)} - ${it.NextWorkingDay}\n${it.NextWorkingLoc}"*/
                        var time = it.NextWorkingDayWaveTime?:" "
                        var date = it.NextWorkingDate?:" "
                        var nextLoc = it.NextWorkingLoc?:" "
                        mbinding.textView5.text =
                            "${convertDateFormat(date)} ${time.split(":")[0]} : ${time.split(":")[1]} - ${nextLoc}"

                    }catch (_:Exception){
                        mbinding.textView5.text = "Not allocated"
                    }



                    mbinding.tvIsWorkingShowSunday.text = it.sundayLocation
                    mbinding.tvIsWorkingShowTuesday.text = it.tuesdayLocation
                    mbinding.tvIsWorkingShowWed.text = it.wednesdayLocation
                    mbinding.tvIsWorkingShowThu.text = it.thursdayLocation
                    mbinding.tvIsWorkingShowFri.text = it.fridayLocation
                    mbinding.tvIsWorkingShowMonday.text = it.mondayLocation
                    mbinding.tvIsWorkingShowSat.text = it.saturdayLocation

                }
            }
            else {
                mbinding.viewfullschedule.isClickable = false
                mbinding.viewfullschedule.isEnabled = false
                mbinding.viewfulldatalayout.visibility = View.GONE
                mbinding.viewfullschedule.visibility = View.GONE
                mbinding.llnodata.visibility = View.VISIBLE
                mbinding.rlicons.visibility = View.GONE
                mbinding.textView5.text = "Not allocated"

            }

        }
    }


}