package com.example.teamval

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

/**
 * End-to-end flows for stage 4.1: rating a completed scrim (with its
 * anti-abuse restrictions), team identity customization with save/reset/cancel
 * and the logo editor with persistence.
 */
class Stage41FlowTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    /**
     * Notices (snackbars) overlay the lower area of the screen where the main
     * actions live, swallowing their taps until they auto-dismiss.
     */
    private fun waitNoticesGone() {
        try {
            compose.waitUntil(timeoutMillis = 8_000) {
                compose.onAllNodesWithTag("snackbar_content").fetchSemanticsNodes().isEmpty()
            }
        } catch (t: Throwable) {
            compose.onAllNodesWithTag("snackbar_content").onFirst().printToLog("STUCK_SNACKBAR")
            throw t
        }
    }

    /** Waits for notices to clear, scrolls the tagged action into view and taps it. */
    private fun click(tag: String) {
        waitNoticesGone()
        compose.onNodeWithTag(tag).performScrollTo().performClick()
    }

    private fun openScrims() {
        compose.onNodeWithText("Un espacio para los próximos desafíos de tu equipo.")
            .performScrollTo().performClick()
        compose.onNodeWithText("Equipos y scrims").assertIsDisplayed()
    }

    private fun createTeam(name: String, tag: String) {
        click("create_team")
        compose.onNodeWithTag("team_name").performTextInput(name)
        compose.onNodeWithTag("team_tag").performTextInput(tag)
        compose.onNodeWithTag("team_description")
            .performTextInput("Equipo de prueba para la verificación de la etapa 4.1.")
        click("save_team")
        waitNoticesGone()
    }

    /** Simulates an incoming proposal, accepts it and registers a 13–9 result. */
    private fun completeIncomingScrim() {
        click("segment_scrims")
        compose.onNodeWithText("Simular propuesta").performScrollTo().performClick()
        click("manage_scrim")
        click("accept_scrim")
        compose.onNodeWithTag("score_home").performScrollTo().performTextInput("13")
        compose.onNodeWithTag("score_away").performTextInput("9")
        click("complete_scrim")
        waitNoticesGone()
    }

    @Test
    fun rateCompletedScrimAndPreventDuplicates() {
        openScrims()
        createTeam("Equipo Prisma", "PRS")
        completeIncomingScrim()

        click("rate_scrim")

        // The five categories measure conduct, not skill.
        listOf("Respeto", "Seriedad", "Puntualidad", "Comunicación", "Deportividad")
            .forEach { category ->
                compose.onNodeWithText(category).assertExists()
            }

        // Star rows are editable (defaults are 3 stars).
        compose.onNodeWithTag("star_5_respect").performScrollTo().performClick()
        compose.onNodeWithTag("star_1_sportsmanship").performScrollTo().performClick()

        compose.onNodeWithTag("rating_comment")
            .performTextInput("Buen trato, muy puntuales y con buena comunicación.")
        click("submit_rating")

        compose.onNodeWithTag("rating_sent").assertExists()
        compose.onNodeWithText("Valoración enviada").assertExists()

        click("rating_back")

        // Back on the scrim detail: the single allowed rating is consumed.
        compose.onNodeWithText("Ya valoraste a este equipo en esta scrim.")
            .performScrollTo().assertExists()
        compose.onNodeWithTag("rate_scrim").assertDoesNotExist()
    }

    @Test
    fun ratingFormEnforcesCommunityCommentRules() {
        openScrims()
        createTeam("Equipo Prisma", "PRS")
        completeIncomingScrim()

        click("rate_scrim")
        compose.onNodeWithTag("rating_comment").performTextInput("malo")
        click("submit_rating")

        // The form stays open showing the rule violation; nothing was sent.
        compose.onNodeWithText("El comentario debe tener al menos 10 caracteres.").assertExists()
        compose.onNodeWithTag("submit_rating").assertExists()
        compose.onNodeWithTag("rating_sent").assertDoesNotExist()
    }

    @Test
    fun identityCustomizationSavesResetsAndCancels() {
        openScrims()
        createTeam("Equipo Prisma", "PRS")
        compose.onNodeWithText("Ver mi equipo").performScrollTo().performClick()

        // Own profile shows reputation (empty) and scrim history panels.
        compose.onNodeWithTag("reputation_card").performScrollTo().assertExists()
        compose.onNodeWithText("Este equipo aún no tiene valoraciones.").assertExists()
        compose.onNodeWithTag("scrim_history").assertExists()

        click("customize_team")
        compose.onNodeWithTag("identity_preview").assertExists()

        // 1) Save: the motto is persisted and rendered on the profile.
        compose.onNodeWithTag("identity_motto").performTextInput("Somos la nueva generación")
        click("save_identity")
        waitNoticesGone()
        compose.onNodeWithText("Somos la nueva generación").performScrollTo().assertExists()

        // 2) Reset: an unsaved edit is restored from the saved identity.
        click("customize_team")
        compose.onNodeWithTag("identity_motto").performTextInput(" Lema reemplazado")
        click("reset_identity")
        click("save_identity")
        waitNoticesGone()
        compose.onNodeWithText("Somos la nueva generación").performScrollTo().assertExists()
        compose.onNodeWithText(
            "Somos la nueva generación Lema reemplazado", substring = true
        ).assertDoesNotExist()
        compose.onNodeWithText(
            "Lema reemplazadoSomos la nueva generación", substring = true
        ).assertDoesNotExist()

        // 3) Cancel: a discarded edit never reaches the saved identity.
        click("customize_team")
        compose.onNodeWithTag("identity_motto").performTextInput(" Lema descartado")
        click("cancel_identity")
        waitNoticesGone()
        compose.onNodeWithText("Somos la nueva generación").performScrollTo().assertExists()

        click("customize_team")
        compose.onNodeWithText("Lema descartado", substring = true).assertDoesNotExist()
        click("cancel_identity")
    }

    @Test
    fun logoDesignSavesAndRestores() {
        openScrims()
        createTeam("Equipo Prisma", "PRS")
        compose.onNodeWithText("Ver mi equipo").performScrollTo().performClick()
        click("customize_team")
        click("edit_logo")

        // Default design and live preview summary.
        compose.onNodeWithTag("logo_preview").assertExists()
        compose.onNodeWithText("Estrella · Circular").assertExists()

        // Change symbol and shape through the editor controls.
        compose.onNodeWithText("Rayo").performScrollTo().performClick()
        compose.onNodeWithText("Cuadrado").performScrollTo().performClick()
        compose.onNodeWithText("Rayo · Cuadrado").assertExists()

        click("save_logo")
        waitNoticesGone()

        // Back on the profile editor; reopening the logo editor shows the saved design.
        compose.onNodeWithTag("identity_preview").assertExists()
        click("edit_logo")
        compose.onNodeWithText("Rayo · Cuadrado").assertExists()
    }

    @Test
    fun rivalProfileShowsReputationExample() {
        openScrims()
        compose.onNodeWithText("Eclipse Esports").performScrollTo().assertExists()
        compose.onNode(
            hasText("Ver equipo") and hasAnyAncestor(hasTestTag("rival_card_team-eclipse"))
        ).performScrollTo().performClick()

        // Overall example score, count and per-category averages of the demo.
        compose.onNodeWithTag("reputation_card").performScrollTo().assertExists()
        compose.onNodeWithText("4,8/5 estrellas").performScrollTo().assertExists()
        compose.onNodeWithText("10 valoraciones").assertExists()
        compose.onNodeWithText("Respeto · 4,9/5").assertExists()
        compose.onNodeWithText("Comunicación · 4,6/5").assertExists()
        compose.onNodeWithText("Deportividad · 5,0/5").assertExists()

        // Scrim history of confirmed and completed matches.
        compose.onNodeWithTag("scrim_history").performScrollTo().assertExists()
        compose.onNodeWithText("Historial de scrims").assertExists()
        compose.onNodeWithText("13 scrims registradas").assertExists()
    }
}
