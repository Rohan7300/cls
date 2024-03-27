package com.clebs.celerity.ui

import android.R.attr
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.chart.common.listener.Event
import com.anychart.chart.common.listener.ListenersInterface
import com.anychart.enums.Align
import com.anychart.enums.LegendLayout
import com.clebs.celerity.databinding.FragmentHomedemoBinding
import com.clebs.celerity.utils.showToast


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::mbinding.isInitialized) {
            mbinding = FragmentHomedemoBinding.inflate(inflater, container, false)

            /*val pie =   AnyChart.pie3d()
            pie.setOnClickListener(object :
                ListenersInterface.OnClickListener(arrayOf<String>("x", "value")) {
                override fun onClick(event: Event) {
                    showToast(
                        event.getData().get("x") + ":" + event.getData().get("value").toString(),requireContext()
                    )
                }
            })
            val data = mutableListOf<ValueDataEntry>()
            data.add(ValueDataEntry("Van rental", 100).apply { setValue("fill", "#f76f6f") },)
            data.add(ValueDataEntry("Deductions", 50).apply { setValue("fill", "#fdb64e") },)
            data.add(ValueDataEntry("Earnings", 20).apply { setValue("fill", "#77dfd8") },)
            pie.data(data as List<DataEntry>?)
            pie.credits().enabled(false);

            pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER)

            mbinding.anychartview.setChart(pie)*/

//            mbinding.web.loadUrl("C:\\Users\\chakshit.awasthi\\AppData\\Roaming\\Google\\AndroidStudio2023.1\\scratches\\scratch.html")
  /*          val rotateAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.anam)
            mbinding.imgCircleLogo.startAnimation(rotateAnimation)
//            mbinding.imgCircleLogo.animate()
//                .rotationBy(360f)
//                .setDuration(2000)
//
//                .setInterpolator(AccelerateInterpolator())
//                .setListener(null)


            mbinding.icLoh.animate()
                .rotationBy(360f)
                .setDuration(2000)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setListener(null)*/

        }
        return mbinding.root

    }


}