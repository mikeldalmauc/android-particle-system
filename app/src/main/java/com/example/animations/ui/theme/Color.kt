package com.example.animations.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val Orange = Color(0xFFFF7700)
val YellowGlow = Color(0xD3EEC956)

val DarkBlue = Color(0xFF0A0E21)
val LightOcre = Color(0xD3F3D5A6)

val Blue = Color(0xFF1129A2)
val LightBlue = Color(0xFFDDEAE8)

val Red = Color(0xFF671405)
val LightRed = Color(0xFFF1C69C)

val Green = Color(0xFF05330C)
val LightGreen = Color(0xFF97C7AC)

val Yellow = Color(0xFF382D00)
val LightYellow = Color(0xFFF6D5A7)
val VeryLightYellow = Color(0xFFEFEFEF)

val Gray = Color(0xFF333333)
val MidGray = Color(0xFFADADAD)
val LightGray = Color(0xFFE7E7E7)

val Brown = Color(0xFF381805)
val LightBrown = Color(0xFF9D8C81)

fun fontColor(color: Color): Color {
    when(color){
        DarkBlue -> return LightOcre
        LightOcre -> return DarkBlue
        LightRed -> return DarkBlue
        LightGreen -> return DarkBlue
        LightYellow -> return DarkBlue
        LightBrown -> return DarkBlue
        LightGray -> return DarkBlue
        LightBlue -> return DarkBlue
        else -> return DarkBlue
    }
}

