package com.kevo.displaydemo.ui.secondarydisplay

import android.content.Context
import android.hardware.display.DisplayManager
import android.hardware.display.DisplayManager.DisplayListener
import android.util.Log
import android.view.Display

/**
 * Manages listening to DisplayManager display state changes and using display manager to setup a
 * Presentation to display on a secondary screen.
 *
 * In order to use PresentationHelper forward your Activity/Fragment onResume()/onPause() callbacks
 * to [PresentationHelper.onResume] and [PresentationHelper.onPause] respectively so it can set
 * things up and clean up after itself.
 */
class PresentationHelper(val context: Context, val listener: Listener): DisplayListener {
    private var isFirstRun = true
    private var manager: DisplayManager? = null
    private var currentDisplay: Display? = null

    // Get the DisplayManager and register ourself as a listener for display changes
    fun onResume() {
        manager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        handleRoute()
        manager?.registerDisplayListener(this, null)
    }

    // Do housekeeping to clear the current display and unregister ourself from the DisplayManager
    fun onPause() {
        listener.clearPreso(false)
        currentDisplay = null
        manager?.unregisterDisplayListener(this)
    }

    /**
     * Handles most of the business logic for getting a potential display to use from the
     * DisplayManager and setting it up depending on our current state. IE: Whether we're setting
     * up a new display for the first time, swapping from a disconnected display to a newly
     * connected display, or the display we were using was simply disconnected and we have to
     * stop what we were doing.
     */
    private fun handleRoute() {
        if (manager == null) {
            Log.i(TAG, "handleRoute() called but DisplayManager is null!")
            return
        }

        // Get a list of all displays sorted with the best option for a secondary display first
        val displays = manager!!.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION)

        if (displays.isEmpty()) {
            // No valid display is connected so clear the current presentation if we haven't already
            if (currentDisplay != null || isFirstRun) {
                listener.clearPreso(true)
                currentDisplay = null
            }
        } else {
            // At least one valid secondary display is present for us to setup
            // TODO test if this code still runs if there is only 1 display (the main one)
            //  ideally it doesn't
            val newDisplay: Display = displays[0]

            if (newDisplay.isValid) {
                if (currentDisplay == null) {
                    // The new display is valid and there is no old one to clear
                    listener.showPreso(newDisplay)
                    currentDisplay = newDisplay
                } else if (currentDisplay?.displayId != newDisplay.displayId) {
                    // The new display is valid and an the old one needs to be cleared
                    listener.clearPreso(true)
                    listener.showPreso(newDisplay)
                    currentDisplay = newDisplay
                }
            } else if (currentDisplay != null) {
                // The new display is invalid (possibly disconnected) so we must assume there are
                // no good displays and clear the current one
                listener.clearPreso(true)
                currentDisplay = null
            }
        }

        isFirstRun = false
    }

    // Anytime there's a display change we want to run handleRoute() to perform any required changes
    override fun onDisplayAdded(p0: Int) {
        handleRoute()
    }

    override fun onDisplayRemoved(p0: Int) {
        handleRoute()
    }

    override fun onDisplayChanged(p0: Int) {
        handleRoute()
    }

    interface Listener {
        fun showPreso(display: Display)

        fun clearPreso(showInLine: Boolean)
    }

    companion object {
        const val TAG = "PresentationHelper"
    }
}
