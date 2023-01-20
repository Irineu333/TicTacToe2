package com.neo.hash.model

data class HashState(
    val rows: Int,
    val columns: Int,
    val winner: Winner? = null,
    val blocks: List<List<Block>> = emptyBlocks(rows, columns)
) {

    init {
        check(blocks.size == rows)
        check(blocks.all { it.size == columns })
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

    data class Block(
        val row: Int,
        val column: Int,
        val symbol: Symbol? = null
    ) {
        enum class Symbol(val code : Int) {
            O(code = 0),
            X(code = 1);

            val enemy get() = when(this) {
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

private fun <E> List<E>.updateAt(
    index: Int,
    update: (E) -> E
): List<E> {
    return List(size) {
        val element = get(it)

        if (it == index) {
            update(element)
        } else {
            element
        }
    }
}
