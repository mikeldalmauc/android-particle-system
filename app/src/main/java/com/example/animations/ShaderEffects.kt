package com.example.animations

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.example.animations.ui.theme.Yellow


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun GlowingComponent(
    modifier: Modifier = Modifier,
    glowColor: Color = Yellow,
    glowRadius: Float = 20f,
    intensity: Float = 1.5f,
    content: @Composable () -> Unit,
) {
    // Convierte el color de Compose a un formato RGBA para AGSL
    val glowColorVector = remember(glowColor) {
        android.graphics.Color.valueOf(
            glowColor.red,
            glowColor.green,
            glowColor.blue,
            glowColor.alpha
        ).toArgb()
    }

    // Define el efecto RenderEffect con AGSL
    val glowShader = remember {
        RuntimeShader(
            """
            uniform shader content;      // Contenido del componente
            uniform float4 glowColor;    // Color del resplandor
            uniform float glowRadius;    // Radio del resplandor
            uniform float intensity;     // Intensidad del resplandor
            
            half4 main(float2 fragCoord) {
                // Evalúa el contenido base del componente
                half4 baseColor = content.eval(fragCoord);
            
                // Asegúrate de que el contenido base se renderice
                if (baseColor.a == 0.0) {
                    return baseColor; // Si no hay contenido, no aplica resplandor
                }
            
                // Detecta los bordes con base en la transparencia
                float edgeFactor = smoothstep(0.0, 1.0, baseColor.a);
            
                // Calcula el resplandor
                float glow = smoothstep(glowRadius, 0.0, edgeFactor * intensity);
            
                // Aplica el color del resplandor
                half4 finalGlow = half4(glowColor.rgb * glow, glowColor.a * glow);
            
                // Combina el color base con el resplandor
                return mix(baseColor, baseColor + finalGlow, glowColor.a);
            }
            """.trimIndent()
        )
    }

    // Aplica parámetros dinámicos al shader
    LaunchedEffect(glowColorVector, glowRadius, intensity) {
        glowShader.setFloatUniform("glowRadius", glowRadius)
        glowShader.setFloatUniform("intensity", intensity)
        glowShader.setFloatUniform(
            "glowColor",
            glowColor.red, glowColor.green, glowColor.blue, glowColor.alpha
        )
    }

    Box(
        modifier = modifier.graphicsLayer {
            RenderEffect.createRuntimeShaderEffect(glowShader, "content")
        }
    ) {
        content()
    }
}
