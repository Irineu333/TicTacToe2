package com.neo.hash

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.DrawResult
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
                width = 4.dp,
                animate = true
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
        hash = hash,
        config = config.symbol,
        onClick = onClick,
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
    hash: HashState,
    config: HashTableConfig.Symbol,
    onClick: (HashState.Block) -> Unit,
    modifier: Modifier = Modifier
) = BoxWithConstraints(modifier) {

    val rowSize = maxHeight / hash.rows
    val columnSize = maxWidth / hash.columns

    for (row in 0 until hash.rows) {
        for (column in 0 until hash.columns) {

            val block = hash[row, column]

            Block(
                block = block,
                onClick = onClick,
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
    modifier = modifier.onBlockClick(block, onClick),
    contentAlignment = Alignment.Center
) {
    if (block.player != null) {
        Player(
            player = block.player,
            config = config,
            modifier = Modifier.fillMaxSize(
                0.5f
            )
        )
    }
}

@Composable
fun Player(
    player: HashState.Block.Player,
    config: HashTableConfig.Symbol,
    modifier: Modifier = Modifier
) {
    val animation = rememberSaveable(player, saver = AnimatableSaver()) {
        Animatable(if (config.animate) 0f else 2f)
    }

    LaunchedEffect(player) {
        animation.animateTo(
            targetValue = 2f
        )
    }

    CanvasWithCache(modifier) {

        val sweepAngle = 360f * animation.value / 2f

        val stroke = Stroke(
            width = config.width.toPx(),
            cap = StrokeCap.Round
        )

        val size = Size(size.height, size.width)

        onDrawBehind {

            when (player) {
                HashState.Block.Player.O -> {

                    drawArc(
                        color = config.color,
                        startAngle = 0f,
                        sweepAngle = sweepAngle,
                        size = size,
                        useCenter = false,
                        style = stroke
                    )
                }
                HashState.Block.Player.X -> {

                    val width1 = when (animation.value) {
                        in 0f..1f -> size.width * animation.value
                        else -> size.width
                    }

                    val height1 = when (animation.value) {
                        in 0f..1f -> size.height * animation.value
                        else -> size.height
                    }

                    val width2 = when (animation.value) {
                        in 1f..2f -> size.width * animation.value.dec()
                        else -> 0f
                    }

                    val height2 = when (animation.value) {
                        in 1f..2f -> size.height * animation.value.dec()
                        else -> 0f
                    }

                    fun drawLine(
                        start: Offset,
                        end: Offset
                    ) = drawLine(
                        color = config.color,
                        start = start,
                        end = end,
                        strokeWidth = stroke.width,
                        cap = stroke.cap
                    )

                    drawLine(
                        start = Offset(
                            x = 0f,
                            y = 0f
                        ),
                        end = Offset(
                            x = width1,
                            y = height1
                        )
                    )

                    drawLine(
                        start = Offset(
                            x = width2,
                            y = size.height - height2
                        ),
                        end = Offset(
                            x = 0f,
                            y = size.height
                        )
                    )
                }
            }
        }
    }
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
) = CanvasWithCache(modifier) {

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
fun CanvasWithCache(
    modifier: Modifier = Modifier,
    onBuildDrawCache: CacheDrawScope.() -> DrawResult
) = Spacer(modifier.drawWithCache(onBuildDrawCache))
