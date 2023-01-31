@file:OptIn(ExperimentalPagerApi::class)

package com.neo.hash.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*

@Composable
internal fun HomeAdaptiveContent(
    pageState: PagerState,
    pagesCount: Int,
    options: @Composable () -> Unit,
    hashTable: @Composable context(BoxWithConstraintsScope, PagerScope) (Int) -> Unit,
    modifier: Modifier = Modifier
) = BoxWithConstraints(modifier) {
    if (maxHeight > maxWidth) {
        HomePortraitContent(
            pageState = pageState,
            pagesCount = pagesCount,
            modifier = Modifier.fillMaxSize(),
            hashTable = { pageIndex ->
                hashTable(this@BoxWithConstraints, this, pageIndex)
            },
            options = options
        )
    } else {
        HomeLandscapeContent(
            pageState = pageState,
            pagesCount = pagesCount,
            modifier = Modifier.fillMaxSize(),
            hashTable = { pageIndex ->
                hashTable(this@BoxWithConstraints, this, pageIndex)
            },
            options = options
        )
    }
}

@Composable
internal fun HomeLandscapeContent(
    pageState: PagerState,
    pagesCount: Int,
    hashTable: @Composable() (PagerScope.(Int) -> Unit),
    options: @Composable () -> Unit,
    modifier: Modifier = Modifier
) = Row(
    modifier.padding(horizontal = 16.dp),
    Arrangement.SpaceEvenly,
    Alignment.CenterVertically
) {

    options()

    BoxWithConstraints(
        modifier = Modifier
            .weight(1f, false)
    ) {

        val tableSize = minOf(maxWidth, maxHeight)

        val padding = (maxHeight - tableSize) / 2

        VerticalPager(
            count = pagesCount,
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
internal fun HomePortraitContent(
    pageState: PagerState,
    pagesCount: Int,
    hashTable: @Composable() (PagerScope.(Int) -> Unit),
    options: @Composable () -> Unit,
    modifier: Modifier = Modifier
) = Column(
    modifier.padding(vertical = 16.dp),
    Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    Alignment.CenterHorizontally,
) {
    BoxWithConstraints(Modifier.weight(1f, false)) {

        val tableSize = minOf(maxWidth, maxHeight)

        val padding = (maxWidth - tableSize) / 2

        HorizontalPager(
            count = pagesCount,
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