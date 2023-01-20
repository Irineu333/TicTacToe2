package com.neo.hash.ui.screen.start

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.neo.hash.model.GameConfig
import com.neo.hash.ui.screen.start.viewModel.CreateGameViewModel
import com.neo.hash.ui.screen.start.viewModel.OpenGameViewModel

@Composable
fun StartRemote(
    modifier: Modifier = Modifier,
    onGameStart: (GameConfig.Remote) -> Unit
) = Column(modifier) {

    var userName by rememberSaveable { mutableStateOf("") }

    val navController = rememberNavController()

    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

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
                onCreateGame = {
                    navController.navigate("create")
                },
                onOpenGame = {
                    navController.navigate("open")
                },
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        composable("create") {
            CreateGame(
                userName = userName,
                onGameStart = onGameStart,
                onBackNavigation = {
                    navController.popBackStack()
                },
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        composable("open") {
            OpenGame(
                userName = userName,
                onGameStart = onGameStart
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
    modifier: Modifier = Modifier,
    gameKey: String? = null
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
        modifier = Modifier
            .fillMaxWidth(),
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
                backgroundColor = MaterialTheme.colors.onSurface
                    .copy(alpha = 0.3f)
                    .compositeOver(MaterialTheme.colors.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
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
        CreateGameViewModel.UiState.Error -> {
            LaunchedEffect(state) {
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
    viewModel: OpenGameViewModel = viewModel()
) = Column(modifier) {

    var gameKey by rememberSaveable { mutableStateOf("") }

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
        is OpenGameViewModel.UiState.Finished -> {
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
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
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
            WaitingEnemy()
        }
    }
}
