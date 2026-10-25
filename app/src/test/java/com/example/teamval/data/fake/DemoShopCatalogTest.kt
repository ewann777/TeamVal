package com.example.teamval.data.fake

import com.example.teamval.domain.model.AvatarFrame
import com.example.teamval.domain.model.EquipTarget
import com.example.teamval.domain.model.ItemCategory
import com.example.teamval.domain.model.MissionKind
import com.example.teamval.domain.model.PlayerProfile
import com.example.teamval.domain.model.ProfileBackground
import com.example.teamval.domain.model.ProfileBadge
import com.example.teamval.domain.model.ProfileBanner
import com.example.teamval.domain.model.ProfileCardStyle
import com.example.teamval.domain.model.ProfilePet
import com.example.teamval.domain.model.ProfileSticker
import com.example.teamval.domain.model.MissionRules
import com.example.teamval.domain.model.TeamBackground
import com.example.teamval.domain.model.TeamBadge
import com.example.teamval.domain.model.ProfileFrame
import com.example.teamval.domain.model.VisualThemePresets
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Integridad del catálogo demo y de las misiones de la etapa 5: identificadores
 * únicos, precios coherentes, vínculos válidos y economía balanceada.
 */
class DemoShopCatalogTest {

    private val catalog = DemoShopCatalog.items

    private inline fun <reified T : Enum<T>> enumByNameOrNull(name: String): T? =
        enumValues<T>().firstOrNull { it.name == name }

    @Test
    fun idsAreUniqueAndPresent() {
        assertEquals(catalog.size, catalog.map { it.id }.toSet().size)
        assertTrue(catalog.all { it.id.isNotBlank() })
        assertTrue(catalog.all { it.name.isNotBlank() && it.description.isNotBlank() })
        assertNotNull(DemoShopCatalog.find("frame_lavanda"))
        assertNull(DemoShopCatalog.find("no_existe"))
    }

    @Test
    fun everyItemHasExactlyOnePriceAndPositiveAmount() {
        catalog.forEach { item ->
            val hasCoins = item.priceCoins != null
            val hasClp = item.premiumPriceClp != null
            assertTrue("${item.id}: debe tener un solo tipo de precio", hasCoins xor hasClp)
            (item.priceCoins ?: item.premiumPriceClp)?.let {
                assertTrue("${item.id}: el precio debe ser positivo", it > 0)
            }
            assertEquals("${item.id}: solo un destino de precio", hasClp, item.isPremium)
        }
    }

    @Test
    fun allElevenCategoriesAreRepresented() {
        assertEquals(
            "El catálogo debe cubrir las 11 categorías",
            ItemCategory.entries.size,
            catalog.map { it.category }.toSet().size
        )
    }

    @Test
    fun examplePricesMatchTheDesignBrief() {
        assertEquals(100, DemoShopCatalog.find("frame_lavanda")?.priceCoins)
        assertEquals(150, DemoShopCatalog.find("bg_pastel")?.priceCoins)
        assertEquals(80, DemoShopCatalog.find("badge_estrella")?.priceCoins)
        assertEquals(250, DemoShopCatalog.find("card_especial")?.priceCoins)
        assertEquals(400, DemoShopCatalog.find("pet_bruno")?.priceCoins)
        assertEquals(990, DemoShopCatalog.find("prem_frame_animado")?.premiumPriceClp)
        assertEquals(1490, DemoShopCatalog.find("prem_bg_fondo")?.premiumPriceClp)
        assertEquals(1990, DemoShopCatalog.find("prem_sticker_pack")?.premiumPriceClp)
        assertEquals(2990, DemoShopCatalog.find("prem_theme_pack")?.premiumPriceClp)
    }

    @Test
    fun freeItemsAreAffordableWithTheStartingBonusAndRewards() {
        val cheapOnes = catalog.filter { it.priceCoins != null }
        // Con el bono de 500 y las recompensas gratuitas se alcanzan varios artículos.
        val cheapest = cheapOnes.minOf { it.priceCoins ?: Int.MAX_VALUE }
        assertTrue("El artículo más barato debe ser alcanzable", cheapest <= DemoShopCatalog.StartingBonus)
        assertTrue(
            "La suma de recompensas gratuitas supera el paquete de inicio",
            DemoRewardData.missions.sumOf { it.rewardCoins } >= DemoShopCatalog.StartingBonus
        )
        // Un jugador nuevo puede comprar al menos tres artículos sin pagar.
        val affordable = cheapOnes.count { (it.priceCoins ?: 0) <= DemoShopCatalog.StartingBonus / 3 }
        assertTrue("Debe haber varios artículos muy accesibles", affordable >= 3)
    }

    @Test
    fun bundleContentsExistAndAreValid() {
        catalog.filter { it.isBundle }.forEach { bundle ->
            assertTrue("${bundle.id}: un paquete debe incluir artículos", bundle.contains.isNotEmpty())
            assertTrue("${bundle.id}: un paquete no se vincula a un campo", bundle.binding == null)
            bundle.contains.forEach { contentId ->
                assertNotNull("${bundle.id}: contenido $contentId inexistente", DemoShopCatalog.find(contentId))
                assertFalse("${bundle.id}: no puede contenerse a sí mismo", contentId == bundle.id)
            }
            assertEquals(
                "${bundle.id}: contenido duplicado",
                bundle.contains.size,
                bundle.contains.toSet().size
            )
        }
    }

    @Test
    fun bindingsPointToRealEnumVariants() {
        catalog.mapNotNull { it.binding }.forEach { binding ->
            when (binding.target) {
                EquipTarget.Profile -> {
                    val valid = when (binding.field) {
                        com.example.teamval.domain.model.EquipField.AvatarFrame ->
                            enumByNameOrNull<AvatarFrame>(binding.variant) != null
                        com.example.teamval.domain.model.EquipField.ProfileBackground ->
                            enumByNameOrNull<ProfileBackground>(binding.variant) != null
                        com.example.teamval.domain.model.EquipField.ProfileBanner ->
                            enumByNameOrNull<ProfileBanner>(binding.variant) != null
                        com.example.teamval.domain.model.EquipField.ProfileSticker ->
                            enumByNameOrNull<ProfileSticker>(binding.variant) != null
                        com.example.teamval.domain.model.EquipField.ProfilePet ->
                            enumByNameOrNull<ProfilePet>(binding.variant) != null
                        com.example.teamval.domain.model.EquipField.PlayerCard ->
                            enumByNameOrNull<ProfileCardStyle>(binding.variant) != null
                        com.example.teamval.domain.model.EquipField.ProfileBadge ->
                            enumByNameOrNull<ProfileBadge>(binding.variant) != null
                        else -> false
                    }
                    assertTrue("${binding.variant} no existe como variante de perfil", valid)
                }
                EquipTarget.Team -> {
                    val valid = when (binding.field) {
                        com.example.teamval.domain.model.EquipField.TeamFrame ->
                            enumByNameOrNull<ProfileFrame>(binding.variant) != null
                        com.example.teamval.domain.model.EquipField.TeamBackground ->
                            enumByNameOrNull<TeamBackground>(binding.variant) != null
                        com.example.teamval.domain.model.EquipField.TeamBadge ->
                            enumByNameOrNull<TeamBadge>(binding.variant) != null
                        else -> false
                    }
                    assertTrue("${binding.variant} no existe como variante de equipo", valid)
                }
                EquipTarget.Appearance ->
                    assertTrue(
                        "Tema sin preset: ${binding.variant}",
                        VisualThemePresets.presets.containsKey(binding.variant)
                    )
            }
        }
    }

    @Test
    fun premiumItemsAreBlockedAndHaveNoBinding() {
        catalog.filter { it.isPremium }.forEach { premium ->
            assertNull("${premium.id}: los artículos premium no se equipan hasta tener pagos", premium.binding)
            assertFalse("${premium.id}: no puede tener precio en monedas", premium.priceCoins != null)
            assertFalse("${premium.id}: no se incluye en paquetes", premium.contains.isNotEmpty())
        }
        assertEquals(4, catalog.count { it.isPremium })
    }

    @Test
    fun bundlesNeverGrantCompetitiveAdvantageFields() {
        // Solo estética: ningún vínculo apunta a stats, ranking o reputación.
        catalog.mapNotNull { it.binding }.forEach { binding ->
            assertTrue(
                "${binding.variant}: destino inesperado",
                binding.target in listOf(EquipTarget.Profile, EquipTarget.Team, EquipTarget.Appearance)
            )
        }
    }

    @Test
    fun missionsAreWellFormedAndNeverRewardRatings() {
        assertEquals(DemoRewardData.missions.size, DemoRewardData.missions.map { it.id }.toSet().size)
        DemoRewardData.missions.forEach { mission ->
            assertTrue("${mission.id}: recompensa no positiva", mission.rewardCoins > 0)
            assertTrue("${mission.id}: objetivo inválido", mission.target > 0)
            assertNotNull(DemoRewardData.find(mission.id))
        }
        assertEquals(1, DemoRewardData.missions.count { it.kind == MissionKind.ProfileCompletion })
        assertEquals(2, DemoRewardData.missions.count { it.kind == MissionKind.VerificationPending })
        assertTrue(DemoRewardData.missions.count { it.kind == MissionKind.DemoProgress } >= 3)
        // Las misiones de verificación jamás se completan sin sistema verificado.
        DemoRewardData.missions.filter { it.kind == MissionKind.VerificationPending }.forEach { mission ->
            assertFalse(MissionRules.isComplete(mission, PlayerProfile(headline = "x"), null))
            assertFalse(MissionRules.canClaim(mission, PlayerProfile(headline = "x"), null))
        }
    }
}
