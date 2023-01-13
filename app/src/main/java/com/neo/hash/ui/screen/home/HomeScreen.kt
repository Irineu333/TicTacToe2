@file:OptIn(ExperimentalPagerApi::class)

package com.neo.hash.ui.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
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
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.neo.hash.component.hash.HashTable
import com.neo.hash.model.HashState
import com.neo.hash.ui.theme.HashBackground
import com.neo.hash.ui.theme.HashTheme
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
) = Column(modifier, Arrangement.Center) {
    BoxWithConstraints(contentAlignment = Alignment.Center) {
        HorizontalPager(
            count = HashList.size,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = maxWidth * 0.2f),
        ) { pageIndex ->
            HashTable(
                hash = HashList[pageIndex],
                enabledOnClick = false,
                modifier = Modifier
                    .graphicsLayer {

                        val pageOffset = calculateCurrentOffsetForPage(pageIndex).absoluteValue

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
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
        }
    }

    Spacer(Modifier.height(16.dp))

    AdaptiveBox(Modifier.fillMaxWidth()) {

        Button(onClick = { }) {
            Text(text = "button1")
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(onClick = { }) {
            Text(text = "button2")
        }

    }
}

@Composable
fun AdaptiveBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxWithConstraintsScope.(Orientation) -> Unit
) = BoxWithConstraints(modifier, contentAlignment) {
    if (maxHeight > maxWidth) {
        Column {
            this@BoxWithConstraints.content(Orientation.Vertical)
        }
    } else {
        Row {
            this@BoxWithConstraints.content(Orientation.Horizontal)
        }
    }
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