@file:OptIn(ExperimentalComposeUiApi::class)

package com.neo.hash.ui.screen.start

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.neo.hash.model.GameConfig
import com.neo.hash.ui.theme.HashBackground
import com.neo.hash.ui.theme.HashTheme

enum class GameMode(val title: String) {
    INPUT("Jogador vs Jogados"),
    PHONE("Jogados vs AI"),
    REMOTE("Jogar online")
}

@Composable
fun StartDialog(
    gameMode: GameMode,
    onGameStart: (GameConfig) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) = Dialog(
    onDismissRequest = onDismissRequest,
    properties = DialogProperties(usePlatformDefaultWidth = false)
) {
    Column(
        modifier
            .padding(32.dp)
            .background(
                colors.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 2.dp,
                brush = SolidColor(colors.primary),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
        horizontalAlignment = CenterHorizontally
    ) {

        Text(
            text = gameMode.title,
            style = typography.subtitle1,
            fontSize = 18.sp
        )

        Spacer(Modifier.height(8.dp))

        when (gameMode) {
            GameMode.INPUT -> Unit
            GameMode.PHONE -> Unit
            GameMode.REMOTE -> StartRemote(
                onGameStart = onGameStart
            )
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    HashTheme {
        HashBackground {
            StartDialog(
                GameMode.REMOTE,
                onDismissRequest = {},
                onGameStart = {}
            )
        }
    }
}