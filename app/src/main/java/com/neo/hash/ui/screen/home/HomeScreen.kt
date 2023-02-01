@file:OptIn(ExperimentalPagerApi::class)

package com.neo.hash.ui.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.*
import com.google.accompanist.pager.*
import com.neo.hash.annotation.DevicesPreview
import com.neo.hash.annotation.ThemesPreview
import com.neo.hash.component.box.DepthPageBox
import com.neo.hash.component.button.CustomButton
import com.neo.hash.component.hashTable.HashTable
import com.neo.hash.component.hashTable.HashTableConfig
import com.neo.hash.component.spacer.HorizontalSpacer
import com.neo.hash.component.spacer.VerticalSpacer
import com.neo.hash.model.HashState
import com.neo.hash.ui.screen.start.GameMode
import com.neo.hash.ui.screen.start.StartDialog
import com.neo.hash.ui.theme.HashBackground
import com.neo.hash.ui.theme.HashTheme
import com.neo.hash.util.extension.preview

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

    HomeAdaptiveContent(
        pageState = rememberPagerState(),
        modifier = modifier,
        pagesCount = HashList.size + 1,
        hashTable = { pageIndex ->
            DepthPageBox(pageIndex) {
                if (pageIndex < HashList.size) {
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
                            .border(
                                border = BorderStroke(2.dp, colors.primary),
                                shape = RoundedCornerShape(5)
                            )
                            .padding(16.dp)
                    )
                } else {
                    var hash by remember {
                        mutableStateOf(
                            HashState(6, 7).preview()
                        )
                    }

                    HashTable(
                        hash = hash,
                        enabledOnClick = false,
                        config = HashTableConfig.getDefault(
                            symbol = HashTableConfig.Symbol(
                                color = colors.primary,
                                width = 2.dp,
                                animate = false
                            ),
                            scratch = HashTableConfig.Scratch(
                                color = colors.primary.copy(alpha = 0.5f),
                                width = 8.dp,
                                animate = false
                            )
                        ),
                        modifier = Modifier
                            .padding(
                                horizontal = 9.dp,
                                vertical = 9.dp
                            )
                            .border(
                                border = BorderStroke(2.dp, colors.primary),
                                shape = RoundedCornerShape(5)
                            )
                            .padding(16.dp)
                    )

                    Column(
                        Modifier
                            .align(
                                Alignment.CenterEnd
                            )
                            .background(
                                color = colors.primary,
                                shape = RoundedCornerShape(50)
                            )
                            .width(20.dp)
                            .padding(2.dp)
                    ) {
                        CustomButton(
                            onClick = {
                                hash = hash.updatedBlocks(
                                    rows = hash.rows.inc(),
                                ).preview()
                            },
                            indication = rememberRipple(bounded = false, radius = 10.dp),
                            modifier = Modifier.aspectRatio(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null
                            )
                        }

                        VerticalSpacer(size = 6.dp)

                        CustomButton(
                            onClick = {
                                hash = hash.updatedBlocks(
                                    rows = hash.rows.dec(),
                                ).preview()
                            },
                            indication = rememberRipple(bounded = false, radius = 10.dp),
                            modifier = Modifier.aspectRatio(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Remove,
                                contentDescription = null
                            )
                        }
                    }

                    Row(
                        Modifier
                            .align(
                                Alignment.TopCenter
                            )
                            .background(
                                color = colors.primary,
                                shape = RoundedCornerShape(50)
                            )
                            .height(20.dp)
                            .padding(2.dp)
                    ) {

                        CustomButton(
                            onClick = {
                                hash = hash.updatedBlocks(
                                    columns = hash.columns.dec(),
                                ).preview()
                            },
                            indication = rememberRipple(bounded = false, radius = 10.dp),
                            modifier = Modifier.aspectRatio(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Remove,
                                contentDescription = null
                            )
                        }

                        HorizontalSpacer(size = 6.dp)

                        CustomButton(
                            onClick = {
                                hash = hash.updatedBlocks(
                                    columns = hash.columns.inc(),
                                ).preview()
                            },
                            indication = rememberRipple(bounded = false, radius = 10.dp),
                            modifier = Modifier.aspectRatio(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
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