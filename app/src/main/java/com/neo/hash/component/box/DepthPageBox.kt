@file:OptIn(ExperimentalPagerApi::class)

package com.neo.hash.component.box

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerScope
import com.neo.hash.util.extension.depthByOffset

@Composable
fun PagerScope.DepthPageBox(
    pageIndex: Int,
    minScale: Float = 0.8f,
    minAlpha: Float = 0.5f,
    content: @Composable BoxScope.() -> Unit
) = Box(
    modifier = Modifier.depthByOffset(
        pageIndex = pageIndex,
        minScale = minScale,
        minAlpha = minAlpha
    ),
    content = content
)