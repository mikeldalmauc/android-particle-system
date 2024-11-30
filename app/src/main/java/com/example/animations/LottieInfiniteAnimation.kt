package com.example.animations

import android.graphics.RenderEffect
import android.graphics.RuntimeShader

import android.os.Build
import androidx.annotation.RawRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.animations.ui.theme.Yellow

/**
 * https://github.com/airbnb/lottie/blob/master/android-compose.md
 *https://medium.com/@thecodingmontana/implement-a-lottie-animation-in-the-android-app-with-jetpack-compose-356da0ccc145
 */


@Composable
fun LottieLego(innerPadding: PaddingValues) {
    Box(modifier = Modifier.padding(innerPadding)) {
        LottieInfinite(R.raw.lottie_lego)
    }
}

@Composable
fun LottieInfinite(@RawRes resId: Int) {
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
fun LottieInfinite(@RawRes resId: Int, modifier: Modifier) {
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
