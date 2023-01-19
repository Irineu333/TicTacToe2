@file:OptIn(ExperimentalComposeUiApi::class)

package com.neo.hash.ui.screen.start

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.neo.hash.model.GameConfig
import com.neo.hash.ui.screen.start.viewModel.CreateGameViewModel
import com.neo.hash.ui.screen.start.viewModel.OpenGameViewModel
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

@Composable
private fun StartRemote(
    modifier: Modifier = Modifier,
    onGameStart: (GameConfig.Remote) -> Unit
) = Column(modifier) {

    var userName by rememberSaveable { mutableStateOf("") }

    val navController = rememberNavController()

    val currentDestination = navController
        .currentBackStackEntryAsState()
        .value?.destination

    OutlinedTextField(
        value = userName,
        onValueChange = {
            userName = it.trim()
        },
        label = {
            Text(text = "Nome do jogador")
        },
        enabled = currentDestination?.route == "choose",
        modifier = Modifier.fillMaxWidth()
    )

    NavHost(
        navController = navController,
        startDestination = "choose"
    ) {
        composable("choose") {
            Choose(
                enabled = userName.isNotBlank(),
                modifier = Modifier.padding(top = 8.dp),
                onPlayGame = {},
                onCreateGame = {
                    navController.navigate("create")
                },
                onOpenGame = {
                    navController.navigate("open")
                }
            )
        }

        composable("create") {
            CreateGame(
                userName = userName,
                modifier = Modifier.padding(top = 8.dp),
                onGameStart = onGameStart,
                onBackNavigation = {
                    navController.popBackStack()
                }
            )
        }

        composable("open") {
            OpenGame(
                userName = userName,
                onGameStart = onGameStart,
                onBackNavigation = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
private fun OpenGame(
    modifier: Modifier = Modifier,
    userName: String,
    onGameStart: (GameConfig.Remote) -> Unit,
    onBackNavigation: () -> Unit,
    viewModel: OpenGameViewModel = viewModel()
) = Column(modifier) {

    var gameKey by remember { mutableStateOf("") }

    val state = viewModel.uiState.collectAsState().value

    OutlinedTextField(
        value = gameKey,
        onValueChange = {
            gameKey = it.trim()
        },
        label = {
            Text(text = "Código do jogo")
        },
        enabled = state is OpenGameViewModel.UiState.InputKey,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(8.dp))

    when (state) {
        is OpenGameViewModel.UiState.Opened -> {
            LaunchedEffect(state) {
                onGameStart(state.gameConfig)
            }
        }
        OpenGameViewModel.UiState.InputKey -> {
            Button(
                onClick = {
                    viewModel.openGame(
                        userName = userName,
                        gameKey = gameKey
                    )
                },
                enabled = gameKey.isNotBlank(),
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Abrir")
            }
        }

        OpenGameViewModel.UiState.Opening -> {
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

                    Text(text = "Abrindo jogo...")
                }
            }
        }
    }
}

@Composable
private fun CreateGame(
    modifier: Modifier = Modifier,
    userName: String,
    onGameStart: (GameConfig.Remote) -> Unit,
    onBackNavigation: () -> Unit,
    viewModel: CreateGameViewModel = viewModel()
) = Column(modifier) {

    LaunchedEffect(Unit) {
        viewModel.createGame(userName)
    }

    when (val state = viewModel.uiState.collectAsState().value) {
        is CreateGameViewModel.UiState.Creating -> {
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

        is CreateGameViewModel.UiState.Waiting -> {
            val clipboardManage = LocalClipboardManager.current
            val context = LocalContext.current

            Button(
                onClick = {
                    clipboardManage.setText(AnnotatedString(state.gameKey))
                    Toast.makeText(context, "Copiado", Toast.LENGTH_SHORT).show()
                },
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = state.gameKey)
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

        is CreateGameViewModel.UiState.Created -> {
            LaunchedEffect(state) {
                onGameStart(state.gameConfig)
            }
        }
        CreateGameViewModel.UiState.Error -> {
            LaunchedEffect(state) {
                onBackNavigation()
            }
        }
    }
}

@Composable
private fun Choose(
    enabled: Boolean,
    onPlayGame: () -> Unit,
    onCreateGame: () -> Unit,
    onOpenGame: () -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier) {

    Button(
        onClick = onPlayGame,
        contentPadding = PaddingValues(12.dp),
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Jogar")
    }

    Button(
        onClick = onCreateGame,
        contentPadding = PaddingValues(12.dp),
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Criar jogo")
    }

    Button(
        onClick = onOpenGame,
        enabled = enabled,
        contentPadding = PaddingValues(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Abrir jogo")
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