package com.example.sistemadeparticulas

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp


@Composable
fun CharacterAnimation(characterController: CharacterViewModel) {
    val currentFrame by characterController.currentFrame.observeAsState(0)

    val spriteSheet = ImageBitmap.imageResource(R.drawable.char_main)

    Log.d("SpriteAnimations", "currentFrame: $currentFrame")

    Box(modifier = Modifier.size(61.dp, 63.dp)
    ) {
        SpriteAnimationFromSheet(
            spriteSheet = spriteSheet,
            size = 5,
            frameWidth = 32,
            frameHeight = 32,
            row = characterController.animationRow(),
            currentFrame = currentFrame,
            yCorrection = 1
        )
    }
}

@Composable
fun ZombiAnimation(characterController: CharacterViewModel, modifier: Modifier = Modifier.size(96.dp, 96.dp)) {

    val currentFrame by characterController.currentFrame.observeAsState(0)
    val currentAnimation by characterController.currentAnimationName.observeAsState("idle")

    val spriteSheet = when(currentAnimation){
        "ide" -> ImageBitmap.imageResource(R.drawable.zombi_idle)
        "attack_1" -> ImageBitmap.imageResource(R.drawable.zombi_attack_1)
        "attack_2" -> ImageBitmap.imageResource(R.drawable.zombi_attack_2)
        "attack_3" -> ImageBitmap.imageResource(R.drawable.zombi_attack_3)
        "run" -> ImageBitmap.imageResource(R.drawable.zombi_run)
        "jump" -> ImageBitmap.imageResource(R.drawable.zombi_jump)
        "walk" -> ImageBitmap.imageResource(R.drawable.zombi_walk)
        "dead" -> ImageBitmap.imageResource(R.drawable.zombi_dead)
        "hurt" -> ImageBitmap.imageResource(R.drawable.zombi_hurt)
        "eating" -> ImageBitmap.imageResource(R.drawable.zombi_eating)
        else -> ImageBitmap.imageResource(R.drawable.zombi_idle)
    }

    Box(modifier = modifier
    ) {
        SpriteAnimationFromSheet(
            spriteSheet = spriteSheet,
            size = 5,
            frameWidth = 96,
            frameHeight = 97,
            row = 0,
            currentFrame = currentFrame,
            dstOffset = IntOffset(-100, -260),
            canvasX = 96,
            canvasY = 97,
        )
    }
}


/**
 * Dibuja una animación de un sprite a partir de una hoja de sprites
 * @param spriteSheet hoja de sprites
 * @param size tamaño de la imagen
 * @param frameWidth ancho de cada frame
 * @param frameHeight alto de cada frame
 * @param columns número de columnas en la hoja de sprites
 * @param row fila de la hoja de sprites
 * @param currentFrame frame actual
 * @param gap espacio entre frames
 */
@Composable
fun SpriteAnimationFromSheet(
    spriteSheet: ImageBitmap,
    size : Int,
    frameWidth: Int,
    frameHeight: Int,
    row: Int,
    currentFrame: Int, // Nuevo parámetro para el frame actual
    canvasX: Int = 64,
    canvasY: Int = 64,
    gap: Int = 0,
    dstOffset: IntOffset = IntOffset(0, 0),
    yCorrection : Int = 0,
    xCorrection : Int = 0
) {
    val srcOffsetX = currentFrame * (frameWidth + gap) + xCorrection
    val srcOffsetY = row * (frameHeight + gap) + yCorrection

    Canvas(modifier = Modifier.size(canvasX.dp,canvasY.dp)) {
        drawImage(
            image = spriteSheet,
            srcOffset = IntOffset(srcOffsetX, srcOffsetY),
            srcSize = IntSize(frameWidth, frameHeight),
            dstOffset = dstOffset,
            dstSize = IntSize(frameWidth, frameHeight)*size
        )
    }
}
