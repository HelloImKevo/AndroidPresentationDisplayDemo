package com.kevo.displaydemo.util

import android.view.View
import com.google.android.material.snackbar.Snackbar

object SnackbarHelper {

    @JvmStatic
    fun showLong(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Cool", null).show()
    }
}
