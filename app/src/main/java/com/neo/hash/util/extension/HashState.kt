package com.neo.hash.util.extension

import com.neo.hash.model.HashState

fun HashState.preview(): HashState {

    var state: HashState = this
    val blocks = mutableListOf<HashState.Block>()

    for (index in 0 until  winnerBlocks) {
        state = state.addedPlayer(
            index, index,
            HashState.Block.Symbol.O
        )

        blocks.add(HashState.Block(index, index))
    }

    return state.copy(
        winner = HashState.Winner(
            blocks = blocks,
            symbol = HashState.Block.Symbol.O
        )
    )
}