package com.clebs.celerity_admin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment

class MyApplicationIntro : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(
            AppIntroFragment.createInstance(
                title = "WELCOME TO CLS - OSM",
                description = "We are the friendly face of our business - Providing OSM features right away .",
                imageDrawable = R.drawable.cls_final_logo,
                backgroundDrawable = R.drawable.back_three,
                titleColorRes = R.color.white,
                descriptionColorRes = R.color.white,

                )
        )
        addSlide(
            AppIntroFragment.createInstance(
                title = "THE GREAT STUFF WE OFFER",
                description = "1. Pallet Service \n 2. Man Delivery \n 3. First Mile/Middle Mile/Last Mile \n 4. Same Day & Express Delivery \n 5. Warehousing \n 6. Customized Solutions",
                imageDrawable = R.drawable.we,
                backgroundDrawable = R.drawable.back_slide,
                titleColorRes = R.color.white,
                descriptionColorRes = R.color.white,

                )
        )

        addSlide(
            AppIntroFragment.createInstance(
                title = "LETS GET STARTED",
                description = "We are the fastest growing and most stable industry to be in right now.",
                imageDrawable = R.drawable.started,
                backgroundDrawable = R.drawable.back_three,
                titleColorRes = R.color.white,
                descriptionColorRes = R.color.white,

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


        val i = Intent(
            this@MyApplicationIntro,
            LoginActivityTwo::class.java
        )

        startActivity(i)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        val i = Intent(
            this@MyApplicationIntro,
            LoginActivityTwo::class.java
        )

        startActivity(i)
        finish()

    }
}
