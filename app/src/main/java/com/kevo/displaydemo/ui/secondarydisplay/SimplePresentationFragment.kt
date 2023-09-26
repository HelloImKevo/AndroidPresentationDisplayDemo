package com.kevo.displaydemo.ui.secondarydisplay

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kevo.displaydemo.databinding.SecondScreenLogoBinding
import com.kevo.displaydemo.util.SnackbarHelper

/**
 * @param themedContext Should be a View context that contains UI theme information.
 * The "Application" context may not contain the needed theme details to inflate the
 * [PresentationFragment].
 * @param themeResourceId For whatever reason, the Android architects did not provide
 * a convenient way to determine the Theme Resource ID, which is required by the native
 * [Presentation] class to function properly.
 */
class SimplePresentationFragment(
    themedContext: Context,
    themeResourceId: Int,
    display: Display,
) : PresentationFragment() {

    private var _binding: SecondScreenLogoBinding? = null
    private val binding get() = _binding!!

    init {
        setDisplay(themedContext, themeResourceId, display)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SecondScreenLogoBinding.inflate(inflater, container, false).apply {
            btnRed.setOnClickListener {
                SnackbarHelper.showLong(it, "You clicked the Red button")
            }
            btnYellow.setOnClickListener {
                SnackbarHelper.showLong(it, "You clicked the Yellow button")
            }
            btnGreen.setOnClickListener {
                SnackbarHelper.showLong(it, "You clicked the Green button")
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        const val TAG = "SimplePresentationFragment"
    }
}
