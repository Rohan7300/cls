package com.clebs.celerity.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import android.widget.TextView
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

import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.dbLog
import com.clebs.celerity.utils.getDeviceID
import com.clebs.celerity.utils.showToast
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.singletons.PublicConstants
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class HomeActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

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
    var apiCount = 0
    var ninetydaysBoolean: Boolean? = null
    var lastName = ""
    var isLeadDriver = false

    var date = ""
    lateinit var loadingDialog: LoadingDialog

    companion object {
        fun showLog(tag: String, message: String) {
            Log.e(tag, message)
        }

        lateinit var ActivityHomeBinding: ActivityHomeBinding
        var checked: String? = ""
        var Boolean: Boolean = false
        var lmId: Int = 0
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
            Log.d("hdhsdshdsdjshhsds", "main $message")
            val tempCode =
                intent.getIntExtra(PublicConstants.quoteCreationFlowStatusCodeKeyInIntent, -1)
            if (tempCode == 200) {
                Log.d("hdhsdshdsdjshhsds", "200 $message")
                Prefs.getInstance(this).saveBoolean("Inspection", true)
                //inspectionstarted = true
                navController.navigate(R.id.completeTaskFragment)
                showToast("Vehicle Inspection is successfully completed ", this)
            } else {
                Log.d("hdhsdshdsdjshhsds", "else $message")
                navController.navigate(R.id.completeTaskFragment)
                showToast("inspection Failed", this)
                Prefs.getInstance(this).saveBoolean("Inspection", false)
                //inspectionstarted = false

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
        Log.d("hdhsdshdsdjshhsds", "No Intent")
    }

    @SuppressLint("HardwareIds")
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
//        bottomNavigationView.menu.findItem(R.id.passwords).setTooltipText("Notifications")
        getWindow().getDecorView()
            .setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);

        getDeviceID()
        val deviceID =
            Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toString()
        Log.e("kjkcjkvckvck", "onCreate: " + deviceID)
//        if (navController.currentDestination!!.id.equals(R.id.profileFragment)){
//
//            ActivityHomeBinding.title.setText("User Profile")
//        }
        try {
            val apiService = RetrofitService.getInstance().create(ApiService::class.java)
            val mainRepo = MainRepo(apiService)
            val imagesRepo =
                ImagesRepo(ImageDatabase.invoke(this), Prefs.getInstance(applicationContext))

            viewModel =
                ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]

            GetDriversBasicInformation()
            if (ninetydaysBoolean?.equals(true) == true) {
                showAlertChangePasword90dys()
            }

            viewModel.getVehicleDefectSheetInfoLiveData.observe(this) {
                Log.d("GetVehicleDefectSheetInfoLiveData ", "$it")
                hideDialog()

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

            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    try {
                        val prefs = Prefs.getInstance(applicationContext)
                        val fragmentStack = prefs.getNavigationHistory()
                        if (prefs.get("90days")
                                .equals("1") && navController.currentDestination?.id == R.id.profileFragment
                        ) {
                            showToast("Please do profile changes first", this@HomeActivity)
                        }
                        if (navController.currentDestination?.id == R.id.completeTaskFragment || navController.currentDestination?.id == R.id.dailyWorkFragment || navController.currentDestination?.id == R.id.homeFragment) {
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
            })


            imageViewModel.images.observe(this) { imageEntity ->
                dbLog(imageEntity)
            }

            ActivityHomeBinding.imgDrawer.setOnClickListener {

                navController.navigate(R.id.profileFragment)

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
                        showDialog()
                        /*        progressBarVisibility(
                                    true,
                                    ActivityHomeBinding.homeActivityPB,
                                    ActivityHomeBinding.overlayViewHomeActivity
                                )*/
                        true
                    }

                    R.id.invoices -> {
                        ActivityHomeBinding.title.text = "Invoices"
                        navController.navigate(R.id.invoicesFragment)
                        true
                    }

//                    R.id.passwords -> {
//                        ActivityHomeBinding.title.text = "Notifications"
//                        navController.navigate(R.id.notifficationsFragment)
//
//                        true
//                    }

                    R.id.tickets -> {
                        ActivityHomeBinding.title.text = "User Tickets"
                        navController.navigate(R.id.userTicketsFragment)

                        true

                    }

                    else -> false
                }

            }
            ActivityHomeBinding.imgLogoutMain.setOnClickListener {
                showAlertLogout()
            }
        } catch (e: Exception) {
            RetrofitService.handleNetworkError(e, fragmentManager)
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
        if (Prefs.getInstance(App.instance).get("90days")
                .equals("1") && navController.currentDestination?.id == R.id.profileFragment
        ) {
            showToast("Please do profile changes first", this)
        }
        Log.d("NavCurrScreenID", "screenid ${screenid}")
        try {
            val prefs = Prefs.getInstance(applicationContext)
            val fragmentStack = prefs.getNavigationHistory()
            Log.d("NavCurrScreenID", "${navController.currentDestination?.id}")
            if (navController.currentDestination?.id == R.id.completeTaskFragment || navController.currentDestination?.id == R.id.dailyWorkFragment || navController.currentDestination?.id == R.id.homeFragment) {
                prefs.clearNavigationHistory()
            } else if (fragmentStack.size > 1) {
                fragmentStack.pop()
                val previousFragment = fragmentStack.peek()
                if (previousFragment != R.id.dailyWorkFragment) {
                    navController.navigate(previousFragment)
                    prefs.saveNavigationHistory(fragmentStack)
                }
            } else {
                super.onBackPressed()
            }
        } catch (_: Exception) {

        }
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
            Log.d("NavCurrScreenID", "${navController.currentDestination?.id}")
            if (navController.currentDestination?.id == R.id.completeTaskFragment || navController.currentDestination?.id == R.id.dailyWorkFragment || navController.currentDestination?.id == R.id.homeFragment) {
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

     fun showAlertLogout() {
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


    /*private fun cqSDKInitializer() {
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
    }*/

    private fun cqSDKInitializer() {
        cqSDKInitializer = CQSDKInitializer(this)
        cqSDKInitializer.triggerOfflineSync()
        if (!cqSDKInitializer.isCQSDKInitialized()) {
            Log.e("intialized", "cqSDKInitializer: ")
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


    fun hideDialog() {
        apiCount--
        if (apiCount <= 0) {
            loadingDialog.cancel()
            apiCount = 0
            viewModel.ldcompleteTaskLayoutObserver.postValue(-1)
        }
    }

    public fun showDialog() {
        if (apiCount == 0) {
            loadingDialog.show()
        }
        viewModel.ldcompleteTaskLayoutObserver.postValue(0)
        apiCount++
    }

    fun GetDriversBasicInformation() {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM")
        date = today.format(formatter)
        showDialog()
        viewModel.GetDriversBasicInformation(
            Prefs.getInstance(App.instance).userID.toDouble()
        ).observe(this, Observer {
            hideDialog()
            if (it != null) {
                try {
                    try {
                        Prefs.getInstance(this).vmRegNo = it.vmRegNo!!
                        viewModel.GetVehicleInformation(userId, it.vmRegNo)
                        if (it.currentlocation != null)
                            Prefs.getInstance(this).currLocationName = it.currentlocation
                        if (it.workinglocation != null)
                            Prefs.getInstance(this).workLocationName = it.workinglocation
                    } catch (e: Exception) {
                        Log.e("GetVehicleInformation Exception", "$e")
                    }
                    Prefs.getInstance(this).lmid = it.lmID
                    lmId = it.lmID
                } catch (e: Exception) {
                    Log.d("sds", e.toString())
                }
                firstName = it.firstName
                lastName = it.lastName
                Prefs.getInstance(this).userName = "$firstName $lastName"
                isLeadDriver = it.IsLeadDriver
                ninetydaysBoolean = it.IsUsrProfileUpdateReqin90days
                if (it.IsUsrProfileUpdateReqin90days.equals(true)) {
                    Prefs.getInstance(applicationContext).days = "1"
                    showAlertChangePasword90dys()
                } else {
                    Prefs.getInstance(applicationContext).days = "0"
                }
            }
        })
    }

    fun showAlertChangePasword90dys() {
        val factory = LayoutInflater.from(this)
        val view: View = factory.inflate(R.layout.change_passwordninetydays, null)
        val deleteDialog: android.app.AlertDialog = android.app.AlertDialog.Builder(this).create()
        deleteDialog.setView(view)

        val button: TextView = view.findViewById(R.id.save)
        button.setOnClickListener {
            navController.navigate(R.id.profileFragment)

//            Prefs.getInstance(App.instance).save("90days", "1")
            deleteDialog.dismiss()


        }
        deleteDialog.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                true
            } else {
                false
            }
        }
        deleteDialog.setCancelable(false)
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        deleteDialog.show();

    }

    fun disableBottomNavigationView() {
        bottomNavigationView.visibility = View.GONE
//        bottomNavigationView. = false
//        bottomNavigationView.isClickable=false
    }

    fun enableBottomNavigationView() {
        bottomNavigationView.visibility = View.VISIBLE
//        bottomNavigationView.isEnabled = true
//        bottomNavigationView.isClickable=true
    }
}