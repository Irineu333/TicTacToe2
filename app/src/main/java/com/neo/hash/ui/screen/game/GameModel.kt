package com.neo.hash.ui.screen.game

import androidx.lifecycle.ViewModel
import com.neo.hash.model.HashState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameModel : ViewModel() {
    private val _hashState = MutableStateFlow(
        HashState(
            3, 3,
            winner = HashState.Winner(
                listOf(
                    HashState.Block(1,1),
                    HashState.Block(2,2),
                    HashState.Block(3,3),
                ),
                HashState.Block.Symbol.X
            )
        )
    )
    val hashState = _hashState.asStateFlow()

    fun test(block: HashState.Block) {
        _hashState.update {
            it.addedPlayer(
                block = block,
                newSymbol = HashState.Block.Symbol.values().random()
            )
        }
    }
}
