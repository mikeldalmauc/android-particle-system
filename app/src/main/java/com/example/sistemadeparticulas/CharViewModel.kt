package com.example.sistemadeparticulas

import androidx.compose.animation.core.Animation
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

class CharacterViewModel(val animations: Map<String, Pair<Int, Int>>, startAnimation: String = "idle") : ViewModel() {

    // Estado actual de la animación
    private val _currentAnimation = MutableLiveData(startAnimation)
    val currentAnimationName = _currentAnimation

    private val _nextAnimation = MutableStateFlow(startAnimation)
    val nextAnimation = _nextAnimation.asStateFlow()

    private val _currentFrame = MutableLiveData<Int>(0)
    val currentFrame : LiveData<Int> = _currentFrame

    private val _isAnimationComplete = MutableStateFlow(true)
    val isAnimationComplete = _isAnimationComplete.asStateFlow()

    var job: Job? = null

    init {
        characterLoop() // Iniciar la primera animación
    }

    private fun characterLoop(){

        if(_isAnimationComplete.value){
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
    private fun playAnimation() : Job {
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
    "slash" to (8 to 8))

fun createCharacter() : CharacterViewModel {
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
    "eating" to (9 to 11))

fun createZombi() : CharacterViewModel {
    return CharacterViewModel(zombieAnimations)
}

fun createZombiRandom() : CharacterViewModel {
    return CharacterViewModel(
        zombieAnimations,
        zombieAnimations.keys.toList()[Random.nextInt(0, 9)]
    )
}
