package com.example.teamval

import android.graphics.Paint
import androidx.core.content.res.ResourcesCompat
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.teamval.data.repository.LocalPreferencesRepository
import com.example.teamval.data.repository.teamValPreferences
import com.example.teamval.domain.model.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class AppearanceIdentityTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val repository get() = LocalPreferencesRepository(compose.activity.applicationContext.teamValPreferences)

    private fun openAppearance() {
        compose.onNodeWithTag("nav_profile").performClick()
        compose.onNodeWithTag("profile_appearance").performScrollTo().performClick()
    }

    @Test
    fun presetsPreviewDiscardSaveRecreateAndRestoreDefault() {
        openAppearance()
        compose.onNodeWithTag("preset_Classic").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("preset_BlueCharcoal").performScrollTo().performClick()
        // La vista previa no debe escribir antes de Guardar.
        assertEquals(AppAppearance(), runBlocking { repository.preferences.first().appearance })
        compose.onNodeWithText("Volver").performClick()
        openAppearance()
        compose.onNodeWithTag("preset_Classic").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("preset_ElegantLight").performScrollTo().performClick()
        compose.onNodeWithTag("save_preferences").performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Cambios guardados en este dispositivo.").fetchSemanticsNodes().isNotEmpty() }
        assertEquals(AppearancePreset.ElegantLight.apply(AppAppearance()), runBlocking { repository.preferences.first().appearance })
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("preset_ElegantLight").performScrollTo().assertIsSelected()
        compose.onNodeWithText(compose.activity.getString(R.string.appearance_reset)).performScrollTo().performClick()
        compose.onNodeWithTag("save_preferences").performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Cambios guardados en este dispositivo.").fetchSemanticsNodes().isNotEmpty() }
        assertEquals(AppAppearance(), runBlocking { repository.preferences.first().appearance })
        compose.onNodeWithText("Volver").performClick()
        compose.onNodeWithTag("nav_home").performClick()
        compose.onNodeWithTag("home_balance").assertTextContains("TeamVal Coins")
    }

    @Test
    fun bundledFontsLoadAllWeightsAndRenderSpanish() {
        val resources = listOf(R.font.outfit_regular, R.font.outfit_medium, R.font.outfit_semibold, R.font.outfit_bold,
            R.font.exo2_regular, R.font.exo2_medium, R.font.exo2_semibold, R.font.exo2_bold)
        resources.forEach { id ->
            val typeface = ResourcesCompat.getFont(compose.activity, id)
            assertNotNull(typeface)
            val paint = Paint().apply { this.typeface = typeface; textSize = 32f }
            "áéíóúüñÁÉÍÓÚÜÑ¿¡0123456789".forEach { assertTrue("Fuente $id, carácter $it", paint.hasGlyph(it.toString())) }
        }
        listOf("licenses/outfit_ofl.txt", "licenses/exo2_ofl.txt").forEach {
            val license = compose.activity.assets.open(it).bufferedReader().use { reader -> reader.readText() }
            assertTrue(license.contains("SIL OPEN FONT LICENSE"))
        }
    }
}
