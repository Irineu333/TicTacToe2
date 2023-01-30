package com.neo.hash.component.hashTable

import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class HashTableConfig(
    val hash: Hash,
    val symbol: Symbol,
    val scratch: Scratch
) {

    sealed interface Line {
        val color: Color
        val width: Dp
    }

    data class Hash(
        override val color: Color,
        override val width: Dp
    ) : Line

    data class Symbol(
        override val color: Color,
        override val width: Dp,
        val animate: Boolean
    ) : Line

    data class Scratch(
        override val color: Color,
        override val width: Dp,
        val animate: Boolean
    ) : Line

    companion object {
        @Composable
        fun getDefault(
            hash: Hash = Hash(
                color = colors.onBackground,
                width = 2.dp
            ),
            symbol: Symbol = Symbol(
                color = colors.primary,
                width = 2.dp,
                animate = true
            ),
            scratch: Scratch = Scratch(
                color = symbol.color.copy(alpha = 0.5f),
                width = 8.dp,
                animate = true
            )
        ) = HashTableConfig(
            hash = hash,
            symbol = symbol,
            scratch = scratch
        )
    }
}