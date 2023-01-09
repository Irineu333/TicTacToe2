package com.neo.hash

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    private val _hashState = MutableStateFlow(HashState(3, 3))
    val hashState = _hashState.asStateFlow()

    fun test(block: HashState.Block) {
        _hashState.update {
            it.changePlayer(
                block = block,
                newPlayer = HashState.Block.Player.values().random()
            )
        }
    }
}
