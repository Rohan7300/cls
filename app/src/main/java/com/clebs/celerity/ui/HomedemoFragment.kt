package com.clebs.celerity.ui


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
  lateinit var mbinding:FragmentHomedemoBinding
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
//        val urlRqs3DPie = (urlGoogleChart
//                + urlp3Api
//                + "Rental") + "," + "Deductions" + "," + "Earnings"
//        val bm3DPie: Bitmap? = loadChart(urlRqs3DPie)
//        if(bm3DPie == null){
//
//        }else{
//           mbinding. pieChart.setImageBitmap(bm3DPie);
//
//    }
        val pieChart = mbinding.pieChart.findViewById<PieChart>(R.id.pieChart)
pieChart.isAnimationEnabled=true

        pieChart.slices = listOf(
            PieChart.Slice(0.3f, resources.getColor(R.color.red_light),resources.getColor(R.color.red_light),"Van rental",),
            PieChart.Slice(0.4f, resources.getColor(R.color.peek_orange),resources.getColor(R.color.peek_orange),"Deductions"),
            PieChart.Slice(0.3f, resources.getColor(R.color.blue_hex),resources.getColor(R.color.blue_hex),"Earnings"),

        )

        return mbinding.root

    }


}