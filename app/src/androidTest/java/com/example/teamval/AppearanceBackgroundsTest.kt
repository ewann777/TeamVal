package com.example.teamval

import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.teamval.data.repository.LocalPreferencesRepository
import com.example.teamval.data.repository.teamValPreferences
import com.example.teamval.domain.model.AppAppearance
import com.example.teamval.domain.model.AppBackground
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.math.abs

/**
 * Verificación por píxeles de la mejora de apariencia: los tres fondos tienen
 * que distinguirse de verdad y las miniaturas de selección tienen que representar
 * fielmente el fondo real (se dibujan con la misma implementación,
 * `AppBackgroundView`).
 *
 * - Liso: color uniforme, sin ornamentos.
 * - Aurora: degradado amplio y suave, sin destellos puntuales.
 * - Estrellas: campo escaso y delicado de puntos pequeños; los umbrales de
 *   destello y diferencia media reflejan ese nuevo diseño (menos estrellas y
 *   más tenues que la versión anterior), no el campo denso de antes.
 */
class AppearanceBackgroundsTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val repository get() = LocalPreferencesRepository(
        InstrumentationRegistry.getInstrumentation().targetContext.teamValPreferences
    )

    @Before
    fun resetAppearance() = runBlocking { repository.saveAppearance(AppAppearance()) }

    @After
    fun restoreAppearance() = runBlocking { repository.saveAppearance(AppAppearance()) }

    private fun openAppearance() {
        compose.onNodeWithTag("nav_profile").performClick()
        compose.onNodeWithTag("profile_appearance").performScrollTo().performClick()
    }

    /** Límites del recorte: deja fuera el filete y las esquinas redondeadas. */
    private fun bounds(map: PixelMap): IntArray = intArrayOf(
        (map.width * 0.04f).toInt(), (map.width * 0.96f).toInt(),
        (map.height * 0.06f).toInt(), (map.height * 0.94f).toInt()
    )

    /**
     * Luminancia de un píxel, o `null` si queda fuera de la forma: las esquinas
     * de la miniatura son transparentes y no deben contar como puntos negros.
     */
    private fun luminanceAt(map: PixelMap, x: Int, y: Int): Float? =
        map[x, y].takeIf { it.alpha > 0.5f }?.luminance()

    /** Miniatura con el margen mínimo que esconde bordes y esquinas. */
    private fun thumb(tag: String): PixelMap {
        // La tarjeta es seleccionable y fusiona su semántica, así que la miniatura
        // interior solo aparece en el árbol sin fusionar.
        val node = compose.onNodeWithTag(tag, useUnmergedTree = true)
        node.performScrollTo()
        compose.waitForIdle()
        return node.captureToImage().toPixelMap()
    }

    private fun centerCrop(map: PixelMap): List<Float> {
        val (x0, x1, y0, y1) = bounds(map)
        val values = ArrayList<Float>((x1 - x0) * (y1 - y0))
        for (y in y0 until y1) for (x in x0 until x1) luminanceAt(map, x, y)?.let { values.add(it) }
        return values
    }

    private fun variance(values: List<Float>): Float {
        val mean = values.average().toFloat()
        return values.fold(0f) { acc, v -> acc + (v - mean) * (v - mean) } / values.size
    }

    /** Píxeles con un salto brusco frente a su vecino izquierdo (destellos). */
    private fun sparkleCount(map: PixelMap): Int {
        val (x0, x1, y0, y1) = bounds(map)
        var count = 0
        for (y in y0 until y1) for (x in x0 + 1 until x1) {
            val current = luminanceAt(map, x, y) ?: continue
            val left = luminanceAt(map, x - 1, y) ?: continue
            if (abs(current - left) > 0.08f) count++
        }
        return count
    }

    /** Diferencia media de luminosidad entre dos miniaturas. */
    private fun meanDiff(a: PixelMap, b: PixelMap): Float {
        val av = centerCrop(a)
        val bv = centerCrop(b)
        return av.indices.fold(0f) { acc, i -> acc + abs(av[i] - bv[i]) } / av.size
    }

    @Test
    fun theThreeBackgroundsDifferAndTheirThumbnailsAreFaithful() {
        openAppearance()
        listOf(R.string.app_background_plain, R.string.app_background_aurora, R.string.app_background_stars)
            .forEach { compose.onNodeWithText(compose.activity.getString(it)).performScrollTo().assertExists() }

        val plain = thumb("background_thumb_Plain")
        val aurora = thumb("background_thumb_Aurora")
        val stars = thumb("background_thumb_Stars")

        val plainVar = variance(centerCrop(plain))
        val auroraVar = variance(centerCrop(aurora))
        val starsVar = variance(centerCrop(stars))
        assertTrue("Liso debe ser uniforme, varianza=$plainVar", plainVar < 0.0005f)
        assertTrue("Aurora debe mostrar degradado, varianza=$auroraVar", auroraVar > plainVar + 0.0004f)
        assertTrue("Estrellas debe mostrar puntos, varianza=$starsVar", starsVar > plainVar + 0.0004f)

        val plainSparks = sparkleCount(plain)
        val auroraSparks = sparkleCount(aurora)
        val starsSparks = sparkleCount(stars)
        assertTrue("Liso no tiene destellos, tenía=$plainSparks", plainSparks < 50)
        assertTrue("Aurora es un degradado suave, destellos=$auroraSparks", auroraSparks < 50)
        assertTrue("Estrellas dibuja puntos, destellos=$starsSparks", starsSparks > 30)
        assertTrue(
            "Estrellas multiplica los destellos: estrellas=$starsSparks, aurora=$auroraSparks, liso=$plainSparks",
            starsSparks > 4 * maxOf(auroraSparks, plainSparks, 6)
        )

        assertTrue("Liso y Aurora difieren", meanDiff(plain, aurora) > 0.01f)
        // Estrellas es ahora un campo escaso y tenue: la diferencia media baja,
        // pero sigue siendo claramente distinta de un fondo liso o degradado.
        assertTrue("Aurora y Estrellas difieren", meanDiff(aurora, stars) > 0.002f)
        assertTrue("Liso y Estrellas difieren", meanDiff(plain, stars) > 0.002f)
    }

    @Test
    fun starsSelectionPersistsAfterRecreatingTheActivity() {
        openAppearance()
        compose.onNodeWithTag("background_Stars").performScrollTo().performClick()
        compose.onNodeWithTag("background_Stars").assertIsSelected()
        // La vista previa no escribe hasta que se pulsa Guardar.
        assertEquals(AppBackground.Plain, runBlocking { repository.preferences.first().appearance.background })
        compose.onNodeWithTag("save_preferences").performClick()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("¡Apariencia actualizada!").fetchSemanticsNodes().isNotEmpty()
        }
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("background_Stars").performScrollTo().assertIsSelected()
        assertEquals(AppBackground.Stars, runBlocking { repository.preferences.first().appearance.background })
        // Restablecer solo afecta a la vista previa: lo guardado sigue en Estrellas.
        compose.onNodeWithText(compose.activity.getString(R.string.appearance_reset)).performScrollTo().performClick()
        compose.onNodeWithTag("background_Plain").performScrollTo().assertIsSelected()
        assertEquals(AppBackground.Stars, runBlocking { repository.preferences.first().appearance.background })
    }
}
