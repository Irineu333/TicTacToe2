package com.neo.hash.util.extension

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.Modifier

context(BoxWithConstraintsScope)
fun Modifier.squareSize() = aspectRatio(
    ratio = 1f,
    matchHeightConstraintsFirst = maxHeight > maxWidth
)