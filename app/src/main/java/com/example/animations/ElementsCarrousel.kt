package com.example.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FloatSpringSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

// Esta interfaz define las propiedades y métodos del estado del carrusel circular
@Stable
interface CircularCarouselState {
    val angle: Float // Ángulo actual del carrusel
    val minorAxisFactor: Float // Factor del eje menor para definir la elipse

    // Métodos para manejar el movimiento del carrusel
    suspend fun stop() // Detener la animación actual
    suspend fun snapTo(angle: Float) // Llevar el carrusel instantáneamente al ángulo dado
    suspend fun decayTo(angle: Float, velocity: Float) // Animación con desaceleración hasta un ángulo
    suspend fun decay2To(angle: Float, velocity: Float, degreesPerPixel: Float) // Decaimiento prolongado para un frenado natural
    suspend fun smoothSnapTo(angle: Float) // Animación suave hasta un ángulo determinado
    fun setMinorAxisFactor(factor: Float) // Ajustar el factor del eje menor para la elipse
}

// Implementación concreta del estado del carrusel circular
class CircularCarouselStateImpl : CircularCarouselState {
    // Variable animable que mantiene el ángulo del carrusel
    private val _angle = Animatable(0f)
    // Estado mutable para la excentricidad del carrusel (eje menor)
    private val _eccentricity = mutableStateOf(1f)

    // Getter para obtener el valor actual del ángulo
    override val angle: Float
        get() = _angle.value

    // Getter para obtener el factor del eje menor
    override val minorAxisFactor: Float
        get() = _eccentricity.value

    // Especificación de animación para el decaimiento rápido del carrusel
    private val decayAnimationSpec = FloatSpringSpec(
        dampingRatio = Spring.DampingRatioNoBouncy, // Sin rebote
        stiffness = Spring.StiffnessMedium, // Rigidez media para controlar la velocidad
    )

    // Especificación de animación prolongada para un frenado natural del carrusel
    val prolongedDecayAnimationSpec = FloatSpringSpec(
        dampingRatio = Spring.DampingRatioMediumBouncy,  // Usar una amortiguación más suave para un efecto de rebote
        stiffness = Spring.StiffnessLow  // Reducir la rigidez para que el frenado sea más lento y gradual
    )

    // Detener cualquier animación activa del carrusel
    override suspend fun stop() {
        _angle.stop()
    }

    // Llevar el carrusel al ángulo dado instantáneamente
    override suspend fun snapTo(angle: Float) {
        _angle.snapTo(angle)
    }

    // Realizar una animación con desaceleración hasta el ángulo objetivo
    override suspend fun decayTo(angle: Float, velocity: Float) {
        _angle.animateTo(
            targetValue = angle,
            initialVelocity = velocity,
            animationSpec = decayAnimationSpec,
        )
    }

    // Realizar una animación prolongada con desaceleración suave
    override suspend fun decay2To(angle: Float, velocity: Float, degreesPerPixel: Float) {
        _angle.animateTo(
            targetValue = angle,
            initialVelocity = velocity * degreesPerPixel,
            animationSpec = prolongedDecayAnimationSpec
        )
    }

    // Realizar una animación suave hacia el ángulo objetivo
    override suspend fun smoothSnapTo(angle: Float) {
        _angle.animateTo(
            targetValue = angle,
            animationSpec = tween(durationMillis = 700) // Duración prolongada para un movimiento más suave
        )
    }

    // Ajustar el factor del eje menor para darle forma de elipse
    override fun setMinorAxisFactor(factor: Float) {
        _eccentricity.value = factor.coerceIn(-1f, 1f)
    }
}

// Función para recordar y crear el estado del carrusel
@Composable
fun rememberCircularCarouselState(): CircularCarouselState = remember {
    CircularCarouselStateImpl()
}

// Carrusel Circular Composable que muestra elementos en una disposición circular
@Composable
fun CircularCarousel(
    numItems: Int,
    modifier: Modifier = Modifier,
    itemFraction: Float = .25f,
    state: CircularCarouselState = rememberCircularCarouselState(),
    contentFactory: @Composable (Int) -> Unit,
) {
    // Verificación de condiciones iniciales para asegurar valores válidos
    require(numItems > 0) { "The number of items must be greater than 0" }
    require(itemFraction > 0f && itemFraction < .5f) { "Item fraction must be in the (0f, .5f) range" }

    Layout(
        modifier = modifier.pointerInput(Unit) {
            // Definición del decaimiento usando spline para la animación inercial
            val decay = splineBasedDecay<Float>(this)

            // Coroutines para gestionar el movimiento sin bloquear la interfaz de usuario
            coroutineScope {
                while (true) {
                    // Esperar a que el usuario toque la pantalla
                    val pointerInput = awaitPointerEventScope { awaitFirstDown() }
                    state.stop() // Detener cualquier animación activa al empezar un nuevo gesto
                    val tracker = VelocityTracker() // Para rastrear la velocidad del movimiento del usuario
                    val degreesPerPixel = 180f / size.width.toFloat() // Factor para convertir el desplazamiento a grados

                    awaitPointerEventScope {
                        var lastOffset = pointerInput.position
                        while (true) {
                            val event = awaitPointerEvent()
                            val dragAmount = event.changes.first().position - lastOffset

                            // Determinar la dirección del desplazamiento según la posición vertical del toque
                            val signum = if (pointerInput.position.y < size.height / 2f) -1f else 1f
                            val horizontalDragOffset = state.angle + signum * dragAmount.x * degreesPerPixel

                            // Ajustar el ángulo del carrusel según el desplazamiento
                            launch {
                                state.snapTo(horizontalDragOffset)
                            }

                            // Ajustar el factor del eje menor (escala vertical) según el desplazamiento en Y
                            val scaleFactor = state.minorAxisFactor + dragAmount.y / (size.height / 2f)
                            state.setMinorAxisFactor(scaleFactor)

                            // Registrar la posición para calcular la velocidad
                            tracker.addPosition(event.changes.first().uptimeMillis, event.changes.first().position)
                            lastOffset = event.changes.first().position

                            // Consumir el cambio para evitar efectos de rebote en la UI
                            event.changes.forEach { it.consume() }

                            // Salir del bucle si todos los punteros están levantados
                            if (event.changes.all { !it.pressed }) break
                        }

                        // Calcular la velocidad y el ángulo objetivo tras terminar el arrastre
                        val velocity = tracker.calculateVelocity().x
                        val targetAngle = decay.calculateTargetValue(state.angle, velocity)

                        // Lanzar la acción suspendida para el decaimiento del carrusel
                        launch {
                            // Utilizar la animación prolongada para un frenado natural
                            state.decay2To(targetAngle, velocity, degreesPerPixel)

                            // Calcular el ángulo más cercano para realizar el "snap"
                            val angleStep = 360f / numItems
                            val closestIndex = ((state.angle / angleStep).roundToInt() % numItems + numItems) % numItems
                            val snapAngle = closestIndex * angleStep

                            // Ajustar `state.angle` para minimizar la diferencia con `snapAngle`
                            var adjustedAngle = state.angle
                            while (adjustedAngle - snapAngle > 180f) {
                                adjustedAngle -= 360f
                            }
                            while (snapAngle - adjustedAngle > 180f) {
                                adjustedAngle += 360f
                            }

                            // Hacer el "snap" al ángulo ajustado antes de la animación suave
                            state.snapTo(adjustedAngle)

                            // Realizar el "smooth snap" al ángulo más cercano
                            state.smoothSnapTo(snapAngle)
                        }
                    }
                }
            }
        },
        content = {
            // Calcular los pasos de ángulo para distribuir los elementos de forma equidistante
            val angleStep = 360f / numItems.toFloat()
            repeat(numItems) { index ->
                val itemAngle = (state.angle + angleStep * index.toFloat()).normalizeAngle()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(if (itemAngle <= 180f) 180f - itemAngle else itemAngle - 180f)
                        .graphicsLayer {
                            // Configurar la distancia de la cámara para dar un efecto 3D
                            cameraDistance = 16f * density
                            rotationY = itemAngle

                            // Ajustar la transparencia de los elementos dependiendo de su ángulo
                            alpha = if (itemAngle < 90f || itemAngle > 270f) {
                                1f - (abs(itemAngle - 180f) / 180f) * 0.02f
                            } else {
                                if (abs(itemAngle - 180f) < 45)
                                    abs(itemAngle - 180f) / 200f
                                else
                                    0.4f
                            }

                            // Ajustar la escala para dar un efecto de perspectiva a los elementos
                            val scale = 1f - .2f * when {
                                itemAngle <= 180f -> itemAngle / 180f
                                else -> (360f - itemAngle) / 180f
                            }
                            scaleX = scale
                            scaleY = scale
                        }
                ) {
                    contentFactory(index)
                }
            }
        }
    ) { measurables, constraints ->
        // Definir las restricciones para los elementos del carrusel
        val itemDimension = constraints.maxHeight * itemFraction
        val itemConstraints = Constraints.fixed(
            width = itemDimension.toInt(),
            height = itemDimension.toInt(),
        )
        val placeables = measurables.map { measurable -> measurable.measure(itemConstraints) }
        layout(
            width = constraints.maxWidth,
            height = constraints.maxHeight,
        ) {
            // Definir la disposición en el espacio, aumentando el ancho disponible para dar una apariencia de elipse
            val extendedWidth = constraints.maxWidth * 1.6
            val availableVerticalSpace = constraints.maxHeight - itemDimension
            val horizontalOffset = (constraints.maxWidth - itemDimension) / 2.0
            val verticalOffset = availableVerticalSpace / 2.0
            val angleStep = 2.0 * PI / numItems.toDouble()

            placeables.forEachIndexed { index, placeable ->
                val itemAngle = (state.angle.toDouble().degreesToRadians() + (angleStep * index.toDouble())) % (2 * PI)
                val offset = getCoordinates(
                    width = extendedWidth / 2.0,
                    height = availableVerticalSpace / 4.0 * state.minorAxisFactor,
                    angle = itemAngle,
                )
                placeable.placeRelative(
                    x = (horizontalOffset + offset.x).roundToInt(),
                    y = (verticalOffset + offset.y).roundToInt(),
                )
            }
        }
    }
}

// Obtener las coordenadas x, y basadas en un ángulo y dimensiones de la elipse
private fun getCoordinates(width: Double, height: Double, angle: Double): Offset {
    val x = width * sin(angle)
    val y = height * cos(angle)
    return Offset(
        x = x.toFloat(),
        y = y.toFloat(),
    )
}

// Convertir grados a radianes para las operaciones trigonométricas
private fun Double.degreesToRadians(): Double = this / 360.0 * 2.0 * PI

// Normalizar el ángulo para que esté en el rango de [0, 360)
private fun Float.normalizeAngle(): Float =
    (this % 360f).let { angle -> if (angle < 0f) 360f + angle else angle }

// Composable para inicializar el carrusel con elementos específicos
@Composable
fun Carousel() {
    val elements = listOf(
        R.raw.luz, R.raw.roca, R.raw.agua, R.raw.fire, R.raw.planta
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize()
                .offset(y = 100.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircularCarousel(
                numItems = elements.size,
                itemFraction = 0.49f,
                modifier = Modifier
                    .width(2000.dp)
                    .height(600.dp)
            ) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    var modifier = Modifier.alpha(1.0f)

                    when (elements[index]) {
                        R.raw.luz -> {
                            LottieWithColorFilterAndPulsatingEffect()
                        }
                        R.raw.fire -> {
                            modifier = modifier.scale(0.9f)
                            LottieInfinite(elements[index], modifier)
                        }
                        R.raw.planta -> {
                            modifier = modifier.scale(1.1f)
                            LottieInfinite(elements[index], modifier)
                        }
                        else -> {
                            LottieInfinite(elements[index], modifier)
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CarouselPreview() {
    Carousel()
}

@Composable
fun LottieWithColorFilterAndPulsatingEffect() {
    // Crear una animación para la escala que represente la palpitación de la luz
    val scale by rememberInfiniteScalePulseAnimation()

    // Crear una animación para la opacidad para dar un efecto de pulso
    val alphaP by rememberInfiniteAlphaPulseAnimation()

    // Envolver la Lottie con los efectos deseados
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Primer Círculo: Gradiente Amarillo Suave
        Canvas(modifier = Modifier.size(300.dp)) {
            scale(scale) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFF176), // Amarillo suave sólido en el centro
                            Color.Transparent // Transparente hacia el exterior
                        ),
                        center = Offset(size.width / 2, size.height / 2),
                        radius = size.minDimension / 2
                    ),
                    alpha = alphaP
                )
            }
        }

        // Segundo Círculo: Gradiente Anaranjado con Menor Radio
        Canvas(modifier = Modifier.size(200.dp)) {
            scale(scale) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFA726), // Anaranjado en el centro
                            Color.Transparent // Transparente hacia el exterior
                        ),
                        center = Offset(size.width / 2, size.height / 2),
                        radius = size.minDimension / 2
                    ),
                    alpha = alphaP
                )
            }
        }
    }
}

// Animación de escala para el efecto palpitante
@Composable
fun rememberInfiniteScalePulseAnimation(): State<Float> {
    val scale = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000),
                repeatMode = RepeatMode.Reverse
            )
        )
    }
    return scale.asState()
}

// Animación de alpha para el efecto de brillo
@Composable
fun rememberInfiniteAlphaPulseAnimation(): State<Float> {
    val alpha = remember { Animatable(0.8f) }
    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000),
                repeatMode = RepeatMode.Reverse
            )
        )
    }
    return alpha.asState()
}