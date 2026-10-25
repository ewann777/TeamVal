package com.example.teamval.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.teamval.domain.model.AppFont

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = (-1.5).sp
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
        letterSpacing = 0.6.sp
    )
)

fun teamValTypography(font: AppFont): Typography {
    val family = when (font) {
        AppFont.Modern -> FontFamily.SansSerif
        AppFont.Editorial -> FontFamily.Serif
        AppFont.Mono -> FontFamily.Monospace
    }
    return Typography.copy(
        displayLarge = Typography.displayLarge.copy(fontFamily = family),
        displayMedium = Typography.displayMedium.copy(fontFamily = family),
        displaySmall = Typography.displaySmall.copy(fontFamily = family),
        headlineLarge = Typography.headlineLarge.copy(fontFamily = family),
        headlineMedium = Typography.headlineMedium.copy(fontFamily = family),
        headlineSmall = Typography.headlineSmall.copy(fontFamily = family),
        titleLarge = Typography.titleLarge.copy(fontFamily = family),
        titleMedium = Typography.titleMedium.copy(fontFamily = family),
        titleSmall = Typography.titleSmall.copy(fontFamily = family),
        bodyLarge = Typography.bodyLarge.copy(fontFamily = family),
        bodyMedium = Typography.bodyMedium.copy(fontFamily = family),
        bodySmall = Typography.bodySmall.copy(fontFamily = family),
        labelLarge = Typography.labelLarge.copy(fontFamily = family),
        labelMedium = Typography.labelMedium.copy(fontFamily = family),
        labelSmall = Typography.labelSmall.copy(fontFamily = family)
    )
}
