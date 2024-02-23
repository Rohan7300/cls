package com.clebs.celerity.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.ImageViewModel
import com.clebs.celerity.ViewModel.ImageViewModelProviderFactory
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.database.ImageDatabase
import com.clebs.celerity.database.ImagesRepo
import com.clebs.celerity.databinding.ActivityHomeBinding
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.dbLog
import com.clebs.celerity.utils.toast
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {
    lateinit var ActivityHomeBinding: ActivityHomeBinding
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var imageViewModel: ImageViewModel
    var screenid: Int = 0
    lateinit var navController: NavController
    public lateinit var viewModel: MainViewModel
    private lateinit var navGraph: NavGraph
    private var completeTaskScreen: Boolean = false

    companion object {
        fun showLog(tag: String, message: String) {
            Log.e(tag, message)
        }

        var checked: String? = ""
        var Boolean: Boolean = false
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        bottomNavigationView = ActivityHomeBinding.bottomNavigatinView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navController.addOnDestinationChangedListener(this)

        bottomNavigationView.selectedItemId = R.id.home
        bottomNavigationView.getMenu().findItem(R.id.daily).setTooltipText("Daily work")
        bottomNavigationView.getMenu().findItem(R.id.passwords).setTooltipText("Notifications")
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        val imagesRepo =
            ImagesRepo(ImageDatabase.invoke(this), Prefs.getInstance(applicationContext))

        viewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo)).get(MainViewModel::class.java)

        viewModel.getVehicleDefectSheetInfoLiveData.observe(this, Observer {
            Log.d("GetVehicleDefectSheetInfoLiveData ", "$it")
            ActivityHomeBinding.homeActivityPB.visibility = View.GONE
            if (it != null) {
                completeTaskScreen = it.IsSubmited
            }
            if (!completeTaskScreen) {
                screenid = viewModel.getLastVisitedScreenId(this)
                if (screenid.equals(0)) {
                    navController.navigate(R.id.homeFragment)
                    navController.currentDestination!!.id = R.id.homeFragment

                } else {

                    navController.navigate(screenid)
                    navController.currentDestination!!.id = screenid

                }
            } else {
                navController.navigate(R.id.completeTaskFragment)
            }
        })
        //viewModel.GetVehicleDefectSheetInfo(Prefs.getInstance(applicationContext).userID.toInt())


        imageViewModel = ViewModelProvider(
            this,
            ImageViewModelProviderFactory(imagesRepo)
        ).get(ImageViewModel::class.java)

        imageViewModel.images?.observe(this) { imageEntity ->
            dbLog(imageEntity)
        }


//        checkIftodayCheckIsDone()
//
//        if (Build.VERSION.SDK_INT >= 33) {
//            onBackInvokedDispatcher.registerOnBackInvokedCallback(
//                OnBackInvokedDispatcher.PRIORITY_DEFAULT
//            ) {
//                navPop()
//
//            }
//        } else {
//            onBackPressedDispatcher.addCallback(
//                this,
//                object : OnBackPressedCallback(true) {
//                    override fun handleOnBackPressed() {
//
//                        navPop()
//
//                    }
//                })
//        }


        ActivityHomeBinding.imgDrawer.setOnClickListener {
            navController.navigate(R.id.profileFragment)
        }
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.home -> {

                    navController.navigate(R.id.homedemoFragment)



                    true
                }

                R.id.daily -> {
                    ActivityHomeBinding.homeActivityPB.visibility = View.VISIBLE
                    viewModel.GetVehicleDefectSheetInfo(Prefs.getInstance(applicationContext).userID.toInt())

                    true
                }

                R.id.invoices -> {


                    navController.navigate(R.id.invoicesFragment)


                    true
                }

                R.id.passwords -> {

                    ActivityHomeBinding.title.text = "Notifications"
                    navController.navigate(R.id.notifficationsFragment)

                    true
                }

                R.id.tickets -> {
                    ActivityHomeBinding.title.text = "User Tickets"
                    navController.navigate(R.id.userTicketsFragment)

                    true

                }

                else -> false
            }

        }
        ActivityHomeBinding.imgLogout.setOnClickListener {
            logout()

        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)


    }

    fun logout() {
        viewModel.Logout().observe(this@HomeActivity, Observer {

            if (it!!.responseType.equals("Success")) {

                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("logout", "0")
                startActivity(intent)
                setLoggedIn(false)
            }
        })

    }

//    fun navPop(){
//        when (navController.currentDestination?.id) {
//
//            R.id.windScreenFragment, R.id.windowsGlassFragment -> {
//
//                navController.navigateUp()
//
//            }
//        }

    //    }
//    override fun onBackPressed() {
//
//
//        // Get the currently focused view
//        val focusedView = currentFocus
//
//        // Check if the focused view is the BottomNavigationView
//        if (focusedView is BottomNavigationView) {
//            if (navController.currentDestination?.id!!.equals(screenid)) {
//                navController.navigateUp()
//
//            }
//        }
//
//
//
//    }

    @SuppressLint("MissingSuperCall")

    override fun onBackPressed() {
        //super.onBackPressed()
        screenid = viewModel.getLastVisitedScreenId(this)
        backNav()
    }


    override fun onDestinationChanged(
        controller: NavController, destination: NavDestination, arguments: Bundle?
    ) {


    }

    private fun setLoggedIn(isLoggedIn: Boolean) {
        Prefs.getInstance(applicationContext).saveBoolean("isLoggedIn", isLoggedIn)
    }

    fun checkIftodayCheckIsDone() {


        viewModel.CheckIFTodayCheckIsDone().observe(this@HomeActivity, Observer {
            if (it != null) {
                Log.e("ldkkcjvckvjc", "checkIftodayCheckIsDone: ")
                if (it.isSubmited.equals(true)) {
                    checked = "1"
                } else {
                    checked = "0"
                }

            }

        })
    }

    fun backNav() {
        val prefs = Prefs.getInstance(applicationContext)
        val fragmentStack = prefs.getNavigationHistory()
        if (fragmentStack.size > 1) {
            fragmentStack.pop()
            val previousFragment = fragmentStack.peek()
            if (previousFragment != R.id.dailyWorkFragment) {
                navController.navigate(previousFragment)
                prefs.saveNavigationHistory(fragmentStack)
            } else {
            }
        } else {
            //super.onBackPressed()
        }
    }
}