package com.kevo.displaydemo.ui

import androidx.fragment.app.Fragment
import com.kevo.displaydemo.MainActivity

abstract class BaseFragment : Fragment() {

    protected fun showFullScreenImage() {
        (activity as? MainActivity)?.showFullScreenImage()
    }
}
