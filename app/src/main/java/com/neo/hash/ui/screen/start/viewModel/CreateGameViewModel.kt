package com.neo.hash.ui.screen.start.viewModel

import androidx.lifecycle.ViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
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

        installation.id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                newGameRef.setValue(
                    mapOf(
                        "symbol_starts" to HashState.Block.Symbol.O,
                        "players" to listOf(
                            mapOf(
                                "id" to task.result,
                                "symbol" to HashState.Block.Symbol.O,
                                "name" to userName
                            )
                        )
                    )
                ).addOnSuccessListener {
                    _uiState.value = UiState.Waiting(newGameRef.key!!)
                }.addOnFailureListener {
                    _uiState.value = UiState.Error
                }
            } else {
                _uiState.value = UiState.Error
            }
        }
    }

    sealed interface UiState {
        object Creating : UiState

        object Error : UiState

        data class Waiting(
            val gameKey : String
        ) : UiState

        data class Created(
            val gameConfig: GameConfig.Remote
        ) : UiState
    }
}