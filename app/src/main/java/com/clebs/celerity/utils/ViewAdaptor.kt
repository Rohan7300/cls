package com.clebs.celerity.utils

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.clebs.celerity.fragments.DeliveryProcedures
import com.clebs.celerity.fragments.FinalAssesmentFragment
import com.clebs.celerity.fragments.GoingOn
import com.clebs.celerity.fragments.Prepardness
import com.clebs.celerity.fragments.ReturnToStation
import com.clebs.celerity.fragments.StartUp

class ViewAdaptor(
    private val myContext: Context,
    fm: FragmentManager?,
    var totalTabs: Int
) :
    FragmentPagerAdapter(fm!!) {

    init {
        observeData()
    }

    private fun observeData() {

    }

    override fun getItem(position: Int): Fragment {

        return when (position) {
            0 -> {
                //Prepardness()
                StartUp()
            }

            1 -> {
               // StartUp()
                DeliveryProcedures()
            }

            2 -> {
                FinalAssesmentFragment()
               // GoingOn()
            }

            /*3 -> {
              DeliveryProcedures()
            }

            4 -> {
                ReturnToStation()
            }
            5->{
                FinalAssesmentFragment()
            }*/

            else -> getItem(position)
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}