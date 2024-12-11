package com.example.animations.level

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.animations.character.CharacterControlUI
import com.example.animations.character.CharacterState
import com.example.animations.character.CharacterView
import com.example.animations.character.CharacterViewModel

// Dimensiones lógicas de la escena
var sceneLogicalWidth = 800f
var sceneLogicalHeight = 600f

class LevelViewModel(
) : ViewModel() {

    private val _scale = MutableLiveData<Float>(0f)
    val scale: LiveData<Float> = _scale

    fun setScale(containerWidth: Float, containerHeight: Float) {
        // Calcular el factor de escala para mantener la escena completa visible
        _scale.value =
            minOf(containerWidth / sceneLogicalWidth, containerHeight / sceneLogicalHeight)
    }
}

@Composable
@Preview
fun LevelPreview() {
    Level()
}

@Composable
fun Level() {
    val level = LevelViewModel()
    val character: CharacterViewModel = CharacterViewModel(CharacterState.IDLE)

    val scale by level.scale.observeAsState(1f)

    // Medir el tamaño real del contenedor donde se dibujará la escena
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        level.setScale(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())


        Box(
            modifier = Modifier
                // Ajustar el tamaño lógico escalado
                .size((sceneLogicalWidth * scale).dp, (sceneLogicalHeight * scale).dp)
                .graphicsLayer {
                    // Aquí se puede hacer otro tipo de ajuste, aunque .size ya ajustó el contenedor
                }


        ) {
        }

        Terrain()
        CharacterControlUI(character)
        CharacterView(character, scale)

    }
}

@Composable
fun Terrain() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
    }
}