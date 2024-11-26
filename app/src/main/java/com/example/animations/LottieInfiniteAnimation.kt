package com.example.animations

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
