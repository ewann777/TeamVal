package com.example.teamval.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Integración entre los cosméticos de la tienda y los editores de las etapas 3
 * y 4.1: equipar aplica la variante, desequipar restaura el valor base y cada
 * cosmético solo toca su propio objetivo (perfil, apariencia o equipo).
 */
class CosmeticBindingsTest {

    private fun binding(target: EquipTarget, field: EquipField, variant: String) =
        CosmeticBinding(target, field, variant)

    private fun context(
        profile: PlayerProfile = PlayerProfile(),
        appearance: AppAppearance = AppAppearance(),
        team: TeamIdentity? = TeamIdentity(motto = "m", description = "d")
    ) = CosmeticContext(profile, appearance, team)

    // --- Perfil ---------------------------------------------------------------
    @Test
    fun avatarFrameEquipsAndRestoresBaseline() {
        val equip = binding(EquipTarget.Profile, EquipField.AvatarFrame, "Lavanda")
        val equipped = equip.applyToProfile(PlayerProfile(), equip = true)
        assertEquals(AvatarFrame.Lavanda, equipped.frame)
        assertTrue(equip.isEquipped(CosmeticContext(profile = equipped)))

        val restored = equip.applyToProfile(equipped, equip = false)
        assertEquals(AvatarFrame.Halo, restored.frame)
        assertFalse(equip.isEquipped(CosmeticContext(profile = restored)))
    }

    @Test
    fun profileBackgroundEquipsAndRestoresNebula() {
        val equip = binding(EquipTarget.Profile, EquipField.ProfileBackground, "Galaxia")
        val equipped = equip.applyToProfile(PlayerProfile(), equip = true)
        assertEquals(ProfileBackground.Galaxia, equipped.background)
        assertEquals(
            ProfileBackground.Nebula,
            equip.applyToProfile(equipped, equip = false).background
        )
    }

    @Test
    fun bannerStickerPetAndCardFollowTheSameRule() {
        val banner = binding(EquipTarget.Profile, EquipField.ProfileBanner, "Estelar")
        val sticker = binding(EquipTarget.Profile, EquipField.ProfileSticker, "Fuego")
        val pet = binding(EquipTarget.Profile, EquipField.ProfilePet, "Bruno")
        val card = binding(EquipTarget.Profile, EquipField.PlayerCard, "Especial")

        var profile = PlayerProfile()
        listOf(banner, sticker, pet, card).forEach { profile = it.applyToProfile(profile, true) }

        assertEquals(ProfileBanner.Estelar, profile.banner)
        assertEquals(ProfileSticker.Fuego, profile.sticker)
        assertEquals(ProfilePet.Bruno, profile.pet)
        assertEquals(ProfileCardStyle.Especial, profile.card)

        val ctx = CosmeticContext(profile = profile)
        listOf(banner, sticker, pet, card).forEach { assertTrue(it.isEquipped(ctx)) }

        listOf(banner, sticker, pet, card).forEach { profile = it.applyToProfile(profile, false) }
        assertEquals(ProfileBanner.None, profile.banner)
        assertEquals(ProfileSticker.None, profile.sticker)
        assertEquals(ProfilePet.None, profile.pet)
        assertEquals(ProfileCardStyle.Base, profile.card)
    }

    @Test
    fun badgesAddAndRemoveWithoutTouchingOthers() {
        val equip = binding(EquipTarget.Profile, EquipField.ProfileBadge, "Estrella")
        val base = PlayerProfile()

        val withBadge = equip.applyToProfile(base, equip = true)
        assertTrue(ProfileBadge.Estrella in withBadge.badges)
        assertTrue("Las insignias base deben conservarse", ProfileBadge.Teamwork in withBadge.badges)

        val removed = equip.applyToProfile(withBadge, equip = false)
        assertFalse(ProfileBadge.Estrella in removed.badges)
        assertEquals(base.badges, removed.badges)
        assertFalse(equip.isEquipped(CosmeticContext(profile = removed)))
    }

    @Test
    fun profileCosmeticsNeverAffectOtherTargets() {
        val profileBinding = binding(EquipTarget.Profile, EquipField.AvatarFrame, "Neon")
        val profile = PlayerProfile()
        val team = TeamIdentity(motto = "m", description = "d")
        val appearance = AppAppearance()

        profileBinding.applyToProfile(profile, equip = true)
        assertEquals(TeamIdentity(motto = "m", description = "d").frame, team.frame)
        assertEquals(AppAppearance().accentHex, appearance.accentHex)
        // Un cosmético de equipo jamás modifica el perfil personal.
        val teamBinding = binding(EquipTarget.Team, EquipField.TeamFrame, "Orbit")
        assertEquals(profile, teamBinding.applyToProfile(profile, equip = true))
    }

    // --- Apariencia global ----------------------------------------------------
    @Test
    fun visualThemeAppliesPresetAndKeepsThemeMode() {
        val theme = binding(EquipTarget.Appearance, EquipField.VisualTheme, "Aqualux")
        val appearance = AppAppearance(mode = AppThemeMode.Light)

        val equipped = theme.applyToAppearance(appearance, equip = true)
        val preset = VisualThemePresets.presets.getValue("Aqualux")
        assertEquals(preset.accentHex, equipped.accentHex)
        assertEquals(preset.background, equipped.background)
        assertEquals(preset.font, equipped.font)
        assertEquals("El modo del tema debe preservarse", AppThemeMode.Light, equipped.mode)
        assertTrue(theme.isEquipped(CosmeticContext(appearance = equipped)))

        val restored = theme.applyToAppearance(equipped, equip = false)
        assertEquals(AppAppearance(mode = AppThemeMode.Light), restored)
        assertFalse(theme.isEquipped(CosmeticContext(appearance = restored)))
    }

    // --- Equipo ---------------------------------------------------------------
    @Test
    fun teamFrameBackgroundAndBadgeEquipAndRestore() {
        val frame = binding(EquipTarget.Team, EquipField.TeamFrame, "Prism")
        val background = binding(EquipTarget.Team, EquipField.TeamBackground, "Atardecer")
        val badge = binding(EquipTarget.Team, EquipField.TeamBadge, "Flame")
        val base = TeamIdentity(motto = "m", description = "d")

        var identity = base
        listOf(frame, background, badge).forEach { identity = it.applyToTeamIdentity(identity, true) }

        assertEquals(ProfileFrame.Prism, identity.frame)
        assertEquals(TeamBackground.Atardecer, identity.background)
        assertTrue(TeamBadge.Flame in identity.badges)
        assertTrue(TeamBadge.FairPlay in identity.badges)

        val ctx = CosmeticContext(teamIdentity = identity)
        listOf(frame, background, badge).forEach { assertTrue(it.isEquipped(ctx)) }

        listOf(frame, background, badge).forEach { identity = it.applyToTeamIdentity(identity, false) }
        assertEquals(ProfileFrame.Ring, identity.frame)
        assertEquals(TeamBackground.Dusk, identity.background)
        assertFalse(TeamBadge.Flame in identity.badges)
    }

    // --- Bloqueo de variantes sin comprar -------------------------------------
    @Test
    fun lockedVariantsHideUnownedShopCosmeticsOnly() {
        val catalog = com.example.teamval.data.fake.DemoShopCatalog.items
        val noneOwned = shopLockedVariants(catalog, owned = emptySet(), target = EquipTarget.Profile)
        val frameOwned = shopLockedVariants(catalog, owned = setOf("frame_lavanda"), target = EquipTarget.Profile)

        assertTrue("Lavanda" in noneOwned)
        assertTrue("Galaxia" in noneOwned)
        assertFalse("La variante adquirida no debe bloquearse", "Lavanda" in frameOwned)
        assertTrue("Galaxia" in frameOwned)
        assertTrue(
            "Los cosméticos de equipo desbloqueados quedan libres",
            "Orbit" !in shopLockedVariants(catalog, owned = setOf("team_logo_orbit"), target = EquipTarget.Team)
        )
        assertTrue("Orbit" in shopLockedVariants(catalog, owned = emptySet(), target = EquipTarget.Team))
        assertTrue(
            "Las variantes base del editor nunca se bloquean",
            shopLockedVariants(catalog, emptySet(), EquipTarget.Profile).none { it == "Halo" || it == "Nebula" }
        )
    }
}
