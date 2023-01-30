package com.neo.hash.ui.screen.start

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.neo.hash.component.icon.Symbol
import com.neo.hash.model.GameConfig
import com.neo.hash.model.HashState
import com.neo.hash.ui.screen.start.viewModel.CreateGameViewModel
import com.neo.hash.ui.screen.start.viewModel.OpenGameViewModel
import com.neo.hash.util.extension.alpha
import com.neo.hash.util.extension.currentRouteAsState
import com.neo.hash.util.extension.stateColor

@Composable
fun StartRemote(
    modifier: Modifier = Modifier, onGameStart: (GameConfig.Remote) -> Unit
) = Column(modifier) {

    var waitingInOpenGame by remember { mutableStateOf(false) }

    var userName by rememberSaveable { mutableStateOf("") }
    var symbol by rememberSaveable { mutableStateOf(HashState.Block.Symbol.random()) }

    val navController = rememberNavController()

    val currentRoute = navController.currentRouteAsState().value

    val isChooseScreen = currentRoute == "choose_screen"

    OutlinedTextField(
        value = userName,
        onValueChange = {
            userName = it.trim()
        },
        label = {
            Text(text = "Nome do jogador")
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = currentRoute != "open_screen" || waitingInOpenGame,
                enter = fadeIn() + slideInHorizontally(
                    initialOffsetX = {
                        it / 2
                    }
                ),
                exit = fadeOut() + slideOutHorizontally(
                    targetOffsetX = {
                        it / 2
                    }
                ),
            ) {
                IconButton(
                    onClick = {
                        symbol = symbol.enemy
                    },
                    enabled = isChooseScreen
                ) {
                    Symbol(
                        symbol = symbol,
                        color = colors.stateColor(isChooseScreen),
                    )
                }
            }
        },
        enabled = isChooseScreen,
        modifier = Modifier.fillMaxWidth()
    )

    NavHost(
        navController = navController,
        startDestination = "choose_screen"
    ) {
        composable("choose_screen") {
            Choose(
                enabled = userName.isNotBlank(),
                onCreateGame = {
                    navController.navigate("create_screen")
                },
                onOpenGame = {
                    navController.navigate("open_screen")
                },
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        composable("create_screen") {
            CreateGame(
                userName = userName,
                symbol = symbol,
                onGameStart = onGameStart,
                onBackNavigation = {
                    navController.popBackStack()
                },
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        composable("open_screen") {
            OpenGame(
                userName = userName,
                onGameStart = onGameStart,
                updatePlayerConfig = {

                    waitingInOpenGame = it != null

                    if (it != null) {
                        userName = it.name
                        symbol = it.symbol
                    }
                }
            )
        }
    }
}

@Composable
private fun Choose(
    enabled: Boolean,
    onCreateGame: () -> Unit,
    onOpenGame: () -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier) {

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

@Composable
private fun WaitingEnemy(
    modifier: Modifier = Modifier, gameKey: String? = null
) = Column(modifier) {

    val clipboardManage = LocalClipboardManager.current
    val context = LocalContext.current

    if (gameKey != null) {
        Button(
            onClick = {
                clipboardManage.setText(AnnotatedString(gameKey))
                Toast.makeText(context, "Copiado", Toast.LENGTH_SHORT).show()
            },
            contentPadding = PaddingValues(12.dp),
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth()
        ) {
            Text(text = gameKey)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
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

@Composable
private fun CreateGame(
    modifier: Modifier = Modifier,
    userName: String,
    symbol: HashState.Block.Symbol,
    onGameStart: (GameConfig.Remote) -> Unit,
    onBackNavigation: () -> Unit,
    viewModel: CreateGameViewModel = viewModel()
) = Column(modifier) {

    LaunchedEffect(Unit) {
        viewModel.createGame(
            userName = userName,
            symbol = symbol
        )
    }

    when (val state = viewModel.uiState.collectAsState().value) {
        is CreateGameViewModel.UiState.Creating -> {
            Card(
                backgroundColor = colors.onSurface
                    .alpha(0.3f, colors.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp, Alignment.CenterHorizontally
                    ),
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

        is CreateGameViewModel.UiState.WaitingEnemy -> {
            WaitingEnemy(gameKey = state.gameKey)
        }

        is CreateGameViewModel.UiState.Finished -> {
            LaunchedEffect(state) {
                onGameStart(state.gameConfig)
            }
        }
        is CreateGameViewModel.UiState.Error -> {

            val context = LocalContext.current

            LaunchedEffect(state) {

                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()

                onBackNavigation()
            }
        }
    }
}

@Composable
private fun OpenGame(
    modifier: Modifier = Modifier,
    userName: String,
    onGameStart: (GameConfig.Remote) -> Unit,
    updatePlayerConfig: (userName: GameConfig.Player?) -> Unit,
    viewModel: OpenGameViewModel = viewModel()
) = Column(modifier) {

    var gameKey by rememberSaveable { mutableStateOf("") }

    val state = viewModel.uiState.collectAsState().value

    val context = LocalContext.current

    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    val isInputKeyScreen = state is OpenGameViewModel.UiState.InputKey

    OutlinedTextField(
        value = gameKey,
        onValueChange = {
            gameKey = it.trim()
        },
        label = {
            Text(text = "Código do jogo")
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = isInputKeyScreen,
                enter = fadeIn() + slideInHorizontally(
                    initialOffsetX = {
                        it / 2
                    }
                ),
                exit = fadeOut() + slideOutHorizontally(
                    targetOffsetX = {
                        it / 2
                    }
                ),
            ) {
                IconButton(
                    onClick = {
                        clipboardManager.getText()?.let {
                            gameKey = it.text
                        }
                    },
                    enabled = isInputKeyScreen
                ) {

                    Icon(
                        imageVector = Icons.Rounded.ContentPaste,
                        tint = colors.primary,
                        contentDescription = null
                    )
                }
            }
        },
        enabled = isInputKeyScreen,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(8.dp))

    when (state) {
        is OpenGameViewModel.UiState.Finished -> {
            LaunchedEffect(state) {
                onGameStart(state.gameConfig)
            }
        }

        is OpenGameViewModel.UiState.InputKey -> {

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
                    .alpha(0.3f, colors.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp, Alignment.CenterHorizontally
                    ),
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
        is OpenGameViewModel.UiState.WaitingEnemy -> {

            LaunchedEffect(state) {
                updatePlayerConfig(state.myPlayer)
            }

            DisposableEffect(state) {
                onDispose {
                    updatePlayerConfig(null)
                }
            }

            WaitingEnemy()
        }
    }
}
