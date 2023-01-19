package com.neo.hash.model

sealed interface GameConfig {

    val players: List<Player>
    val symbolStarts : HashState.Block.Symbol

    data class Local(
        override val players: List<Player>,
        override val symbolStarts: HashState.Block.Symbol
    ) : GameConfig

    data class Remote(
        override val players: List<Player>,
        override val symbolStarts: HashState.Block.Symbol,
        val gameKey: String
    ) : GameConfig

    sealed interface Player {

        val symbol: HashState.Block.Symbol
        val name: String

        data class Input(
            override val symbol: HashState.Block.Symbol,
            override val name: String
        ) : Player

        data class Phone(
            override val symbol: HashState.Block.Symbol
        ) : Player {
            override val name: String = "IA"
        }

        data class Remote(
            override val symbol: HashState.Block.Symbol,
            override val name: String
        ) : Player
    }
}