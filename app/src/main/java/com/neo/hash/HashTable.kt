package com.neo.hash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.DrawResult
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
        override val width: Dp
    ) : Line

    data class Scratch(
        override val color: Color,
        override val width: Dp
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
                width = 2.dp
            ),
            scratch: Scratch = Scratch(
                color = symbol.color.copy(alpha = 0.5f),
                width = 4.dp
            )
        ) = HashTableConfig(
            hash = hash,
            symbol = symbol,
            scratch = scratch
        )
    }
}

@Composable
fun HashTable(
    hash: HashState,
    modifier: Modifier = Modifier,
    onClick: (HashState.Block) -> Unit,
    config: HashTableConfig = HashTableConfig.getDefault()
) = Box(modifier) {
    DrawHash(
        rows = hash.rows,
        columns = hash.columns,
        config = config.hash,
        modifier = Modifier.matchParentSize()
    )
}

@Composable
fun DrawHash(
    rows: Int,
    columns: Int,
    config: HashTableConfig.Hash,
    modifier: Modifier = Modifier
) = DrawWithCache(modifier) {

    val rowSize = size.height / rows
    val columnSize = size.width / columns
    val lineWidth = config.width.toPx()

    onDrawBehind {

        fun drawLine(
            start: Offset,
            end: Offset
        ) = drawLine(
            color = config.color,
            start = start,
            end = end,
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        fun drawRow(position: Int) = drawLine(
            start = Offset(0f, rowSize * position),
            end = Offset(size.width, rowSize * position),
        )

        fun drawColumn(position: Int) = drawLine(
            start = Offset(columnSize * position, 0f),
            end = Offset(columnSize * position, size.height),
        )

        for (index in 1 until rows) {
            drawRow(index)
        }

        for (index in 1 until columns) {
            drawColumn(index)
        }
    }
}

@Composable
fun DrawWithCache(
    modifier: Modifier = Modifier,
    onBuildDrawCache: CacheDrawScope.() -> DrawResult
) = Spacer(modifier.drawWithCache(onBuildDrawCache))
