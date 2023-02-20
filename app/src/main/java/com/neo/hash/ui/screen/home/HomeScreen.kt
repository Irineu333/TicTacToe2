@file:OptIn(ExperimentalPagerApi::class, ExperimentalAnimationApi::class)

package com.neo.hash.ui.screen.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.neo.hash.annotation.DevicesPreview
import com.neo.hash.annotation.ThemesPreview
import com.neo.hash.component.box.DepthPageBox
import com.neo.hash.component.hashTable.HashTable
import com.neo.hash.component.hashTable.HashTableConfig
import com.neo.hash.model.HashState
import com.neo.hash.ui.screen.start.GameMode
import com.neo.hash.ui.screen.start.StartDialog
import com.neo.hash.ui.theme.HashBackground
import com.neo.hash.ui.theme.HashTheme
import com.neo.hash.util.extension.preview
import com.neo.hash.util.extension.squareSize
import kotlin.math.pow
import kotlin.math.sqrt

internal val HashList = listOf(
    HashState(3, 3).preview(),
    HashState(4, 4).preview(),
    HashState(5, 5).preview(),
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {

    var startDialog by rememberSaveable { mutableStateOf<GameMode?>(null) }

    val pageState = rememberPagerState()

    HomeAdaptiveContent(
        pageState = pageState,
        modifier = modifier,
        pagesCount = HashList.size + 1,
        hashTable = { pageIndex ->

            DepthPageBox(pageIndex) {
                if (pageIndex < HashList.size) {

                    val symbol = HashTableConfig.Symbol(
                        color = colors.primary,
                        width = 2.dp,
                        animate = false
                    )

                    HashTable(
                        hash = HashList[pageIndex],
                        enabledOnClick = false,
                        config = HashTableConfig.getDefault(
                            symbol = symbol,
                            scratch = HashTableConfig.Scratch(
                                color = symbol.color.copy(alpha = 0.5f),
                                width = 8.dp,
                                animate = false
                            )
                        ),
                        modifier = Modifier
                            .border(
                                border = BorderStroke(2.dp, colors.primary),
                                shape = RoundedCornerShape(5)
                            )
                            .padding(16.dp)
                    )
                } else {

                    Column(
                        Modifier
                            .squareSize()
                            .border(
                                border = BorderStroke(2.dp, colors.primary),
                                shape = RoundedCornerShape(5)
                            )
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                    }
                }
            }
        },
        startGameOptions = {

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
        },
        saveHashOptions = {
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Cancelar")
            }

            Button(onClick = { /*TODO*/ }) {
                Text(text = "Salvar")
            }
        },
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

fun diagonal(value: Dp): Dp {
    return diagonal(value.value.toDouble()).dp
}

fun diagonal(x: Dp, y: Dp): Dp {
    return diagonal(x.value.toDouble(), y.value.toDouble()).dp
}

fun diagonal(value: Double): Double {
    return value * sqrt(2f)
}

fun diagonal(x: Double, y: Double): Double {
    return sqrt(x.pow(2) + y.pow(2))
}