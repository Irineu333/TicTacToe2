package com.neo.hash

data class HashState(
    val rows: Int,
    val columns: Int,
    val blocks: List<List<Block>> = emptyBlocks(rows, columns)
) {

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