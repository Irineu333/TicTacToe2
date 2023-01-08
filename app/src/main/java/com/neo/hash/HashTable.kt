package com.neo.hash

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    Blocks(
        rows = hash.rows,
        columns = hash.columns,
        blocks = hash.blocks,
        config = config.symbol,
        modifier = Modifier.fillMaxSize()
    )
    Hash(
        rows = hash.rows,
        columns = hash.columns,
        config = config.hash,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun Blocks(
    rows: Int,
    columns: Int,
    blocks: List<List<HashState.Block>>,
    config: HashTableConfig.Symbol,
    modifier: Modifier = Modifier
) = BoxWithConstraints(modifier) {

    val rowSize = maxHeight / rows
    val columnSize = maxWidth / columns

    for (row in 0 until rows) {
        for (column in 0 until columns) {

            val block = blocks[row][column]

            Block(
                block = block,
                onClick = {},
                config = config,
                modifier = Modifier
                    .size(
                        width = columnSize,
                        height = rowSize
                    )
                    .offset(
                        x = columnSize * column,
                        y = rowSize * row,
                    )
            )
        }
    }
}

@Composable
fun Block(
    block: HashState.Block,
    onClick: (HashState.Block) -> Unit,
    config: HashTableConfig.Symbol,
    modifier: Modifier = Modifier
) = Box(
    modifier = modifier
        .onBlockClick(block, onClick)
        .background(Color.Red),
) {
    Text(
        text = "(${block.row},${block.column})",
        modifier = Modifier.align(Alignment.Center)
    )
}

private fun Modifier.onBlockClick(
    block: HashState.Block,
    onClick: (HashState.Block) -> Unit
): Modifier {
    return if (block.player == null) {
        clickable {
            onClick(block)
        }
    } else {
        this
    }
}

@Composable
fun Hash(
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
