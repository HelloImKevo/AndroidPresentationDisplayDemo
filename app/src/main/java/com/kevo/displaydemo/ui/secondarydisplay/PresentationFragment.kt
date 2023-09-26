package com.kevo.displaydemo.ui.secondarydisplay

import android.app.Dialog
import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Display
import androidx.fragment.app.DialogFragment

open class PresentationFragment : DialogFragment() {

    private var display: Display? = null
    private var preso: Presentation? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return if (preso == null) {
            super.onCreateDialog(savedInstanceState)
        } else {
            preso!!
        }
    }

    fun setDisplay(themedContext: Context, themeResourceId: Int, display: Display?) {
        if (display == null) {
            preso = null
        } else {
            if (themeResourceId == 0) {
                Log.w(
                    TAG,
                    "The Theme Resource ID has not been initialized. The app will probably crash."
                )
            }

            /*
            Currently, we must past a Theme Resource ID, and if we don't pass the right Theme
            Resource ID, we will get an InflateException crash:

            Caused by: java.lang.IllegalArgumentException: The style on this component requires your app theme to be
            Theme.MaterialComponents (or a descendant).
              at com.google.android.material.internal.ThemeEnforcement.checkTheme(ThemeEnforcement.java:243)
              at com.google.android.material.internal.ThemeEnforcement.checkMaterialTheme(ThemeEnforcement.java:217)
              at com.google.android.material.internal.ThemeEnforcement.checkCompatibleTheme(ThemeEnforcement.java:145)
              at com.google.android.material.internal.ThemeEnforcement.obtainStyledAttributes(ThemeEnforcement.java:76)
              at com.google.android.material.button.MaterialButton.<init>(MaterialButton.java:229)
             */
            preso = Presentation(themedContext, display, themeResourceId)
        }

        this.display = display
    }

    fun getDisplay() = display

    override fun getContext(): Context? {
        return if (preso != null) {
            preso!!.context
        } else {
            super.getContext()
        }
    }

    companion object {

        const val TAG = "PresentationFragment"
    }
}
