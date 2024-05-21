package com.clebs.celerity.utils

import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit
import com.clebs.celerity.ui.App

object TutTrackerTwo {
    private const val PREF_HAS_SHOWN_TUTORIAL = "has_shown_tutorial_second"
    private val prefs: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.instance)
    }

    fun hasTutorialBeenShown(): Boolean {
        return prefs.getBoolean(PREF_HAS_SHOWN_TUTORIAL, false)
    }

    fun markTutorialAsShown() {
        prefs.edit {
            putBoolean(PREF_HAS_SHOWN_TUTORIAL, true)
        }
    }
}