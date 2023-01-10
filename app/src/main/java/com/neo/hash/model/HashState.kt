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

    fun changePlayer(
        block: Block,
        newPlayer: Block.Player?
    ) = changePlayer(
        block.row,
        block.column,
        newPlayer
    )

    fun changePlayer(
        row: Int,
        column: Int,
        newPlayer: Block.Player?
    ) = copy(
        blocks = blocks.updateAt(row) { columns ->
            columns.updateAt(column) {
                it.copy(
                    player = newPlayer
                )
            }
        }
    )

    data class Block(
        val row: Int,
        val column: Int,
        val player: Player? = null
    ) {
        enum class Player {
            X,
            O
        }
    }

    data class Winner(
        val blocks: List<Block>,
        val player: Block.Player
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
