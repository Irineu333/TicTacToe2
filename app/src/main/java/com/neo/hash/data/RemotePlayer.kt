package com.neo.hash.data

import com.neo.hash.model.GameConfig
import com.neo.hash.model.HashState

class RemotePlayer() {
    lateinit var id : String
    lateinit var name: String
    lateinit var symbol: HashState.Block.Symbol

    constructor(
        id : String,
        name : String,
        symbol: HashState.Block.Symbol
    ) : this() {
        this.id = id
        this.name = name
        this.symbol = symbol
    }
}

fun RemotePlayer.toModel() = GameConfig.Player.Remote(
    name = name,
    symbol = symbol
)