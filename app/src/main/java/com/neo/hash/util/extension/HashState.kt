package com.neo.hash.util.extension

import com.neo.hash.model.HashState

fun HashState.preview(): HashState {

    var state: HashState = this
    val blocks = mutableListOf<HashState.Block>()

    for (column in 0 until  winnerBlocks) {

        val row = rows.dec() - column

        state = state.addedPlayer(
            row,
            column,
            HashState.Block.Symbol.O
        )

        blocks.add(
            HashState.Block(row, column)
        )
    }

    return state.copy(
        winner = HashState.Winner(
            blocks = blocks,
            symbol = HashState.Block.Symbol.O
        )
    )
}