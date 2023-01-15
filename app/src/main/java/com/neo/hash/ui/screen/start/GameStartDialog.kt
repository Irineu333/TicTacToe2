package com.neo.hash.ui.screen.start

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.neo.hash.model.GameConfig

enum class GameMode {
    INPUT,
    PHONE,
    REMOTE
}

@Composable
fun GameStartDialog(
    gameMode: GameMode,
    onGameStart: (GameConfig) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) = Dialog(
    onDismissRequest = onDismissRequest
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, MaterialTheme.colors.primary)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(text = "Olá, mundo", style = typography.h6)

            when (gameMode) {
                GameMode.INPUT -> Unit
                GameMode.PHONE -> Unit
                GameMode.REMOTE -> Unit
            }
        }
    }
}
