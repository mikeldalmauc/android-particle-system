package com.example.animations.character

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitDragOrCancellation
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.animations.ui.theme.LightYellow
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.math.roundToInt


@Composable
@Preview
fun ControlUiPreview() {
    val char = CharacterViewModel(CharacterState.IDLE)
    CharacterView(char, 1.0f)
    CharacterControlUI(char)
}


@Composable
fun CharacterControlUI(
    viewModel: CharacterViewModel,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.onEvent(CharacterEvent.Attack) },
                modifier = Modifier
                    .size(64.dp)
                    .padding(16.dp)
            ) {
                Text("Attack")
            }
            JoyStick( { offsetX: Float, offsetY: Float -> viewModel.updateFromJoystick(offsetX, offsetY)})

        }
    }

}


@Composable
fun JoyStick(onJoyStickChange: (offsetX: Float, offsetY: Float) -> Unit = { _, _ -> }) {

    val joystickSize = 110.dp
    val radius = with(LocalDensity.current) { (joystickSize.toPx() / 2) }

    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    // Estados para señalizar acciones suspendidas a ejecutar fuera del scope restringido
    var requestStop by remember { mutableStateOf(false) }
    var requestUpdateOffset: Pair<Float, Float>? by remember { mutableStateOf(null) }
    var requestCenter by remember { mutableStateOf(false) }

    // Efecto para ejecutar acciones suspendidas fuera del `awaitPointerEventScope`
    LaunchedEffect(requestStop, requestUpdateOffset, requestCenter) {
        // Si hay petición de stop
        if (requestStop) {
            offsetX.stop()
            offsetY.stop()
            requestStop = false
        }

        // Si hay una petición de actualización de offset
        requestUpdateOffset?.let { (newX, newY) ->
            val dist = hypot(newX, newY)
            if (dist <= radius) {
                offsetX.snapTo(newX)
                offsetY.snapTo(newY)
            } else {
                val scale = radius / dist
                offsetX.snapTo(newX * scale)
                offsetY.snapTo(newY * scale)
            }
            requestUpdateOffset = null
        }

        // Si hay una petición de volver al centro
        if (requestCenter) {
            // Lanzar ambas animaciones en paralelo
            val jobX = launch { offsetX.animateTo(0f, animationSpec = spring()) }
            val jobY = launch { offsetY.animateTo(0f, animationSpec = spring()) }

            // Esperar a que ambas terminen
            jobX.join()
            jobY.join()

            requestCenter = false
        }
    }


    Box(
        modifier = Modifier
            .size(joystickSize)
            .padding(16.dp)
            .pointerInput(Unit) {
                forEachGesture {
                    awaitPointerEventScope {
                        // Señalamos que hay que parar las animaciones corrientes
                        requestStop = true

                        val down = awaitFirstDown()
                        val pointerId = down.id
                        val initialPos = down.position
                        val drag = awaitDragOrCancellation(pointerId)

                        if (drag == null) {
                            // Fue un tap sin drag
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f
                            val relativeX = initialPos.x - centerX
                            val relativeY = initialPos.y - centerY

                            // Señalamos que hay que actualizar el offset tras salir del scope
                            requestUpdateOffset = relativeX to relativeY
                            // Y luego volver al centro
                            requestCenter = true
                        } else {
                            // Hubo un drag
                            var prevPos = drag.position
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.find { it.id == pointerId }
                                if (change == null || !change.pressed) {
                                    // Se levantó el dedo
                                    break
                                }

                                val dragAmount = change.position - prevPos
                                prevPos = change.position

                                // Señalar que hay que actualizar el offset
                                val newX = offsetX.value + dragAmount.x
                                val newY = offsetY.value + dragAmount.y
                                requestUpdateOffset = newX to newY

                                // Llamar al callback
                                onJoyStickChange.invoke(offsetX.value, offsetY.value)

                                change.consume()
                            }
                            // Al soltar, volver al centro
                            requestCenter = true
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.LightGray.copy(alpha = 0.5f),
                radius = size.minDimension / 1.96f
            )
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
                .size(33.dp)
                .background(LightYellow, shape = CircleShape)
                .border(2.dp, Color.White, CircleShape)
        )
    }
}