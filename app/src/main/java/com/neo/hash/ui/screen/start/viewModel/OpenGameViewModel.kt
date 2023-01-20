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

        val inGame = remotePlayers.any { it.id == installationId }

        when {
            inGame && remotePlayers.size == 1 -> {
                // go to waiting
            }

            inGame && remotePlayers.size == 2 -> {
                // run game
            }

            !inGame && remotePlayers.size == 1 -> {
                // insert my player && run game
            }

            !inGame && remotePlayers.size == 2 -> {
                // enter as an observer
            }
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