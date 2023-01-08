package com.neo.hash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.neo.hash.ui.theme.HashBackground
import com.neo.hash.ui.theme.HashTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HashTheme {
                HashBackground(Modifier.fillMaxSize()) {
                    Box(contentAlignment = Alignment.Center) {
                        val hashState = viewModel.hashState.collectAsState().value

                        HashTable(
                            hash = hashState,
                            onClick = {},
                            modifier = Modifier.aspectRatio(1f)
                        )
                    }
                }
            }
        }
    }
}