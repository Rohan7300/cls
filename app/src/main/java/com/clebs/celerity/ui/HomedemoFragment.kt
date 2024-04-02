package com.clebs.celerity.ui


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.size.Dimension
import com.clebs.celerity.R
import com.clebs.celerity.databinding.FragmentHomedemoBinding
import ir.mahozad.android.PieChart
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class HomedemoFragment : Fragment() {
    lateinit var mbinding: FragmentHomedemoBinding

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

        var pieChart = mbinding.pieChart.findViewById<PieChart>(R.id.pieChart)
        pieChart.isAnimationEnabled = true



        pieChart.slices = listOf(
            PieChart.Slice(
                0.3f,
                resources.getColor(R.color.red_light),
                resources.getColor(R.color.red_light),
                "Third Party Deductions",
                labelSize = 30f
            ),
            PieChart.Slice(
                0.4f,
                resources.getColor(R.color.peek_orange),
                resources.getColor(R.color.peek_orange), legend = "Deductions", label =
                "Deductions",   labelSize = 30f
            ),
            PieChart.Slice(
                0.3f,
                resources.getColor(R.color.blue_hex),
                resources.getColor(R.color.blue_hex),
                "Profits",   labelSize = 30f
            ),

            )
        pieChart.labelType = PieChart.LabelType.OUTSIDE_CIRCULAR_OUTWARD
        pieChart.labelsColor=resources.getColor(R.color.orange)
pieChart.holeRatio=0f
        pieChart.overlayRatio=0f


        return mbinding.root

    }


}