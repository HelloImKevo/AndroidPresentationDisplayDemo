package com.kevo.displaydemo.ui.secondarydisplay

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * A singleton helper class that acts as the messenger between our UI and the customer facing
 * display (CFD). If a Fragment (or any other UI controller) calls one of these [CfdHelper]
 * methods while the CFD's [SimplePresentationFragment] is not ready yet. [CfdHelper] will queue
 * that command and execute it in a coroutine later when the [SimplePresentationFragment] is ready.
 */
object CfdHelper {

    private const val TAG = "CfdHelper"

    private var lifecycle: Lifecycle? = null
    private var preso: SimplePresentationFragment? = null
    private val commandQueue = mutableListOf<Runnable>()

    fun clearCommandQueue() {
        commandQueue.clear()
    }

    fun setPreso(newPreso: SimplePresentationFragment?) {
        preso = newPreso
        lifecycle = newPreso?.lifecycle
        newPreso?.lifecycle?.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                // Start executing any queued jobs if there are any
                if (commandQueue.isNotEmpty()) {
                    Log.i(TAG, "Launching coroutines for ${commandQueue.size} tasks in queue")

                    owner.lifecycleScope.launch {
                        // Iterate through the queue and launch a coroutine to complete each one
                        for (runnable in commandQueue) {
                            launch {
                                runnable.run()
                            }
                        }
                    }
                }
            }
        })
    }

    fun setText(newString: String) {
        if (lifecycle != null) {
            if (lifecycle!!.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                Log.i(TAG, "preso is ready to go setting text to $newString")

                // The SimplePresentationFragment is ready to take commands
                preso?.setText(newString)
                return
            }
        }

        Log.i(TAG, "preso not ready yet adding runnable to the queue")
        // The SimplePresentationFragment is not ready to take commands yet
        commandQueue.add(Runnable {
            preso?.setText(newString)
        })
    }
}
