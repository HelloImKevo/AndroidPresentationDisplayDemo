package com.kevo.displaydemo.ui.admob

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdMobViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the AdMob Fragment"
    }
    val text: LiveData<String> = _text
}
