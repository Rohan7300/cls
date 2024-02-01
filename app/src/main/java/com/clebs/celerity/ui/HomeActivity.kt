package com.clebs.celerity.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.clebs.celerity.R
import com.clebs.celerity.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeActivity : AppCompatActivity() {
    lateinit var ActivityHomeBinding: ActivityHomeBinding
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var navController: NavController

    companion object {
        fun showLog(tag: String, message: String) {
            Log.e(tag, message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        bottomNavigationView = ActivityHomeBinding.bottomNavigatinView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView.selectedItemId = R.id.daily
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.home -> {
                    navController.navigate(R.id.invoicesFragment)
                    true
                }

                R.id.daily -> {

                    navController.navigate(R.id.homeFragment)
                    true
                }

                R.id.invoices -> {
                    navController.navigate(R.id.invoicesFragment)
                    true
                }

                R.id.passwords -> {
                    navController.navigate(R.id.passwordsFragment)
                    true
                }

                R.id.tickets -> {
                    navController.navigate(R.id.ticketsFragment)
                    true

                }

                else -> false
            }

        }

    }

    private fun updateNavigationBarState(actionId: Int) {
        val menu = bottomNavigationView.menu
        var i = 0
        val size = menu.size()
        while (i < size) {
            val item = menu.getItem(i)
            item.setChecked(item.itemId == actionId)
            i++
        }
    }
}