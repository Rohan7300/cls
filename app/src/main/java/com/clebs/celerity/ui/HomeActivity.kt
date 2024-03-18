package com.clebs.celerity.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.ImageViewModel
import com.clebs.celerity.ViewModel.ImageViewModelProviderFactory
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.database.ImageDatabase
import com.clebs.celerity.database.ImagesRepo
import com.clebs.celerity.databinding.ActivityHomeBinding
import com.clebs.celerity.fragments.CompleteTaskFragment
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.dbLog
import com.clebs.celerity.utils.showToast
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.singletons.PublicConstants
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class HomeActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {
    private lateinit var ActivityHomeBinding: ActivityHomeBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    lateinit var imageViewModel: ImageViewModel
    private var screenid: Int = 0
    private lateinit var navController: NavController
    lateinit var viewModel: MainViewModel
    private lateinit var navGraph: NavGraph
    private var completeTaskScreen: Boolean = false
    private lateinit var cqSDKInitializer: CQSDKInitializer
    lateinit var fragmentManager: FragmentManager
    private var sdkkey = ""
    var userId: Int = 0
    var firstName = ""
    var lastName = ""
    var isLeadDriver = false
    var date = ""
    lateinit var loadingDialog: LoadingDialog

    companion object {
        fun showLog(tag: String, message: String) {
            Log.e(tag, message)
        }

        var checked: String? = ""
        var Boolean: Boolean = false
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            val identifier =
                intent.getStringExtra(PublicConstants.quoteCreationFlowStatusIdentifierKeyInIntent)
                    ?: "Could not identify Identifier"
            val message =
                intent.getStringExtra(PublicConstants.quoteCreationFlowStatusMsgKeyInIntent)
                    ?: "Could not identify status message"
            val tempCode =
                intent.getIntExtra(PublicConstants.quoteCreationFlowStatusCodeKeyInIntent, -1)
            if (tempCode == 200) {
                CompleteTaskFragment.inspectionstarted = true

                showToast("inspection success", this)
            } else {

                showToast("inspection Failed", this)
                CompleteTaskFragment.inspectionstarted = false

            }
            // Check if identifier is valid
            if (identifier == PublicConstants.quoteCreationFlowStatusIdentifier) {
                // Get code
                val code = if (tempCode == -1) {
                    "Could not identify status code"
                } else {
                    tempCode
                }

                // Update message in the dia
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        bottomNavigationView = ActivityHomeBinding.bottomNavigatinView

        loadingDialog = LoadingDialog(this)
        sdkkey = "09f36b6e-deee-40f6-894b-553d4c592bcb.eu"
        cqSDKInitializer()
        userId = Prefs.getInstance(this).userID.toInt()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navController.addOnDestinationChangedListener(this)
        fragmentManager = this.supportFragmentManager
        bottomNavigationView.selectedItemId = R.id.home
        bottomNavigationView.menu.findItem(R.id.daily).setTooltipText("Daily work")
        bottomNavigationView.menu.findItem(R.id.passwords).setTooltipText("Notifications")

        try {
            val apiService = RetrofitService.getInstance().create(ApiService::class.java)
            val mainRepo = MainRepo(apiService)
            val imagesRepo =
                ImagesRepo(ImageDatabase.invoke(this), Prefs.getInstance(applicationContext))

            viewModel =
                ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]


            getVehicleLocationInfo()
            viewModel.getVehicleDefectSheetInfoLiveData.observe(this) {
                Log.d("GetVehicleDefectSheetInfoLiveData ", "$it")
                loadingDialog.cancel()
                /*progressBarVisibility(
                    false,
                    ActivityHomeBinding.homeActivityPB,
                    ActivityHomeBinding.overlayViewHomeActivity
                )*/
                if (it != null) {
                    completeTaskScreen = it.IsSubmited
                    if (!completeTaskScreen) {
                        screenid = viewModel.getLastVisitedScreenId(this)
                        if (screenid == 0) {
                            navController.navigate(R.id.homeFragment)
                            // navigateTo(R.id.homeFragment)
                            navController.currentDestination!!.id = R.id.homeFragment

                        } else {
                            try {
                                navController.navigate(screenid)
                                navController.currentDestination!!.id = screenid
                            } catch (_: Exception) {
                                navController.navigate(R.id.homeFragment)
                                navController.currentDestination!!.id = R.id.homeFragment
                            }
                        }
                    } else {
                        navController.navigate(R.id.completeTaskFragment)
                    }
                }
            }

            imageViewModel = ViewModelProvider(
                this,
                ImageViewModelProviderFactory(imagesRepo)
            )[ImageViewModel::class.java]

            imageViewModel.images.observe(this) { imageEntity ->
                dbLog(imageEntity)
            }

            ActivityHomeBinding.imgDrawer.setOnClickListener {

            }
            bottomNavigationView.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {

                    R.id.home -> {
                        ActivityHomeBinding.title.text = ""
                        navController.navigate(R.id.homedemoFragment)
                        true
                    }

                    R.id.daily -> {
                        /*     navController.navigate(R.id.homeFragment)
                             navController.currentDestination!!.id = R.id.homeFragment
         */
                        ActivityHomeBinding.title.text = ""
                        viewModel.GetVehicleDefectSheetInfo(Prefs.getInstance(applicationContext).userID.toInt())
                        loadingDialog.show()
                        /*        progressBarVisibility(
                                    true,
                                    ActivityHomeBinding.homeActivityPB,
                                    ActivityHomeBinding.overlayViewHomeActivity
                                )*/
                        true
                    }

                    R.id.invoices -> {
                        ActivityHomeBinding.title.text = ""
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
                showAlertLogout()
            }
        }catch (e:Exception){
            RetrofitService.handleNetworkError(e,fragmentManager)
        }


    }

     fun getVehicleLocationInfo() {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM")
        date = today.format(formatter)

        loadingDialog.show()

        viewModel.GetDriversBasicInformation(
            userId.toDouble()
        ).observe(this) {
            loadingDialog.cancel()
            if (it != null) {
                try {
                    //if (it.vmRegNo != null)
                        viewModel.GetVehicleInformation(userId, Prefs.getInstance(this).vmRegNo)


                } catch (e: Exception) {
                    Log.d("sds", e.toString())
                }

                firstName = it.firstName
                lastName = it.lastName
                isLeadDriver = it.IsLeadDriver
            }
        }
    }


    private fun logout() {
        viewModel.Logout().observe(this@HomeActivity) {
            if (it!!.responseType == "Success") {
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("logout", "0")
                finish()
                startActivity(intent)
                setLoggedIn(false)
            }
        }
    }

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
                checked = if (it.isSubmited) {
                    "1"
                } else {
                    "0"
                }

            }
        })
    }

    private fun backNav() {
        try {
            val prefs = Prefs.getInstance(applicationContext)
            val fragmentStack = prefs.getNavigationHistory()
            if (navController.currentDestination?.id == R.id.completeTaskFragment) {
                prefs.clearNavigationHistory()
            } else if (fragmentStack.size > 1) {
                fragmentStack.pop()
                val previousFragment = fragmentStack.peek()
                if (previousFragment != R.id.dailyWorkFragment) {
                    navController.navigate(previousFragment)
                    prefs.saveNavigationHistory(fragmentStack)
                }
            }
        } catch (_: Exception) {

        }

    }

    private fun showAlertLogout() {
        val factory = LayoutInflater.from(this)
        val view: View = factory.inflate(R.layout.logout_layout, null)
        val deleteDialog: AlertDialog = AlertDialog.Builder(this).create()
        val imageView: ImageView = view.findViewById(R.id.ic_cross_orange)
        imageView.setOnClickListener {
            deleteDialog.dismiss()
        }
        val btone: Button = view.findViewById(R.id.bt_no)
        val bttwo: Button = view.findViewById(R.id.bt_yes)

        btone.setOnClickListener {
            deleteDialog.dismiss()
        }

        bttwo.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("logout", "0")
            Prefs.getInstance(applicationContext).clearPreferences()
            finish()
            startActivity(intent)
            setLoggedIn(false)
            //logout()
        }
        deleteDialog.setView(view)
        deleteDialog.setCanceledOnTouchOutside(false)
        deleteDialog.setCancelable(false)
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteDialog.show()

    }


    private fun cqSDKInitializer() {
        cqSDKInitializer = CQSDKInitializer(this)
        cqSDKInitializer.triggerOfflineSync()

        cqSDKInitializer.initSDK(
            sdkKey = sdkkey,
            result = { isInitialized, code, _ ->
                if (isInitialized && code == PublicConstants.sdkInitializationSuccessCode) {
                    Prefs.getInstance(applicationContext).saveCQSdkKey(sdkkey)
                } else {
                    showToast("Error initializing SDK", this)
                }
            }
        )
    }
}