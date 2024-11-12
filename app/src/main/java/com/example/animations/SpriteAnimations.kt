package com.example.animations

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class CharacterViewModel(
    val animations: Map<String, Pair<Int, Int>>,
    startAnimation: String = "idle"
) : ViewModel() {

    // Estado actual de la animación
    private val _currentAnimation = MutableLiveData(startAnimation)
    val currentAnimationName = _currentAnimation

    private val _nextAnimation = MutableStateFlow(startAnimation)
    val nextAnimation = _nextAnimation.asStateFlow()

    private val _currentFrame = MutableLiveData<Int>(0)
    val currentFrame: LiveData<Int> = _currentFrame

    private val _isAnimationComplete = MutableStateFlow(true)
    val isAnimationComplete = _isAnimationComplete.asStateFlow()

    var job: Job? = null

    init {
        characterLoop() // Iniciar la primera animación
    }

    private fun characterLoop() {

        if (_isAnimationComplete.value) {
            _isAnimationComplete.value = false
            _currentAnimation.value = _nextAnimation.value

            job = playAnimation()

            viewModelScope.launch {
                job?.join()
                _isAnimationComplete.value = true
                characterLoop()
            }
        }
    }

    // Función para reproducir la animación actual
    private fun playAnimation(): Job {
        return viewModelScope.launch {
            val animation = animations[_currentAnimation.value] ?: return@launch
            val (_, totalFrames) = animation
            _isAnimationComplete.value = false
            // Reproducir la animación frame a frame
            for (frame in 0 until totalFrames) {
                _currentFrame.value = frame
                delay(230L) // Duración del frame
            }
        }
    }

    fun changeAnimation(newAnimation: String) {
        _nextAnimation.value = newAnimation
    }

    fun animationTotalFrames(): Int {
        return animations[_currentAnimation.value]?.second ?: 2
    }

    fun animationRow(): Int {
        return animations[_currentAnimation.value]?.first ?: 0
    }

    fun animationNames(): List<String> {
        return animations.keys.toList()
    }
}

var characterAnimations = mapOf(
    "idle" to (0 to 2),
    "idle_tired" to (1 to 2),
    "walk" to (2 to 4),
    "run" to (3 to 8),
    "sleepy" to (4 to 6),
    "jump" to (5 to 8),
    "dissolve" to (6 to 3),
    "die" to (7 to 8),
    "slash" to (8 to 8)
)

fun createCharacter(): CharacterViewModel {
    return CharacterViewModel(characterAnimations)
}

var zombieAnimations = mapOf(
    "idle" to (0 to 9),
    "attack_1" to (1 to 4),
    "attack_2" to (2 to 4),
    "attack_3" to (3 to 4),
    "run" to (4 to 8),
    "jump" to (5 to 6),
    "walk" to (6 to 10),
    "dead" to (7 to 5),
    "hurt" to (8 to 5),
    "eating" to (9 to 11)
)

fun createZombi(): CharacterViewModel {
    return CharacterViewModel(zombieAnimations)
}

fun createZombiRandom(): CharacterViewModel {
    return CharacterViewModel(
        zombieAnimations,
        zombieAnimations.keys.toList()[Random.nextInt(0, 9)]
    )
}


@Composable
fun CharacterAnimation(characterController: CharacterViewModel) {
    val currentFrame by characterController.currentFrame.observeAsState(0)

    val spriteSheet = ImageBitmap.imageResource(R.drawable.char_main)

    Log.d("SpriteAnimations", "currentFrame: $currentFrame")

    Box(
        modifier = Modifier.size(61.dp, 63.dp)
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
fun ZombiAnimation(
    characterController: CharacterViewModel,
    modifier: Modifier = Modifier.size(96.dp, 96.dp)
) {

    val currentFrame by characterController.currentFrame.observeAsState(0)
    val currentAnimation by characterController.currentAnimationName.observeAsState("idle")

    val spriteSheet = when (currentAnimation) {
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

    Box(
        modifier = modifier
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
    size: Int,
    frameWidth: Int,
    frameHeight: Int,
    row: Int,
    currentFrame: Int, // Nuevo parámetro para el frame actual
    canvasX: Int = 64,
    canvasY: Int = 64,
    gap: Int = 0,
    dstOffset: IntOffset = IntOffset(0, 0),
    yCorrection: Int = 0,
    xCorrection: Int = 0
) {
    val srcOffsetX = currentFrame * (frameWidth + gap) + xCorrection
    val srcOffsetY = row * (frameHeight + gap) + yCorrection

    Canvas(modifier = Modifier.size(canvasX.dp, canvasY.dp)) {
        drawImage(
            image = spriteSheet,
            srcOffset = IntOffset(srcOffsetX, srcOffsetY),
            srcSize = IntSize(frameWidth, frameHeight),
            dstOffset = dstOffset,
            dstSize = IntSize(frameWidth, frameHeight) * size
        )
    }
}
