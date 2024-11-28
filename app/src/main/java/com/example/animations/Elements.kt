package com.example.animations

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FloatSpringSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.Layout
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

@Stable
interface CircularCarouselState {
    val angle: Float
    val minorAxisFactor: Float

    suspend fun stop()
    suspend fun snapTo(angle: Float)
    suspend fun decayTo(angle: Float, velocity: Float)
    suspend fun smoothSnapTo(angle: Float)
    fun setMinorAxisFactor(factor: Float)
}

class CircularCarouselStateImpl : CircularCarouselState {
    private val _angle = Animatable(0f)
    private val _eccentricity = mutableStateOf(1f)

    override val angle: Float
        get() = _angle.value

    override val minorAxisFactor: Float
        get() = _eccentricity.value

    private val decayAnimationSpec = FloatSpringSpec(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessVeryLow,
    )

    override suspend fun stop() {
        _angle.stop()
    }

    override suspend fun snapTo(angle: Float) {
        _angle.snapTo(angle)
    }

    override suspend fun decayTo(angle: Float, velocity: Float) {
        _angle.animateTo(
            targetValue = angle,
            initialVelocity = velocity,
            animationSpec = decayAnimationSpec,
        )
    }

    override suspend fun smoothSnapTo(angle: Float) {
        _angle.animateTo(
            targetValue = angle,
            animationSpec = tween(durationMillis = 700) // Duración más prolongada para un movimiento más suave
        )
    }

    override fun setMinorAxisFactor(factor: Float) {
        _eccentricity.value = factor.coerceIn(-1f, 1f)
    }
}

@Composable
fun rememberCircularCarouselState(): CircularCarouselState = remember {
    CircularCarouselStateImpl()
}

@Composable
fun CircularCarousel(
    numItems: Int,
    modifier: Modifier = Modifier,
    itemFraction: Float = .25f,
    state: CircularCarouselState = rememberCircularCarouselState(),
    contentFactory: @Composable (Int) -> Unit,
) {
    require(numItems > 0) { "The number of items must be greater than 0" }
    require(itemFraction > 0f && itemFraction < .5f) { "Item fraction must be in the (0f, .5f) range" }

    Layout(
        modifier = modifier.pointerInput(Unit) {
            val decay = splineBasedDecay<Float>(this)
            coroutineScope {
                while (true) {
                    val pointerInput = awaitPointerEventScope { awaitFirstDown() }
                    state.stop()
                    val tracker = VelocityTracker()
                    val degreesPerPixel = 180f / size.width.toFloat()

                    awaitPointerEventScope {
                        var lastOffset = pointerInput.position
                        while (true) {
                            val event = awaitPointerEvent()
                            val dragAmount = event.changes.first().position - lastOffset

                            // Actualizar el ángulo basándose en el desplazamiento
                            val signum = if (pointerInput.position.y < size.height / 2f) -1f else 1f
                            val horizontalDragOffset = state.angle + signum * dragAmount.x * degreesPerPixel

                            // Lanzar la acción suspendida en un contexto adecuado
                            launch {
                                state.snapTo(horizontalDragOffset)
                            }

                            // Ajustar el factor del eje menor basado en el desplazamiento vertical
                            val scaleFactor = state.minorAxisFactor + dragAmount.y / (size.height / 2f)
                            state.setMinorAxisFactor(scaleFactor)

                            tracker.addPosition(event.changes.first().uptimeMillis, event.changes.first().position)
                            lastOffset = event.changes.first().position

                            // Consumir el cambio
                            event.changes.forEach { it.consume() }

                            // Salir del bucle si todos los punteros están levantados
                            if (event.changes.all { !it.pressed }) break
                        }

                        // Cuando termine el arrastre, hacer que el elemento más cercano se snapee al centro con suavidad
                        val velocity = tracker.calculateVelocity().x
                        val targetAngle = decay.calculateTargetValue(state.angle, velocity)

                        // Lanzar la acción suspendida para decaer en el contexto adecuado
                        launch {
                            state.decayTo(
                                angle = targetAngle,
                                velocity = velocity * degreesPerPixel
                            )

                            // Snap al elemento más cercano al centro con una animación suave
                            val angleStep = 360f / numItems
                            val closestIndex = ((state.angle / angleStep).roundToInt() % numItems + numItems) % numItems
                            val snapAngle = closestIndex * angleStep

                            // Ajustar el valor de `state.angle` para minimizar la diferencia con `snapAngle`
                            var adjustedAngle = state.angle
                            while (adjustedAngle - snapAngle > 180f) {
                                adjustedAngle -= 360f
                            }
                            while (snapAngle - adjustedAngle > 180f) {
                                adjustedAngle += 360f
                            }

                            // Establecer el ángulo ajustado para que sea lo más cercano al `snapAngle`
                            state.snapTo(adjustedAngle)

                            // Ahora realizar el smooth snap al ángulo más cercano con una animación suave
                            state.smoothSnapTo(snapAngle)
                        }


                    }
                }
            }
        },
        content = {
            val angleStep = 360f / numItems.toFloat()
            repeat(numItems) { index ->
                val itemAngle = (state.angle + angleStep * index.toFloat()).normalizeAngle()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(if (itemAngle <= 180f) 180f - itemAngle else itemAngle - 180f)
                        .graphicsLayer {
                            cameraDistance = 16f * density  // Aumenta la distancia de cámara para un efecto más dramático
                            rotationY = itemAngle

                            // Ajuste dinámico de transparencia basado en la posición
                            alpha = if (itemAngle < 90f || itemAngle > 270f) {
                                1f - (abs(itemAngle - 180f) / 180f) * 0.02f  // Transparencia más baja en el centro y mayor en los bordes
                            } else {
                                0.4f  // Más transparente para los elementos de los extremos
                            }

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
            // Ampliar el valor del ancho disponible para permitir que la elipse sea mucho más ancha en el eje X
            val extendedWidth = constraints.maxWidth * 1.6 // Aumentar significativamente el ancho efectivo de la elipse
            val availableVerticalSpace = constraints.maxHeight - itemDimension
            val horizontalOffset = (constraints.maxWidth - itemDimension) / 2.0
            val verticalOffset = availableVerticalSpace / 2.0
            val angleStep = 2.0 * PI / numItems.toDouble()

            placeables.forEachIndexed { index, placeable ->
                val itemAngle = (state.angle.toDouble().degreesToRadians() + (angleStep * index.toDouble())) % (2 * PI)
                val offset = getCoordinates(
                    width = extendedWidth / 2.0, // Aumentar el valor del ancho para que sea mucho más amplio
                    height = availableVerticalSpace / 4.0 * state.minorAxisFactor, // Reducir el valor de la altura para darle un aspecto más horizontal
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

// Funciones auxiliares que faltaban:
private fun getCoordinates(width: Double, height: Double, angle: Double): Offset {
    val x = width * sin(angle)
    val y = height * cos(angle)
    return Offset(
        x = x.toFloat(),
        y = y.toFloat(),
    )
}

private fun Double.degreesToRadians(): Double = this / 360.0 * 2.0 * PI

private fun Float.normalizeAngle(): Float =
    (this % 360f).let { angle -> if (angle < 0f) 360f + angle else angle }

@Composable
fun Carousel() {
    val elements = listOf(
        R.raw.aire, R.raw.roca, R.raw.agua, R.raw.fire, R.raw.planta
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize(),
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
                        R.raw.aire -> {
                            modifier = modifier.scale(2.4f)
                        }
                        R.raw.fire -> {
                            modifier = modifier.scale(0.9f)
                        }
                        R.raw.planta -> {
                            modifier = modifier.scale(1.1f)
                        }
                        else -> {}
                    }
                    LottieInfinite(elements[index], modifier)
                }
            }
        }
    }
}
