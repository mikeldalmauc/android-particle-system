package com.example.animations.character

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.hypot

enum class CharacterState {
    IDLE,
    MOVING_LEFT,
    MOVING_RIGHT,
    JUMPING,
    ATTACKING,
    DEAD
}

sealed class CharacterEvent {
    object MoveLeft : CharacterEvent()
    object MoveRight : CharacterEvent()
    object StopMoving : CharacterEvent()
    object StartJump : CharacterEvent()
    object StopJump : CharacterEvent()
    object Attack : CharacterEvent()
    object Die : CharacterEvent()
}

class CharacterViewModel(
    startState: CharacterState = CharacterState.IDLE
) : ViewModel() {

    val stateAnimations :  Map<CharacterState, Pair<Int, Int>> = mapOf(
        CharacterState.IDLE to (0 to 2),
        CharacterState.MOVING_LEFT to (2 to 4),
        CharacterState.MOVING_RIGHT to (2 to 4),
        CharacterState.JUMPING to (5 to 8),
        CharacterState.DEAD to (7 to 8),
        CharacterState.ATTACKING to (8 to 8)
    )

    private val _currentState : MutableStateFlow<CharacterState> = MutableStateFlow(startState)
    val currentState = _currentState.asStateFlow()

    private val _currentFrame = MutableLiveData<Int>(0)
    val currentFrame: LiveData<Int> = _currentFrame

    private val _isAnimationComplete = MutableStateFlow(true)
    val isAnimationComplete = _isAnimationComplete.asStateFlow()

    var animationJob: Job? = null

    // Variables de salto
    private var jumpStartTime: Long = 0
    private val maxJumpDuration: Long = 500L
    private val minJumpDuration: Long = 100L
    private val jumpFrameDuration: Long = 50L

    // Variables de movimiento
    private var isMoving = false

    // Posición y orientación del personaje
    var characterX = 0f
    var characterY = 0f
    var facingRight = true

    // Velocidad máxima del personaje
    private val maxSpeed = 5f

    init {
        characterLoop()
    }

    private fun characterLoop() {
        if (_isAnimationComplete.value) {
            _isAnimationComplete.value = false
            // Iniciar la animación del estado actual
            animationJob = playAnimation()

            viewModelScope.launch {
                animationJob?.join()
                _isAnimationComplete.value = true
                characterLoop()
            }
        }
    }

    private fun playAnimation(): Job {
        return viewModelScope.launch {
            val animation = stateAnimations[_currentState.value] ?: return@launch
            val (_, totalFrames) = animation
            _isAnimationComplete.value = false

            for (frame in 0 until totalFrames) {
                _currentFrame.value = frame
                delay(230L)
            }

            when (_currentState.value) {
                CharacterState.JUMPING -> {
                    if (isMoving) {
                        // Si se estaba moviendo antes, asume la última orientación
                        _currentState.value = if (facingRight) CharacterState.MOVING_RIGHT else CharacterState.MOVING_LEFT
                    } else {
                        _currentState.value = CharacterState.IDLE
                    }
                }
                CharacterState.ATTACKING -> {
                    _currentState.value = CharacterState.IDLE
                }
                CharacterState.DEAD -> {
                    // se queda muerto
                }
                else -> {
                    // Otros estados no requieren acción adicional
                }
            }
        }
    }

    fun onEvent(event: CharacterEvent) {
        when (event) {
            CharacterEvent.MoveLeft -> {
                isMoving = true
                if (_currentState.value != CharacterState.JUMPING && _currentState.value != CharacterState.DEAD) {
                    _currentState.value = CharacterState.MOVING_LEFT
                    facingRight = false
                    resetAnimation()
                }
            }
            CharacterEvent.MoveRight -> {
                isMoving = true
                if (_currentState.value != CharacterState.JUMPING && _currentState.value != CharacterState.DEAD) {
                    _currentState.value = CharacterState.MOVING_RIGHT
                    facingRight = true
                    resetAnimation()
                }
            }
            CharacterEvent.StopMoving -> {
                isMoving = false
                if (_currentState.value != CharacterState.JUMPING && _currentState.value != CharacterState.DEAD && _currentState.value != CharacterState.ATTACKING) {
                    _currentState.value = CharacterState.IDLE
                    resetAnimation()
                }
            }
            CharacterEvent.StartJump -> {
                if (_currentState.value != CharacterState.JUMPING && _currentState.value != CharacterState.DEAD) {
                    jumpStartTime = System.currentTimeMillis()
                    _currentState.value = CharacterState.JUMPING
                    resetAnimation()
                }
            }
            CharacterEvent.StopJump -> {
                // Al soltar el salto, podría variar la duración
            }
            CharacterEvent.Attack -> {
                if (_currentState.value != CharacterState.ATTACKING && _currentState.value != CharacterState.DEAD) {
                    _currentState.value = CharacterState.ATTACKING
                    resetAnimation()
                }
            }
            CharacterEvent.Die -> {
                _currentState.value = CharacterState.DEAD
                resetAnimation()
            }
        }
    }

    private fun resetAnimation() {
        animationJob?.cancel()
        _isAnimationComplete.value = true
    }

    fun animationTotalFrames(): Int {
        return stateAnimations[_currentState.value]?.second ?: 2
    }

    fun animationRow(): Int {
        return stateAnimations[_currentState.value]?.first ?: 0
    }

    // Lógica del joystick: llamado desde la UI cuando cambie el offset del joystick
    // x e y son las posiciones del joystick (por ejemplo offsetX y offsetY)
    // maxRadius es el radio máximo del joystick.
    // Esta función actualiza el estado interno del personaje.
    fun updateFromJoystick(x: Float, y: Float) {
        val dist = hypot(x, y)

        // Si la distancia es mayor a cero, obtenemos un vector normalizado.
        // De lo contrario, dx y dy quedan en 0 para evitar división por cero.
        val (dx, dy) = if (dist > 0f) {
            val nx = x / dist
            val ny = y / dist
            nx to ny
        } else {
            0f to 0f
        }

        // Calcular velocidad relativa
        // Por ejemplo, que la velocidad sea directamente proporcional a dist, con un tope:
        val speed = if (dist > maxSpeed) maxSpeed else dist

        if (dist > 0.1f) {
            // Hay movimiento
            isMoving = true

            // Determinar orientación (solo si se está moviendo)
            // Si x > 0, mirando a la derecha; si x < 0, mirando a la izquierda
            // Determinar orientación según x
            facingRight = x > 0

            // Actualizar posición del personaje
            // Asumimos movimiento horizontal principal, puedes agregar vertical si gustas
            // (x / dist, y / dist) es un vector unitario en la dirección del joystick
            characterX += (x / dist) * speed
            characterY += (y / dist) * speed

            // Actualizar estado según dirección
            _currentState.value = if (facingRight) CharacterState.MOVING_RIGHT else CharacterState.MOVING_LEFT
        } else {
            // Joystick en (0,0) => sin movimiento
            // El personaje se detiene instantáneamente
            isMoving = false
            // El personaje se queda quieto, pero conserva la orientación (facingRight no cambia)
            _currentState.value = CharacterState.IDLE
        }
    }
}
