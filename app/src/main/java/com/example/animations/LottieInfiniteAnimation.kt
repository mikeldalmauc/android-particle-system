package com.example.animations

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

/**
 * https://github.com/airbnb/lottie/blob/master/android-compose.md
 *https://medium.com/@thecodingmontana/implement-a-lottie-animation-in-the-android-app-with-jetpack-compose-356da0ccc145
 */


@Composable
@Preview
fun RamaErrorPreview(){
    RamaError(PaddingValues(12.dp))
}

@Composable
fun RamaError(innerPadding: PaddingValues){
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.padding(innerPadding)
            .fillMaxSize()
        ) {
        Column {
            LottieInfinit(R.raw.ramaerror, Modifier.scale(0.5f))
        }
    }
}

@Composable
fun LottieLego(innerPadding: PaddingValues) {
    Box(modifier = Modifier.padding(innerPadding)) {
        LottieInfinit(R.raw.lottie_lego)
    }
}

@Composable
fun LottieInfinit(@RawRes resId: Int){
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
    )
}


@Composable
fun LottieInfinit(@RawRes resId: Int, modifier: Modifier){
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
    )
    LottieAnimation(
        composition = composition,
        modifier = modifier,
        progress = { progress },
    )
}
