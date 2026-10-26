package com.example.teamval.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.example.teamval.domain.model.AppAppearance
import com.example.teamval.domain.model.PlayerProfile
import com.example.teamval.domain.model.ProfileField
import com.example.teamval.domain.model.Reputation
import com.example.teamval.domain.model.PlayerReview
import com.example.teamval.domain.model.AppearancePreset
import com.example.teamval.domain.model.AppFont
import androidx.compose.ui.graphics.lerp
import org.junit.Assert.*
import org.junit.Test

class PersonalizationThemeTest {
    @Test
    fun customAccentsMaintainTextContrastInBothThemes() {
        for (hex in listOf("000000", "FFFFFF", "FF0000", "00FF00", "0000FF", "C4B5FD", "88DCC4")) {
            for (dark in listOf(true, false)) {
                val colors = teamValColorScheme(AppAppearance(accentHex = hex), dark)
                assertTrue("Primary contrast: $hex dark=$dark", contrast(colors.primary, colors.onPrimary) >= 4.5f)
                assertTrue("Accent text: $hex dark=$dark", contrast(colors.primary, colors.background) >= 4.5f)
                assertTrue(contrast(colors.onSurface, colors.surface) >= 4.5f)
                assertTrue(contrast(colors.onPrimaryContainer, colors.primaryContainer) >= 4.5f)
            }
        }
    }

    @Test
    fun brandColorsAndPresetSurfacesKeepReadableText() {
        val classic = teamValColorScheme(AppAppearance(), dark = true)
        assertEquals(ValorantRed, classic.primary)
        assertEquals(CarbonBlue, classic.background)
        assertEquals(Ivory, classic.onBackground)
        for (preset in AppearancePreset.entries) for (dark in listOf(true, false)) {
            val colors = teamValColorScheme(preset.apply(AppAppearance()), dark)
            val visuals = teamValVisuals(colors, dark)
            listOf(colors.background, colors.surface, colors.surfaceContainerHigh).forEach { surface ->
                assertTrue("$preset main text", contrast(colors.onSurface, surface) >= 4.5f)
                assertTrue("$preset secondary text", contrast(colors.onSurfaceVariant, surface) >= 4.5f)
                assertTrue("$preset accent", contrast(visuals.accentText, surface) >= 4.5f)
            }
            assertTrue(contrast(colors.onPrimary, colors.primary) >= 4.5f)
            // Botones degradados: también deben contrastar en sus extremos.
            val away = if (colors.onPrimary.luminance() > 0.5f) Color.Black else Color.White
            assertTrue(contrast(colors.onPrimary, lerp(colors.primary, away, 0.06f)) >= 4.5f)
            val chipFill = lerp(colors.surface, colors.primary, 0.14f)
            assertTrue(contrast(contrastingColor(colors.primary, chipFill), chipFill) >= 4.5f)
        }
    }

    @Test
    fun gamingTypographyCoversEveryMaterialRoleAndKeepsLegacySelection() {
        val type = teamValTypography(AppFont.Gaming)
        listOf(type.displayLarge, type.displayMedium, type.displaySmall, type.headlineLarge,
            type.headlineMedium, type.headlineSmall, type.titleLarge, type.titleMedium, type.titleSmall)
            .forEach { assertEquals(Exo2, it.fontFamily) }
        listOf(type.bodyLarge, type.bodyMedium, type.bodySmall, type.labelLarge, type.labelMedium, type.labelSmall)
            .forEach { assertEquals(Outfit, it.fontFamily) }
        assertEquals(androidx.compose.ui.text.font.FontFamily.Serif, teamValTypography(AppFont.Editorial).bodyLarge.fontFamily)
        assertEquals(androidx.compose.ui.text.font.FontFamily.Monospace, teamValTypography(AppFont.Mono).headlineLarge.fontFamily)
    }

    @Test
    fun optionalBiographyAndHeadlineCanBeClearedButNameCannot() {
        assertTrue(PlayerProfile(headline = "", biography = "").invalidFields().isEmpty())
        assertEquals(setOf(ProfileField.Name), PlayerProfile(name = "  ").invalidFields())
        assertEquals(setOf(ProfileField.Headline, ProfileField.Biography), PlayerProfile(headline = "x".repeat(81), biography = "x".repeat(301)).invalidFields())
    }

    @Test
    fun reputationAverageComesFromReviewsAndHandlesAnEmptyList() {
        assertEquals(0.0, Reputation(emptyList(), 0, 0, 0).average, 0.001)
        assertEquals(4.5, Reputation(listOf(PlayerReview("a", 5, ""), PlayerReview("b", 4, "")), 90, 90, 90).average, 0.001)
    }

    private fun contrast(a: Color, b: Color): Float =
        (maxOf(a.luminance(), b.luminance()) + 0.05f) / (minOf(a.luminance(), b.luminance()) + 0.05f)
}
