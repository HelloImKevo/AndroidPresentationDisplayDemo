package com.kevo.displaydemo.ui.slideshow

import androidx.annotation.DrawableRes

data class SlideItem(val key: Key, @DrawableRes val imageResId: Int) {

    enum class Key {
        CustomerOrderEntry,
        LoyaltyPhone,
        ReceiptSelection,
        SampleAd4
    }
}
