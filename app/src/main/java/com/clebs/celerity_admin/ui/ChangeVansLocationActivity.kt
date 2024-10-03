package com.clebs.celerity_admin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.databinding.ActivityChangeVansLocationBinding

class ChangeVansLocationActivity : AppCompatActivity() {
    lateinit var binding: ActivityChangeVansLocationBinding
    private var isTextViewVisible = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeVansLocationBinding.inflate(layoutInflater)
        binding.llm.visibility = View.VISIBLE
        binding.llm2.visibility = View.VISIBLE
        binding.materialTextView.setOnClickListener {
            isTextViewVisible = !isTextViewVisible // Toggle the boolean
//            binding.edtLayouts.visibility = if (isTextViewVisible) View.VISIBLE else View.GONE
            binding.llm.visibility = if (isTextViewVisible) View.VISIBLE else View.GONE
            binding.llm2.visibility = if (isTextViewVisible) View.VISIBLE else View.GONE
            binding.cancel.setOnClickListener {
                finish()
            }
            binding.ivback.setOnClickListener {
                finish()
            }
            //Do something after   }, 1000)


        }
        setContentView(binding.root)
    }


}