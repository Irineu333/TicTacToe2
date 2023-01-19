package com.neo.hash.data

import com.neo.hash.model.GameConfig
import com.neo.hash.model.HashState

class DataPlayer {
    lateinit var name: String
    lateinit var symbol: HashState.Block.Symbol
}

fun DataPlayer.toModel() = GameConfig.Player.Remote(
    name = name,
    symbol = symbol
)