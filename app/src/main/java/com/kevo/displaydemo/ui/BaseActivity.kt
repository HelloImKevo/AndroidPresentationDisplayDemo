package com.kevo.displaydemo.ui

import android.util.Log
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    @Suppress("PropertyName")
    protected abstract val TAG: String

    protected var themeResourceId: Int = 0

    override fun setTheme(resId: Int) {
        super.setTheme(resId)
        Log.i(TAG, "Setting Theme ID: $resId")
        themeResourceId = resId
    }
}
