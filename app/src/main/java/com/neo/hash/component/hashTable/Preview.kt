package com.neo.hash.component.hashTable

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.neo.hash.annotation.ThemesPreview
import com.neo.hash.model.HashState
import com.neo.hash.ui.theme.HashBackground
import com.neo.hash.ui.theme.HashTheme

@ThemesPreview
@Composable
private fun DefaultPreview() {
    HashTheme {
        HashBackground {
            var hashState by remember {
                mutableStateOf(
                    HashState(
                        rows = 3,
                        columns = 3
                    ).addedPlayer(
                        2, 0, HashState.Block.Symbol.O
                    ).addedPlayer(
                        1, 1, HashState.Block.Symbol.O
                    ).addedPlayer(
                        0, 2, HashState.Block.Symbol.O
                    ).addedPlayer(
                        1, 0, HashState.Block.Symbol.X
                    ).addedPlayer(
                        0, 1, HashState.Block.Symbol.X
                    ).copy(
                        winner = HashState.Winner(
                            blocks = listOf(
                                HashState.Block(
                                    0, 2
                                ),
                                HashState.Block(
                                    2, 0
                                )
                            ),
                            symbol = HashState.Block.Symbol.O
                        )
                    )
                )
            }

            HashTable(
                hash = hashState,
                onClick = { block ->
                    hashState = hashState.addedPlayer(
                        block = block,
                        newSymbol = HashState.Block.Symbol.O
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun RectanglePreview() {
    HashTheme {
        HashBackground {
            var hashState by remember {
                mutableStateOf(
                    HashState(
                        rows = 3,
                        columns = 4
                    ).addedPlayer(
                        0, 0, HashState.Block.Symbol.O
                    ).addedPlayer(
                        1, 1, HashState.Block.Symbol.O
                    ).addedPlayer(
                        2, 2, HashState.Block.Symbol.O
                    ).copy(
                        winner = HashState.Winner(
                            blocks = listOf(
                                HashState.Block(0,0),
                                HashState.Block(1,1),
                                HashState.Block(2,2)
                            ),
                            symbol = HashState.Block.Symbol.O
                        )
                    )
                )
            }

            HashTable(
                hash = hashState,
                onClick = { block ->
                    hashState = hashState.addedPlayer(
                        block = block,
                        newSymbol = HashState.Block.Symbol.O
                    )
                }
            )
        }
    }
}