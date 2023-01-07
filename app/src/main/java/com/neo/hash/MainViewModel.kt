package com.neo.hash

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    private val _hashState = MutableStateFlow(HashState(3, 3))
    val hashState = _hashState.asStateFlow()

}
