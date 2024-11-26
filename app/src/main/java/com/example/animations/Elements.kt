package com.example.animations

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


val Elementos = listOf(
    R.raw.agua,
    R.raw.roca,
    R.raw.aire,
    R.raw.planta,
    R.raw.fire
)

@Composable
@Preview
fun ElementsPreview(){
    ElementoAnimado()
}

@Composable
fun ElementoAnimado() {
    LazyColumn {
        Elementos.forEach {
            item {
                LottieInfinit(it)
            }
        }
    }
}

