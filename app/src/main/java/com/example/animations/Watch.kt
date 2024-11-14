package com.example.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutElastic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animations.ui.theme.Orange
import com.example.animations.ui.theme.Yellow
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
fun DigitalClockPrev() {
    DigitalClock()
}

@Composable
fun DigitalClock() {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }

    var segundos by remember { mutableFloatStateOf(calendar.get(Calendar.SECOND).toFloat()) }
    val segundosAngulo = remember { Animatable(segundos * 6f) }

    var minutos by remember { mutableFloatStateOf(calendar.get(Calendar.MINUTE).toFloat()) }
    val minutosAngulo = remember { Animatable(minutos * 6f) }

    var horas by remember { mutableFloatStateOf(calendar.get(Calendar.HOUR).toFloat()) }
    val horasAngulo = remember { Animatable(horas * 30f) }

    val scope = rememberCoroutineScope()
    val textMeasurer = rememberTextMeasurer() // Medidor de texto nativo en Compose

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTime()
            calendar = Calendar.getInstance()

           /*
            scope.launch {
                for(i in 1..30){
                    segundosAngulo = ((segundos * 6f) + i*(6f/30)*easeInOutElastic(i/30.0)).toFloat()
                    delay(10)
                }
            }*/

            segundos = calendar.get(Calendar.SECOND).toFloat()
            scope.launch{
                segundosAngulo.animateTo(
                    targetValue = if (segundos == 0f) 360f else segundos * 6f,
                    animationSpec = tween(300, easing = EaseInOutElastic)
                )
                if(segundos == 0f){
                    segundosAngulo.snapTo(0f)
                }
            }

            minutos = calendar.get(Calendar.MINUTE).toFloat()
            scope.launch{
                minutosAngulo.animateTo(
                    targetValue = if (minutos == 0f) 360f else minutos * 6f,
                    animationSpec = tween(300, easing = EaseInOutElastic)
                )
                if(minutos == 0f){
                    minutosAngulo.snapTo(0f)
                }
            }

            horas = calendar.get(Calendar.HOUR).toFloat()
            scope.launch{
                horasAngulo.animateTo(
                    targetValue = if (horas == 1f) 390f else horas * 30f,
                    animationSpec = tween(300, easing = EaseInOutElastic)
                )
                if(horas == 1f){
                    horasAngulo.snapTo(30f)
                }
            }


            delay(1000L) // Actualiza cada segundo
        }

    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp)
        ) {

            val centerX = size.minDimension / 2
            val centerY = size.minDimension / 2

            drawCircle(
                color = Color.Black,
                radius = size.minDimension / 2,
                center = center,
                style = Stroke(15f)
            )


            // hOras
            rotate(horasAngulo.value)
            {
                drawLine(
                    color = Color.Black,
                    start = center,
                    end = Offset(center.x, center.y - size.minDimension * 0.2f),
                    strokeWidth = 14f
                )
            }

            rotate(minutosAngulo.value)
            {
                // minutos
                drawLine(
                    color = Color.Black,
                    start = center,
                    end = Offset(center.x, center.y - size.minDimension * 0.4f),
                    strokeWidth = 8f
                )
            }

            // Segundos
            rotate(degrees = segundosAngulo.value){
                drawLine(
                    color = Color.Red,
                    start = center,
                    end = Offset(center.x, center.y - size.minDimension * 0.4f),
                    strokeWidth = 4f
                )
            }

            for (i in 0..59) {
                rotate(i*6f){
                    drawLine(
                        color = Color.Black,
                        start = Offset(center.x, center.y - size.minDimension * 0.47f),
                        end = Offset(center.x, 0f),
                        strokeWidth = 4f
                    )

                    if(i.rem(5) == 0){
                        drawLine(
                            color = Color.Black,
                            start = Offset(center.x, center.y - size.minDimension * 0.45f),
                            end = Offset(center.x, 0f),
                            strokeWidth = 8f
                        )
                    }
                }

            }

            for(i in 1..12){
                val ang = 2*Math.PI/12 * i - 2*Math.PI/4
                val x : Float = center.x + 0.8f*size.minDimension/2 * cos(ang).toFloat()
                val y : Float = center.y + 0.8f*size.minDimension/2 * sin(ang).toFloat()

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
            fontWeight = FontWeight.Bold
        )
    }

}

fun getCurrentTime(): String {
    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return dateFormat.format(Date())
}