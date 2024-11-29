package com.example.animations

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animations.ui.theme.DarkBlue
import com.example.animations.ui.theme.LightOcre
import com.example.animations.ui.theme.MomsTypewriter
import com.example.animations.ui.theme.Yellow
import kotlinx.coroutines.delay


@Preview
@Composable
fun TextAnimationPreview() {
    TextAnimation()
}

@Composable
fun TextAnimation() {

    var text by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        lorem.forEach { char ->
            val delayTime = when (char) {
                ',' -> 200L
                '.', '!', '?' -> 500L
                '\n' -> 1000L
                ' ' -> 50L
                else -> 30L
            }

            text += char
            delay(delayTime)
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Surface (
            color = LightOcre,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, Yellow),
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(0.8f)
                .padding(6.dp)
        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = MomsTypewriter,
                    fontSize = 14.sp,
                    color = DarkBlue
                )
                Spacer(modifier = Modifier.fillMaxHeight(0.07f)) //
            }
        }
    }

}

val lorem = """
    Lorem Ipsum es simplemente el texto de relleno de las imprentas y archivos de texto. Lorem Ipsum ha sido el texto de relleno estándar de las industrias desde el año 1500, cuando un impresor (N. del T. persona que se dedica a la imprenta) desconocido usó una galería de textos y los mezcló de tal manera que logró hacer un libro de textos especimen. No sólo sobrevivió 500 años, sino que tambien ingresó como texto de relleno en documentos electrónicos, quedando esencialmente igual al original. Fue popularizado en los 60s con la creación de las hojas "Letraset", las cuales contenian pasajes de Lorem Ipsum, y más recientemente con software de autoedición, como por ejemplo Aldus PageMaker, el cual incluye versiones de Lorem Ipsum.
""".trimIndent()