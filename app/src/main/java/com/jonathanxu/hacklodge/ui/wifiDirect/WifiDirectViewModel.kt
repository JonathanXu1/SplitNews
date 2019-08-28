package com.jonathanxu.hacklodge.ui.wifiDirect

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WifiDirectViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "mynamejeff"
    }
    val text: LiveData<String> = _text
}