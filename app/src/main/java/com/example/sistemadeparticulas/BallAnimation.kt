package com.example.sistemadeparticulas

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.sin
import com.example.sistemadeparticulas.ui.theme.Orange
import com.example.sistemadeparticulas.ui.theme.Yellow
import com.example.sistemadeparticulas.ui.theme.YellowGlow
import com.example.sistemadeparticulas.ui.theme.DarkBlue
import com.example.sistemadeparticulas.ui.theme.Purple40
import com.example.sistemadeparticulas.ui.theme.PurpleGrey80
import com.example.sistemadeparticulas.ui.theme.SistemaDeParticulasTheme

@Composable
@Preview
fun BallAnimationPreview() {
    SistemaDeParticulasTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            fondoEstrellas()
            SinusoidalBallAnimation()
        }
    }
}


@Composable
fun SinusoidalBallAnimation(  amplitude: Float = 500f
                            , frequency: Float = 0.01f
                            , ballRadius: Float = 100f
                            , initalXOffset: Float = -200f
                            , rotation: Float = 45f
                            , steps: Int = 20
                            , duration: Int = 50000
                            , estrellas: List<Offset> = generateRandomEstrellas(100)
                            , estrellasMedianas: List<Offset> = generateRandomEstrellas(50)
) {


    // Animatable para la posición en el eje X
    val ballX = remember { Animatable(initalXOffset) }

    // Variable de estado para almacenar el ancho del Canvas
    val canvasWidth by remember { mutableStateOf(1000f) } // Estimamos un ancho para la animación continua

    val random by remember { mutableStateOf(java.util.Random(2)) }

    // Solo lanzará la animación cuando canvasWidth esté disponible (mayor a 0)
    LaunchedEffect(canvasWidth) {
        if (canvasWidth > 0f) {
            ballX.animateTo(
                targetValue = canvasWidth * 10, // Mueve la bola a la derecha de forma continua
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = duration, easing = LinearEasing)
                )
            )
        }
    }


    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
            .drawBehind { fondoEstrellas(estrellas, estrellasMedianas)(this) }
    ) {
        // Asigna el tamaño de Canvas a canvasWidth una vez esté disponible
        val actualWidth = size.width + 500


        // Calculamos `actualStep` de forma continua usando un seno para suavidad
        val actualStep = ballX.value / 500

        for (x in 0 until actualWidth.toInt() step steps) {
            val y = center.y + amplitude * sin(frequency * x + actualStep)
            val uv = Offset(x.toFloat(), y)

            rotate(
                degrees = rotation,
                pivot = Offset(center.x,center.y)
            ) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Orange, Yellow),
                        center = Offset(x.toFloat() + 5, y + 5),
                        radius = ballRadius * 0.85f,
                        tileMode = TileMode.Clamp),
                    radius = ballRadius,
                    center = uv
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(YellowGlow, Color.Transparent),
                        center = Offset(x.toFloat(), y),
                        radius = ballRadius*2,
                        tileMode = TileMode.Decal),
                    radius = ballRadius*2,
                    center = uv,
                    blendMode = BlendMode.ColorDodge
                )
            }
        }
    }
}


fun fondoEstrellas(
    estrellas: List<Offset> = generateRandomEstrellas(100)
    , estrellasMedianas: List<Offset> = generateRandomEstrellas(50)
    , rotation: Float = 45f
) :  DrawScope.() -> Unit {
 return {
     rotate(
         degrees = rotation + 30,
         pivot = Offset(center.x, center.y)
     ) {

         drawOval(
             color = Purple40,
             topLeft = Offset(-1000f, center.y - 100),
             size = Size(5000f, 2000f),
             alpha = 0.3f,
             style = Stroke(500f),
             blendMode = BlendMode.Lighten
         )
     }

     rotate(
         degrees = rotation + 10,
         pivot = Offset(center.x, center.y)
     ) {

         drawOval(
             color = Purple40,
             topLeft = Offset(-1000f, center.y - 100),
             size = Size(5000f, 2000f),
             alpha = 0.3f,
             style = Stroke(1000f),
             blendMode = BlendMode.ColorDodge
         )

         drawOval(
             color = Orange,
             topLeft = Offset(-1000f, center.y - 100),
             size = Size(5000f, 2000f),
             alpha = 0.2f,
             style = Stroke(700f),
             blendMode = BlendMode.ColorDodge
         )


         drawOval(
             color = Yellow,
             topLeft = Offset(-700f, center.y - 100),
             size = Size(5000f, 2000f),
             alpha = 0.3f,
             style = Stroke(700f),
             blendMode = BlendMode.Softlight
         )

         // Dibuja muchas estrellas puqueñas
         estrellas.map() {
             drawCircle(
                 color = Color.White,
                 radius = 1f,
                 center = it
             )
         }
         estrellasMedianas.map() {
             drawCircle(
                 color = Color.White,
                 radius = 2f,
                 center = it
             )
         }
     }


     // Ovalo birllante y mas fino
     rotate(
         degrees = rotation + 10,
         pivot = Offset(center.x, center.y)
     ) {

         drawOval(
             color = Purple40,
             topLeft = Offset(-1000f, center.y - 100),
             size = Size(5000f, 2000f),
             alpha = 0.3f,
             style = Stroke(400f),
             blendMode = BlendMode.ColorDodge
         )

         drawOval(
             color = PurpleGrey80,
             topLeft = Offset(-1000f, center.y - 100),
             size = Size(5000f, 2000f),
             alpha = 0.4f,
             style = Stroke(
                 20f,
                 pathEffect = PathEffect.dashPathEffect(floatArrayOf(500f, 300f), 200f)
             ),
             blendMode = BlendMode.ColorDodge
         )
     }
}
}


fun generateRandomEstrellas(cantida: Int): List<Offset> {
    val random = java.util.Random()
    val estrellas = mutableListOf<Offset>()
    for (i in 0 until cantida) {
        val x = random.nextInt(3000).toFloat()
        val y = random.nextInt(3000).toFloat()
        estrellas.add(Offset(x, y))
    }
    return estrellas
}
