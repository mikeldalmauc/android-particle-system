package com.example.animations

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

    var modifier = Modifier
        .fillMaxWidth(0.3f)

    LazyColumn {
        Elementos.forEach {
            when (it){
                R.raw.fire -> item {
                    modifier = modifier
                        .fillMaxWidth(0.25f)

                    LottieInfinit(it, modifier)
                }
                else -> item {

                    modifier = modifier
                        .fillMaxWidth(0.3f)
                    LottieInfinit(it, modifier)
                }
            }
        }
    }
}

