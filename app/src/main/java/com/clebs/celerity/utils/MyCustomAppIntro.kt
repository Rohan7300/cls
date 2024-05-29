package com.clebs.celerity.utils

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.dialogs.NoInternetDialog
import com.clebs.celerity.models.response.SaveDeviceInformationRequest
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.ui.LoginActivity
import com.clebs.celerity.ui.PolicyDocsActivity
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


class MyCustomAppIntro : AppIntro() {

    lateinit var dialog: NoInternetDialog
    lateinit var fragmentManager: FragmentManager
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // Make sure you don't call setContentView!
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)


        /*mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo)).get(MainViewModel::class.java)*/


        mainViewModel = DependencyProvider.getMainVM(this)
        // Call addSlide passing your Fragments.
        // You can use AppIntroFragment to use a pre-built fragment
        addSlide(
            AppIntroFragment.createInstance(
                title = "Welcome to the CLS",
                description = "You will be the friendly face of our business - the helpful driver who delivers the unique blend of quality and outstanding service right to our customers' door.",
                imageDrawable = R.drawable.cls_final_logo,
                backgroundDrawable = R.drawable.back_four,
                titleColorRes = R.color.black,
                descriptionColorRes = R.color.black,
                titleTypefaceFontRes = R.font.poppins_semibold,
                descriptionTypefaceFontRes = R.font.poppins
            )
        )
        addSlide(
            AppIntroFragment.createInstance(
                title = "THE GREAT STUFF WE OFFER",
                description = "Two full training days given to you and paid at £86.00 per day = £172.00\n" +
                        "Performance Incentive Payment available up to £27.00 per day)",
                imageDrawable = R.drawable.we,
                backgroundDrawable = R.drawable.back_three,
                titleColorRes = R.color.black,
                descriptionColorRes = R.color.black,
                titleTypefaceFontRes = R.font.poppins_semibold,
                descriptionTypefaceFontRes = R.font.poppins
            )
        )

        addSlide(
            AppIntroFragment.createInstance(
                title = "LETS GET STARTED",
                description = "Join the fastest growing and most stable industry to be in right now!)",
                imageDrawable = R.drawable.started,
                backgroundDrawable = R.drawable.back_two,
                titleColorRes = R.color.black,
                descriptionColorRes = R.color.black,
                titleTypefaceFontRes = R.font.poppins_semibold,
                descriptionTypefaceFontRes = R.font.poppins
            )
        )
        // Fade Transition

        // Show/hide status bar
        //Enable the color "fade" animation between two slides (make sure the slide implements SlideBackgroundColorHolder)

        //Prevent the back button from exiting the slides


        //Enable immersive mode (no status and nav bar)
        setImmersiveMode();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }

        //Enable/disable page indicators
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Decide what to do when the user clicks on "Skip"
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Decide what to do when the user clicks on "Done"
        android.os.Handler().postDelayed({
            if (isLoggedIn()) {
                retrieveAndSaveFCMToken()
                navigateToHomeScreen()
            } else {
                navigateToLoginScreen()
            }
            finish()
        }, 2000)

    }

    private fun isLoggedIn(): Boolean {
        return Prefs.getInstance(applicationContext).getBoolean("isLoggedIn", false)
    }

    fun navigateToLoginScreen() {
        // Navigate to the login screen
        val i = Intent(
            this@MyCustomAppIntro,
            LoginActivity::class.java
        )

        startActivity(i)

    }

    fun navigateToHomeScreen() {

        if (Prefs.getInstance(applicationContext).getBoolean("isSignatureReq", false)
                .equals(true)
        ) {
            val i = Intent(
                this@MyCustomAppIntro,
                PolicyDocsActivity::class.java
            )
            startActivity(i)
        } else {
            val i = Intent(
                this@MyCustomAppIntro,
                HomeActivity::class.java
            )
            intent.putExtra("destinationFragment", "HomeFragment")
            intent.putExtra("actionToperform", "undef")
            intent.putExtra("actionID", "0")
            intent.putExtra("tokenUrl", "undef")
            intent.putExtra("notificationId", "0")
            startActivity(i)
        }
    }
        private fun retrieveAndSaveFCMToken() {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {

                    return@OnCompleteListener
                }

                val token = task.result
                    mainViewModel.SaveDeviceInformation(
                        SaveDeviceInformationRequest(
                            FcmToken = token,
                            UsrId = Prefs.getInstance(this).clebUserId.toInt(),
                            UsrDeviceId = getDeviceID(),
                            UsrDeviceType = "Android"
                        )
                    )

            })
        }

}