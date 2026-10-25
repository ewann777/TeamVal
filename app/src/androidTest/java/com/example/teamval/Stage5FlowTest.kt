package com.example.teamval

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.printToLog
import org.junit.Assert.assertTrue
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters

/**
 * Etapa 5 end-to-end: tienda, compra, saldo insuficiente, inventario con
 * equipar/desequipar, integración con el perfil y recompensas.
 *
 * Con el Orchestrator (clearPackageData) cada prueba arranca con datos limpios
 * y el bono inicial de 500 monedas. Sin Orchestrator se deben ejecutar los
 * métodos individualmente, borrando los datos antes de cada uno.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class Stage5FlowTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    /** Los avisos (snackbars) tapan la zona inferior: esperar a que desaparezcan. */
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

    private fun click(tag: String) {
        waitNoticesGone()
        // La barra de navegación inferior no es desplazable: solo hacer clic.
        compose.onNodeWithTag(tag).performClick()
    }

    /** Espera a que un texto (posiblemente de snackbar) aparezca. */
    private fun waitUntilText(text: String, substring: Boolean = false) {
        compose.waitUntil(timeoutMillis = 8_000) {
            compose.onAllNodesWithText(text, substring = substring).fetchSemanticsNodes().isNotEmpty()
        }
    }

    /** Lleva [productTag] a la vista dentro de la cuadrícula [gridTag]. */
    private fun scrollGrid(gridTag: String, productTag: String) {
        compose.onNodeWithTag(gridTag).performScrollToNode(hasTestTag(productTag))
    }

    /** Toca una acción con etiqueta [label] dentro de la tarjeta [tag]. */
    private fun cardClick(tag: String, label: String) {
        waitNoticesGone()
        compose.onNode(hasText(label) and hasAnyAncestor(hasTestTag(tag)))
            .performScrollTo()
            .performClick()
    }

    /** Estado visible ("Disponible", "Adquirido", "Equipado", "Bloqueado"). */
    private fun stateInCard(gridTag: String, productTag: String): String? {
        scrollGrid(gridTag, productTag)
        val labels = listOf("Bloqueado", "Disponible", "Adquirido", "Equipado")
        for (label in labels) {
            // fetchSemanticsNodes() es internal en SemanticsNodeInteraction:
            // se usa la colección pública onAllNodes().
            val nodes = compose.onAllNodes(hasText(label) and hasAnyAncestor(hasTestTag(productTag))).fetchSemanticsNodes()
            if (nodes.isNotEmpty()) return label
        }
        return null
    }

    /**
     * Intenta comprar [productTag] y clasifica el aviso mostrado:
     * "rejected" (sin saldo), "owned" (ya era tuyo) o "bought".
     */
    private fun attemptPurchase(gridTag: String, productTag: String): String {
        scrollGrid(gridTag, productTag)
        cardClick(productTag, "Comprar")
        compose.onNodeWithText("Comprar ahora").performClick()
        compose.waitUntil(timeoutMillis = 8_000) {
            listOf(
                "No tienes saldo suficiente",
                "comprado",
                "ya es tuyo"
            ).any { snippet ->
                compose.onAllNodesWithText(snippet, substring = true).fetchSemanticsNodes().isNotEmpty()
            }
        }
        val outcome = when {
            compose.onAllNodesWithText("No tienes saldo suficiente", substring = true).fetchSemanticsNodes()
                .isNotEmpty() -> "rejected"

            compose.onAllNodesWithText("ya es tuyo", substring = true).fetchSemanticsNodes().isNotEmpty() -> "owned"
            else -> "bought"
        }
        waitNoticesGone()
        return outcome
    }

    private fun openShop() {
        click("nav_store")
        compose.onNodeWithTag("shop_balance").assertIsDisplayed()
    }

    // --- Exploración de la tienda --------------------------------------------
    @Test
    fun test01_shopShowsBalanceFeaturedCategoriesAndDetails() {
        openShop()
        compose.onNodeWithText("Destacados").assertExists()
        scrollGrid("shop_grid", "shop_categories")
        compose.onNodeWithText("Categorías").assertExists()

        // Las categorías deslizables filtran el catálogo.
        compose.onNodeWithText("Fondos de perfil").performScrollTo().performClick()
        scrollGrid("shop_grid", "product_bg_pastel")
        compose.onNodeWithTag("product_bg_pastel").assertExists()
        compose.onNodeWithTag("product_frame_lavanda").assertDoesNotExist()
        scrollGrid("shop_grid", "shop_categories")
        compose.onNodeWithText("Todo").performScrollTo().performClick()
        scrollGrid("shop_grid", "product_frame_lavanda")
        compose.onNodeWithTag("product_frame_lavanda").assertExists()

        // Vista previa del producto.
        cardClick("product_frame_lavanda", "Ver detalles")
        waitUntilText("Cerrar")
        compose.onNodeWithText("Cerrar").performClick()
    }

    // --- Monedero -------------------------------------------------------------
    @Test
    fun test02_walletShowsBalanceHistoryAndDisclaimer() {
        openShop()
        compose.onNodeWithText("Monedero").performClick()
        compose.onNodeWithTag("wallet_summary").assertIsDisplayed()
        compose.onNodeWithTag("wallet_list").performScrollToNode(hasText("Bono inicial", substring = true))
        compose.onAllNodesWithText("Bono inicial", substring = true).onFirst().assertExists()
        compose.onNode(hasText("sin valor real", substring = true)).assertExists()
        compose.onNodeWithTag("wallet_list").performScrollToNode(hasText("Historial de movimientos"))
        compose.onNode(hasText("Historial de movimientos")).assertExists()
    }

    // --- Compra + equipado + integración con el perfil ------------------------
    @Test
    fun test03_buyStickerEquipItAndSeeItAppliedOnProfile() {
        openShop()
        // Compra (o reconoce el artículo ya adquirido en una corrida previa).
        var stickerState = stateInCard("shop_grid", "product_sticker_gg")
        if (stickerState == "Disponible") {
            val outcome = attemptPurchase("shop_grid", "product_sticker_gg")
            assertTrue("Compra del sticker no resuelta: $outcome", outcome != "rejected")
        }
        stickerState = stateInCard("shop_grid", "product_sticker_gg")
        assertTrue(
            "El sticker debe figurar adquirido o equipado (fue: $stickerState)",
            stickerState == "Adquirido" || stickerState == "Equipado"
        )

        // Equipar si aún no lo está.
        if (stickerState != "Equipado") {
            cardClick("product_sticker_gg", "Equipar")
            waitNoticesGone()
        }
        assertTrue(
            "El sticker debe quedar equipado",
            stateInCard("shop_grid", "product_sticker_gg") == "Equipado"
        )

        // El cosmético aparece en el perfil real (integración con la etapa 3).
        click("nav_profile")
        compose.onNode(hasText("GG") and hasAnyAncestor(hasTestTag("player_profile"))).assertExists()
        compose.activityRule.scenario.recreate()
        waitUntilText("GG")
        compose.onNode(hasText("GG") and hasAnyAncestor(hasTestTag("player_profile"))).assertExists()
    }

    // --- Inventario -----------------------------------------------------------
    @Test
    fun test04_inventoryShowsStatesAndEquipsItems() {
        openShop()
        compose.onNodeWithText("Inventario").performClick()
        compose.onNodeWithTag("inventory_summary").assertIsDisplayed()

        var bgState = stateInCard("inventory_grid", "product_bg_pastel")
        if (bgState == "Disponible") {
            val outcome = attemptPurchase("inventory_grid", "product_bg_pastel")
            assertTrue("Compra del fondo no resuelta: $outcome", outcome != "rejected")
        }
        bgState = stateInCard("inventory_grid", "product_bg_pastel")
        assertTrue(
            "El fondo debe figurar adquirido o equipado (fue: $bgState)",
            bgState == "Adquirido" || bgState == "Equipado"
        )

        if (bgState != "Equipado") {
            cardClick("product_bg_pastel", "Equipar")
            waitNoticesGone()
        }
        assertTrue(
            "El fondo debe quedar equipado",
            stateInCard("inventory_grid", "product_bg_pastel") == "Equipado"
        )

        cardClick("product_bg_pastel", "Desequipar")
        waitNoticesGone()
        assertTrue(
            "Tras desequipar debe volver a Adquirido",
            stateInCard("inventory_grid", "product_bg_pastel") == "Adquirido"
        )
    }

    // --- Saldo insuficiente ---------------------------------------------------
    @Test
    fun test05_purchaseIsRejectedWhenBalanceIsNotEnough() {
        openShop()
        // Intenta comprar artículos caros hasta que el saldo no alcance.
        // La suma de la lista supera con holgura cualquier saldo posible.
        val candidates = listOf(
            "product_pet_bruno",      // 400
            "product_bundle_inicio",  // 500
            "product_card_especial",  // 250
            "product_bg_pastel",      // 150
            "product_frame_lavanda",  // 100
            "product_banner_diurno"   // 120
        )
        var rejectedTag: String? = null
        for (tag in candidates) {
            if (stateInCard("shop_grid", tag) != "Disponible") continue
            if (attemptPurchase("shop_grid", tag) == "rejected") {
                rejectedTag = tag
                break
            }
        }
        assertTrue(
            "Alguna compra debía rechazarse por saldo insuficiente",
            rejectedTag != null
        )
        // El artículo rechazado sigue disponible y sin adquirirse.
        scrollGrid("shop_grid", rejectedTag!!)
        assertTrue(
            "El artículo rechazado debe seguir Disponible",
            stateInCard("shop_grid", rejectedTag) == "Disponible"
        )
    }

    // --- Recompensas ----------------------------------------------------------
    @Test
    fun test06_rewardsClaimOnceAndKeepVerificationMissionsPending() {
        openShop()
        compose.onNodeWithText("Recompensas").performClick()
        compose.onNodeWithTag("rewards_available").assertExists()
        compose.onNodeWithTag("rewards_claimed").assertExists()
        compose.onNodeWithTag("rewards_pending").assertExists()

        // Las misiones de verificación no se reclaman ni se simulan cumplidas.
        compose.onNode(
            hasText("Requiere verificación") and hasAnyAncestor(hasTestTag("mission_m_sessions"))
        ).assertExists()
        compose.onNode(
            hasText("Reclamar") and hasAnyAncestor(hasTestTag("mission_m_sessions"))
        ).assertDoesNotExist()

        // Misión ficticia: simular progreso hasta completarla y reclamar una vez.
        repeat(3) {
            compose.onNode(
                hasText("Simular progreso") and hasAnyAncestor(hasTestTag("mission_m_scrims"))
            ).performScrollTo().performClick()
        }
        waitNoticesGone()
        compose.onNode(
            hasText("Reclamar") and hasAnyAncestor(hasTestTag("mission_m_scrims"))
        ).performScrollTo().performClick()
        waitNoticesGone()

        compose.onNode(
            hasText("Reclamada") and hasAnyAncestor(hasTestTag("mission_m_scrims"))
        ).assertExists()
        compose.onNode(
            hasText("Reclamar") and hasAnyAncestor(hasTestTag("mission_m_scrims"))
        ).assertDoesNotExist()
    }

    @Test
    fun test07_premiumIsPreviewOnlyAndUnownedVariantsStayLocked() {
        openShop()
        assertTrue(stateInCard("shop_grid", "product_prem_frame_animado") == "Bloqueado")
        compose.onNode(hasText("Próximamente") and hasAnyAncestor(hasTestTag("product_prem_frame_animado")) and
            androidx.compose.ui.test.hasClickAction()).assertIsNotEnabled()
        cardClick("product_prem_frame_animado", "Ver detalles")
        compose.onNode(hasText("$990 CLP") and hasAnyAncestor(isDialog())).assertExists()
        compose.onNodeWithText("Comprar ahora").assertDoesNotExist()
        compose.onNodeWithText("Cerrar").performClick()
        click("nav_profile")
        compose.onNodeWithText("Editar perfil").performScrollTo().performClick()
        compose.onNodeWithText("Lavanda · Tienda").performScrollTo().assertIsNotEnabled()
    }

    @Test
    fun test08_teamCosmeticUsesExistingSaveAndCancelFlow() {
        click("nav_scrims")
        compose.onNodeWithTag("create_team").performScrollTo().performClick()
        compose.onNodeWithTag("team_name").performTextInput("Equipo Tienda")
        compose.onNodeWithTag("team_tag").performTextInput("TND")
        compose.onNodeWithTag("team_description").performTextInput("Equipo para probar cosméticos comprados.")
        compose.onNodeWithTag("team_description").performImeAction()
        compose.onNodeWithTag("save_team").performScrollTo().performClick()
        openShop()
        assertTrue(attemptPurchase("shop_grid", "product_team_deco_atardecer") == "bought")
        cardClick("product_team_deco_atardecer", "Abrir editor de equipo")
        compose.onNodeWithTag("team_cosmetic_team_deco_atardecer").performScrollTo().performClick()
        compose.onNodeWithTag("team_cosmetic_team_deco_atardecer").assertIsSelected()
        compose.onNodeWithTag("cancel_identity").performScrollTo().performClick()
        waitNoticesGone()
        assertTrue(stateInCard("shop_grid", "product_team_deco_atardecer") == "Adquirido")
        cardClick("product_team_deco_atardecer", "Abrir editor de equipo")
        compose.onNodeWithTag("team_cosmetic_team_deco_atardecer").performScrollTo().assertIsNotSelected()
        compose.onNodeWithTag("team_cosmetic_team_deco_atardecer").performClick()
        compose.onNodeWithTag("save_identity").performScrollTo().performClick()
        waitNoticesGone()
        assertTrue(stateInCard("shop_grid", "product_team_deco_atardecer") == "Equipado")
    }

    @Test
    fun test09_profileRewardAndBundleKeepExactBalanceAndOwnership() {
        click("nav_profile")
        compose.onNodeWithText("Editar perfil").performScrollTo().performClick()
        compose.onNodeWithTag("profile_headline").performScrollTo().performTextReplacement("Mi perfil de Teamval")
        compose.onNodeWithTag("profile_headline").performImeAction()
        compose.onNodeWithTag("save_preferences").performClick()
        waitUntilText("Cambios guardados en este dispositivo.")
        compose.onNodeWithText("Volver").performClick()
        openShop()
        compose.onNodeWithText("Recompensas").performClick()
        cardClick("mission_m_profile", "Reclamar")
        waitNoticesGone()
        compose.onNode(hasText("Reclamada") and hasAnyAncestor(hasTestTag("mission_m_profile"))).assertExists()
        click("nav_home")
        compose.onNodeWithTag("home_balance").assertTextContains("600")
        click("nav_store")
        // La pestaña conserva la pantalla Recompensas; volver a la tienda.
        compose.onNodeWithTag("rewards_available").assertExists()
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithTag("shop_grid").assertExists()
        assertTrue(attemptPurchase("shop_grid", "product_bundle_inicio") == "bought")
        listOf("frame_lavanda", "bg_pastel", "badge_estrella", "bundle_inicio").forEach {
            assertTrue(stateInCard("shop_grid", "product_$it") == "Adquirido")
        }
        compose.onNode(hasText("Comprar") and hasAnyAncestor(hasTestTag("product_bundle_inicio"))).assertDoesNotExist()
        click("nav_home")
        compose.onNodeWithTag("home_balance").assertTextContains("100")
    }
}
