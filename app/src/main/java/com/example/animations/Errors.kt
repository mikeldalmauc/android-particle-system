package com.example.animations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.animations.ui.theme.MomsTypewriter


@Composable
@Preview
fun ErrorPagePreview(){
    ErrorPage(PaddingValues(12.dp))
}

@Composable
fun ErrorPage(contentPadding: PaddingValues) {
    Column (verticalArrangement = Arrangement.Center
        , horizontalAlignment = Alignment.CenterHorizontally
        , modifier = Modifier.fillMaxSize()) {
        RamaRota(contentPadding)
        Text("Oops...\nparece que algo\n ha fallado",
            fontSize  = 24.sp,
            fontFamily = MomsTypewriter,
            color = Color.LightGray,
            textAlign = TextAlign.Center,
            lineHeight = 31.sp,
            modifier = Modifier.padding(16.dp))
    }
}



@Composable
fun RamaRota(innerPadding: PaddingValues){

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ramaerror))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1 // Ejecuta una vez completa en cada activación
    )

    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.padding(innerPadding)
            .wrapContentSize(align = Alignment.Center)
    ) {
        Column (verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.fillMaxWidth(0.6f),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}