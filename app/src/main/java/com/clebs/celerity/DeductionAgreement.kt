package com.clebs.celerity

import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.clebs.celerity.DeductionAgreement.Companion.path
import com.clebs.celerity.DrawViewClass.Companion.pathList
import com.clebs.celerity.databinding.ActivityDeductionAgreementBinding

class DeductionAgreement : AppCompatActivity() {
    companion object {
        var path = Path()
        var brush = Paint()
    }

    //    var retry: RelativeLayout = findViewById(R.id.RetryLay)
    lateinit var binding: ActivityDeductionAgreementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityDeductionAgreementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        withDialog()

    }

    fun withDialog() {
        binding.radImg1.setOnClickListener() {
            if (binding.radImg1.drawable.mutate().colorFilter == null) {
                binding.radImg1.getDrawable().mutate()
                    .setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_IN);
//                var builder: AlertDialog.Builder = AlertDialog.Builder(this)
//
//                var view = this.layoutInflater.inflate(R.layout.fragment_sign_fragement, null)
//                builder.setView(view)
//                var dialog: AlertDialog = builder.create()
//                dialog.show()

                custDialog().show(supportFragmentManager, "sign")

                // to remove other radio
                binding.radImg2.drawable.mutate().colorFilter = null
            } else {
                binding.radImg1.drawable.mutate().colorFilter = null
            }
        }

        binding.radImg2.setOnClickListener() {
            if (binding.radImg2.drawable.mutate().colorFilter == null) {
                binding.radImg2.getDrawable().mutate()
                    .setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_IN);
                binding.DisputeH.visibility = View.VISIBLE

                // to remove other radio
                binding.radImg1.drawable.mutate().colorFilter = null
            } else {
                binding.radImg2.drawable.mutate().colorFilter = null

                binding.DisputeH.visibility = View.GONE
            }
        }

        fun woDialog() {
//        binding.radImg1.setOnClickListener() {
//            if (binding.radImg1.drawable.mutate().colorFilter == null) {
//                binding.radImg1.getDrawable().mutate()
//                    .setColorFilter(getResources().getColor(R.color.pink), PorterDuff.Mode.SRC_IN);
////                binding.signH.visibility = View.VISIBLE
//
//                // to remove other radio
//                binding.radImg2.drawable.mutate().colorFilter = null
//                binding.DisputeH.visibility = View.GONE
//            } else {
//                binding.radImg1.drawable.mutate().colorFilter = null
//                binding.signH.visibility = View.GONE
//            }
//        }
//
//        binding.RetryLay.setOnClickListener() {
//            pathList.clear()
//            path.reset()
//        }
//
//        binding.radImg2.setOnClickListener() {
//            if (binding.radImg2.drawable.mutate().colorFilter == null) {
//                binding.radImg2.getDrawable().mutate()
//                    .setColorFilter(getResources().getColor(R.color.pink), PorterDuff.Mode.SRC_IN);
//                binding.signH.visibility = View.VISIBLE
//                binding.DisputeH.visibility = View.VISIBLE
//
//                // to remove other radio
//                binding.radImg1.drawable.mutate().colorFilter = null
//            } else {
//                binding.radImg2.drawable.mutate().colorFilter = null
//                binding.signH.visibility = View.GONE
//                binding.DisputeH.visibility = View.GONE
//            }
//        }

        }
    }
}

class custDialog : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = getDialog()?.getWindow()!!.getAttributes()
        params.width = LayoutParams.MATCH_PARENT
        params.height = LayoutParams.WRAP_CONTENT
        getDialog()?.getWindow()?.setAttributes(params as LayoutParams)
        getDialog()?.getWindow()?.setBackgroundDrawableResource(R.drawable.round_corners)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.sign_layout, container, false)
        var retry = rootView.findViewById<RelativeLayout>(R.id.RetryLay)
        var close = rootView.findViewById<TextView>(R.id.cl)
        var save = rootView.findViewById<AppCompatButton>(R.id.sv)


        retry.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                pathList.clear()
                path.reset()
            }
        })

        close.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dismiss()
            }
        })
        return rootView
    }
}



