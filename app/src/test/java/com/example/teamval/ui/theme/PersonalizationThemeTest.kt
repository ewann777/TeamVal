package com.example.teamval.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.example.teamval.domain.model.AppAppearance
import com.example.teamval.domain.model.PlayerProfile
import com.example.teamval.domain.model.ProfileField
import com.example.teamval.domain.model.Reputation
import com.example.teamval.domain.model.PlayerReview
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
