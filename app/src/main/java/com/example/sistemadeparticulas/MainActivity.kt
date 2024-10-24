package com.example.sistemadeparticulas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sistemadeparticulas.ui.theme.SistemaDeParticulasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SistemaDeParticulasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ParticlesBox(innerPadding)
                }
            }
        }
    }
}

@Composable
fun ParticlesBox(innerPadding: PaddingValues){

    Box(modifier = Modifier.padding(innerPadding)){
        Particles()
    }
}


