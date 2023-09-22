package com.kevo.displaydemo.ui.secondarydisplay

import android.app.Dialog
import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import androidx.fragment.app.DialogFragment

open class PresentationFragment: DialogFragment() {
    private var display: Display? = null
    private var preso: Presentation? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return if (preso == null) {
            super.onCreateDialog(savedInstanceState)
        } else {
            preso!!
        }
    }

    fun setDisplay(context: Context, display: Display?) {
        if (display == null) {
            preso = null
        } else {
            preso = Presentation(context, display, theme)
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
}
