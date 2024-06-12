package com.clebs.celerity_admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity_admin.databinding.ActivityMainTwoBinding
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.ui.CLSloction.ChangeVehicleFragment
import com.clebs.celerity_admin.viewModels.MainViewModel
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.singletons.PublicConstants

class MainActivityTwo : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainTwoBinding
    private lateinit var cqSDKInitializer: CQSDKInitializer



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.appBarMainActivityTwo.toolbar)

        binding.appBarMainActivityTwo.fab.setOnClickListener { view ->
            Snackbar.make(view, "Refreshing...", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
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
                R.id.nav_gallery, R.id.nav_slideshow,R.id.nav_weblogin
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
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
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val tempCode =
            intent?.getIntExtra(PublicConstants.quoteCreationFlowStatusCodeKeyInIntent, -1)

        if (tempCode == 200) {
            Toast.makeText(this, "Inspection Done", Toast.LENGTH_SHORT).show()


        } else {
        }
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
}