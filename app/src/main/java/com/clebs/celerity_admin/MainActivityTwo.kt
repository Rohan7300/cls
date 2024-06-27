package com.clebs.celerity_admin

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.clebs.celerity_admin.databinding.ActivityMainTwoBinding
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.ui.App
import com.clebs.celerity_admin.ui.CLSloction.ChangeVehicleFragment
import com.clebs.celerity_admin.utils.FabClick
import com.clebs.celerity_admin.utils.OnButtonClickListener
import com.clebs.celerity_admin.viewModels.MainViewModel
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.singletons.PublicConstants
import kotlinx.coroutines.launch

class MainActivityTwo : AppCompatActivity(), OnNavigationItemSelectedListener{

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainTwoBinding
    private var activityViewClickListener: FabClick? = null
    lateinit var resumedialog: AlertDialog
    private lateinit var myFragment: ChangeVehicleFragment
    private lateinit var cqSDKInitializer: CQSDKInitializer
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]
        setSupportActionBar(binding.appBarMainActivityTwo.toolbar)
        myFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main_activity_two) as ChangeVehicleFragment


//        binding.appBarMainActivityTwo.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Refreshing...", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//            activityViewClickListener?.onActivityViewClicked(view)
//        }
//        if (myFragment!=null){
//            myFragment= supportFragmentManager.findFragmentById(R.id.nav_gallery) as ChangeVehicleFragment
//        }


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main_activity_two)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_weblogin
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener(this)
        lifecycleScope.launch {

            if (!App.offlineSyncDB!!.isUserTableEmpty()) {

                resumeDialog()
            }
        }

        cqSDKInitializer = CQSDKInitializer(this)
        if (!cqSDKInitializer.isCQSDKInitialized()) {
            cqSDKInitializer.initSDK(
                sdkKey = "09f36b6e-deee-40f6-894b-553d4c592bcb.eu",
                result = { isInitialized, code, message ->
                    if (code == PublicConstants.sdkInitializationSuccessCode) {
                        Log.e("sucesss", "onCreateView: ")
                    } else {
                        Log.e("failed", "onCreateView: ")
                    }
                })
        }

        binding.appBarMainActivityTwo.cardone.setOnClickListener {

            navController.navigate(R.id.nav_gallery)
            binding.appBarMainActivityTwo.cardone.setCardBackgroundColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.maroon
                )
            )
            binding.appBarMainActivityTwo.cardtwo.setCardBackgroundColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.light_grey
                )
            )
        }
        binding.appBarMainActivityTwo.cardtwo.setOnClickListener {
            navController.navigate(R.id.nav_slideshow)
            binding.appBarMainActivityTwo.cardtwo.setCardBackgroundColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.maroon
                )
            )
            binding.appBarMainActivityTwo.cardone.setCardBackgroundColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.light_grey
                )
            )
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        //inspectionstarted = true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_activity_two, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main_activity_two)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main_activity_two)
        when (item.itemId) {
            R.id.nav_gallery -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                navController.navigate(R.id.nav_gallery)
                binding.appBarMainActivityTwo.cardone.setCardBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.maroon
                    )
                )
                binding.appBarMainActivityTwo.cardtwo.setCardBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.light_grey
                    )
                )
            }

            R.id.nav_slideshow -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                navController.navigate(R.id.nav_slideshow)

                binding.appBarMainActivityTwo.cardone.setCardBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.light_grey
                    )
                )
                binding.appBarMainActivityTwo.cardtwo.setCardBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.maroon
                    )
                )
            }

            R.id.nav_weblogin -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                navController.navigate(R.id.nav_weblogin)

                binding.appBarMainActivityTwo.cardone.setCardBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.light_grey
                    )
                )
                binding.appBarMainActivityTwo.cardtwo.setCardBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.maroon
                    )
                )
            }

        }
        return true
    }

    fun resumeDialog() {
        val factory = LayoutInflater.from(this@MainActivityTwo)
        val view: View = factory.inflate(R.layout.resumeitdialog, null)
        resumedialog = AlertDialog.Builder(
            this
        ).create()
        resumedialog.setView(view)

        val bt: Button = view.findViewById(R.id.bt_yes)
        val bt2: Button = view.findViewById(R.id.bt_no)

        bt.setOnClickListener {
            resumedialog.dismiss()


        }
        bt2.setOnClickListener {
            App.offlineSyncDB!!.clearAllTables()

            resumedialog.dismiss()


        }
        resumedialog.setCanceledOnTouchOutside(false)
        resumedialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        resumedialog.setCancelable(false)
        resumedialog.show()
    }


}