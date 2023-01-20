package com.neo.hash.ui.screen.start.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.neo.hash.data.RemotePlayer
import com.neo.hash.data.toModel
import com.neo.hash.model.GameConfig
import com.neo.hash.model.HashState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class OpenGameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.InputKey)
    val uiState = _uiState.asStateFlow()

    private val gamesRef by lazy { Firebase.database.getReference("games") }

    private val installation by lazy { FirebaseInstallations.getInstance() }

    private val _uiMessage = Channel<String>(capacity = Channel.UNLIMITED)
    val uiMessage = _uiMessage.receiveAsFlow()

    fun openGame(
        userName: String,
        gameKey: String
    ) {
        _uiState.value = UiState.Opening

        val gameRef = gamesRef.child(gameKey)

        installation.id.addOnSuccessListener { installationId ->
            gameRef.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            handlerOpenGame(
                                snapshot = snapshot,
                                installationId = installationId,
                                userName = userName,
                                gameKey = gameKey
                            )
                        } else {
                            viewModelScope.launch {
                                _uiMessage.send("Jogos não encontrado!")
                            }

                            _uiState.value = UiState.InputKey
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _uiState.value = UiState.InputKey
                    }
                }
            )
        }.addOnFailureListener {
            _uiState.value = UiState.InputKey
        }
    }

    private fun handlerOpenGame(
        snapshot: DataSnapshot,
        installationId: String,
        userName: String,
        gameKey: String
    ) {
        val playersSnapshot = snapshot.child("players")
        val symbolStartsSnapshot = snapshot.child("symbol_starts")

        val remotePlayers = playersSnapshot.getValue<List<RemotePlayer>>()!!
        val symbolStarts = symbolStartsSnapshot.getValue<HashState.Block.Symbol>()!!

        val isMyGame = remotePlayers.any { it.id == installationId }

        when {
            isMyGame && remotePlayers.size == 1 -> {

                _uiState.value = UiState.WaitingEnemy(gameKey)

                waitEnemy(gameKey) { updatedRemotePlayers ->

                    _uiState.value = UiState.Finished(
                        GameConfig.Remote(
                            players = updatedRemotePlayers.map { it.toModel() },
                            symbolStarts = symbolStarts,
                            gameKey = gameKey
                        )
                    )
                }
            }

            isMyGame && remotePlayers.size == 2 -> {
                _uiState.value = UiState.Finished(
                    GameConfig.Remote(
                        players = remotePlayers.map { it.toModel() },
                        symbolStarts = symbolStarts,
                        gameKey = gameKey
                    )
                )
            }

            !isMyGame && remotePlayers.size == 1 -> {

                val mySymbol = remotePlayers[0].symbol.enemy

                val myPlayer = RemotePlayer(
                    id = installationId,
                    symbol = mySymbol,
                    name = userName
                )

                playersSnapshot.ref.updateChildren(
                    mapOf("1" to myPlayer)
                ).addOnSuccessListener {

                    val players = (remotePlayers + myPlayer).map { it.toModel() }

                    _uiState.value = UiState.Finished(
                        GameConfig.Remote(
                            players = players,
                            symbolStarts = symbolStarts,
                            gameKey = gameKey
                        )
                    )
                }.addOnFailureListener {
                    _uiState.value = UiState.InputKey
                }
            }

            !isMyGame && remotePlayers.size == 2 -> {
                _uiState.value = UiState.InputKey

                viewModelScope.launch {
                    _uiMessage.send("O jogo atingiu o máximo de jogadores!")
                }
            }
        }
    }

    private fun waitEnemy(
        gameKey: String,
        onFinish: (List<RemotePlayer>) -> Unit
    ) {
        gamesRef.child(gameKey)
            .child("players")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.childrenCount == 2L) {
                            onFinish(snapshot.getValue<List<RemotePlayer>>()!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _uiState.value = UiState.InputKey
                    }
                }
            )
    }

    sealed interface UiState {
        object InputKey : UiState

        object Opening : UiState

        data class WaitingEnemy(
            val gameKey: String
        ) : UiState

        data class Finished(
            val gameConfig: GameConfig.Remote
        ) : UiState
    }
}