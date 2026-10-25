package com.example.teamval

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class StacksFlowTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    private fun openStacks() {
        compose.onNodeWithText("Descubrir Stacks").performScrollTo().performClick()
        compose.onNodeWithText("Encuentra tu equipo").assertIsDisplayed()
    }

    private fun choose(current: String, option: String) {
        compose.onNode(hasText(current) and hasClickAction()).performScrollTo().performClick()
        compose.onNode(hasText(option) and hasAnyAncestor(isPopup())).performClick()
    }

    @Test
    fun filtersCombineShowEmptyStateAndSurviveRecreation() {
        openStacks()
        compose.onNodeWithText("Filtrar stacks").performScrollTo().performClick()
        choose("Todos los rangos", "Oro")
        choose("Todas las regiones", "LATAM Sur")
        choose("Todas las modalidades", "Competitivo")
        compose.onNodeWithTag("stack_explorer").performScrollToNode(hasText("1 publicación"))
        compose.onNodeWithText("1 publicación").assertIsDisplayed()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("stack_explorer").performScrollToNode(hasText("1 publicación"))
        compose.onNodeWithText("1 publicación").assertIsDisplayed()
        choose("LATAM Sur", "Europa")
        compose.onNodeWithTag("stack_explorer").performScrollToNode(hasText("Tu próximo equipo está por llegar"))
        compose.onNodeWithText("Tu próximo equipo está por llegar").assertIsDisplayed()
        // Return to the filter panel before clearing (the empty-state also has a clear action).
        compose.onNodeWithTag("stack_explorer").performScrollToNode(hasText("Todos los rangos") or hasText("Rango"))
        compose.onNode(hasText("Limpiar filtros") and hasAnyAncestor(hasTestTag("stack_filters"))).performScrollTo().performClick()
        compose.onNodeWithTag("stack_explorer").performScrollToNode(hasText("5 publicaciones"))
        compose.onNodeWithText("5 publicaciones").assertIsDisplayed()
    }

    @Test
    fun createValidateRestoreDraftAndManageSimulatedRequests() {
        openStacks()
        compose.onNodeWithText("Crear publicación").performClick()
        compose.onNodeWithTag("publish_stack").performScrollTo().performClick()
        compose.onNodeWithText(compose.activity.getString(R.string.title_error)).assertExists()
        compose.onNodeWithTag("post_title").performScrollTo().performTextInput("Equipo de prueba")
        compose.onNodeWithTag("post_description").performScrollTo().performTextInput("Buscamos compañeros para jugar con buena comunicación.")
        compose.onNodeWithTag("post_description").performImeAction()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("post_title").assertTextContains("Equipo de prueba")
        compose.onNodeWithTag("publish_stack").performScrollTo().performClick()
        compose.onNodeWithTag("stack_explorer").performScrollToNode(hasText("Equipo de prueba"))
        compose.onNodeWithText("Equipo de prueba").assertIsDisplayed()
        compose.onNodeWithText("Gestionar solicitudes (0 pendientes)").performScrollTo().performClick()
        compose.onNodeWithText("Simular solicitud").performClick()
        compose.onNodeWithTag("stack_requests").performScrollToNode(hasText("Aceptar"))
        compose.onNodeWithText("Aceptar").performClick()
        compose.onNodeWithText("Solicitud aceptada").assertExists()
        compose.onNodeWithText("2 de 5 jugadores").assertExists()
        compose.onNodeWithTag("stack_requests").performScrollToNode(hasText("Simular solicitud"))
        compose.onNodeWithText("Simular solicitud").performClick()
        compose.onNodeWithTag("stack_requests").performScrollToNode(hasText("Rechazar"))
        compose.onNodeWithText("Rechazar").performClick()
        compose.onNodeWithText("Solicitud rechazada").assertExists()
        compose.activityRule.scenario.onActivity { activity -> activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithTag("stack_explorer").performScrollToNode(hasText("Equipo de prueba"))
        compose.onNodeWithText("Equipo de prueba").assertIsDisplayed()
    }

    @Test
    fun sentRequestCannotBeDuplicatedAndSurvivesTabChanges() {
        openStacks()
        compose.onNodeWithTag("stack_explorer").performScrollToNode(hasText("Solicitar unirme"))
        compose.onNode(hasText("Solicitar unirme") and hasAnyAncestor(hasTestTag("post-demo-duo"))).performClick()
        compose.onNodeWithText("Solicitud pendiente").assertIsNotEnabled()
        compose.onNodeWithText("Inicio").performClick()
        compose.onNodeWithText("Descubrir Stacks").performScrollTo().performClick()
        compose.onNodeWithTag("stack_explorer").performScrollToNode(hasText("Solicitudes"))
        compose.onNodeWithText("Solicitudes").performClick()
        compose.onNodeWithText("Enviadas").performClick()
        compose.onNodeWithTag("stack_requests").performScrollToNode(hasText("Solicitud pendiente"))
        compose.onNodeWithText("Solicitud pendiente").assertIsDisplayed()
        compose.onNodeWithText("Un dúo con buena comunicación").assertExists()
    }
}
