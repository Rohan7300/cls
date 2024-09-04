package com.clebs.celerity.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.AUTOFILL_TYPE_NONE
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
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
import com.clebs.celerity.ViewModel.OSyncVMProvider
import com.clebs.celerity.ViewModel.OSyncViewModel
import com.clebs.celerity.database.ImageDatabase
import com.clebs.celerity.database.ImagesRepo
import com.clebs.celerity.database.OfflineSyncEntity
import com.clebs.celerity.databinding.ActivityHomeBinding
import com.clebs.celerity.dialogs.BirthdayDialog
import com.clebs.celerity.dialogs.ExpiredDocDialog
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.dialogs.NoInternetDialog
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.DependencyProvider.getMainVM
import com.clebs.celerity.utils.DependencyProvider.handlingDeductionNotification
import com.clebs.celerity.utils.DependencyProvider.handlingExpiredDialogNotification
import com.clebs.celerity.utils.DependencyProvider.handlingRotaNotification
import com.clebs.celerity.utils.DependencyProvider.isComingBackFromFaceScan
import com.clebs.celerity.utils.DependencyProvider.notificationWatcher
import com.clebs.celerity.utils.DependencyProvider.notify
import com.clebs.celerity.utils.DependencyProvider.offlineSyncRepo
import com.clebs.celerity.utils.InspectionIncompleteDialog
import com.clebs.celerity.utils.InspectionIncompleteListener
import com.clebs.celerity.utils.NetworkManager
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.SaveChangesCallback
import com.clebs.celerity.utils.SaveVehicleInspection
import com.clebs.celerity.utils.checkIfInspectionFailed
import com.clebs.celerity.utils.checkTokenExpirationAndLogout
import com.clebs.celerity.utils.dailyRota
import com.clebs.celerity.utils.dbLog
import com.clebs.celerity.utils.deductions
import com.clebs.celerity.utils.expiredDocuments
import com.clebs.celerity.utils.expiringDocument
import com.clebs.celerity.utils.getCurrentAppVersion
import com.clebs.celerity.utils.getDeviceID
import com.clebs.celerity.utils.getVRegNo
import com.clebs.celerity.utils.invoiceReadyToView
import com.clebs.celerity.utils.isVersionNewer
import com.clebs.celerity.utils.logOSEntity
import com.clebs.celerity.utils.parseToInt
import com.clebs.celerity.utils.showBirthdayCard
import com.clebs.celerity.utils.showBreakDownDialog
import com.clebs.celerity.utils.showToast
import com.clebs.celerity.utils.showUpdateDialog
import com.clebs.celerity.utils.startUploadWithWorkManager
import com.clebs.celerity.utils.vehicleAdvancePaymentAgreement
import com.clebs.celerity.utils.vehicleExpiringDocuments
import com.clebs.celerity.utils.weeklyLocationRota
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.kotlinpermissions.notNull
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.singletons.PublicConstants
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class HomeActivity : AppCompatActivity(), NavController.OnDestinationChangedListener,
    SaveChangesCallback, InspectionIncompleteListener {
    private var saveChangesCallback: SaveChangesCallback? = null
    private var doubleBackToExitPressedOnce = false
    private lateinit var bottomNavigationView: BottomNavigationView
    lateinit var imageViewModel: ImageViewModel
    private var screenid: Int = 0
    private lateinit var navController: NavController
    lateinit var viewModel: MainViewModel
    private lateinit var navGraph: NavGraph
    lateinit var mainRepo: MainRepo
    lateinit var deleteDialogtwo: android.app.AlertDialog
    private var completeTaskScreen: Boolean = false
    private lateinit var cqSDKInitializer: CQSDKInitializer
    lateinit var fragmentManager: FragmentManager
    lateinit var internetDialog: NoInternetDialog
    var isNetworkActive: Boolean = true
    private var sdkkey = ""
    var clebuserID: Int = 0
    var firstName = ""
    lateinit var osData: OfflineSyncEntity
    var apiCount = 0
    val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(
        Date()
    )
    private var ninetydaysBoolean: Boolean? = null
    var lastName = ""
    var isLeadDriver = false
    lateinit var oSyncViewModel: OSyncViewModel
    lateinit var prefs: Prefs
    var date = ""
    lateinit var loadingDialog: LoadingDialog
    lateinit var loadingDialogtwo: LoadingDialog
    lateinit var networkManager: NetworkManager

    private var isApiResponseTrue = false
    private var trueCount = 0
    private var isChangesSaved = false
    lateinit var ActivityHomeBinding: ActivityHomeBinding

    companion object {
        fun showLog(tag: String, message: String) {
            Log.e(tag, message)
        }

        var checked: String? = ""
        var Boolean: Boolean = false
        var lmId: Int = 0
    }

    fun getAutofillType(): Int {
        return AUTOFILL_TYPE_NONE
    }

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        //bottomNavigationView = ActivityHomeBinding.bottomNavigatinView
        bottomNavigationView = ActivityHomeBinding.bottomNavigatinView

        fragmentManager = this.supportFragmentManager
        internetDialog = NoInternetDialog()
        networkManager = NetworkManager(this)

        loadingDialogtwo = LoadingDialog(this@HomeActivity)
        networkManager.observe(this) {
            isNetworkActive = if (it) {
                ActivityHomeBinding.nointernetLL.visibility = View.GONE
                true
            } else {
                ActivityHomeBinding.nointernetLL.visibility = View.VISIBLE
                false
            }
        }
        deleteDialogtwo = android.app.AlertDialog.Builder(this).create()
        prefs = Prefs.getInstance(this)
        checkTokenExpirationAndLogout(this, prefs)
        loadingDialog = LoadingDialog(this)
        sdkkey = "09f36b6e-deee-40f6-894b-553d4c592bcb.eu"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.importantForAutofill =
                View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS;
        }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val todayDate = dateFormat.format(Date())
        try {
            this.osData = DependencyProvider.osData
        } catch (_: Exception) {

        }

        val osRepo = offlineSyncRepo(this)
        oSyncViewModel = ViewModelProvider(
            this, OSyncVMProvider(osRepo, prefs.clebUserId.toInt(), todayDate)
        )[OSyncViewModel::class.java]

        val inspectionFailedDialog = InspectionIncompleteDialog()
        inspectionFailedDialog.setListener(this)

        oSyncViewModel.osData.observe(this) {
            logOSEntity("HomeActivity", it)
            osData = it
            DependencyProvider.osData = it
            if (it.isIni) {
                osData.vehicleID = prefs.scannedVmRegNo
                osData.dawDate = todayDate
                if (checkIfInspectionFailed(it)) {
                    inspectionFailedDialog.showDialog(this.supportFragmentManager)
                }
            } else {
                osData.clebID = prefs.clebUserId.toInt()
                osData.dawDate = todayDate
                osData.vehicleID = prefs.scannedVmRegNo
                osData.isIni = true
            }
        }
        cqSDKInitializer()
        clebuserID = prefs.clebUserId.toInt()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navController.addOnDestinationChangedListener(this)
        fragmentManager = this.supportFragmentManager
        bottomNavigationView.selectedItemId = R.id.home
        bottomNavigationView.menu.findItem(R.id.daily).setTooltipText("Daily work")
        val menu: MenuItem = ActivityHomeBinding.navView.menu.findItem(R.id.EnableDisableBio)
        if (isLoggedInBio()) {
            Log.e("kdjfjkfdk", "onCreate: ")
            menu.setTitle("Disable Biometric")
        } else {
            Log.e("kdjfjkfdktwo", "onCreate: ")
            menu.setTitle("Enable Biometric")
        }
        getDeviceID()
        val deviceID =
            Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
                .toString()
        Log.e("kjkcjkvckvck", "onCreate: " + deviceID)

        try {
            val apiService = RetrofitService.getInstance().create(ApiService::class.java)
            mainRepo = MainRepo(apiService)
            val imagesRepo =
                ImagesRepo(ImageDatabase.invoke(this), Prefs.getInstance(applicationContext))

            viewModel =
                ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]
            observers()
            viewModel.GetNotificationListByUserId(prefs.clebUserId.toInt())

            getScannedNumberVehicleInfo()
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
                        if (screenid == 0 || screenid == R.id.newCompleteTaskFragment) {
                            navController.navigate(R.id.homeFragment)
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
                        navController.navigate(R.id.newCompleteTaskFragment)
                    }
                } else {
                    try {
                        navController.navigate(R.id.homeFragment)
                        navController.currentDestination!!.id = R.id.homeFragment
                    } catch (_: Exception) {

                    }
                    /*                    navController.navigate(R.id.homeFragment)
                                        navController.currentDestination!!.id = R.id.homeFragment*/
                }
            }

            viewModel.GetDAVehicleExpiredDocuments(prefs.clebUserId.toInt())
            val expiredDocDialog = ExpiredDocDialog()
            viewModel.liveDataGetDAVehicleExpiredDocuments.observe(this) {
                if (it != null) {
                    prefs.saveExpiredDocuments(it)
                    expiredDocDialog.showDialog(supportFragmentManager)
                    expiredDocDialog.isCancelable = false
                } else {
                    viewModel.GetDeductionAgreement(prefs.clebUserId.toInt(), 0)
                }
            }

            notificationObserver()
            viewModel.WeeklyRotaExistForDAApproval(Prefs.getInstance(this@HomeActivity).clebUserId.toInt())
            viewModel.liveDataDeductionAgreement.observe(this) {
                if (it != null) {
                    if (!handlingDeductionNotification) {
                        val intent =
                            Intent(this@HomeActivity, DeductionAgreementActivity::class.java)
                        intent.putExtra("actionID", it.DaDedAggrId)
                        intent.putExtra("notificationID", it.NotificationId)
                        startActivity(intent)
                    }
                } else {
                    viewModel.GetVehBreakDownInspectionInfobyDriver(prefs.clebUserId.toInt())
                }
            }

            viewModel.liveDataVehBreakDownInspectionInfobyDriverResponse.observe(this){
                if(it!=null){
                    /*if(it.size>0)
                    showBreakDownDialog(fragmentManager)*/
                }
            }

            viewModel.liveDataWeeklyRotaExistForDAApproval.observe(this) {
                if (it != null) {
                    it.Data[0].notNull { itx ->
                        if (!handlingRotaNotification) {
                            val intent =
                                Intent(this@HomeActivity, WeeklyRotaApprovalActivity::class.java)
                            intent.putExtra("actionID", itx.LrnId)
                            intent.putExtra("notificationID", itx.NotificationId)
                            prefs.updateWeeklyRotaApprovalCheck(true)
                            startActivity(intent)
                        }
                    }
                } else {
                    //prefs.updateWeeklyRotaApprovalCheck(false)
                }
            }

            imageViewModel = ViewModelProvider(
                this, ImageViewModelProviderFactory(imagesRepo)
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
                        } else {
                            val prefs = Prefs.getInstance(applicationContext)
                            val fragmentStack = prefs.getNavigationHistory()
                            Log.d("NavCurrScreenID", "${navController.currentDestination?.id}")
                            if (navController.currentDestination?.id == R.id.newCompleteTaskFragment
                                || navController.currentDestination?.id == R.id.dailyWorkFragment ||
                                navController.currentDestination?.id == R.id.homeFragment
                            ) {
                                bottomNavigationView.selectedItemId = R.id.home

                                prefs.clearNavigationHistory()
                            } else if (navController.currentDestination?.id == R.id.invoicesFragment ||
                                navController.currentDestination?.id == R.id.userTicketsFragment ||
                                navController.currentDestination?.id == R.id.profileFragment ||
                                navController.currentDestination?.id == R.id.notifficationsFragment
                            ) {
                                bottomNavigationView.selectedItemId = R.id.home
                            } else if (navController.currentDestination?.id == R.id.onRoadHoursFragment ||
                                navController.currentDestination?.id == R.id.rideAlongFragment ||
                                navController.currentDestination?.id == R.id.updateOnRoadHoursFragment
                                || navController.currentDestination?.id == R.id.questinareFragment ||
                                navController.currentDestination?.id == R.id.feedbackFragment
                            ) {
                                bottomNavigationView.selectedItemId = R.id.daily
                                prefs.clearNavigationHistory()
                            } else if (navController.currentDestination?.id == R.id.CLSInvoicesFragment ||
                                navController.currentDestination?.id == R.id.CLSThirdPartyFragment
                            ) {
                                bottomNavigationView.selectedItemId = R.id.invoicesb
                            } else if (fragmentStack.size > 1) {
                                fragmentStack.pop()
                                val previousFragment = fragmentStack.peek()
                                if (previousFragment != R.id.dailyWorkFragment) {
                                    navController.navigate(previousFragment)
                                    prefs.saveNavigationHistory(fragmentStack)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("HomeActivity", "BackException ${e.printStackTrace()}")
                    }

                    if (doubleBackToExitPressedOnce) {
                        //finishAffinity()
                        moveTaskToBack(true)
                    }

                    doubleBackToExitPressedOnce = true
                    Snackbar.make(
                        ActivityHomeBinding.bottomNavigatinView,
                        "Please click BACK again to exit",
                        Snackbar.LENGTH_SHORT
                    ).setAction("Action", null).show()

                    Handler(Looper.getMainLooper()).postDelayed({
                        doubleBackToExitPressedOnce = false
                    }, 2000)
                }
            })

            imageViewModel.images.observe(this) { imageEntity ->
                dbLog(imageEntity)
            }

            ActivityHomeBinding.imgDrawer.setOnClickListener {
                //navController.navigate(R.id.profileFragment)
                ActivityHomeBinding.drawerLayout.openDrawer(GravityCompat.START)
            }

            ActivityHomeBinding.imgNotification.setOnClickListener {
                ActivityHomeBinding.title.text = "Notifications"
                navController.navigate(R.id.notifficationsFragment)
                notify.postValue(false)

            }


            bottomNavigationView.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {

                    R.id.home -> {
                        ActivityHomeBinding.title.text = "Home"
                        ActivityHomeBinding.logout.visibility = View.GONE
                        ActivityHomeBinding.imgNotification.visibility = View.VISIBLE
                        navController.navigate(R.id.homedemoFragment)
                        true
                    }

                    R.id.daily -> {
                        /*navController.navigate(R.id.homeFragment)
                        navController.currentDestination!!.id = R.id.homeFragment*/

                        ActivityHomeBinding.searchLayout.visibility = View.VISIBLE
                        showDialogtwo()
                        if (isNetworkActive) {

                            ActivityHomeBinding.logout.visibility = View.GONE
                            ActivityHomeBinding.title.text = "Routes"
                            ActivityHomeBinding.imgNotification.visibility = View.VISIBLE
                            viewModel.GetVehicleDefectSheetInfo(
                                Prefs.getInstance(
                                    applicationContext
                                ).clebUserId.toInt()
                            )
//                            showDialog()
                            hidedialogtwo()
                            ActivityHomeBinding.searchLayout.visibility = View.GONE
                        } else {
                            hidedialogtwo()
                            ActivityHomeBinding.searchLayout.visibility = View.GONE
                            if (osData.isDefectSheetFilled)
                                navController.navigate(R.id.newCompleteTaskFragment)
                            else {
                                navController.navigate(R.id.homeFragment)
                                navController.currentDestination!!.id = R.id.homeFragment
                            }
                        }
                        true
                    }

                    R.id.invoicesb -> {
                        ActivityHomeBinding.title.text = "Invoices"
                        ActivityHomeBinding.logout.visibility = View.GONE
                        ActivityHomeBinding.imgNotification.visibility = View.VISIBLE
                        navController.navigate(R.id.invoicesFragment)
                        true
                    }

                    R.id.tickets -> {
                        ActivityHomeBinding.logout.visibility = View.GONE
                        ActivityHomeBinding.title.text = "Tickets"
                        ActivityHomeBinding.imgNotification.visibility = View.VISIBLE
                        navController.navigate(R.id.userTicketsFragment)

                        true

                    }

                    else -> false
                }
            }

            ActivityHomeBinding.navView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home -> {
                        ActivityHomeBinding.title.text = "Home"
                        ActivityHomeBinding.logout.visibility = View.GONE
                        ActivityHomeBinding.imgNotification.visibility = View.VISIBLE
                        bottomNavigationView.selectedItemId = R.id.home
                        //     navController.navigate(R.id.homedemoFragment)
                        true
                    }

                    R.id.daily -> {/*     navController.navigate(R.id.homeFragment)
                             navController.currentDestination!!.id = R.id.homeFragment
         */
                        bottomNavigationView.selectedItemId = R.id.daily
                        /*                        if (isNetworkActive) {

                                                    ActivityHomeBinding.logout.visibility = View.GONE
                                                    ActivityHomeBinding.title.text = ""
                                                    ActivityHomeBinding.imgNotification.visibility = View.VISIBLE
                                                    viewModel.GetVehicleDefectSheetInfo(Prefs.getInstance(applicationContext).clebUserId.toInt())
                                                    showDialog()
                                                } else {
                                                    if (osData.isDefectSheetFilled) navController.navigate(R.id.newCompleteTaskFragment)
                                                    else {
                                                        //navController.navigate(R.id.homeFragment)
                                                        bottomNavigationView.selectedItemId = R.id.home
                                                        navController.currentDestination!!.id = R.id.homeFragment
                                                    }
                                                }*/
                        true
                    }

                    R.id.invoices -> {
                        bottomNavigationView.selectedItemId = R.id.invoicesb
                        /*                        ActivityHomeBinding.title.text = "Invoices"
                                                ActivityHomeBinding.logout.visibility = View.GONE
                                                ActivityHomeBinding.imgNotification.visibility = View.VISIBLE
                                                navController.navigate(R.id.invoicesFragment)*/
                        true
                    }

                    R.id.tickets -> {
                        bottomNavigationView.selectedItemId = R.id.tickets
                        /*               ActivityHomeBinding.logout.visibility = View.GONE
                                       ActivityHomeBinding.title.text = "User Tickets"
                                       ActivityHomeBinding.imgNotification.visibility = View.VISIBLE
                                       navController.navigate(R.id.userTicketsFragment)*/

                        true

                    }

                    R.id.profileBtn -> {
                        navController.navigate(R.id.profileFragment)
                        true
                    }

                    R.id.emergencyBtn -> {
                        startActivity(
                            Intent(
                                this@HomeActivity,
                                EmergencyContactActivity::class.java
                            )
                        )
                        //showToast("Under Development!!", this@HomeActivity)
                    }

                    R.id.signedDocs -> {
                        startActivity(Intent(this@HomeActivity, SignedDocActivity::class.java))
                    }

                    R.id.deductionAgreements -> {
                        startActivity(
                            Intent(
                                this@HomeActivity,
                                OutstandingDeductionActivity::class.java
                            )
                        )
                    }

                    R.id.nextWeekSchedule -> {
                        startActivity(
                            Intent(
                                this@HomeActivity,
                                NextWeekScheduleActivity::class.java
                            )
                        )
                    }

                    R.id.logoutNav -> {
                        showAlertLogout()
                    }

                    R.id.WeeklyPerformanceBtn -> {

                        startActivity(
                            Intent(
                                this@HomeActivity,
                                WeeklyPerformanceActivity::class.java
                            )
                        )
                    }

                    R.id.EnableDisableBio -> {
                        if (isLoggedInBio()) {
                            Prefs.getInstance(this).useBiometric = false
                            val snackbar = Snackbar
                                .make(
                                    ActivityHomeBinding.drawerLayout,
                                    "Biometric Auth is disabled",
                                    Snackbar.LENGTH_LONG
                                )
                                .setAction(
                                    "Ok"
                                )  // If the Undo button
// is pressed, show
// the message using Toast
                                {

                                }

                            snackbar.show()
                            setLoggedInBio(false)
                            menu.setTitle("Enable Biometric")

                        } else {
                            Prefs.getInstance(this).useBiometric = true
                            val snackbar = Snackbar
                                .make(
                                    ActivityHomeBinding.drawerLayout,
                                    "Biometric Auth is Enabled",
                                    Snackbar.LENGTH_LONG
                                )
                                .setAction(
                                    "Ok"
                                )  // If the Undo button
// is pressed, show
// the message using Toast
                                {

                                }

                            snackbar.show()
                            setLoggedInBio(true)
                            menu.setTitle("Disable Biometric")

                        }

//                        showAlertLogout()
                    }

                    else -> return@OnNavigationItemSelectedListener false
                }
                ActivityHomeBinding.drawerLayout.closeDrawer(GravityCompat.START)
                true
            })

            ActivityHomeBinding.logout.setOnClickListener {
                showAlertLogout()
            }
        } catch (e: Exception) {
            RetrofitService.handleNetworkError(e, fragmentManager)
        }
        val destinationFragment = intent.getStringExtra("destinationFragment")
        if (destinationFragment != null) {

            Log.d("HomeActivityX", destinationFragment!!)
            if (destinationFragment == "NotificationsFragment") {
                ActivityHomeBinding.title.text = "Notifications"
                navController.navigate(R.id.notifficationsFragment)
            } else if (destinationFragment == "ThirdPartyAcess") {
                navController.navigate(R.id.profileFragment)
            }
        } else if (destinationFragment == "CompleteTask") {
            navController.navigate(R.id.newCompleteTaskFragment)
        } else if (destinationFragment == "ThirdPartyAcess") {
            navController.navigate(R.id.profileFragment)
        }
    }

    private fun notificationObserver() {
        notificationWatcher.observe(this) { nType ->
            if (nType != 0) {
                when (nType) {
                    1 -> {
                        viewModel.GetDAVehicleExpiredDocuments(prefs.clebUserId.toInt())
                    }

                    2 -> {
                        viewModel.GetDeductionAgreement(prefs.clebUserId.toInt(), 0)
                    }

                    3 -> {
                        viewModel.WeeklyRotaExistForDAApproval(Prefs.getInstance(this@HomeActivity).clebUserId.toInt())
                    }
                }
            }
        }
    }

    private fun observers() {


        viewModel.liveDataGetLatestAppVersion.observe(this) {
            val currentAppVersion = getCurrentAppVersion(this)
            if (it != null) {
                if (isVersionNewer(currentAppVersion, it.AndroidAppVersion)) {
                    val playStoreUrl =
                        "https://play.google.com/store/apps/details?id=com.clebs.celerity&hl=en"
                    showUpdateDialog(this, playStoreUrl)
                } else {
                    viewModel.GetDAVehicleExpiredDocuments(prefs.clebUserId.toInt())
                }
            } else {
                viewModel.GetDAVehicleExpiredDocuments(prefs.clebUserId.toInt())
                showToast("Failed to fetch the latest app version", this@HomeActivity)
            }

        }

        viewModel.vechileInformationLiveData.observe(this) {
            if (it != null) {
                prefs.VinNumber = it.VinNumber
                prefs.VehicleMake = it.VehicleMake
                prefs.VehicleBodyStyle = it.VehicleBodyStyle ?: "Van"
                prefs.VehicleModel = it.VehicleModel ?: "Any Model"
                prefs.VmCreatedDate = it.VmCreatedDate
            }
        }
        notify.observe(this) {
            if (it == true) {
                ActivityHomeBinding.newnotify.visibility = View.VISIBLE
            } else {
                ActivityHomeBinding.newnotify.visibility = View.GONE
            }
        }

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

            handleNotifications(intent)

            val tempCode =
                intent.getIntExtra(PublicConstants.quoteCreationFlowStatusCodeKeyInIntent, -1)

            if (tempCode == 200) {
                Log.d("hdhsdshdsdjshhsds", "200 $message")
                prefs.saveBoolean("Inspection", true)
                //prefs.updateInspectionStatus(true)
                //inspectionstarted = true

                val currentDate =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()).format(
                        Date()
                    )

                val currentloction = Prefs.getInstance(App.instance).currLocationId
                val workinglocation = Prefs.getInstance(App.instance).workLocationId
                val locationID: Int
                locationID = if (workinglocation != 0) {
                    workinglocation
                } else {
                    currentloction
                }

                /*                viewModel.SaveVehicleInspectionInfo(
                                    SaveVehicleInspectionInfo(
                                        Prefs.getInstance(App.instance).clebUserId.toInt(),
                                        currentDate,
                                        Prefs.getInstance(App.instance).inspectionID,
                                        locationID,
                                        Prefs.getInstance(App.instance).VmID.toString().toInt()
                                    )
                                )*/

                viewModel.livedataSavevehicleinspectioninfo.observe(this, Observer {
                    if (it != null) {
                        if (it.Message.equals("200")) Log.e(
                            "verygood",
                            "onNewIntent: " + it.Message
                        )
                        showToast("Vehicle Inspection info saved", this)
                    }
                })
                //  navController.navigate(R.id.newCompleteTaskFragment)
                showToast("Vehicle Inspection is successfully completed ", this)
            } else {
                Log.d("hdhsdshdsdjshhsds", "else $message")
                //    navController.navigate(R.id.newCompleteTaskFragment)
            }
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

    private fun handleNotifications(intent: Intent) {
        val destinationFragment = intent.getStringExtra("destinationFragment")
        val actionToPerform = intent.getStringExtra("actionToperform") ?: "undef"
        val tokenUrl = intent.getStringExtra("tokenUrl") ?: "undef"
        val actionID = intent.getStringExtra("actionID") ?: "0"
        val notificationID = intent.getStringExtra("notificationId") ?: "0"
        Log.d(
            "NotExtras",
            " $destinationFragment $actionToPerform $tokenUrl $actionID $notificationID"
        )
        if (destinationFragment != null) {
            Log.d("HomeActivityX", destinationFragment!!)
            if (destinationFragment == "NotificationsFragment") {

                if (actionToPerform == "Deductions" || actionToPerform == "Driver Deduction with Agreement" || actionToPerform == "DriverDeductionWithAgreement") {
                    if (!handlingDeductionNotification)
                        deductions(this, parseToInt(actionID), parseToInt(notificationID))
                } else if (actionToPerform == "Daily Location Rota" || actionToPerform == "Daily Rota Approval" || actionToPerform == "DailyRotaApproval") {
                    if (getMainVM(this) != null)
                        dailyRota(
                            getMainVM(this), tokenUrl, this, this, parseToInt(notificationID)
                        )
                    else {
                        ActivityHomeBinding.title.text = "Notifications"
                        navController.navigate(R.id.notifficationsFragment)
                        return
                    }
                } else if (actionToPerform == "Invoice Ready To Review" || actionToPerform == "Invoice Ready to Review" || actionToPerform == "InvoiceReadyToReview") {
                    invoiceReadyToView(
                        parseToInt(notificationID),
                        supportFragmentManager,
                        "Your CLS Invoice is available for review."
                    )
                } else if (actionToPerform == "Weekly Location Rota" || actionToPerform == "Weekly Rota Approval" || actionToPerform == "WeeklyRotaApproval") {
                    if (!handlingRotaNotification)
                        weeklyLocationRota(
                            this, parseToInt(notificationID), parseToInt(actionID)
                        )
                } else if (actionToPerform == "Expired Document" || actionToPerform == "ExpiredDocuments") {
                    if (!handlingExpiredDialogNotification)
                        expiredDocuments(
                            getMainVM(this),
                            this,
                            this,
                            supportFragmentManager,
                            parseToInt(notificationID)
                        )
                } else if (actionToPerform.equals("Vehicle Advance Payment Aggrement") || actionToPerform.equals(
                        "Vehicle Advance Payment Agreement"
                    )
                ) {
                    vehicleAdvancePaymentAgreement(
                        this, parseToInt(notificationID), getMainVM(this), this
                    )
                } else if (actionToPerform.equals("Expiring Document") || actionToPerform.equals(
                        "ExpiringDocuments"
                    ) || actionToPerform.equals("UserExpiringDocuments")
                ) {
                    expiringDocument(
                        this, parseToInt(notificationID)
                    )
                } else if (actionToPerform == "VehicleExpiringDocuments") {
                    vehicleExpiringDocuments(this, parseToInt(notificationID))
                } else if (actionToPerform == "ThirdPartyAccessRequestNotification") {
                    navController.navigate(R.id.profileFragment)
                } else {
                    ActivityHomeBinding.title.text = "Notifications"
                    navController.navigate(R.id.notifficationsFragment)
                    return
                }/*              }
                                  catch (_: Exception) {
                                      ActivityHomeBinding.title.text = "Notifications"
                                      navController.navigate(R.id.notifficationsFragment)
                                      return
                                  }*/

                /*                    ActivityHomeBinding.title.text = "Notifications"
                                    navController.navigate(R.id.notifficationsFragment)
                                    return*/
                return
            } else if (destinationFragment == "CompleteTask") {
                navController.navigate(R.id.newCompleteTaskFragment)
            } else if (destinationFragment == "ThirdPartyAcess") {
                try {
                    viewModel.MarkNotificationAsRead(parseToInt(notificationID))
                } catch (_: Exception) {

                }

                navController.navigate(R.id.profileFragment)
            } else {
                navController.navigate(R.id.newCompleteTaskFragment)
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
        if (ActivityHomeBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            ActivityHomeBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            screenid = viewModel.getLastVisitedScreenId(this)
            if (Prefs.getInstance(App.instance).get("90days")
                    .equals("1") && navController.currentDestination?.id == R.id.profileFragment
            ) {
                showToast("Please do profile changes first", this)
            } else {
                Log.d("NavCurrScreenID", "screenid ${screenid}")
                try {
                    val prefs = Prefs.getInstance(applicationContext)
                    val fragmentStack = prefs.getNavigationHistory()
                    Log.d("NavCurrScreenID", "${navController.currentDestination?.id}")
                    if (navController.currentDestination?.id == R.id.newCompleteTaskFragment
                        || navController.currentDestination?.id == R.id.dailyWorkFragment ||
                        navController.currentDestination?.id == R.id.homeFragment
                    ) {
                        //navController.navigate(R.id.homedemoFragment)
                        bottomNavigationView.selectedItemId = R.id.home

                        prefs.clearNavigationHistory()
                    } else if (navController.currentDestination?.id == R.id.invoicesFragment ||
                        navController.currentDestination?.id == R.id.userTicketsFragment ||
                        navController.currentDestination?.id == R.id.profileFragment ||
                        navController.currentDestination?.id == R.id.notifficationsFragment
                    ) {
                        bottomNavigationView.selectedItemId = R.id.home
                    } else if (navController.currentDestination?.id == R.id.onRoadHoursFragment ||
                        navController.currentDestination?.id == R.id.rideAlongFragment ||
                        navController.currentDestination?.id == R.id.updateOnRoadHoursFragment
                        || navController.currentDestination?.id == R.id.questinareFragment ||
                        navController.currentDestination?.id == R.id.feedbackFragment
                    ) {
                        bottomNavigationView.selectedItemId = R.id.daily
                        prefs.clearNavigationHistory()
                    } else if (navController.currentDestination?.id == R.id.CLSInvoicesFragment ||
                        navController.currentDestination?.id == R.id.CLSThirdPartyFragment
                    ) {
                        bottomNavigationView.selectedItemId = R.id.invoicesb
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
                    bottomNavigationView.selectedItemId = R.id.home
                }
            }

        }
    }

    override fun onDestinationChanged(
        controller: NavController, destination: NavDestination, arguments: Bundle?
    ) {


    }

    private fun setLoggedIn(isLoggedIn: Boolean) {
        Prefs.getInstance(applicationContext).saveBoolean("isLoggedIn", isLoggedIn)
    }

    private fun isLoggedInBio(): Boolean {
        return Prefs.getInstance(applicationContext).getBoolean("isLoggedInBio", false)
    }

    private fun setLoggedInBio(isLoggedIn: Boolean) {
        Prefs.getInstance(applicationContext).saveBoolean("isLoggedInBio", isLoggedIn)
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
            if (navController.currentDestination?.id == R.id.newCompleteTaskFragment || navController.currentDestination?.id == R.id.dailyWorkFragment || navController.currentDestination?.id == R.id.homeFragment) {
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
        /*        val imageView: ImageView = view.findViewById(R.id.ic_cross_orange)
                imageView.setOnClickListener {
                    deleteDialog.dismiss()
                }*/
        val btone: Button = view.findViewById(R.id.bt_no)
        val bttwo: Button = view.findViewById(R.id.bt_yes)

        btone.setOnClickListener {
            deleteDialog.dismiss()
        }

        bttwo.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("logout", "0")
            intent.putExtra("downloadCQ", Prefs.getInstance(App.instance).isFirst)
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
        try {
            deleteDialog.show()
        } catch (_: Exception) {

        }

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
            cqSDKInitializer.initSDK(sdkKey = sdkkey, result = { isInitialized, code, _ ->
                if (isInitialized && code == PublicConstants.sdkInitializationSuccessCode) {
                    Prefs.getInstance(applicationContext).saveCQSdkKey(sdkkey)
                } else {
                    showToast("Error initializing SDK", this)
                }
            })
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

    public fun showDialogtwo() {
        loadingDialogtwo.show()
    }

    fun hidedialogtwo() {
        loadingDialogtwo.dismiss()
    }

    fun GetDriversBasicInformation() {

        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM")
        date = today.format(formatter)
        showDialog()
        viewModel.GetDriversBasicInformation(
            Prefs.getInstance(App.instance).clebUserId.toDouble()
        ).observe(this, Observer {
            hideDialog()
            if (it != null) {
                Log.e(
                    "GetDriversBasicInformationInspection",
                    "GetDriversBasicInformation: " + it.IsVehicleInspectionDone
                )
                if (it.workingLocationId != null) prefs.workLocationId = it.workingLocationId
                if (it.currentLocationId != null) prefs.currLocationId = it.currentLocationId
                try {
                    viewModel.GetVehicleInformation(
                        prefs.clebUserId.toInt(), prefs.vmId.toDouble()
                    )
                    if (it.currentlocation != null) prefs.currLocationName = it.currentlocation
                    if (it.workinglocation != null) prefs.workLocationName = it.workinglocation
                    prefs.lmid = it.lmID
                    lmId = it.lmID

                } catch (e: Exception) {
                    Log.d("sds", e.toString())
                }
                prefs.thridPartyAcess = it.IsThirdPartyChargeAccessAllowed
                prefs.UsrCreatedOn = it.UsrCreatedOn
                firstName = it.firstName
                lastName = it.lastName
                prefs.userName = "$firstName $lastName"
                val headerView: View = ActivityHomeBinding.navView.getHeaderView(0)
                val navHeaderName =
                    headerView.findViewById<TextView>(com.clebs.celerity.R.id.navHeaderName)
                navHeaderName.text = "Celerity - " + prefs.userName
                isLeadDriver = it.IsLeadDriver
                ninetydaysBoolean = it.IsUsrProfileUpdateReqin90days
                isApiResponseTrue = it.IsUsrProfileUpdateReqin90days
                Log.d("BirthdayDialog", " ${showBirthdayCard(it.UsrDOB, prefs)}")
                if (showBirthdayCard(it.UsrDOB, prefs)) {
                    val firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean(
                        "firstrun", true
                    )
                    if (firstrun) {
                        BirthdayDialog(prefs).showDialog(supportFragmentManager)
                        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                            .putBoolean("firstrun", false).apply()
                    }

                }
                if (isApiResponseTrue) {
                    trueCount++
                } else {
                    trueCount = 0
                }
                if (it.IsUsrProfileUpdateReqin90days.equals(true)) {
                    Prefs.getInstance(applicationContext).days = "1"
                    showAlertChangePasword90dys()
                } else {
                    Prefs.getInstance(applicationContext).days = "0"
                }

                /*                if (!it.IsVehicleInspectionDone) {
                                    if (prefs.isInspectionDoneToday()) SaveVehicleInspection(viewModel)
                                } else {
                                    prefs.updateInspectionStatus(true)
                                }*/
            }
        })

    }


    fun showAlertChangePasword90dys() {
        val factory = LayoutInflater.from(this)
        val view: View = factory.inflate(R.layout.change_passwordninetydays, null)
        val deleteDialog: android.app.AlertDialog = android.app.AlertDialog.Builder(this).create()
        deleteDialog.setView(view)
        val textView = view.findViewById<TextView>(R.id.keeping_you)
        val button: TextView = view.findViewById(R.id.save)
        if (isApiResponseTrue && trueCount >= 2 && isChangesSaved) {
            textView.setText("You have not updated your profile for 90 days, please update it.")
        } else {
            textView.setText(" Please update your information in case if you find it incorrect.")
        }
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
        deleteDialog.apply {
            saveChangesCallback = this@HomeActivity
        }
        deleteDialog.show();

    }

    fun disableBottomNavigationView() {
        bottomNavigationView.visibility = View.GONE
        ActivityHomeBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        ActivityHomeBinding.imgDrawer.visibility = View.GONE
//        ActivityHomeBinding.imgNotification.visibility = View.GONE
//        bottomNavigationView. = false
//        bottomNavigationView.isClickable=false
    }

    fun enableBottomNavigationView() {
        bottomNavigationView.visibility = View.VISIBLE
        ActivityHomeBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        ActivityHomeBinding.imgDrawer.visibility = View.VISIBLE
//        ActivityHomeBinding.imgNotification.visibility = View.GONE
//        bottomNavigationView.isEnabled = true
//        bottomNavigationView.isClickable=true
    }

    override fun onChangesSaved() {
        isChangesSaved = true
    }

    private fun getScannedNumberVehicleInfo() {
        viewModel.GetVehicleInfobyDriverId(
            Prefs.getInstance(App.instance).clebUserId.toInt(), currentDate
        )
        viewModel.livedataGetVehicleInfobyDriverId.observe(this) {
            if (it != null) {
                Prefs.getInstance(App.instance).scannedVmRegNo = it.vmRegNo
                if (Prefs.getInstance(App.instance).vmId == 0) {
                    Prefs.getInstance(App.instance).vmId = it.vmId.toString().toInt()
                }
            }
            GetDriversBasicInformation()
        }
    }

    override fun onButtonClick() {
        val intent = Intent(this, AddInspectionActivity2::class.java)
        startActivity(intent)
    }


    override fun onResume() {
        super.onResume()
        try {
            handlingDeductionNotification = false
            handlingRotaNotification = false
            handlingExpiredDialogNotification = false
            if (oSyncViewModel != null || this::oSyncViewModel.isInitialized) {
                oSyncViewModel.getData()
                if (isComingBackFromFaceScan) {
                    navController.navigate(R.id.newCompleteTaskFragment)
                }
            }


            viewModel.GetLatestAppVersion()
            //if (!prefs.isPolicyCheckToday()) {
            viewModel.getDriverSignatureInfo(prefs.clebUserId.toDouble())
                .observe(this, Observer {
                    if (it != null) {
                        loadingDialog.dismiss()
                        prefs.handbookId = it.handbookId
                        prefs.updatePolicyCheckToday(true)
                        if (it!!.isSignatureReq.equals(true) && (it.isAmazonSignatureReq || it.isOtherCompanySignatureReq)) {
                            Prefs.getInstance(applicationContext)
                                .saveBoolean("isSignatureReq", it.isSignatureReq)
                            Prefs.getInstance(applicationContext)
                                .saveBoolean("IsamazonSign", it.isAmazonSignatureReq)
                            Prefs.getInstance(applicationContext)
                                .saveBoolean("isother", it.isOtherCompanySignatureReq)


                            val intent = Intent(this, PolicyDocsActivity::class.java)

                            intent.putExtra("signature_required", "0")
                            startActivity(intent)
                            finish()
                        }
                    }
                })
            // }

        } catch (_: Exception) {

        }
    }


}