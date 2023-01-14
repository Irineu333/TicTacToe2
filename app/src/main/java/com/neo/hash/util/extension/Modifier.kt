@file:OptIn(ExperimentalPagerApi::class)

package com.neo.hash.util.extension

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerScope
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.neo.hash.util.function.interpolate
import com.neo.hash.util.function.inverseOf
import kotlin.math.absoluteValue

context (PagerScope)
fun Modifier.showAround(pageIndex : Int) = graphicsLayer {

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