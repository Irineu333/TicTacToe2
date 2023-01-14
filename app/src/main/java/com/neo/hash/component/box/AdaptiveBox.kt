package com.neo.hash.component.box

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

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