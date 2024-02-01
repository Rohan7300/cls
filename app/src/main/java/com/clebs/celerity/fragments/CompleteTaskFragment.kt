package com.clebs.celerity.fragments


import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.clebs.celerity.R
import com.clebs.celerity.databinding.FragmentCompleteTaskBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CompleteTaskFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CompleteTaskFragment : Fragment() {
    lateinit var mbinding: FragmentCompleteTaskBinding
    private var isclicked: Boolean = true
    private var isclickedtwo: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::mbinding.isInitialized) {
            mbinding = FragmentCompleteTaskBinding.inflate(inflater, container, false)
        }
        val clickListener = View.OnClickListener {
            showAlert()
        }

        mbinding.rlcomtwoBreak.setOnClickListener(clickListener)
        mbinding.downIvsBreak.setOnClickListener(clickListener)
        mbinding.parentBreak.setOnClickListener(clickListener)








        mbinding.rlcom.setOnClickListener {
            if (isclicked) {
                mbinding.taskDetails.visibility = View.VISIBLE
                mbinding.downIv.setImageResource(R.drawable.green_down_arrow)
                mbinding.view2.visibility = View.VISIBLE


            } else {
                mbinding.taskDetails.visibility = View.GONE
                mbinding.downIv.setImageResource(R.drawable.grey_right_arrow)
                mbinding.view2.visibility = View.GONE
                mbinding.uploadLayouts.visibility = View.VISIBLE

            }
            isclicked = !isclicked

        }
        mbinding.run {


            mbinding.tvNext.isEnabled = !isclicked
            if (tvNext.isEnabled) {
                tvNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                tvNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
            }

        }
        mbinding.tvNext.setOnClickListener {
            if (isclickedtwo) {

                mbinding.uploadLayouts.visibility = View.GONE
            } else {

                mbinding.uploadLayouts.visibility = View.VISIBLE
            }
            isclickedtwo = !isclickedtwo
        }
        mbinding.taskDetails.getViewTreeObserver()
            .addOnGlobalLayoutListener(OnGlobalLayoutListener { // Check if the view is currently visible or gone
                val isVisible = mbinding.taskDetails.visibility == View.VISIBLE

                // Apply animation based on the visibility
                if (isVisible) {
                    val slideInAnimation: Animation =
                        AnimationUtils.loadAnimation(context, com.clebs.celerity.R.anim.slide_down)
                    mbinding.taskDetails.startAnimation(slideInAnimation)
                } else {
                    val slideOutAnimation: Animation =
                        AnimationUtils.loadAnimation(context, com.clebs.celerity.R.anim.slide_up)
                    mbinding.taskDetails.startAnimation(slideOutAnimation)
                }
            })
        mbinding.rlcomtwoRoad.setOnClickListener {

            mbinding.routeLayout.visibility = View.VISIBLE
        }
//        val listData : MutableList<ParentData> = ArrayList()
//        val parentData: Array<String> = arrayOf("Completed Task")
//
//        val childDataData1: MutableList<ChildData> = mutableListOf(ChildData("Vehicle Defect Sheet","0"),ChildData("Vehcile Pictures","0"),ChildData("Clocked In","0"))
//
//        val parentObj1 = ParentData(parentTitle = parentData[0], subList = childDataData1)
//        listData.add(parentObj1)
//
//       mbinding.exRecycle.adapter = RecycleAdapter(requireActivity(),listData)
        return mbinding.root
    }

    fun showAlert() {
        val factory = LayoutInflater.from(requireContext())
        val view: View = factory.inflate(R.layout.time_picker_dialog, null)
        val ic_start = view.findViewById<ImageButton>(R.id.ic_breakstart)
        val ic_breakend = view.findViewById<ImageButton>(R.id.ic_breakend)
        val edt_breakstart = view.findViewById<EditText>(R.id.edt_breakstart)
        val edt_breakend = view.findViewById<EditText>(R.id.edt_breakend)
        val deleteDialog: AlertDialog = AlertDialog.Builder(requireContext()).create()
        deleteDialog.setView(view)

        deleteDialog.setCanceledOnTouchOutside(true);
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        deleteDialog.show();

    }

}