package com.neo.hash.component.hash

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.neo.hash.component.CanvasWithCache
import com.neo.hash.model.AnimatableSaver
import com.neo.hash.model.HashState

@Composable
fun HashTable(
    hash: HashState,
    modifier: Modifier = Modifier,
    onClick: (HashState.Block) -> Unit = {},
    config: HashTableConfig = HashTableConfig.getDefault()
) = Box(modifier) {

    Blocks(
        hash = hash,
        onClick = onClick,
        config = config.symbol,
        modifier = Modifier.fillMaxSize()
    )

    Hash(
        rows = hash.rows,
        columns = hash.columns,
        config = config.hash,
        modifier = Modifier.fillMaxSize()
    )

    if (hash.winner != null) {
        Winner(
            rows = hash.rows,
            columns = hash.columns,
            winner = hash.winner,
            config = config.scratch,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun Blocks(
    hash: HashState,
    onClick: (HashState.Block) -> Unit,
    config: HashTableConfig.Symbol,
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
private fun Block(
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
private fun Player(
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

        val stroke = Stroke(
            width = config.width.toPx(),
            cap = StrokeCap.Round
        )

        onDrawBehind {

            when (player) {
                HashState.Block.Player.O -> {

                    drawArc(
                        color = config.color,
                        startAngle = 0f,
                        sweepAngle = 360f * animation.value / 2,
                        size = size,
                        useCenter = false,
                        style = stroke
                    )
                }
                HashState.Block.Player.X -> {

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
                            x = when (animation.value) {
                                in 0f..1f -> size.width * animation.value
                                else -> size.width
                            },
                            y = when (animation.value) {
                                in 0f..1f -> size.height * animation.value
                                else -> size.height
                            }
                        )
                    )

                    if (animation.value > 1f) {
                        drawLine(
                            start = Offset(
                                x = size.width * animation.value.dec(),
                                y = size.height - size.height * animation.value.dec()
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
}

@Composable
private fun Hash(
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
private fun Winner(
    rows: Int,
    columns: Int,
    winner: HashState.Winner,
    config: HashTableConfig.Scratch,
    modifier: Modifier = Modifier
) {
    val animation = rememberSaveable(winner, saver = AnimatableSaver()) {
        Animatable(if (config.animate) 0f else 1f)
    }

    LaunchedEffect(winner) {
        animation.animateTo(
            targetValue = 1f
        )
    }

    CanvasWithCache(modifier) {

        val rowSize = size.height / rows
        val columnSize = size.width / columns

        val lineWidth = config.width.toPx()

        val startBlock = winner.blocks.first()
        val endBlock = winner.blocks.last()

        val rowRadius = rowSize / 2
        val columnRadius = columnSize / 2

        val start = Offset(
            x = startBlock.row * rowSize - rowRadius,
            y = startBlock.column * columnSize - columnRadius
        )

        val end = Offset(
            x = endBlock.row * rowSize - rowRadius,
            y = endBlock.column * columnSize - columnRadius
        )

        val length = end - start

        onDrawBehind {

            fun drawRoundedLine(
                start: Offset,
                end: Offset
            ) = drawLine(
                color = config.color,
                start = start,
                end = end,
                strokeWidth = lineWidth,
                cap = StrokeCap.Round
            )

            drawRoundedLine(
                start = start,
                end = start + length * animation.value
            )
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
