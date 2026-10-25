package com.example.teamval

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

/**
 * End-to-end flows for stage 4: team creation, scrim proposals, simulated incoming
 * proposals and result registration.
 */
class TeamFlowTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    private fun openScrims() {
        compose.onNodeWithText("Un espacio para los próximos desafíos de tu equipo.")
            .performScrollTo().performClick()
        compose.onNodeWithText("Equipos y scrims").assertIsDisplayed()
    }

    private fun createTeam(name: String, tag: String) {
        compose.onNodeWithTag("create_team").performScrollTo().performClick()
        compose.onNodeWithTag("team_name").performTextInput(name)
        compose.onNodeWithTag("team_tag").performTextInput(tag)
        compose.onNodeWithTag("team_description")
            .performTextInput("Equipo de prueba para la verificación de la etapa 4.")
        compose.onNodeWithTag("save_team").performScrollTo().performClick()
    }

    @Test
    fun teamFormValidatesRequiredFields() {
        openScrims()
        compose.onNodeWithTag("create_team").performScrollTo().performClick()
        compose.onNodeWithTag("save_team").performScrollTo().performClick()
        compose.onNodeWithText("Escribe un nombre de entre 3 y 24 caracteres, sin contar espacios extremos.")
            .assertExists()
        compose.onNodeWithText("Volver a Scrims").performClick()
        compose.onNodeWithText("Crea tu primer equipo").assertIsDisplayed()
    }

    @Test
    fun createTeamAndProposeScrim() {
        openScrims()
        createTeam("Equipo Prisma", "PRS")

        compose.onNodeWithText("Ver mi equipo").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("segment_scrims").performClick()
        compose.onNodeWithTag("create_scrim").performClick()

        compose.onNode(hasText("Selecciona un equipo rival") and hasClickAction())
            .performScrollTo().performClick()
        compose.onNode(hasText("Eclipse Esports") and hasAnyAncestor(isPopup())).performClick()
        compose.onNodeWithTag("scrim_schedule").performTextInput("Sábado 19:00")
        compose.onNodeWithTag("send_scrim").performScrollTo().performClick()

        compose.onNodeWithText("Equipo Prisma vs Eclipse Esports").performScrollTo().assertExists()
        compose.onNodeWithTag("manage_scrim").performScrollTo().performClick()
        compose.onNodeWithText("El equipo rival aún no ha respondido a tu propuesta.").assertExists()
        compose.onNodeWithTag("accept_scrim").assertDoesNotExist()
        compose.onNodeWithTag("cancel_scrim").assertExists()
    }

    @Test
    fun simulateIncomingAcceptAndCompleteScrim() {
        openScrims()
        createTeam("Equipo Nova", "NVB")
        compose.onNodeWithTag("segment_scrims").performClick()

        compose.onNodeWithText("Simular propuesta").performScrollTo().performClick()
        compose.onNodeWithTag("manage_scrim").performScrollTo().performClick()

        compose.onNodeWithTag("accept_scrim").performClick()
        compose.onNodeWithTag("accept_scrim").assertDoesNotExist()
        compose.onNodeWithTag("score_home").performScrollTo().performTextInput("13")
        compose.onNodeWithTag("score_away").performTextInput("9")
        compose.onNodeWithTag("complete_scrim").performScrollTo().performClick()

        compose.onNodeWithText("13 – 9").performScrollTo().assertExists()
        compose.onNodeWithTag("complete_scrim").assertDoesNotExist()
    }

    @Test
    fun receiverCanDeclineIncomingProposal() {
        openScrims()
        createTeam("Equipo Delta", "DLT")
        compose.onNodeWithTag("segment_scrims").performClick()
        compose.onNodeWithText("Simular propuesta").performScrollTo().performClick()
        compose.onNodeWithTag("manage_scrim").performScrollTo().performClick()

        compose.onNodeWithTag("decline_scrim").performScrollTo().performClick()
        compose.onNodeWithTag("accept_scrim").assertDoesNotExist()
        compose.onNodeWithText("Esta scrim ya está finalizada.").assertExists()
        compose.onNodeWithText("Volver a Scrims").performClick()
        compose.onNodeWithText("Cancelada").assertExists()
    }
}
