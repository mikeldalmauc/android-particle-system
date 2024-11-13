package com.example.animations

import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutElastic
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.TextLayoutInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.resetToBeginning
import com.example.animations.ui.theme.AnimationsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Preview
@Composable
fun WatchPreview() {
    AnimationsTheme {
        Watch()
    }
}

@Composable
fun Watch() {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }

    // Segundos
    var segundos by remember { mutableFloatStateOf(calendar.get(Calendar.SECOND).toFloat()) }
    val segundosAngulo = remember { Animatable(segundos * 6f) }

    // Minutos
    var minutos by remember { mutableFloatStateOf(calendar.get(Calendar.MINUTE).toFloat()) }
    val minutosAngulo = remember { Animatable(minutos * 6f) }

    // Horas
    var horas by remember { mutableFloatStateOf(calendar.get(Calendar.HOUR).toFloat()) }
    var horasAngulo = remember { Animatable(horas * 30f) }

    val scope = rememberCoroutineScope()
    val textMeasurer = rememberTextMeasurer() // Medidor de texto nativo en Compose

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTime()
            calendar = Calendar.getInstance()

            // Animacion del seugndero
            segundos = calendar.get(Calendar.SECOND).toFloat()
            scope.launch {
                segundosAngulo.animateTo(
                    targetValue = if (segundos == 0f) 360f else segundos * 6f,
                    animationSpec = tween(250, easing = EaseInOutElastic)
                )
                if (segundos == 0f) {
                    // Reinicia a 0 instantáneamente sin animación
                    segundosAngulo.snapTo(0f)
                }
            }

            // Animación del minutero
            minutos = calendar.get(Calendar.MINUTE).toFloat()
            scope.launch {
                minutosAngulo.animateTo(
                    targetValue = if (minutos == 0f) 360f else minutos * 6f,
                    animationSpec = tween(250, easing = FastOutLinearInEasing)
                )
                if (minutos == 0f) {
                    // Reinicia a 0 instantáneamente sin animación
                    minutosAngulo.snapTo(0f)
                }
            }

            // Animacion del horario
            horas = calendar.get(Calendar.HOUR).toFloat() % 12
            scope.launch {
                horasAngulo.animateTo(
                    targetValue = if (horas == 1f) 390f else horas * 30f,
                    animationSpec = tween(250, easing = FastOutSlowInEasing)
                )
                if (horas == 1f) {
                    // Reinicia a 0 instantáneamente sin animación
                    horasAngulo.snapTo(30f)
                }
            }

            delay(1000L) // Actualiza cada medio segundo
        }
    }

    Box(
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Canvas(modifier = Modifier.size(350.dp)) {

                drawCircle(
                    color = Color.Black,
                    radius = size.minDimension / 2,
                    center = center,
                    style = Stroke(width = 9f)
                )

                // Horas
                rotate(degrees = horasAngulo.value) {
                    drawLine(
                        color = Color.Black,
                        start = center,
                        end = Offset(
                            x = center.x,
                            y = center.y - size.minDimension * 0.25f,
                        ),
                        cap = StrokeCap.Butt,
                        strokeWidth = 25f
                    )
                }

                // Minutero
                rotate(degrees = minutosAngulo.value) {
                    drawLine(
                        color = Color.Black,
                        start = center,
                        end = Offset(
                            x = center.x,
                            y = center.y - size.minDimension * 0.4f,
                        ),
                        cap = StrokeCap.Butt,
                        strokeWidth = 14f
                    )
                }

                // Segundero
                rotate(degrees = segundosAngulo.value) {
                    drawLine(
                        color = Color.Red,
                        start = center,
                        end = Offset(
                            x = center.x,
                            y = center.y - size.minDimension * 0.4f,
                        ),
                        cap = StrokeCap.Butt,
                        strokeWidth = 4f
                    )
                }

                for (i in 0..59) {

                    rotate(degrees = i * 6f) {
                        drawLine(
                            color = Color.Black,
                            start = Offset(
                                x = center.x,
                                y = center.y - size.minDimension / 2 * 0.95f,
                            ),
                            end = Offset(
                                x = center.x,
                                y = center.y - size.minDimension / 2,
                            ),
                            cap = StrokeCap.Butt,
                            strokeWidth = 4f
                        )
                    }

                    if (i.rem(5) == 0) {
                        rotate(degrees = i * 6f) {
                            drawLine(
                                color = Color.Black,
                                start = Offset(
                                    x = center.x,
                                    y = center.y - size.minDimension / 2 * 0.90f,
                                ),
                                end = Offset(
                                    x = center.x,
                                    y = center.y - size.minDimension / 2,
                                ),
                                cap = StrokeCap.Butt,
                                strokeWidth = 8f
                            )
                        }
                    }
                }


                // Dibuja los números de las horas usando `drawIntoCanvas` y `textMeasurer`
                for (i in 1..12) {
                    val angle = Math.toRadians((i * 30 - 90).toDouble())
                    val x = (center.x + (size.minDimension / 2 * 0.8) * cos(angle)).toFloat()
                    val y = (center.y + (size.minDimension / 2 * 0.8) * sin(angle)).toFloat()

                    val textLayout = textMeasurer.measure(
                        text = i.toString(),
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    drawText(
                        textLayoutResult = textLayout,
                        topLeft = Offset(
                            x - textLayout.size.width / 2,
                            y - textLayout.size.height / 2
                        )
                    )

                }
            }

            Text(
                text = currentTime,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
        FelicidadesConfeti(segundos)
    }
}

fun getCurrentTime(): String {
    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return dateFormat.format(Date())
}

@Composable
fun FelicidadesConfeti(segundos: Float) {
    // Estado para mostrar el texto "Felicidades" y controlar su tamaño
    var showFelicidades by remember { mutableStateOf(false) }
    val textSize = remember { Animatable(0f) } // Control del tamaño del texto

    // Composición de Lottie y progreso automático al activarse
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.confeti))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = showFelicidades,
        iterations = 1 // Ejecuta una vez completa en cada activación
    )

    // Secuencia de animación sincronizada al llegar el segundero a 0 o 30
    LaunchedEffect(segundos) {
        if (segundos == 0f || segundos == 30f) {
            showFelicidades = true // Activa la animación de confeti y el texto

            // Anima el tamaño del texto "Felicidades" para que aparezca
            textSize.snapTo(0f)
            textSize.animateTo(
                targetValue = 64f,
                animationSpec = tween(durationMillis = 1000)
            )

            // Mantiene el texto visible durante 2 segundos
            delay(2000L)

            // Anima el tamaño del texto para que desaparezca
            textSize.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1000)
            )

            // Oculta la animación y el texto al finalizar la secuencia
            showFelicidades = false
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        // Animación de Lottie con reproducción automática
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize()
        )

        // Texto animado "Felicidades"
        Text(
            text = "Felicidades",
            fontSize = textSize.value.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Green,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
