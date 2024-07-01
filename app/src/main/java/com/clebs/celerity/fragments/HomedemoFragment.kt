package com.clebs.celerity.ui


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentHomedemoBinding
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.convertDateFormat
import com.clebs.celerity.utils.roundOffValues
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import org.checkerframework.checker.units.qual.s
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
    var entries = ArrayList<PieEntry>()
    var year: Int = 0
    var weekprev = 0
    protected val months = arrayOf(
        "Jan", "Feb", "Mar"
    )

    protected val parties = arrayOf(
        "Party A", "Party B", "Party C"

    )


    var totalearning: String = ""
    var totaldedecutions: String = ""
    var thirdparty: String = ""
    var centerprofittext: String = ""
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
        (activity as HomeActivity).ActivityHomeBinding.title.text = ""
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


        mbinding.pieChart.dragDecelerationFrictionCoef = 0.95f;
        mbinding.pieChart.getDescription().setEnabled(false);
        mbinding.pieChart.setDrawCenterText(true);

//        mbinding.pieChart.isRotationEnabled = true;
        mbinding.pieChart.isHighlightPerTapEnabled = true;
        mbinding.pieChart.isDrawHoleEnabled = true
        mbinding.pieChart.holeRadius = 48f;
        mbinding.pieChart.rotationAngle = 0f;
        mbinding.pieChart.setEntryLabelColor(resources.getColor(R.color.black))
        val font = Typeface.createFromAsset(
            requireContext().assets,
            "OpenSans-Bold.ttf"
        )
        mbinding.pieChart.setEntryLabelTypeface(font)
        mbinding.pieChart.setCenterTextTypeface(font)


        mbinding.pieChart.setDrawHoleEnabled(true);
        mbinding.pieChart.setHoleColor(Color.WHITE);

        mbinding.pieChart.setTransparentCircleColor(Color.WHITE);
        mbinding.pieChart.setTransparentCircleAlpha(110);

        mbinding.pieChart.setDrawEntryLabels(true)
        mbinding.pieChart.setEntryLabelColor(Color.BLACK);
        mbinding.pieChart.setEntryLabelTypeface(font)
        mbinding.pieChart.setEntryLabelTextSize(12f);

        mbinding.fab1.setOnClickListener {
            showtooltip(
                it,
                "The Average total is the sum of all your scores divided by the number of scorecards you have received."
            )
        }
        mbinding.fab2.setOnClickListener {
            showtooltip(
                it,
                "This is the score you achieved on the weekly scorecard for the week displayed."
            )
        }

//        mbinding.viewfullschedule.setOnClickListener {
//            if (isclicked) {
////                mbinding.vieww.visibility=View.VISIBLE
//                mbinding.consttwo.visibility = View.GONE
//                mbinding.const1.visibility = View.GONE
//                mbinding.ss.fullScroll(ScrollView.FOCUS_UP);
//                mbinding.viewfulldatalayout.visibility = View.VISIBLE
//                mbinding.fullscheduleIV.visibility = View.GONE
//            } else {
//                mbinding.consttwo.visibility = View.VISIBLE
//                mbinding.const1.visibility = View.VISIBLE
//                mbinding.viewfulldatalayout.visibility = View.GONE
////                mbinding.vieww.visibility=View.GONE
//                mbinding.fullscheduleIV.visibility = View.VISIBLE
//            }
//            isclicked = !isclicked
//        }
        mbinding.fullscheduleIV.setOnClickListener {
            if (isclicked) {
//                mbinding.vieww.visibility=View.VISIBLE
                mbinding.consttwo.visibility = View.GONE
                mbinding.const1.visibility = View.GONE
                mbinding.ss.fullScroll(ScrollView.FOCUS_UP);
                mbinding.viewfulldatalayout.visibility = View.VISIBLE
                mbinding.fullscheduleIV.visibility = View.GONE
            } else {
                mbinding.consttwo.visibility = View.VISIBLE
                mbinding.const1.visibility = View.VISIBLE
                mbinding.viewfulldatalayout.visibility = View.GONE
//                mbinding.vieww.visibility=View.GONE
                mbinding.fullscheduleIV.visibility = View.VISIBLE
            }
            isclicked = !isclicked
        }
        mbinding.viewfullschedule.setOnClickListener {
            if (isclicked) {
//                mbinding.vieww.visibility=View.VISIBLE
                mbinding.consttwo.visibility = View.GONE
                mbinding.const1.visibility = View.GONE
                mbinding.ss.fullScroll(ScrollView.FOCUS_UP);
                mbinding.viewfulldatalayout.visibility = View.VISIBLE
                mbinding.fullscheduleIV.visibility = View.GONE
            } else {
                mbinding.consttwo.visibility = View.VISIBLE
                mbinding.const1.visibility = View.VISIBLE
                mbinding.viewfulldatalayout.visibility = View.GONE
//                mbinding.vieww.visibility=View.GONE
                mbinding.fullscheduleIV.visibility = View.VISIBLE
            }
            isclicked = !isclicked
        }
        mbinding.collapseArrow.setOnClickListener {
            mbinding.fullscheduleIV.visibility = View.VISIBLE
            mbinding.consttwo.visibility = View.VISIBLE
            mbinding.const1.visibility = View.VISIBLE
            mbinding.viewfulldatalayout.visibility = View.GONE
        }

        viewModel.GetAVGscore(
            Prefs.getInstance(requireContext()).clebUserId.toInt(),
            Prefs.getInstance(requireContext()).lmid
        )

        Observers()
        mbinding.btPrev.setOnClickListener {
            mbinding.viewfulldatalayout.visibility = View.GONE
            mbinding.btPrev.visibility = View.GONE
            mbinding.btPrevSecond.visibility = View.VISIBLE
            mbinding.btPrevSecond.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.baseline_keyboard_arrow_left_24,
                0,
                0,
                0
            );
            mbinding.const1.visibility = View.VISIBLE
            mbinding.btThisWeek.visibility = View.VISIBLE

            weekprev = week - 3
            mbinding.txtLastWeek.text = "Week : " + weekprev
            val weekschedule = week - 1
            mbinding.viewfullschedule.text = "Full schedule for week $weekprev"
            showDialog()
            val w = week - 3
//            val baseText = "Cash flow \n week :"+w+"\n"+ "£"+totalearning
//
//            val lastWordStart =
//                baseText.lastIndexOf(' ') + 1 // Start index of the last word
//
//            val lastWord = baseText.substring(lastWordStart) // The last word
//
//            val spannableString = SpannableString(baseText)
//
//            spannableString.setSpan(
//                RelativeSizeSpan(1.5f),  // Change this value to make the text size larger
//                lastWordStart,
//                baseText.length,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//            )

            val baseText =
                "Cash flow \n week : " + weekprev + "\n" + " £" + roundOffValues(centerprofittext)

            val lastWordStart =
                baseText.lastIndexOf(' ') + 1 // Start index of the last word

            val lastWord = baseText.substring(lastWordStart) // The last word

            val spannableString = SpannableString(baseText)

            spannableString.setSpan(
                RelativeSizeSpan(1.5f),  // Change this value to make the text size larger
                lastWordStart,
                baseText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            mbinding.pieChart.centerText = spannableString
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
        mbinding.btPrevSecond.setOnClickListener {
            mbinding.viewfulldatalayout.visibility = View.GONE
            mbinding.btPrev.visibility = View.GONE
            mbinding.btPrevSecond.visibility = View.GONE
            mbinding.const1.visibility = View.VISIBLE
            mbinding.btThisWeek.visibility = View.GONE
            mbinding.btNext.visibility = View.VISIBLE
            mbinding.btPrev.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.baseline_keyboard_arrow_right_24,
                0
            );
            weekprev = week - 4
            mbinding.txtLastWeek.text = "Week : " + weekprev
            val weekschedule = week - 2
            mbinding.viewfullschedule.text = "Full schedule for week $weekprev"
            showDialog()
            val w = week - 4
            //      mbinding.pieChart.setCenterText("Cash flow \n week :" + w+"\n"+"£"+totalearning);
            val baseText =
                "Cash flow \n week : " + weekprev + "\n" + " £" + roundOffValues(centerprofittext)

            val lastWordStart =
                baseText.lastIndexOf(' ') + 1 // Start index of the last word

            val lastWord = baseText.substring(lastWordStart) // The last word

            val spannableString = SpannableString(baseText)

            spannableString.setSpan(
                RelativeSizeSpan(1.5f),  // Change this value to make the text size larger
                lastWordStart,
                baseText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            mbinding.pieChart.centerText = spannableString
            viewModel.GetViewFullScheduleInfo(
                Prefs.getInstance(requireContext()).clebUserId.toInt(), 0, year, week - 2
            )
            showDialog()
            viewModel.GetcashFlowWeek(
                Prefs.getInstance(requireContext()).clebUserId.toInt(), 0, year, week - 4
            )
            viewModel.GetLastWeekSCore(
                Prefs.getInstance(requireContext()).clebUserId.toInt(), week - 4, year
            )
        }

        mbinding.btNext.setOnClickListener {
            mbinding.viewfulldatalayout.visibility = View.GONE
            mbinding.btPrev.visibility = View.GONE
            mbinding.btPrevSecond.visibility = View.VISIBLE

            mbinding.btPrevSecond.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.baseline_keyboard_arrow_left_24,
                0,
                0,
                0
            );
            mbinding.const1.visibility = View.VISIBLE
            mbinding.btThisWeek.visibility = View.VISIBLE
            mbinding.btNext.visibility = View.GONE
            weekprev = week - 3
            mbinding.txtLastWeek.text = "Week : " + weekprev
            val weekschedule = week - 1
            mbinding.viewfullschedule.text = "Full schedule for week $weekprev"

            showDialog()
            val w = week - 3
            val baseText =
                "Cash flow \n week : " + weekprev + "\n" + " £" + roundOffValues(centerprofittext)

            val lastWordStart =
                baseText.lastIndexOf(' ') + 1 // Start index of the last word

            val lastWord = baseText.substring(lastWordStart) // The last word

            val spannableString = SpannableString(baseText)

            spannableString.setSpan(
                RelativeSizeSpan(1.5f),  // Change this value to make the text size larger
                lastWordStart,
                baseText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            mbinding.pieChart.centerText = spannableString
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
            mbinding.const1.visibility = View.VISIBLE
            mbinding.viewfulldatalayout.visibility = View.GONE
            mbinding.btThisWeek.visibility = View.GONE
            mbinding.btPrev.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.baseline_keyboard_double_arrow_left_24,
                0,
                0,
                0
            );
            mbinding.btPrev.visibility = View.VISIBLE
            mbinding.btPrev.setText("Previous Week")
            mbinding.btPrevSecond.visibility = View.GONE
            showDialog()
            weekprev = week - 2
            val w = week - 3
            val baseText =
                "Cash flow \n week : " + weekprev + "\n" + " £" + roundOffValues(centerprofittext)

            val lastWordStart =
                baseText.lastIndexOf(' ') + 1 // Start index of the last word

            val lastWord = baseText.substring(lastWordStart) // The last word

            val spannableString = SpannableString(baseText)

            spannableString.setSpan(
                RelativeSizeSpan(1.5f),  // Change this value to make the text size larger
                lastWordStart,
                baseText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            mbinding.pieChart.centerText = spannableString
            mbinding.txtLastWeek.text = "Week : " + weekprev
            mbinding.viewfullschedule.text = "Full schedule for week " + weekprev
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

                weekprev = week - 2
                val weekprevsecond = week - 4
                mbinding.txtLastWeek.text = "Week : $weekprev"
                mbinding.viewfullschedule.text = "Full schedule for week $week"


                viewModel.GetcashFlowWeek(
                    Prefs.getInstance(requireContext()).clebUserId.toInt(), 0, year, week - 2
                )
                showDialog()
                viewModel.GetViewFullScheduleInfo(
                    Prefs.getInstance(requireContext()).clebUserId.toInt(), 0, year, week
                )

                viewModel.GetLastWeekSCore(
                    Prefs.getInstance(requireContext()).clebUserId.toInt(), week - 2, year
                )
                mbinding.btPrev.text = "Previous Week"
                mbinding.btPrevSecond.text = "Previous Week"

            }
        }

        viewModel.livedataAvgScoreResponse.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {

                if (it.status.equals("200")) {
                    Log.e("hreheyey", "Observers: " + it.avgTotalScore)
                    mbinding.ProgressBar.setProgress(it.avgTotalScore.toDouble().toInt())
                    mbinding.tvPbone.text =
                        "${it.avgTotalScore.toDouble().toInt()}%"

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
                if (it.status == "200") {
                    Log.e("hreheyey", "Observers: " + it.avgTotalScore)
                    mbinding.ProgressBartwo.setProgress(it.avgTotalScore.toDouble().toInt())
                    mbinding.tvPbTwo.text =
                        "${it.avgTotalScore.toDouble().toInt()}%"
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
                depts.map {

                    val totalEarning = it.totalEarning.toDouble()
                    val average =
                        totalEarning + it.totalDeduction + it.charterHireDeduction + it.ThirdPartyDeduction
                    totalearning = totalEarning.toString()
                    totaldedecutions = it.totalDeduction.toDouble().toString()
                    thirdparty = it.ThirdPartyDeduction.toDouble().toString()

                    avprofit = (it.totalEarning / average).toFloat()


                    val income = formatDecimal(it.totalEarning.toInt().toDouble())
                    Log.e("incomessssss", "Observers: " + income)
                    centerprofittext =
                        (it.totalEarning - it.totalDeduction - it.ThirdPartyDeduction).toString()

                    // Net Profit = Income - Deductions - Third Party


                    val w = week - 2
                    val incomes = roundOffValues(centerprofittext)
                    Log.e("Dskjfdjgffjhdhincomes", "Observers: " + incomes)
                    val baseText =
                        "Cash flow \n week : " + weekprev + "\n" + " £" + roundOffValues(
                            centerprofittext
                        )

                    val lastWordStart =
                        baseText.lastIndexOf(' ') + 1 // Start index of the last word

                    val lastWord = baseText.substring(lastWordStart) // The last word

                    val spannableString = SpannableString(baseText)

                    spannableString.setSpan(
                        RelativeSizeSpan(1.5f),  // Change this value to make the text size larger
                        lastWordStart,
                        baseText.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    mbinding.pieChart.centerText = spannableString
                    avdeductions = if (it.totalDeduction > 0)
                        it.totalDeduction / average.toFloat()
                    else
                        0f
                    Log.e("djhfdfhhdhjfdjearning", "Observers: " + avprofit)
                    thirdpartydeductions = if (it.ThirdPartyDeduction > 0) {
                        it.ThirdPartyDeduction.toInt() / average.toFloat()
                    } else {
                        0f
                    }

                }
                entries.clear();
                mbinding.pieChart.invalidate();
                mbinding.pieChart.clear();


                val ttincome = roundOffValues(totalearning)
                val ttdeductions = roundOffValues(totaldedecutions)
                val ttthirddeductions = roundOffValues(thirdparty)
                Log.e(
                    "Totallincimesssdssdsf",
                    "Observers: " + ttincome + ttdeductions + ttthirddeductions
                )

                entries.add(PieEntry(avprofit, ttincome))
                entries.add(PieEntry(avdeductions, ttdeductions))
                entries.add(PieEntry(thirdpartydeductions, ttthirddeductions))
                val dataSet = PieDataSet(entries, "")

                val colors = ArrayList<Int>()
                colors.add(resources.getColor(R.color.blue_hex))
                colors.add(resources.getColor(R.color.peek_orange))
                colors.add(resources.getColor(R.color.red_light))


                if (thirdpartydeductions.equals(0.0f)) {
                    entries.removeAt(2)
                }
                if (avdeductions.equals(0.0f)) {
                    colors[1] = resources.getColor(R.color.red_light)
                    entries.removeAt(1)
                }
                if (avprofit.equals(0.0f)) {
                    colors[0] = resources.getColor(R.color.peek_orange)
                    entries.removeAt(0)
                }



                if (avprofit.equals(0.0f) && avdeductions.equals(0.0f)) {
                    colors[0] = resources.getColor(R.color.red_light)
                    colors[1] = resources.getColor(R.color.red_light)
                }


                dataSet.colors = colors

                val data = PieData(dataSet)


                data.setValueFormatter(PercentFormatter())
//                data.setValueTextSize(11f)
                data.setValueTextColor(resources.getColor(R.color.transparent))

                val l: Legend = mbinding.pieChart.getLegend()
                l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                l.orientation = Legend.LegendOrientation.HORIZONTAL
                l.setDrawInside(false)
                l.xEntrySpace = 7f
                l.yEntrySpace = 7f
                l.yOffset = 7f
                val legendEntryA = LegendEntry()
                legendEntryA.label = "Income"
                legendEntryA.formColor = resources.getColor(R.color.blue_hex)

                val legendEntryb = LegendEntry()
                legendEntryb.label = "Deductions"
                legendEntryb.formColor = resources.getColor(R.color.peek_orange)

                val legendEntryc = LegendEntry()
                legendEntryc.label = "Third party Deductions"
                legendEntryc.formColor = resources.getColor(R.color.red_light)


                mbinding.pieChart.legend.setCustom(
                    listOf(
                        legendEntryA,
                        legendEntryb,
                        legendEntryc
                    )
                )

                mbinding.pieChart.data = data

                // undo all highlights


                mbinding.pieChart.highlightValues(null)
                mbinding.pieChart.invalidate()

//                pieChart.slices = slices
//                pieChart.labelType = PieChart.LabelType.INSIDE
//                pieChart.labelsColor = resources.getColor(R.color.black)
//                pieChart.holeRatio = 0f
//                pieChart.overlayRatio = 0f

            } else {

                hideDialog()
                entries.clear();
                mbinding.pieChart.invalidate();
                mbinding.pieChart.clear();
                mbinding.demo.visibility = View.VISIBLE
                mbinding.pieChart.setNoDataText("No cash flow data found.")
                mbinding.pieChart.setNoDataTextColor(resources.getColor(R.color.red))
//                mbinding.pieChart.setCenterTextSize(5f)
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
                mbinding.fullscheduleIV.visibility = View.VISIBLE
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
                    try {
                        /*         mbinding.textView5.text =
                                     "${convertDateFormat(it.NextWorkingDate)} - ${it.NextWorkingDay}\n${it.NextWorkingLoc}"*/
                        var time = it.NextWorkingDayWaveTime ?: " "
                        var date = it.NextWorkingDate ?: " "
                        var nextLoc = it.NextWorkingLoc ?: " "



                        if (it.NextWorkingDayWaveTime != null) {
                            mbinding.textView5.text =
                                "${convertDateFormat(date)} ${time.split(":")[0]} : ${time.split(":")[1]} - ${nextLoc}"
                        } else {
                            mbinding.textView5.text =
                                "${convertDateFormat(date)} ${time} : ${time} - ${nextLoc}"
                        }
                        if (it.NextWorkingDay.isNullOrEmpty() || it.NextWorkingDay.isNullOrEmpty()) {
                            mbinding.textView5.text = "Not allocated"
                        }


                    } catch (_: Exception) {
                        mbinding.textView5.text = "Not allocated"
                    }




                    mbinding.tvIsWorkingShowSunday.text = it.sundayLocation
                    mbinding.tvIsWorkingShowTuesday.text = it.tuesdayLocation
                    mbinding.tvIsWorkingShowWed.text = it.wednesdayLocation
                    mbinding.tvIsWorkingShowThu.text = it.thursdayLocation
                    mbinding.tvIsWorkingShowFri.text = it.fridayLocation
                    mbinding.tvIsWorkingShowMonday.text = it.mondayLocation
                    mbinding.tvIsWorkingShowSat.text = it.saturdayLocation

                    if (it.sundayLocation.equals("OFF")) {
                        mbinding.constsunday.alpha = 0.4f
                    } else {
                        mbinding.constsunday.alpha = 1f
                    }
                    if (it.mondayLocation.equals("OFF")) {
                        mbinding.constmonday.alpha = 0.4f
                    } else {
                        mbinding.constmonday.alpha = 1f
                    }
                    if (it.tuesdayLocation.equals("OFF")) {
                        mbinding.consttuesday.alpha = 0.4f
                    } else {
                        mbinding.consttuesday.alpha = 1f
                    }
                    if (it.wednesdayLocation.equals("OFF")) {
                        mbinding.constwed.alpha = 0.4f
                    } else {
                        mbinding.constwed.alpha = 1f
                    }
                    if (it.thursdayLocation.equals("OFF")) {
                        mbinding.constthu.alpha = 0.4f
                    } else {
                        mbinding.constthu.alpha = 1f
                    }
                    if (it.fridayLocation.equals("OFF")) {
                        mbinding.constfri.alpha = 0.4f
                    } else {
                        mbinding.constfri.alpha = 1f
                    }
                    if (it.saturdayLocation.equals("OFF")) {
                        mbinding.constsat.alpha = 0.4f
                    } else {
                        mbinding.constsat.alpha = 1f
                    }


                }
            } else {
                mbinding.viewfullschedule.isClickable = false
                mbinding.fullscheduleIV.visibility = View.GONE
                mbinding.viewfullschedule.isEnabled = false
                mbinding.viewfulldatalayout.visibility = View.GONE
                mbinding.viewfullschedule.visibility = View.GONE
                mbinding.llnodata.visibility = View.VISIBLE
                mbinding.rlicons.visibility = View.GONE
                mbinding.textView5.text = "Not allocated"
                mbinding.yourNext.text = "Your next working day"

            }

        }
    }

    private fun crossfade() {
        val shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        mbinding.const1.apply {

            // Set the content view to 0% opacity but visible, so that it is
            // visible but fully transparent during the animation.
            alpha = 0f
            visibility = View.VISIBLE

            // Animate the content view to 100% opacity and clear any animation
            // listener set on the view.
            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }
        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step so it doesn't
        // participate in layout passes.
        mbinding.const1.animate()
            .alpha(0f)
            .setDuration(shortAnimationDuration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mbinding.const1.visibility = View.GONE
                }
            })
        mbinding.consttwo.apply {

            // Set the content view to 0% opacity but visible, so that it is
            // visible but fully transparent during the animation.
            alpha = 0f
            visibility = View.VISIBLE

            // Animate the content view to 100% opacity and clear any animation
            // listener set on the view.
            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }
        mbinding.consttwo.animate()
            .alpha(0f)
            .setDuration(shortAnimationDuration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mbinding.consttwo.visibility = View.GONE
                }
            })
    }


    fun showtooltip(view: View, text: String) {

        val balloon = Balloon.Builder(requireContext())
            .setWidthRatio(0.5f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setWidth(BalloonSizeSpec.WRAP)
            .setText(text)
            .setTextGravity(Gravity.START)
            .setTextColorResource(R.color.black)
            .setTextSize(12f)
            .setAutoDismissDuration(4000)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowSize(10)
            .setArrowColor(ContextCompat.getColor(requireContext(), R.color.medium_orange))
            .setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            .setArrowPosition(0.5f)
            .setPadding(12)
            .setCornerRadius(8f)
            .setBackgroundColorResource(R.color.white)
            .setBalloonAnimation(BalloonAnimation.ELASTIC)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()
        balloon.showAsDropDown(view, 20, 20)
    }

    fun formatDecimal(num: Any?): String {
        return when (num) {
            is Double -> {
                if (num.toString().split(".").getOrNull(1)?.length ?: 0 > 2) {
                    "%.2f".format(num)
                } else {
                    num.toString()
                }
            }

            is Int -> num.toString()
            else -> "Input must be a number."
        }
    }
}