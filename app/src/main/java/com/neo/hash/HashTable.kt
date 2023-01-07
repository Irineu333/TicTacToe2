package com.neo.hash

import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

data class HashConfig(
    val hashColor: Color,
    val symbolsColor: Color,
    val lineColor: Color
) {
    companion object {

        @Composable
        fun getDefault(
            hashColor: Color = colors.onBackground,
            symbolsColor: Color = colors.primary,
            lineColor: Color = symbolsColor.copy(alpha = 0.5f)
        ) = HashConfig(
            hashColor = hashColor,
            symbolsColor = symbolsColor,
            lineColor = lineColor
        )
    }
}

@Composable
fun HashTable(
    hash: HashState,
    modifier: Modifier = Modifier,
    onClick: (HashState.Block) -> Unit,
    config: HashConfig = HashConfig.getDefault()
) = Box(modifier) {
    Text(
        text = "Olá, mundo!",
        modifier = Modifier.align(Alignment.Center)
    )
}

