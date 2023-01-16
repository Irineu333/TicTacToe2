package com.neo.hash.ui.screen.start.viewModel

import androidx.lifecycle.ViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.neo.hash.model.HashState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class StartRemoteViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.InsetName)
    val uiState = _uiState.asStateFlow()

    private val gamesRef by lazy { Firebase.database.getReference("games") }

    private val installation by lazy { FirebaseInstallations.getInstance() }

    fun createRemoteGame(userName: String) {

        _uiState.value = UiState.Creating

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
                    _uiState.value = UiState.InsetName
                }
            } else {
                _uiState.value = UiState.InsetName
            }
        }
    }

    sealed interface UiState {
        object InsetName : UiState
        object Creating : UiState
        data class Waiting(
            val gameHash: String
        ) : UiState
    }
}
