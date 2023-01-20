package com.neo.hash.component.icon

import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.neo.hash.model.HashState

@Composable
fun Symbol(
    symbol: HashState.Block.Symbol,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = if (enabled) colors.primary else
        colors.onSurface.copy(ContentAlpha.disabled)
) = Icon(
    imageVector = when (symbol) {
        HashState.Block.Symbol.O -> Icons.Outlined.Circle
        HashState.Block.Symbol.X -> Icons.Outlined.Close
    },
    tint = color,
    contentDescription = null,
    modifier = modifier
)
