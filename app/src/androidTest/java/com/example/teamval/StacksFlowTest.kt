package com.example.teamval

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class StacksFlowTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    private fun openStacks() {
        compose.onNodeWithText("Encontrar compañeros").performScrollTo().performClick()
        compose.onNode(hasText("Buscar equipo") and hasAnyAncestor(hasTestTag("stack_explorer"))).assertIsDisplayed()
    }

    /** Las snackbars de éxito tapan el final de la pantalla: esperamos a que se retiren. */
    private fun waitUntilNoSnackbar() {
        compose.waitUntil(8_000) {
            compose.onAllNodesWithTag("snackbar_content").fetchSemanticsNodes().isEmpty()
        }
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

    /**
     * Las pestañas de grupo son un elemento más del listado: ya no son una
     * cabecera fija, porque un elemento fijo necesita un fondo opaco que
     * oculte las publicaciones al desplazar y ese fondo era justo el
     * rectángulo que había que eliminar. Al integrarse en el listado pueden
     * quedar fuera de composición y no existir en el árbol de semántica, así
     * que se vuelve al inicio antes de tocarlas. Lo que se comprueba aquí es
     * el cambio de grupo y la ausencia del panel de filtros, no la posición.
     */
    private fun scrollToCategoryTabs() {
        compose.onNodeWithTag("stack_explorer").performScrollToIndex(0)
    }

    @Test
    fun categoryTabsSwitchGroupsWithoutOpeningFilters() {
        openStacks()
        // Sin pestaña activa se ven todas las categorías: 5 publicaciones ajenas.
        compose.onNodeWithTag("stack_explorer").performScrollToNode(hasText("5 publicaciones"))
        compose.onNodeWithText("5 publicaciones").assertIsDisplayed()
        // Cambiar a Dúo no abre el panel de filtros.
        scrollToCategoryTabs()
        compose.onNodeWithTag("category_Duo").performScrollTo().performClick()
        compose.onNodeWithTag("stack_filters").assertDoesNotExist()
        compose.onNodeWithTag("stack_explorer").performScrollToNode(hasText("2 publicaciones"))
        compose.onNodeWithText("2 publicaciones").assertIsDisplayed()
        scrollToCategoryTabs()
        compose.onNodeWithTag("category_Duo").assertIsSelected()
        // Integrantes actuales, cupos y aportación al equipo de 5 quedan diferenciados.
        compose.onNodeWithTag("stack_explorer").performScrollToNode(hasText("Un dúo con buena comunicación"))
        compose.onNode(
            hasText("Integrantes: 1 de 2") and hasAnyAncestor(hasTestTag("post-demo-duo"))
        ).assertExists()
        compose.onNode(
            hasText("1 cupo disponible") and hasAnyAncestor(hasTestTag("post-demo-duo"))
        ).assertExists()
        compose.onNode(
            hasText("Este grupo cubre 2 de 5 jugadores y quedan 3 fuera para completar el equipo.") and
                hasAnyAncestor(hasTestTag("post-demo-duo"))
        ).assertExists()
        compose.onNodeWithTag("stack_explorer").performScrollToNode(hasText("Stack completo"))
        compose.onNodeWithText("Stack completo").assertExists()
        // Trío: solo la publicación ajena de 3 (la propia queda en «Mis publicaciones»).
        scrollToCategoryTabs()
        compose.onNodeWithTag("category_Trio").performScrollTo().performClick()
        compose.onNodeWithTag("stack_explorer").performScrollToNode(hasText("1 publicación"))
        compose.onNodeWithText("1 publicación").assertIsDisplayed()
        // Stack: grupos de 4 y 5 jugadores.
        scrollToCategoryTabs()
        compose.onNodeWithTag("category_Stack").performScrollTo().performClick()
        compose.onNodeWithTag("stack_explorer").performScrollToNode(hasText("2 publicaciones"))
        compose.onNodeWithText("2 publicaciones").assertIsDisplayed()
        compose.onNode(
            hasText("Integrantes: 3 de 5") and hasAnyAncestor(hasTestTag("post-demo-night"))
        ).assertExists()
        compose.onNode(
            hasText("2 cupos disponibles") and hasAnyAncestor(hasTestTag("post-demo-night"))
        ).assertExists()
        compose.onNode(
            hasText("Este grupo ya cubre los 5 jugadores del equipo") and
                hasAnyAncestor(hasTestTag("post-demo-night"))
        ).assertExists()
        // La selección sobrevive a la recreación de la actividad.
        compose.activityRule.scenario.recreate()
        scrollToCategoryTabs()
        compose.onNodeWithTag("category_Stack").assertIsSelected()
        // Volver a pulsar la pestaña activa muestra todos los grupos.
        compose.onNodeWithTag("category_Stack").performScrollTo().performClick()
        compose.onNodeWithTag("stack_explorer").performScrollToNode(hasText("5 publicaciones"))
        compose.onNodeWithText("5 publicaciones").assertIsDisplayed()
        // Los filtros adicionales siguen disponibles y se combinan con la categoría.
        compose.onNodeWithText("Filtrar stacks").performScrollTo().performClick()
        compose.onNodeWithTag("stack_filters").assertExists()
        choose("Todas las regiones", "LATAM Sur")
        scrollToCategoryTabs()
        compose.onNodeWithTag("category_Duo").performScrollTo().performClick()
        compose.onNodeWithTag("stack_explorer").performScrollToNode(hasText("1 publicación"))
        compose.onNodeWithText("1 publicación").assertIsDisplayed()
        compose.onNode(hasText("Limpiar filtros") and hasAnyAncestor(hasTestTag("stack_filters"))).performScrollTo().performClick()
        compose.onNodeWithTag("stack_explorer").performScrollToNode(hasText("2 publicaciones"))
        compose.onNodeWithText("2 publicaciones").assertIsDisplayed()
        scrollToCategoryTabs()
        compose.onNodeWithTag("category_Duo").performScrollTo().performClick()
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
        waitUntilNoSnackbar()
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
        compose.onNodeWithText("Encontrar compañeros").performScrollTo().performClick()
        compose.onNodeWithTag("stack_explorer").performScrollToNode(hasText("Solicitudes"))
        compose.onNodeWithText("Solicitudes").performClick()
        compose.onNodeWithText("Enviadas").performClick()
        compose.onNodeWithTag("stack_requests").performScrollToNode(hasText("Solicitud pendiente"))
        compose.onNodeWithText("Solicitud pendiente").assertIsDisplayed()
        compose.onNodeWithText("Un dúo con buena comunicación").assertExists()
    }
}
