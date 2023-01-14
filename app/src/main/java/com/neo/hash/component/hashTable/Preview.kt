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
fun DefaultPreview() {
    HashTheme {
        HashBackground {
            var hashState by remember {
                mutableStateOf(
                    HashState(
                    rows = 3,
                    columns = 3
                ).changePlayer(
                        row = 1,
                        column = 1,
                        newPlayer = HashState.Block.Player.X
                    )
                )
            }

            HashTable(
                hash = hashState,
                onClick = { block ->
                    hashState = hashState.changePlayer(
                        block = block,
                        newPlayer = HashState.Block.Player.O
                    )
                },
                modifier = Modifier.aspectRatio(1f)
            )
        }
    }
}