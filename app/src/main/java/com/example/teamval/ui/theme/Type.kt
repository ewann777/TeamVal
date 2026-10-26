package com.example.teamval.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import com.example.teamval.R
import androidx.compose.ui.unit.sp
import com.example.teamval.domain.model.AppFont

// Archivos estáticos locales: funcionan sin red y desde API 24.
val Outfit = FontFamily(
    Font(R.font.outfit_regular, FontWeight.Normal),
    Font(R.font.outfit_medium, FontWeight.Medium),
    Font(R.font.outfit_semibold, FontWeight.SemiBold),
    Font(R.font.outfit_bold, FontWeight.Bold)
)
val Exo2 = FontFamily(
    Font(R.font.exo2_regular, FontWeight.Normal),
    Font(R.font.exo2_medium, FontWeight.Medium),
    Font(R.font.exo2_semibold, FontWeight.SemiBold),
    Font(R.font.exo2_bold, FontWeight.Bold)
)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.4).sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.5).sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.2).sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 25.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.2.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp
    )
)

fun teamValTypography(font: AppFont): Typography {
    val family = when (font) {
        AppFont.Gaming -> Outfit
        AppFont.Modern -> FontFamily.SansSerif
        AppFont.Editorial -> FontFamily.Serif
        AppFont.Mono -> FontFamily.Monospace
    }
    val heading = if (font == AppFont.Gaming) Exo2 else family
    return Typography.copy(
        displayLarge = Typography.displayLarge.copy(fontFamily = heading, fontWeight = FontWeight.Bold),
        displayMedium = Typography.displayMedium.copy(fontFamily = heading, fontWeight = FontWeight.Bold),
        displaySmall = Typography.displaySmall.copy(fontFamily = heading, fontWeight = FontWeight.Bold),
        headlineLarge = Typography.headlineLarge.copy(fontFamily = heading),
        headlineMedium = Typography.headlineMedium.copy(fontFamily = heading),
        headlineSmall = Typography.headlineSmall.copy(fontFamily = heading, fontWeight = FontWeight.SemiBold),
        titleLarge = Typography.titleLarge.copy(fontFamily = heading),
        titleMedium = Typography.titleMedium.copy(fontFamily = heading),
        titleSmall = Typography.titleSmall.copy(fontFamily = heading),
        bodyLarge = Typography.bodyLarge.copy(fontFamily = family),
        bodyMedium = Typography.bodyMedium.copy(fontFamily = family),
        bodySmall = Typography.bodySmall.copy(fontFamily = family, fontSize = 13.sp, lineHeight = 19.sp),
        labelLarge = Typography.labelLarge.copy(fontFamily = family),
        labelMedium = Typography.labelMedium.copy(fontFamily = family),
        labelSmall = Typography.labelSmall.copy(fontFamily = family)
    )
}
