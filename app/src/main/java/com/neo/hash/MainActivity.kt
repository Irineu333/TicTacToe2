package com.neo.hash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.neo.hash.ui.theme.HashBackground
import com.neo.hash.ui.theme.HashTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HashTheme {
                HashBackground(Modifier.fillMaxSize()) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "Olá, mundo!")
                    }
                }
            }
        }
    }
}