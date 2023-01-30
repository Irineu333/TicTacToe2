package com.neo.hash.util.extension

import androidx.compose.material.Colors
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun Colors.stateColor(
    enabled: Boolean,
    enabledColor: Color = primary,
    disabledColor: Color = MaterialTheme.colors.onSurface.copy(ContentAlpha.disabled)
) = if (enabled) enabledColor else disabledColor