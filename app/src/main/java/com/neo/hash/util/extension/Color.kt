package com.neo.hash.util.extension

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver

fun Color.alpha(fraction: Float, background: Color): Color {
    return copy(alpha = fraction).compositeOver(background)
}