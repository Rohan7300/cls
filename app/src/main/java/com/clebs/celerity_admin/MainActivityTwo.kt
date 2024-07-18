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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.clebs.celerity_admin.database.CheckInspection
import com.clebs.celerity_admin.databinding.ActivityMainTwoBinding
import com.clebs.celerity_admin.dialogs.LoadingDialog
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.ui.App
import com.clebs.celerity_admin.viewModels.MainViewModel
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.singletons.PublicConstants
import kotlinx.coroutines.launch

class MainActivityTwo : AppCompatActivity(), OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var binding: ActivityMainTwoBinding
    lateinit var loadingDialog: LoadingDialog
    private var saveClickCounter = 0
    lateinit var resumedialog: AlertDialog

    private lateinit var cqSDKInitializer: CQSDKInitializer
    lateinit var mainViewModel: MainViewModel
    lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        loadingDialog = LoadingDialog(this)
        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]
        setSupportActionBar(binding.appBarMainActivityTwo.toolbar)

        getSupportActionBar()?.setDisplayShowTitleEnabled(false)
        binding.appBarMainActivityTwo.toolbarTitle.setText("Vehicle Allocation")

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
        navController = findNavController(R.id.nav_host_fragment_content_main_activity_two)

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
            Log.e("dsjhjfhdfdjfcqkintialization", "onCreate: ", )
            cqSDKInitializer.initSDK(
                sdkKey ="ab8c0110-9529-4e2c-a1d4-d810636bccf3.eu",
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
            binding.appBarMainActivityTwo.toolbarTitle.setText("Vehicle Allocation")
            binding.appBarMainActivityTwo.cardone.setCardBackgroundColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.maroon
                )
            )
            binding.appBarMainActivityTwo.cardone.alpha=1f
            binding.appBarMainActivityTwo.cardtwo.setCardBackgroundColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.darkcommentbg
                )
            )
            binding.appBarMainActivityTwo.cardtwo.alpha=0.4f
        }
        binding.appBarMainActivityTwo.cardtwo.setOnClickListener {
            binding.appBarMainActivityTwo.toolbarTitle.setText("Weekly defects check")
            navController.navigate(R.id.nav_slideshow)
            binding.appBarMainActivityTwo.cardtwo.setCardBackgroundColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.maroon
                )
            )
            binding.appBarMainActivityTwo.cardtwo.alpha=1f
            binding.appBarMainActivityTwo.cardone.setCardBackgroundColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.darkcommentbg
                )
            )
            binding.appBarMainActivityTwo.cardone.alpha=0.4f
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val message =
            intent?.getStringExtra(PublicConstants.quoteCreationFlowStatusMsgKeyInIntent)
                ?: "Could not identify status message"
        val tempCode =
            intent?.getIntExtra(PublicConstants.quoteCreationFlowStatusCodeKeyInIntent, -1)

        if (tempCode == 200) {
            Log.d("hdhsdshdsdjshhsds", "200 $message")
//            prefs.saveBoolean("Inspection", true)
//            prefs.Isfirst = false
//            prefs.updateInspectionStatus(true)
//            SaveVehicleInspection(viewModel)
            lifecycleScope.launch {
                App.offlineSyncDB?.insertInfoUpload(
                    CheckInspection(
                        0,
                        true,
                    )
                )
            }
            navController.navigate(R.id.nav_gallery)

//            uploadStatus()
            Toast.makeText(this, "Vehicle Inspection is successfully completed", Toast.LENGTH_SHORT)
                .show()

        } else {

            //showToast("Vehicle Inspection Failed!! ", this)
            Log.d("hdhsdshdsdjshhsds", "else $tempCode $message")
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main_activity_two)
        when (item.itemId) {
            R.id.nav_gallery -> {
                binding.appBarMainActivityTwo.toolbarTitle.setText("Vehicle Allocation")
                binding.appBarMainActivityTwo.bottomBar.visibility = View.VISIBLE
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
                binding.appBarMainActivityTwo.toolbarTitle.setText("Weekly defects check")
                binding.appBarMainActivityTwo.bottomBar.visibility = View.VISIBLE
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
                binding.appBarMainActivityTwo.bottomBar.visibility = View.GONE
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
            navController.navigate(R.id.nav_gallery)

        }
        bt2.setOnClickListener {
            App.offlineSyncDB!!.clearAllTables()
            navController.navigate(R.id.nav_gallery)
            resumedialog.dismiss()


        }
        resumedialog.setCanceledOnTouchOutside(false)
        resumedialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        resumedialog.setCancelable(false)
        resumedialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}