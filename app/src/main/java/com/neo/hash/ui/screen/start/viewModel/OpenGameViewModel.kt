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

class OpenGameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.InputKey)
    val uiState = _uiState.asStateFlow()

    private val gamesRef by lazy { Firebase.database.getReference("games") }

    private val installation by lazy { FirebaseInstallations.getInstance() }

    fun openGame(
        userName: String,
        gameKey: String
    ) {

        _uiState.value = UiState.Opening

        val gameRef = gamesRef.child(gameKey)

        installation.id.addOnSuccessListener { id ->
            gameRef.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            val playersSnapshot = snapshot.child("players")

                            val remotePlayers = playersSnapshot
                                .getValue<List<RemotePlayer>>()!!

                            val symbolStarts = snapshot.child("symbol_starts")
                                .getValue<HashState.Block.Symbol>()!!

                            if (remotePlayers.size == 1) {

                                val player = RemotePlayer(
                                    id = id,
                                    symbol = HashState.Block.Symbol.X,
                                    name = userName
                                )

                                playersSnapshot.ref.updateChildren(
                                        mapOf("1" to player)
                                    ).addOnSuccessListener {

                                        val players = (remotePlayers + player).map { it.toModel() }

                                        _uiState.value = UiState.Opened(
                                            GameConfig.Remote(
                                                players = players,
                                                symbolStarts = symbolStarts,
                                                gameKey = gameKey
                                            )
                                        )
                                    }.addOnFailureListener {
                                        _uiState.value = UiState.InputKey
                                    }
                            } else {
                                _uiState.value = UiState.InputKey
                            }
                        } else {
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

    sealed interface UiState {
        object Opening : UiState

        data class Opened(
            val gameConfig: GameConfig.Remote
        ) : UiState

        object InputKey : UiState
    }
}