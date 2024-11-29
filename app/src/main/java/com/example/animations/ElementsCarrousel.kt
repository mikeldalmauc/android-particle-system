package com.example.animations

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FloatSpringSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animations.ui.theme.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Preview
@Composable
fun CarouselPreview() {
    Carousel()
}


class CarrouselViewModel : ViewModel() {

    // Lista de elementos
    val elements = Element.elements

    // Elemento seleccionado, inicialmente null
    private val _selectedElement = MutableLiveData<Element?>()
    val selectedElement: MutableLiveData<Element?> get() = _selectedElement

    // Función para seleccionar un elemento
    fun selectElement(element: Element?) {
        Log.d("ElementCarrousel", "Selected: $element")
        _selectedElement.value = element
    }

    fun deselectElement() {
        _selectedElement.value = null
    }

    init {
        _selectedElement.value = Element.elements[0]
    }
}


data class Element(
    val name: String,
    val resourceId: Int,
    val description: String,
    val darkColor: Color,
    val ligthColor: Color,
) {
    companion object {
        val elements = listOf(
            Element("Luz", R.raw.luz, "El orbe", Yellow, LightYellow),
            Element("Roca", R.raw.roca, "La roca", Brown, LightBrown),
            Element("Agua", R.raw.agua, "El agua", Blue, LightBlue),
            Element("Fuego", R.raw.fire, "La llama", Red, LightRed),
            Element("Planta", R.raw.planta, "La planta", Green, LightGreen),
        )
    }
}

// Composable para inicializar el carrusel con elementos específicos
@Composable
fun Carousel() {
    val viewModel: CarrouselViewModel = CarrouselViewModel()

    // Gradiente radial grande para el fondo
    val largeRadialGradient = object : ShaderBrush() {
        override fun createShader(size: Size): Shader {
            val biggerDimension = maxOf(size.height, size.width)
            return RadialGradientShader(
                colors = listOf(VeryLightYellow, LightGray),
                center = size.center,
                radius = biggerDimension / 2f,
                colorStops = listOf(0f, 0.95f)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(largeRadialGradient),
        contentAlignment = Alignment.Center,
    ) {
    }
    CarrouselSection(viewModel)
    TitleSection(viewModel)
}

@Composable
@Preview
fun TitleSectionPreview() {
    val viewModel = CarrouselViewModel()
    LazyColumn {
        item {
            Element.elements.forEach() {
                TitleSection(viewModel )
            }
            TitleSection(viewModel)
        }
    }
}

@Composable
fun TitleSection(viewModel: CarrouselViewModel) {
    val selectedElement by viewModel.selectedElement.observeAsState()
    var previousElement by remember { mutableStateOf<Element?>(Element("", R.raw.luz, "", Gray, LightGray)) }

    val alpha = remember { Animatable(1f) }
    LaunchedEffect(selectedElement) {
        if (selectedElement == null) {
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1000)
            )
        } else {
            previousElement = selectedElement
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000)
            )
        }
    }

    val displayElement = selectedElement ?: previousElement ?: Element("", R.raw.luz, "", Gray, LightGray)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = 100.dp),
    ) {
        Surface(
            shape = Shapes().large,
            color = displayElement.ligthColor,
            shadowElevation = 10.dp,
            border = BorderStroke(1.dp, displayElement.darkColor),
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp)
                .alpha(alpha.value)
        ) {
            Text(
                text = displayElement.description,
                fontFamily = Olivia,
                color = displayElement.darkColor,
                fontSize = 40.sp,
                modifier = Modifier.padding(13.dp),
                letterSpacing = 1.sp
            )
        }
    }

}

@Composable
fun CarrouselSection(viewModel: CarrouselViewModel) {

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularCarouselWithButtons(
            numItems = viewModel.elements.size,
            itemFraction = 0.49f,
            modifier = Modifier
                .width(2000.dp)
                .height(600.dp)
                .offset(y = 100.dp),
            viewModel = viewModel
        ) { index ->

            // Añadir sombra elíptica debajo del elemento del carrusel
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Canvas(
                    modifier = Modifier
                        .width(150.dp)
                        .height(30.dp)
                        .alpha(0.6f)
                ) {
                    drawOval(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.3f),
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2, size.height / 2),
                            radius = size.width /2
                        ),
                        topLeft = Offset(
                            x = size.width * 0.1f,
                            y = size.height * 0.7f
                        ),
                        size = Size(size.width * 0.8f, size.height * 0.3f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                var modifier = Modifier.alpha(1.0f)

                var resourceId = viewModel.elements[index].resourceId
                when (resourceId) {
                    R.raw.luz -> {
                        OrbeDeLuz()
                    }

                    R.raw.fire -> {
                        modifier = modifier.scale(0.9f)
                        LottieInfinite(resourceId, modifier)
                    }

                    R.raw.planta -> {
                        modifier = modifier.scale(1.1f)
                        LottieInfinite(resourceId, modifier)
                    }

                    else -> {
                        LottieInfinite(resourceId, modifier)
                    }
                }
            }
        }
    }
}

@Composable
fun CircularCarouselWithButtons(
    numItems: Int,
    modifier: Modifier = Modifier,
    itemFraction: Float = .25f,
    state: CircularCarouselState = rememberCircularCarouselState(),
    viewModel: CarrouselViewModel,
    contentFactory: @Composable (Int) -> Unit
) {
    val nextElement = remember { mutableStateOf(false) }
    val previousElement = remember { mutableStateOf(false) }

    val selectedElement by viewModel.selectedElement.observeAsState()

    if(selectedElement != null) {
        nextElement.value = false
        previousElement.value = false
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Composable CircularCarousel original
        CircularCarousel(
            numItems = numItems,
            itemFraction = itemFraction,
            state = state,
            onItemDeselected = {
                viewModel.deselectElement()
            },
            onItemSelected = { index ->
                viewModel.selectElement(viewModel.elements[index])
            },
            contentFactory = contentFactory,
            nextElement = nextElement.value,
            previouElement = previousElement.value
        )
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Botón para mover al elemento anterior (izquierda)
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(16.dp)
                .size(48.dp) // Tamaño del botón
                .background(Color.Gray, shape = Shapes().small) // Fondo y forma
                .clickable {
                    viewModel.deselectElement()
                    previousElement.value = true
                },
            contentAlignment = Alignment.Center
        ) {
            Text("<", color = Color.White, fontSize = 24.sp) // Texto del botón izquierdo
        }
        // Botón para mover
        // al siguiente elemento (derecha)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp)
                .size(48.dp) // Tamaño del botón
                .background(Color.Gray, shape = Shapes().small) // Fondo y forma
                .clickable {
                    viewModel.deselectElement()
                    nextElement.value = true
                },
            contentAlignment = Alignment.Center
        ) {
            Text(">", color = Color.White, fontSize = 24.sp) // Texto del botón derecho
        }

    }
}



@Composable
fun OrbeDeLuz() {
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
                        radius = (size.minDimension / 2) * 0.85f
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
                        radius = (size.minDimension / 2) * 0.75f
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
