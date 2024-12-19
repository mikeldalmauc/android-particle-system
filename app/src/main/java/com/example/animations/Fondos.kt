package com.example.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animations.ui.theme.Olivia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt


val fondos = listOf<@Composable () -> Unit>(
    { FondoGradienteRadialDesplazable(AzulCielo) },
    { FondoLiso(color = AzulCielo) },
    { FondoGradienteRadial(color = AzulCielo) },
    { Fondo2Gradientes(colorCirculo = NaranjaOscuro, colorGradienteLineal = AzulCielo, angulo = 30f, tamanioFactor = 0.1f) },
)

@Composable
@Preview
fun MenuDeFondosPreview() {
    MenuDeFondos()
}


@Composable
fun FondoLiso(color: Color){
    Box(
        modifier = Modifier.fillMaxSize()
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
    }
}

@Composable
fun FondoGradienteRadialDesplazable(color: Color) {
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val density = LocalDensity.current
    val maxDistance = with(density) { 100.dp.toPx() }

    // Función para limitar el desplazamiento dentro de un círculo
    fun Offset.coerceInCircle(radius: Float): Offset {
        val length = sqrt(x * x + y * y)
        return if (length > radius) {
            val factor = radius / length
            Offset(x * factor, y * factor)
        } else {
            this
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val largeRadialGradient = remember(color, offset.value) {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                val biggerDimension = maxOf(size.height, size.width)
                return RadialGradientShader(
                    colors = listOf(BlancoAmarillento, color),
                    center = size.center + offset.value,
                    radius = biggerDimension / 2f,
                    colorStops = listOf(0f, 0.95f)
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(largeRadialGradient)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { _, dragAmount ->
                        val currentOffset = offset.value
                        val newOffset = (currentOffset + dragAmount).coerceInCircle(maxDistance)
                        // Actualizamos el offset usando el scope de corrutinas recordado
                        coroutineScope.launch {
                            offset.snapTo(newOffset)
                        }
                    },
                    onDragEnd = {
                        coroutineScope.launch {
                            offset.animateTo(Offset.Zero, tween(1000, 500, EaseOutCubic))
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        // Contenido central (opcional)
    }
}

// Función de extensión para convertir dp a px
@Composable
fun Float.dpToPx(): Float {
    return with(LocalDensity.current) { this@dpToPx.dp.toPx() }
}

// Función de extensión para calcular la distancia
fun Offset.getDistance(): Float {
    return sqrt(x * x + y * y)
}

@Composable
fun FondoGradienteRadial(color: Color) {
    // Gradiente radial grande para el fondo
    val largeRadialGradient = remember(color) {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                val biggerDimension = maxOf(size.height, size.width)
                return RadialGradientShader(
                    colors = listOf(BlancoAmarillento, color),
                    center = size.center,
                    radius = biggerDimension / 2f,
                    colorStops = listOf(0f, 0.95f)
                )
            }
        }
    }

    // Box vacía que ocupa toda la pantalla con el fondo degradado
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(largeRadialGradient),
        contentAlignment = Alignment.Center,
    ) {
    }
}

@Composable
fun Fondo2Gradientes(
    colorCirculo: Color,
    colorGradienteLineal: Color,
    angulo: Float = 45f,       // Ángulo del gradiente lineal en grados
    tamanioFactor: Float = 1f  // Factor de tamaño del gradiente (ajusta la longitud)
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawWithContent {
                // Dibuja el contenido base (fondo 1)
                drawContent()

                // Luego dibuja el segundo fondo con un modo de mezcla
                drawIntoCanvas { canvas ->
                    val paint = Paint()

                    paint.asFrameworkPaint().xfermode =
                        android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.ADD)


                    // --- Dibujar gradiente lineal con rotación por ángulo ---
                    val angleRad = Math.toRadians(angulo.toDouble())
                    // Definimos una longitud en función del tamaño mínimo y el factor
                    val length = size.minDimension * tamanioFactor

                    val center = size.center
                    // Vector direccional basado en el ángulo
                    val dx = cos(angleRad) * length
                    val dy = sin(angleRad) * length

                    // Gradiente lineal desde center - vector a center + vector
                    val from = Offset((center.x - dx).toFloat(), (center.y - dy).toFloat())
                    val to   = Offset((center.x + dx).toFloat(), (center.y + dy).toFloat())

                    val linearGradientShader = LinearGradientShader(
                        from = from,
                        to = to,
                        colors = listOf(Color.Blue, colorGradienteLineal),
                        colorStops = listOf(0f, 1f)
                    )

                    paint.shader = linearGradientShader
                    canvas.drawRect(size.toRect(), paint)

                    // --- Dibujar círculo con gradiente radial ---
                    // Definimos posición, tamaño y gradiente radial
                    val circleCenter = Offset(size.width * 0.7f, size.height * 0.3f)
                    val circleRadius = min(size.width, size.height) / 4f

                    val radialGradientShader = RadialGradientShader(
                        colors = listOf(
                            Color.White.copy(alpha = 0.2f),
                            colorCirculo.copy(alpha = 0.8f),
                            Color.Transparent
                        ),
                        center = circleCenter,
                        radius = circleRadius,
                        colorStops = listOf(0f, 0.7f, 1f)
                    )
                    paint.asFrameworkPaint().xfermode =
                        android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.ADD)

                    paint.shader = radialGradientShader
                    // Si quieres ajustar la opacidad global:
                    // paint.alpha = 200 // Valor entre 0 y 255
                    canvas.drawCircle(circleCenter, circleRadius, paint)

                }
            }
    ) {
        FondoLiso(color = Color.Transparent)
    }
}


@Composable
fun MenuDeFondos() {
    val elementId = remember { mutableIntStateOf(0) }

    // Muestra el fondo actual
    fondos[elementId.value]()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Botón para mover al elemento anterior (izquierda)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(32.dp)
                .size(48.dp) // Tamaño del botón
                .background(Color.Transparent, shape = Shapes().large) // Fondo y forma
                .border(1.dp, Color.DarkGray, shape = Shapes().small) // Borde
                .clickable {
                    elementId.value = elementId.value - 1
                    if (elementId.value < 0) {
                        elementId.value = fondos.size - 1
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text("<", color = Color.DarkGray, fontSize = 24.sp) // Texto del botón izquierdo
        }

        // Botón para mover al siguiente elemento (derecha)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp)
                .size(48.dp) // Tamaño del botón
                .background(Color.Transparent, shape = Shapes().small) // Fondo y forma
                .border(1.dp, Color.DarkGray, shape = Shapes().small) // Borde
                .clickable {
                    elementId.value = elementId.value + 1
                    if (elementId.value >= fondos.size) {
                        elementId.value = 0
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                ">",
                color = Color.DarkGray,
                fontSize = 24.sp,
                fontFamily = Olivia
            ) // Texto del botón derecho
        }

    }
}



val AzulCielo = Color(0xFF87CEEB)
val AzulMarino = Color(0xFF001F3F)
val AzulOscuro = Color(0xFF0B0C10)
val AzulElectrico = Color(0xFF66FCF1)
val VerdeVerano = Color(0xFF5E503F)
val VerdeOscuro = Color(0xFF1B4332)
val VerdeVejiga = Color(0xFFA3CB38)
val VerdeMenta = Color(0xFF98FF98)
val AmarilloOscuro = Color(0xFFFFD700)
val AmarilloClaro = Color(0xFFFFF3B0)
val NaranjaOscuro = Color(0xFFA64D00)
val NaranjaClaro = Color(0xFFFFC89E)
val RojoOscuro = Color(0xFF8B0000)
val RojoClaro = Color(0xFFFFC9C9)
val RosaOscuro = Color(0xFFD11141)
val RosaClaro = Color(0xFFFFC5CB)
val MoradoOscuro = Color(0xFF3C1053)
val MoradoClaro = Color(0xFFD9C6FF)
val GrisOscuro = Color(0xFF333333)
val Gris50 = Color(0xFF808080)
val Gris94 = Color(0xFFF2F2F2)
val Gris97 = Color(0xFFF7F7F7)
val BlancoAmarillento = Color(0xFFEFEFEF)

