@file:OptIn(ExperimentalPagerApi::class)

package com.neo.hash.ui.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.*
import com.google.accompanist.pager.*
import com.neo.hash.annotation.DevicesPreview
import com.neo.hash.annotation.ThemesPreview
import com.neo.hash.component.hashTable.HashTable
import com.neo.hash.component.hashTable.HashTableConfig
import com.neo.hash.model.HashState
import com.neo.hash.ui.screen.start.GameMode
import com.neo.hash.ui.screen.start.StartDialog
import com.neo.hash.ui.theme.HashBackground
import com.neo.hash.ui.theme.HashTheme
import com.neo.hash.util.extension.preview
import com.neo.hash.util.extension.showAround
import com.neo.hash.util.extension.squareSize

internal val HashList = listOf(
    HashState(3, 3).preview(),
    HashState(4, 4).preview(),
    HashState(5, 5).preview(),
    HashState(5, 6).preview(),
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {

    var startDialog by rememberSaveable { mutableStateOf<GameMode?>(null) }

    HomeAdaptiveContent(
        pageState = rememberPagerState(),
        modifier = modifier,
        hashTable = { pageIndex ->
            HashTable(
                hash = HashList[pageIndex],
                enabledOnClick = false,
                config = HashTableConfig.getDefault(
                    symbol = HashTableConfig.Symbol(
                        color = colors.primary,
                        width = 2.dp,
                        animate = false
                    )
                ),
                modifier = Modifier
                    .showAround(pageIndex)
                    .border(
                        BorderStroke(2.dp, colors.primary),
                        RoundedCornerShape(5)
                    ).padding(16.dp)
            )
        },
        options = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                OutlinedButton(
                    onClick = {
                        startDialog = GameMode.PHONE
                    },
                    border = ButtonDefaults.outlinedBorder.copy(
                        brush = SolidColor(colors.primary)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = null,
                    )

                    Text(text = "vs", fontSize = 16.sp)

                    Icon(
                        imageVector = Icons.Rounded.PhoneAndroid,
                        contentDescription = null
                    )
                }

                OutlinedButton(
                    onClick = {
                        startDialog = GameMode.INPUT
                    },
                    border = ButtonDefaults.outlinedBorder.copy(
                        brush = SolidColor(colors.primary)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = null,
                    )

                    Text(text = "vs", fontSize = 16.sp)

                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = null,
                    )
                }

                OutlinedButton(
                    onClick = {
                        startDialog = GameMode.REMOTE
                    },
                    border = ButtonDefaults.outlinedBorder.copy(
                        brush = SolidColor(colors.primary)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = null,
                    )

                    Text(text = "vs", fontSize = 16.sp)

                    Icon(
                        imageVector = Icons.Rounded.Language,
                        contentDescription = null,
                    )
                }

            }
        }
    )

    startDialog?.let {
        StartDialog(
            gameMode = it,
            onGameStart = {
                startDialog = null
            },
            onDismissRequest = {
                startDialog = null
            }
        )
    }
}

@ThemesPreview
@DevicesPreview
@Composable
private fun DefaultPreview() {
    HashTheme {
        HashBackground {
            HomeScreen(Modifier.fillMaxSize())
        }
    }
}