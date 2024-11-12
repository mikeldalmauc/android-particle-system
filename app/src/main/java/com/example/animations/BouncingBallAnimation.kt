import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class BallState { START, TOP, FALL, BOUNCE, RESET }
@Composable
fun BouncingBallAnimation() {
    val transition = rememberInfiniteTransition()

    // Animación vertical (posición Y)
    val ballY by transition.animateFloat(
        initialValue = 800f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000

                // Movimiento hacia arriba
                800f at 0 with FastOutSlowInEasing // Comienza en el suelo
                200f at 900 with FastOutSlowInEasing // Llega arriba y se detiene

                // Movimiento hacia abajo
                800f at 1600 with FastOutLinearInEasing // Baja rápidamente al suelo

                // Rebote en el suelo
                750f at 1800 with LinearEasing // Rebota un poco
                800f at 2000 // Vuelve al suelo
            }
        ), label = ""
    )

    // Animación de anchura (ballWidth)
    val ballWidth by transition.animateFloat(
        initialValue = 80f,
        targetValue = 120f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000

                // Movimiento hacia arriba
                80f at 0 // Comienza en forma original
                50f at 450 using EaseInBounce // Se estrecha a medida que sube
                80f at 900 // Recupera la forma original al detenerse arriba

                // Movimiento hacia abajo
                60f at 1300 // Se estrecha de nuevo al ganar velocidad en la caída
                120f at 1600 // Se estira al tocar el suelo

                // Rebote
                80f at 2000 // Recupera la forma original al finalizar el rebote
            }
        ), label = ""
    )

    // Animación de altura (ballHeight)
    val ballHeight by transition.animateFloat(
        initialValue = 80f,
        targetValue = 120f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000

                // Movimiento hacia arriba
                80f at 0 // Comienza en forma original
                120f at 450 // Se alarga al ganar velocidad
                80f at 900 // Recupera forma original al detenerse arriba

                // Movimiento hacia abajo
                100f at 1300 // Se alarga de nuevo al caer
                50f at 1600 // Se aplana al tocar el suelo

                // Rebote
                80f at 2000 // Recupera la forma original
            }
        ), label = ""
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val x = center.x
        val y = ballY
        val width = ballWidth
        val height = ballHeight

        drawRoundRect(
            color = Color.Red,
            topLeft = Offset(x - width / 2, y - height / 2),
            size = Size(width, height),
            cornerRadius = CornerRadius(width / 2, height / 2)
        )
    }
}
