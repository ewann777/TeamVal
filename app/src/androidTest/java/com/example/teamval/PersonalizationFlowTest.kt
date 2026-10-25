package com.example.teamval

import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.text.input.ImeAction
import androidx.test.platform.app.InstrumentationRegistry
import com.example.teamval.data.repository.LocalPreferencesRepository
import com.example.teamval.data.repository.teamValPreferences
import com.example.teamval.domain.model.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PersonalizationFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val repository get() = LocalPreferencesRepository(InstrumentationRegistry.getInstrumentation().targetContext.teamValPreferences)

    @Before fun resetPreferences() = runBlocking {
        repository.saveProfile(PlayerProfile())
        repository.saveAppearance(AppAppearance())
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Perfil").fetchSemanticsNodes().isNotEmpty() }
    }

    @After fun restoreDefaults() = runBlocking {
        repository.saveProfile(PlayerProfile())
        repository.saveAppearance(AppAppearance())
    }

    private fun openProfile() { compose.onNodeWithText("Perfil").performClick() }
    private fun openAppearance() {
        openProfile()
        compose.onNodeWithText("Apariencia").performScrollTo().performClick()
    }

    private fun saved() {
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Cambios guardados en este dispositivo.").fetchSemanticsNodes().isNotEmpty() }
    }

    @Test fun profileEditingPersistsCosmeticsAndUpdatesStackIdentity() {
        openProfile()
        compose.onNodeWithText("Editar perfil").performScrollTo().performClick()
        compose.onNodeWithTag("profile_name").performScrollTo().performTextReplacement("")
        compose.onNodeWithTag("profile_name").performImeAction()
        compose.onNodeWithTag("save_preferences").performClick()
        compose.onNodeWithText("Revisa el nombre, la presentación y la biografía antes de guardar.").assertIsDisplayed()
        compose.onNodeWithTag("profile_name").performScrollTo().performTextReplacement("Astra#CL")
        compose.onNodeWithTag("profile_biography").performScrollTo().performTextReplacement("Me gusta jugar en equipo y aprender nuevos agentes.")
        compose.onNodeWithTag("profile_biography").performImeAction()
        compose.onNodeWithText("Kitsune").performScrollTo().performClick()
        compose.onNodeWithText("Doble órbita").performScrollTo().performClick()
        compose.onNodeWithText("Océano").performScrollTo().performClick()
        compose.onNodeWithText("Estratega").performScrollTo().performClick()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Océano").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("save_preferences").performClick()
        saved()
        compose.onNodeWithText("Volver").performClick()
        compose.onNodeWithText("Astra#CL").assertExists()
        val profile = runBlocking { repository.preferences.first().profile }
        assertEquals(AvatarStyle.Fox, profile.avatar)
        assertEquals(AvatarFrame.Double, profile.frame)
        assertEquals(ProfileBackground.Ocean, profile.background)
        assertTrue(ProfileBadge.Strategist in profile.badges)
        compose.onNodeWithText("Stacks").performClick()
        compose.onNodeWithText("Mis publicaciones").performScrollTo().performClick()
        compose.onNodeWithTag("stack_explorer").performScrollToNode(hasText("Organiza Astra#CL"))
        compose.onNodeWithText("Organiza Astra#CL").assertIsDisplayed()
    }

    @Test fun appearanceIsGlobalAndSurvivesActivityRecreation() {
        openAppearance()
        compose.onNodeWithText("Claro").performScrollTo().performClick()
        compose.onNodeWithText("Menta").performScrollTo().performClick()
        compose.onNodeWithText("Aurora").performScrollTo().performClick()
        compose.onNodeWithText("Editorial").performScrollTo().performClick()
        compose.onNodeWithTag("save_preferences").performClick()
        saved()
        compose.onNodeWithText("Volver").performClick()
        compose.onNodeWithText("Inicio").performClick()
        compose.onNodeWithText("Tu próximo equipo empieza aquí.").assertExists()
        assertTrue(compose.onRoot().captureToImage().toPixelMap()[8, 100].luminance() > 0.7f)
        compose.activityRule.scenario.recreate()
        assertTrue(compose.onRoot().captureToImage().toPixelMap()[8, 100].luminance() > 0.7f)
        openAppearance()
        compose.onNodeWithText("Claro").performScrollTo().assertIsSelected()
        compose.onNodeWithText("Editorial").performScrollTo().assertIsSelected()
        assertEquals(AppAppearance(AppThemeMode.Light, "88DCC4", AppBackground.Aurora, AppFont.Editorial), runBlocking { repository.preferences.first().appearance })
    }

    @Test fun invalidColorDoesNotSaveAndBackDiscardsPreview() {
        openAppearance()
        compose.onNodeWithText("Claro").performScrollTo().performClick()
        compose.onNodeWithTag("accent_hex").performScrollTo().performTextReplacement("ZZZZZZ")
        compose.onNodeWithTag("accent_hex").performImeAction()
        compose.onNodeWithTag("save_preferences").performClick()
        compose.onNodeWithText("Introduce un color válido de seis dígitos, por ejemplo #C4B5FD.").assertExists()
        assertEquals(AppAppearance(), runBlocking { repository.preferences.first().appearance })
        compose.activityRule.scenario.onActivity { activity -> activity.onBackPressedDispatcher.onBackPressed() }
        assertTrue(compose.onRoot().captureToImage().toPixelMap()[8, 100].luminance() < 0.2f)
        compose.onNodeWithText("Apariencia").performScrollTo().performClick()
        compose.onNodeWithText("Oscuro").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("accent_hex").assertTextContains("C4B5FD")
    }
}
