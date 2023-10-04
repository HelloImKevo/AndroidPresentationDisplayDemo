package com.kevo.displaydemo.ui.slideshow

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class InfiniteSlidePager(val viewPager: ViewPager2) {

    init {
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.offscreenPageLimit = 3
        viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
    }
}
