package com.example.animations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.random.Random
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sqrt

class Particle(
    var x: Float = 0.0f,
    var y: Float = 0.0f,
    var dx: Float = 0.0f,
    var dy: Float = 0.0f,
    var lifeTime: Float = 1.0f,
    var scale: Float = 1.0f,
    var rotation: Float = 0.0f,
    var rotationSpeed: Float = 0.0f,
    var opacity: Float = 1.0f,
    var confetti: Confetti,
) {
    fun draw(drawScope: DrawScope) {
        when (confetti) {
            is Confetti.Square -> drawScope.drawSquare(this)
            is Confetti.Streamer -> drawScope.drawStreamer(this)
        }
    }
}


class ParticleSystem(seed: Int, val modifiers: List<ParticleModifier>) {

    val random = Random(seed)
    private val particles = mutableStateListOf<Particle>()

    // Método para aplicar todos los modificadores a una partícula
    fun applyModifiers(particle: Particle, deltaTime: Float): Particle {
        var modifiedParticle = particle
        modifiers.forEach { modifier ->
            modifiedParticle = modifier.modify(modifiedParticle)
        }

        // Actualizar la posición de la partícula según su velocidad
        modifiedParticle.x += modifiedParticle.dx * deltaTime
        modifiedParticle.y += modifiedParticle.dy * deltaTime

        // Actualizar la rotación de la partícula según su velocidad de rotación
        modifiedParticle.rotation += modifiedParticle.rotationSpeed * deltaTime

        // Disminuir su vida útil
        modifiedParticle.lifeTime -= deltaTime

        return modifiedParticle
    }

    // Método para gestionar la siguiente generación de partículas
    fun computeNextGeneration(deltaTime: Float) {
        particles.replaceAll { particle ->
            applyModifiers(particle, deltaTime)
        }

        // Eliminar partículas cuya vida ha expirado
        particles.removeAll { it.lifeTime <= 0 }
    }

    // Añadir una partícula al sistema
    private fun addParticle(particle: Particle) {
        particles.add(particle)
    }

    // Obtener todas las partículas para renderizarlas
    fun getParticles(): List<Particle> = particles

    fun generateRandomParticles(count: Int, generator: () -> Particle) {
        repeat(count) {
            this.addParticle(generator())
        }
    }
}

abstract class ParticleModifier {
    abstract fun modify(particle: Particle): Particle
}

class DecayModifier(private val decayRate: Float) : ParticleModifier() {
    override fun modify(particle: Particle): Particle {
        particle.scale *= decayRate
        return particle
    }
}

class DragModifier(private val dragFactor: Float) : ParticleModifier() {
    override fun modify(particle: Particle): Particle {
        particle.dx *= dragFactor
        particle.dy *= dragFactor
        return particle
    }
}

class GravityModifier(private val gravity: Float) : ParticleModifier() {
    override fun modify(particle: Particle): Particle {
        particle.dy += gravity
        return particle
    }
}

// Modificador de rotación
class RotationModifier(private val rotationSpeed: Float) : ParticleModifier() {
    override fun modify(particle: Particle): Particle {
        particle.rotationSpeed = rotationSpeed // Aplicar velocidad de rotación
        return particle
    }
}

// Modificador de opacidad
class OpacityModifier(private val opacityDecayRate: Float) : ParticleModifier() {
    override fun modify(particle: Particle): Particle {
        particle.opacity *= opacityDecayRate
        return particle
    }
}

sealed class Confetti {
    data class Square(
        val color: Color,
        val rotations: Float,
        val rotationOffset: Float
    ) : Confetti()

    data class Streamer(
        val color: Color,
        val length: Int,
        val rotations: Float,
        val rotationOffset: Float
    ) : Confetti()
}

enum class ConfettiColor(val color: Color) {
    Red(Color(0xFFFF0000)),
    Pink(Color(0xFFFFC0CB)),
    Yellow(Color(0xFFFFFF00)),
    Green(Color(0xFF008000)),
    Blue(Color(0xFF0000FF))
}

// Vista para renderizar un cuadrado
fun DrawScope.drawSquare(particle: Particle) {
    val square = particle.confetti as Confetti.Square
    rotate(particle.rotation, pivot = Offset(particle.x, particle.y)) {
        drawRect(
            color = square.color,
            size = androidx.compose.ui.geometry.Size(20f * particle.scale, 20f * particle.scale),
            topLeft = Offset(particle.x, particle.y)
        )
    }
}

// Vista para renderizar una serpentina
fun DrawScope.drawStreamer(particle: Particle) {
    val streamer = particle.confetti as Confetti.Streamer
    val angleRadians = atan2(particle.dx, -particle.dy)  // ángulo en radianes
    val angleDegrees = angleRadians * (180f / PI).toFloat()  // convertir a grados manualmente

    rotate(angleDegrees,
        pivot = Offset(particle.x, particle.y)) {
        drawLine(
            color = streamer.color,
            start = Offset(particle.x, particle.y),
            end = Offset(particle.x, particle.y + streamer.length * particle.scale),
            strokeWidth = 4.dp.toPx()
        )
    }
}

fun generateRandomParticle(random: Random): Particle {

    // Determinar el tipo de partícula aleatoriamente
    val isSquare = random.nextBoolean()
    return if (isSquare) {
        // Generar un cuadrado
        val square = generateSquare(random)
        Particle(
            x = (random.nextFloat() - 0.5f) * 100 + 500f,
            y = 500f,
            dx = (random.nextFloat() - 0.5f) * 400f,
            dy = (random.nextFloat() - 0.5f) * 700f,
            lifeTime = random.nextFloat() * 4f + 1f,
            scale = random.nextFloat() * 0.5f + 0.5f,
            rotation = random.nextFloat() * 360f,
            rotationSpeed = random.nextFloat() * 200f - 5f,
            opacity = random.nextFloat() * 0.5f + 0.5f,
            confetti = square
        )
    } else {
        // Generar una serpentina
        val streamer = generateStreamer(random)
        Particle(
            x = (random.nextFloat() - 0.5f) * 100 + 500f,
            y = 500f,
            dx = (random.nextFloat() - 0.5f) * 400f,
            dy = (random.nextFloat() - 0.5f) * 700f,
            lifeTime = random.nextFloat() * 4f + 1f,
            scale = random.nextFloat() * 2f + 1f,
            rotation = random.nextFloat() * 360f,
            rotationSpeed = random.nextFloat() * 200f - 5f,
            opacity = random.nextFloat() * 0.5f + 0.5f,
            confetti = streamer
        )
    }
}

fun generateSquare(random: Random): Confetti.Square {
    val color = when (random.nextFloat()) {
        in 0f..0.2f -> ConfettiColor.Red.color
        in 0.2f..0.4f -> ConfettiColor.Pink.color
        in 0.4f..0.6f -> ConfettiColor.Yellow.color
        else -> ConfettiColor.Green.color
    }

    val rotations = randomGaussian(mean = 1.0, deviation = 0.5).toFloat()  // Valor centrado en 1.0

    val rotationOffset = random.nextFloat() // Distribución uniforme [0,1]

    return Confetti.Square(color = color, rotations = rotations, rotationOffset = rotationOffset)
}

fun generateStreamer(random: Random): Confetti.Streamer {
    val color = when (random.nextFloat()) {
        in 0f..0.5f -> ConfettiColor.Pink.color
        else -> ConfettiColor.Blue.color
    }
    val length = max(10, randomGaussian(mean = 25.0, deviation = 10.0).toInt())

    val rotations = randomGaussian(mean = 1.0, deviation = 0.5).toFloat()

    val rotationOffset = random.nextFloat()

    return Confetti.Streamer(
        color = color,
        length = length,
        rotations = rotations,
        rotationOffset = rotationOffset
    )
}

fun randomGaussian(mean: Double = 0.0, deviation: Double = 1.0): Double {
    val random = Random(System.currentTimeMillis())

    // Dos números aleatorios uniformemente distribuidos en (0, 1]
    val u1 = random.nextDouble()
    val u2 = random.nextDouble()

    // Aplicar la transformación Box-Muller para obtener dos valores distribuidos normalmente
    val z0 = sqrt(-2.0 * ln(u1)) * cos(2.0 * PI * u2)

    // Ajustar el valor con la media y la desviación estándar
    return z0 * deviation + mean
}

@Composable
fun Particles() {
    Box(modifier = Modifier.fillMaxSize()) {
        val particleSystem = remember {
            ParticleSystem(
                5,
                listOf(
                    GravityModifier(9.8f),
                    DragModifier(0.98f)
                )
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            particleSystem.getParticles().forEach { particle ->
                particle.draw(this)  // Dibujar cada partícula con las propiedades actuales
            }
        }

        LaunchedEffect(Unit) {
            val frameTime = 2 * 16L // aproximadamente  FPS
            while (true) {
                val deltaTime = frameTime / 1000f // convertir ms a segundos
                particleSystem.generateRandomParticles(2) { generateRandomParticle(particleSystem.random) }
                particleSystem.computeNextGeneration(deltaTime)
                delay(frameTime)
            }
        }
    }
}

