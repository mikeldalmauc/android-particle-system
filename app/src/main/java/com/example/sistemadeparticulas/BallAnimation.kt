package com.example.sistemadeparticulas

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Composable
fun SinusoidalBallAnimation() {
    // Animatable para la posición en el eje X
    val ballX = remember { Animatable(0f) }

    // Variable para la posición en el eje Y
    val amplitude = 100f // Amplitud de la onda (ajusta según desees)
    val frequency = 0.05f // Frecuencia de la onda (ajusta según desees)
    val ballRadius = 20f // Radio de la bola

    // Variable de estado para almacenar el ancho del Canvas
    var canvasWidth by remember { mutableStateOf(0f) }

    // Solo lanzará la animación cuando canvasWidth esté disponible (mayor a 0)
    LaunchedEffect(canvasWidth) {
        if (canvasWidth > 0f) {
            ballX.animateTo(
                targetValue = canvasWidth,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 5000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Asigna el tamaño de Canvas a canvasWidth una vez esté disponible
        canvasWidth = size.width

        val x = ballX.value
        val y = center.y + amplitude * sin(frequency * x)

        drawCircle(
            color = Color.Red,
            radius = ballRadius,
            center = Offset(x, y)
        )
    }
}
