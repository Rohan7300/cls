package com.clebs.celerity.ui


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.clebs.celerity.R
import com.clebs.celerity.databinding.FragmentHomedemoBinding
import ir.mahozad.android.PieChart
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomedemoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomedemoFragment : Fragment() {
    lateinit var mbinding: FragmentHomedemoBinding
    val urlGoogleChart = "http://chart.apis.google.com/chart"
    val urlp3Api = "?cht=p3&chs=400x150&chl=A|B|C&chd=t:"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::mbinding.isInitialized) {
            mbinding = FragmentHomedemoBinding.inflate(inflater, container, false)

        }

        val pieChart = mbinding.pieChart.findViewById<PieChart>(R.id.pieChart)
        pieChart.isAnimationEnabled = true


        pieChart.apply {
            slices = listOf(
                PieChart.Slice(
                    0.3f,
                    resources.getColor(R.color.blue_hex), resources.getColor(R.color.blue_hex),
                    legend = "Profits", label = "Profits"
                ),
                PieChart.Slice(
                    0.2f,
                    resources.getColor(R.color.red_light), resources.getColor(R.color.red_light),
                    legend = "3rd party deductions",label = "3rd party deductions"
                ),
                PieChart.Slice(
                    0.5f,
                    resources.getColor(R.color.peek_orange),
                    resources.getColor(R.color.peek_orange),
                    legend = "Deductions",label = "Deductions"
                ),

                )

//            gradientType = PieChart.GradientType.RADIAL
//            labelIconsTint = Color.rgb(136, 101, 206)
//            startAngle = -90
//            isLegendEnabled = false

            labelType = PieChart.LabelType.OUTSIDE_CIRCULAR_OUTWARD
            labelsColor=resources.getColor(R.color.orange)

//            isLegendEnabled=true
//            isLegendsPercentageEnabled=true
//            legendIconsMargin =10.dp
//            legendTitleMargin = 14.dp
//            legendLinesMargin = 10.dp
//            legendsMargin = 20.dp
//            legendsPercentageMargin = 8.dp
//            legendsSize = 11.sp
//            legendsPercentageSize = 11.sp

//            labelIconsPlacement = PieChart.IconPlacement.TOP
            gradientType = PieChart.GradientType.SWEEP
            holeRatio = 0f

            overlayRatio = 0f

//            legendsIcon = PieChart.DefaultIcons.SQUARE

            return mbinding.root

        }


    }
}