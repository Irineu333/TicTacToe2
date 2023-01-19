package com.neo.hash.ui.screen.start.viewModel

import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.neo.hash.data.DataPlayer
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

    fun createGame(userName: String) {

        val newGameRef = gamesRef.push()

        val symbolStarts = HashState.Block.Symbol.values().random()

        installation.id.addOnSuccessListener { result ->
            newGameRef.setValue(
                mapOf(
                    "symbol_starts" to symbolStarts,
                    "players" to listOf(
                        mapOf(
                            "id" to result,
                            "symbol" to HashState.Block.Symbol.O,
                            "name" to userName
                        )
                    )
                )
            ).addOnSuccessListener {
                _uiState.value = UiState.Waiting(newGameRef.key!!)

                listenOpponent(
                    gameKey = newGameRef.key!!,
                    symbolStarts = symbolStarts
                )
            }.addOnFailureListener {
                _uiState.value = UiState.Error
            }
        }.addOnFailureListener {
            _uiState.value = UiState.Error
        }
    }

    private fun listenOpponent(
        gameKey: String,
        symbolStarts: HashState.Block.Symbol
    ) {
        gamesRef.child(gameKey)
            .child("players")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.childrenCount == 2L) {

                            val dataPlayer = snapshot.getValue<List<DataPlayer>>()!!

                            _uiState.value = UiState.Created(
                                GameConfig.Remote(
                                    players = dataPlayer.map { it.toModel() },
                                    symbolStarts = symbolStarts,
                                    gameKey = gameKey
                                )
                            )
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _uiState.value = UiState.Error
                    }
                }
            )
    }

    sealed interface UiState {
        object Creating : UiState

        object Error : UiState

        data class Waiting(
            val gameKey: String
        ) : UiState

        data class Created(
            val gameConfig: GameConfig.Remote
        ) : UiState
    }
}