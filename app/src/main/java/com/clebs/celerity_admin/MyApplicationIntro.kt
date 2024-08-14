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
                title = "WELCOME TO CLS OSM",
                description = "We are the friendly face of our business - Providing OSM features right away .",
                imageDrawable = R.drawable.cls_final_logo,
                backgroundDrawable = R.drawable.back_four,
                titleColorRes = R.color.black,
                descriptionColorRes = R.color.black,

                )
        )
        addSlide(
            AppIntroFragment.createInstance(
                title = "THE GREAT STUFF WE OFFER",
                description = "1. PALLET SERVICE \n 2. MAN DELIVERY\n 3.FIRST MILE/MIDDLE MILE/LAST MILE \n 4. SAME DAY & EXPRESS DELIVERY \n 5. WAREHOUSING \n 6. CUSTOMISED SOLUTIONS",
                imageDrawable = R.drawable.we,
                backgroundDrawable = R.drawable.back_three,
                titleColorRes = R.color.black,
                descriptionColorRes = R.color.black,

                )
        )

        addSlide(
            AppIntroFragment.createInstance(
                title = "LETS GO TO OSM",
                description = "We are the fastest growing and most stable industry to be in right now.",
                imageDrawable = R.drawable.started,
                backgroundDrawable = R.drawable.back_two,
                titleColorRes = R.color.black,
                descriptionColorRes = R.color.black,

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
