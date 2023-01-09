package com.neo.hash

data class HashState(
    val rows: Int,
    val columns: Int,
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
    ) = copy(
        blocks = blocks.update(block.row) { columns ->
            columns.update(block.column) {
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

private fun <E> List<E>.update(
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
