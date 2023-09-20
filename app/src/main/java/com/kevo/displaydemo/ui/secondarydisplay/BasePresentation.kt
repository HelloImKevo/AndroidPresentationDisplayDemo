package com.kevo.displaydemo.ui.secondarydisplay

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import com.kevo.displaydemo.R

class BasePresentation (context: Context, display: Display): Presentation(context, display) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.second_screen_logo)
    }
}
