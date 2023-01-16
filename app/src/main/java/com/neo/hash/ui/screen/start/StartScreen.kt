package com.neo.hash.ui.screen.start

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neo.hash.model.GameConfig
import com.neo.hash.ui.screen.start.viewModel.StartRemoteViewModel
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
    onDismissRequest = onDismissRequest
) {
    Card(
        modifier = modifier.wrapContentHeight(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, colors.primary)
    ) {
        Column(modifier.padding(16.dp)) {

            Text(
                text = gameMode.title,
                style = typography.subtitle1.copy(
                    fontSize = 18.sp
                ),
                modifier = Modifier.align(CenterHorizontally)
            )

            Spacer(Modifier.height(8.dp))

            when (gameMode) {
                GameMode.INPUT -> Unit
                GameMode.PHONE -> Unit
                GameMode.REMOTE -> {
                    StartRemote()
                }
            }
        }
    }
}

@Composable
private fun StartRemote(
    modifier: Modifier = Modifier,
    viewModel: StartRemoteViewModel = viewModel()
) = Column(modifier) {

    var userName by rememberSaveable { mutableStateOf("") }

    val state = viewModel.uiState.collectAsState().value

    OutlinedTextField(
        value = userName,
        onValueChange = {
            userName = it.trim()
        },
        label = {
            Text(text = "Nome do jogador")
        },
        enabled = state == StartRemoteViewModel.UiState.InsetName,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(8.dp))

    when (state) {
        StartRemoteViewModel.UiState.InsetName -> {
            Button(
                onClick = {
                    viewModel.createRemoteGame(userName)
                },
                contentPadding = PaddingValues(12.dp),
                enabled = userName.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Criar jogo")
            }

            Button(
                onClick = { },
                enabled = userName.isNotBlank(),
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(text = "Abrir jogo")
            }
        }

        StartRemoteViewModel.UiState.Creating -> {
            Card(
                backgroundColor = colors.onSurface
                    .copy(alpha = 0.3f)
                    .compositeOver(colors.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        color = LocalContentColor.current,
                        strokeWidth = 1.5.dp
                    )

                    Text(text = "Criando jogo...")
                }
            }
        }

        is StartRemoteViewModel.UiState.Waiting -> {

            val clipboardManage = LocalClipboardManager.current
            val context = LocalContext.current

            Button(
                onClick = {
                    clipboardManage.setText(AnnotatedString(state.gameHash))
                    Toast.makeText(context, "Copiado", Toast.LENGTH_SHORT).show()
                },
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = state.gameHash)
            }

            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(12.dp),
                    color = LocalContentColor.current,
                    strokeWidth = 1.5.dp
                )

                Text(text = "Aguardando adiversário...")
            }
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