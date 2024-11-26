package com.example.animations

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


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
        .width(200.dp)
        .height(200.dp)

    LazyColumn {
        Elementos.forEach {
            when (it){
                R.raw.fire -> item {
                    modifier = modifier
                    .padding(16.dp)

                    LottieInfinit(it, modifier)
                }
                R.raw.planta -> item {
                    modifier = modifier
                        .width(280.dp)
                        .height(280.dp)

                    LottieInfinit(it, modifier)
                }
                R.raw.aire -> item {
                    modifier = modifier
                        .padding(32.dp)

                    LottieInfinit(it, modifier)
                }
                else -> item {

                    LottieInfinit(it, modifier)
                }
            }
        }
    }
}

