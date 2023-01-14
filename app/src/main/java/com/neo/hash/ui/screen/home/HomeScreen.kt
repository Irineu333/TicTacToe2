@file:OptIn(ExperimentalPagerApi::class)

package com.neo.hash.ui.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.*
import com.google.accompanist.pager.*
import com.neo.hash.annotation.DevicesPreview
import com.neo.hash.annotation.ThemesPreview
import com.neo.hash.component.hash.HashTable
import com.neo.hash.model.HashState
import com.neo.hash.ui.theme.HashBackground
import com.neo.hash.ui.theme.HashTheme
import com.neo.hash.util.extension.squareSize
import com.neo.hash.util.function.interpolate
import com.neo.hash.util.function.inverseOf
import kotlin.math.absoluteValue

private val HashList = listOf(
    HashState(3, 3),
    HashState(4, 4),
    HashState(5, 5)
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) = HomeAdaptiveContent(
    pageState = rememberPagerState(),
    modifier = modifier,
    hashTable = { pageIndex ->
        HashTable(
            hash = HashList[pageIndex],
            enabledOnClick = false,
            modifier = Modifier
                .graphicsLayer {

                    val pageOffset =
                        calculateCurrentOffsetForPage(pageIndex).absoluteValue

                    interpolate(
                        start = 0.8f,
                        stop = 1f,
                        fraction = pageOffset
                            .coerceIn(0f, 1f)
                            .inverseOf(1f)
                    ).also { scale ->
                        scaleX = scale
                        scaleY = scale
                    }

                    alpha = interpolate(
                        start = 0.5f,
                        stop = 1f,
                        fraction = pageOffset
                            .coerceIn(0f, 1f)
                            .inverseOf(1f)
                    )
                }
                .border(
                    BorderStroke(2.dp, colors.primary),
                    RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
                .squareSize()

        )
    },
    options = {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(onClick = { }) {
                Text(text = "vs player")
            }

            Button(onClick = { }) {
                Text(text = "vs phone")
            }

            Button(onClick = { }) {
                Text(text = "online")
            }

        }
    }
)

@Composable
private fun HomeAdaptiveContent(
    pageState: PagerState,
    options: @Composable () -> Unit,
    hashTable: @Composable context(BoxWithConstraintsScope, PagerScope) (Int) -> Unit,
    modifier: Modifier = Modifier
) = BoxWithConstraints(modifier) {
    if (maxHeight > maxWidth) {
        HomePortraitContent(
            pageState = pageState,
            modifier = Modifier.fillMaxSize(),
            hashTable = { pageIndex ->
                hashTable(this@BoxWithConstraints, this, pageIndex)
            },
            options = options
        )
    } else {
        HomeLandscapeContent(
            pageState = pageState,
            modifier = Modifier.fillMaxSize(),
            hashTable = { pageIndex ->
                hashTable(this@BoxWithConstraints, this, pageIndex)
            },
            options = options
        )
    }
}

@Composable
private fun HomeLandscapeContent(
    pageState: PagerState,
    hashTable: @Composable PagerScope.(Int) -> Unit,
    options: @Composable () -> Unit,
    modifier: Modifier = Modifier
) = Row(
    modifier,
    Arrangement.SpaceEvenly,
    Alignment.CenterVertically
) {

    options()

    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(end = 16.dp)
            .weight(1f, false)
    ) {

        val tableSize = minOf(maxWidth, maxHeight)

        val padding = (maxHeight - tableSize) / 2

        VerticalPager(
            count = HashList.size,
            state = pageState,
            contentPadding = PaddingValues(
                vertical = padding.coerceAtLeast(
                    minimumValue = maxHeight * 0.2f
                )
            ),
            modifier = Modifier.fillMaxHeight()
        ) { pageIndex ->
            hashTable(pageIndex)
        }
    }
}

@Composable
private fun HomePortraitContent(
    pageState: PagerState,
    hashTable: @Composable PagerScope.(Int) -> Unit,
    options: @Composable () -> Unit,
    modifier: Modifier = Modifier
) = Column(
    modifier,
    Arrangement.Center,
    Alignment.CenterHorizontally
) {
    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(top = 16.dp)
            .weight(1f, false)
    ) {

        val tableSize = minOf(maxWidth, maxHeight)

        val padding = (maxWidth - tableSize) / 2

        HorizontalPager(
            count = HashList.size,
            state = pageState,
            contentPadding = PaddingValues(
                horizontal = padding.coerceAtLeast(
                    minimumValue = maxWidth * 0.2f
                )
            ),
            modifier = Modifier.fillMaxWidth()
        ) { pageIndex ->
            hashTable(pageIndex)
        }
    }

    options()
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