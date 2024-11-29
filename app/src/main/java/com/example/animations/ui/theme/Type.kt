package com.example.animations.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.animations.R


// Define tu familia de fuentes
val CuteNotes = FontFamily(
    Font(R.font.cute_notes, FontWeight.Normal), // Carga la fuente normal
)

val Insula = FontFamily(
    Font(R.font.insula, FontWeight.Normal), // Carga la fuente normal
)

val Olivia = FontFamily(
    Font(R.font.olivia, FontWeight.Normal), // Carga la fuente normal
)

// typewriters
val MomsTypewriter = FontFamily(
    Font(R.font.moms_typewriter, FontWeight.Normal), // Carga la fuente normal
)
val OldNewspaperTypes = FontFamily(
    Font(R.font.oldnewspapertypes, FontWeight.Normal), // Carga la fuente normal
)
val Typewriter1942 = FontFamily(
    Font(R.font.typewriter_1942, FontWeight.Normal), // Carga la fuente normal
)


// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)