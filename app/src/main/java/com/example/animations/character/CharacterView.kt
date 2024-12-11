package com.example.animations.character

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.example.animations.R
import com.example.animations.SpriteAnimationFromSheet


@Composable
fun CharacterView(characterViewModel: CharacterViewModel, scale: Float) {

    // Convertir las coordenadas del personaje en escala
    val offsetX = (characterViewModel.characterX * scale).dp
    val offsetY = (characterViewModel.characterY * scale).dp

    // Determinar la orientación para el flipping horizontal
    val facingRight = characterViewModel.facingRight
    val scaleX = if (facingRight) 1f else -1f

    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size((61 * scale).dp, (63 * scale).dp)
            .graphicsLayer(scaleX = scaleX, transformOrigin = TransformOrigin.Center)
    ) {
        val currentFrame by characterViewModel.currentFrame.observeAsState(0)
        val spriteSheet = ImageBitmap.imageResource(R.drawable.char_main)

        SpriteAnimationFromSheet(
            spriteSheet = spriteSheet,
            scale = (6 * scale).toInt(), // Ajustar el escalado del sprite
            frameWidth = 32,
            frameHeight = 32,
            row = characterViewModel.animationRow(),
            currentFrame = currentFrame,
            yCorrection = 1
        )
    }
}
