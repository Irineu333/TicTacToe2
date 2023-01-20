package com.neo.hash.ui.screen.start.viewModel

import androidx.lifecycle.ViewModel
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateGameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Creating)
    val uiState = _uiState.asStateFlow()

    private val gamesRef by lazy { Firebase.database.getReference("games") }

    private val installation by lazy { FirebaseInstallations.getInstance() }

    fun createGame(
        userName: String,
        symbol: HashState.Block.Symbol
    ) {
        val newGameRef = gamesRef.push()

        val playerTurn = HashState.Block.Symbol.values().random()

        installation.id.addOnSuccessListener { result ->
            newGameRef.setValue(
                mapOf(
                    "player_turn" to playerTurn,
                    "players" to listOf(
                        RemotePlayer(
                            id = result,
                            name = userName,
                            symbol = symbol
                        )
                    )
                )
            ).addOnSuccessListener {
                _uiState.value = UiState.WaitingEnemy(newGameRef.key!!)

                waitEnemy(
                    gameKey = newGameRef.key!!,
                ) { remotePlayers ->

                    _uiState.value = UiState.Finished(
                        GameConfig.Remote(
                            players = remotePlayers.map { it.toModel() },
                            gameKey = newGameRef.key!!
                        )
                    )
                }
            }.addOnFailureListener {
                _uiState.value = UiState.Error(it.message)
            }
        }.addOnFailureListener {
            _uiState.value = UiState.Error(it.message)
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
                        _uiState.value = UiState.Error(error.message)
                    }
                }
            )
    }

    sealed interface UiState {
        object Creating : UiState

        data class Error(
            val message: String? = null
        ) : UiState

        data class WaitingEnemy(
            val gameKey: String
        ) : UiState

        data class Finished(
            val gameConfig: GameConfig.Remote
        ) : UiState
    }
}