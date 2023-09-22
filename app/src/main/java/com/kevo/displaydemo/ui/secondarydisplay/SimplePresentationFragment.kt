package com.kevo.displaydemo.ui.secondarydisplay

import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kevo.displaydemo.R

class SimplePresentationFragment(context: Context, display: Display): PresentationFragment() {

    init {
        setDisplay(context, display)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.second_screen_logo, container, false)

        return view.rootView
    }

    companion object {
        const val TAG = "SimplePresentationFragment"
    }
}
