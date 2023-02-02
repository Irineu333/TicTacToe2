package com.neo.hash.model

import com.neo.hash.util.extension.updateAt

data class HashState(
    val rows: Int,
    val columns: Int,
    val winnerBlocks: Int = minOf(rows, columns),
    val winner: Winner? = null,
    val blocks: List<List<Block>> = emptyBlocks(rows, columns)
) {

    init {
        check(rows >= 3 && columns >= 3)
        check(blocks.size == rows)
        check(blocks.all { it.size == columns })
        check(winnerBlocks <= minOf(rows, columns))
        check(winnerBlocks >= 3)
    }

    operator fun get(row: Int, column: Int): Block {
        return blocks[row][column]
    }

    fun addedPlayer(
        block: Block,
        newSymbol: Block.Symbol?
    ) = addedPlayer(
        block.row,
        block.column,
        newSymbol
    )

    fun addedPlayer(
        row: Int,
        column: Int,
        newSymbol: Block.Symbol?
    ) = copy(
        blocks = blocks.updateAt(row) { columns ->
            columns.updateAt(column) {
                it.copy(
                    symbol = newSymbol
                )
            }
        }
    )

    fun updatedBlocks(
        rows: Int = this.rows,
        columns: Int = this.columns,
        winnerBlocks: Int = this.winnerBlocks
    ): HashState {

        val internalRows = rows.coerceAtLeast(3)
        val internalColumns = columns.coerceAtLeast(3)
        val internalWinnerBlocks = winnerBlocks.coerceIn(3, minOf(internalRows, internalColumns))

        return copy(
            rows = internalRows,
            columns = internalColumns,
            winnerBlocks = internalWinnerBlocks,
            blocks = emptyBlocks(internalRows, internalColumns)
        )
    }

    data class Block(
        val row: Int,
        val column: Int,
        val symbol: Symbol? = null
    ) {
        enum class Symbol(val code: Int) {
            O(code = 0),
            X(code = 1);

            val enemy
                get() = when (this) {
                    O -> X
                    X -> O
                }

            companion object {
                fun random(): Symbol {
                    return values().random()
                }
            }
        }
    }

    data class Winner(
        val blocks: List<Block>,
        val symbol: Block.Symbol
    )

    companion object {
        private fun emptyBlocks(
            rows: Int,
            columns: Int
        ) = List(rows) { row ->
            List(columns) { column ->
                Block(
                    row = row,
                    column = column
                )
            }
        }
    }
}