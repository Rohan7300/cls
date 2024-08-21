package com.clebs.celerity_admin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.databinding.ActivityVanHireReturnAgreementBinding

class VanHireReturnAgreementActivity : AppCompatActivity() {
    lateinit var binding:ActivityVanHireReturnAgreementBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVanHireReturnAgreementBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}