package com.clebs.celerity_admin

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment

class MyApplicationIntro :  AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(
            AppIntroFragment.createInstance(
                title = "WELCOME TO CLS OSM",
                description = "We are the friendly face of our business - the helpful driver who delivers the unique blend of quality and outstanding service right to our customers' door.",
                imageDrawable = R.drawable.cls_final_logo,
                backgroundDrawable = R.drawable.back_four,
                titleColorRes = R.color.black,
                descriptionColorRes = R.color.black,

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

            )
        )

        addSlide(
            AppIntroFragment.createInstance(
                title = "LETS GO TO OSM",
                description = "We are the fastest growing and most stable industry to be in right now!)",
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
            LoginActivity::class.java
        )

        startActivity(i)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        val i = Intent(
            this@MyApplicationIntro,
            LoginActivity::class.java
        )

        startActivity(i)
        finish()

    }
}
