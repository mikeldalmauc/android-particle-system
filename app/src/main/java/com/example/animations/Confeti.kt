package com.example.animations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay


@Composable
@Preview
fun ConfettiPreview(){
    Confeti()
}


@Composable
fun Confeti(){

    // Estado para controlar la animación del el confeti
    var showFelicidades by remember { mutableStateOf(false) }

    // Composición de Lottie y progreso automático al activarse
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.confeti))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = showFelicidades,
        iterations = 1 // Ejecuta una vez completa en cada activación
    )

    LaunchedEffect (showFelicidades){
        delay(5500)
        showFelicidades = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón para activar la animación
        Button(onClick = {
            showFelicidades = true // Activa la animación de confeti y el texto
        }) {
            Text("¡Haz clic para felicitar!")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Composición de Lottie y texto de felicitaciones
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(400.dp)
        ) {

            // Animación de Lottie con la reproducción del confeti
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

}