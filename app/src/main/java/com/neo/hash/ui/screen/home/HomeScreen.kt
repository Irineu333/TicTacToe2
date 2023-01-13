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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.google.accompanist.pager.*
import com.neo.hash.component.hash.HashTable
import com.neo.hash.model.HashState
import com.neo.hash.ui.theme.HashBackground
import com.neo.hash.ui.theme.HashTheme
import com.neo.hash.util.extension.squareSize
import com.neo.hash.util.function.interpolate
import kotlin.math.absoluteValue

private val HashList = listOf(
    HashState(3, 3),
    HashState(4, 4),
    HashState(5, 5)
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) = BoxWithConstraints(modifier) {

    val pageState = rememberPagerState()

    @Composable
    fun options() = Column(Modifier.padding(16.dp)) {

        Button(onClick = { }) {
            Text(text = "button1")
        }

        Button(onClick = { }) {
            Text(text = "button2")
        }

    }

    if (maxHeight > maxWidth) {
        Vertical(pageState, Modifier.fillMaxSize()) {
            options()
        }
    } else {
        Horizontal(pageState, Modifier.fillMaxSize()) {
            options()
        }
    }
}

@Composable
private fun Horizontal(
    pageState: PagerState,
    modifier: Modifier = Modifier,
    options: @Composable () -> Unit
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

        val size = minOf(maxWidth, maxHeight)

        val padding = (maxHeight - size) / 2

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
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }

                        alpha = interpolate(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
                    .border(
                        border = BorderStroke(
                            width = 2.dp,
                            color = colors.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
                    .squareSize()

            )
        }
    }
}

@Composable
private fun Vertical(
    pageState: PagerState,
    modifier: Modifier = Modifier,
    options: @Composable () -> Unit
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

        val size = minOf(maxWidth, maxHeight)

        val padding = (maxWidth - size) / 2

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
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }

                        alpha = interpolate(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
                    .border(
                        border = BorderStroke(
                            width = 2.dp,
                            color = colors.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
                    .squareSize()

            )
        }
    }

    options()
}

@Preview
@Composable
private fun DefaultPreview() {
    HashTheme {
        HashBackground {
            HomeScreen(Modifier.fillMaxSize())
        }
    }
}