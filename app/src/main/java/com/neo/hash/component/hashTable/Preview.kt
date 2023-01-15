package com.neo.hash.component.hashTable

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
                        row = 1,
                        column = 1,
                        newSymbol = HashState.Block.Symbol.X
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
                },
                modifier = Modifier.aspectRatio(1f)
            )
        }
    }
}