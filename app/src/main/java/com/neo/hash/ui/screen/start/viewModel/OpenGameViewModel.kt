package com.neo.hash.ui.screen.start.viewModel

import androidx.lifecycle.ViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.neo.hash.model.GameConfig
import com.neo.hash.model.HashState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class OpenGameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Opening)
    val uiState = _uiState.asStateFlow()

    private val gamesRef by lazy { Firebase.database.getReference("games") }

    private val installation by lazy { FirebaseInstallations.getInstance() }

    fun openGame(
        userName: String,
        gameKey: String
    ) {
        val players = gamesRef.child(gameKey).child("players")

        installation.id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                players.updateChildren(
                    mapOf(
                        "1" to mapOf(
                            "id" to task.result,
                            "symbol" to HashState.Block.Symbol.X,
                            "name" to userName
                        )
                    )
                )
            } else {
                _uiState.value = UiState.Error
            }
        }

    }

    sealed interface UiState {
        object Opening : UiState

        object Error : UiState

        data class Opened(
            val gameConfig: GameConfig.Remote
        ) : UiState
    }
}