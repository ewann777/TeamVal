package com.example.teamval.domain.model

/**
 * Integración entre los cosméticos de la tienda (etapa 5) y los editores de
 * personalización de las etapas 3 y 4.1: comprar y equipar un artículo
 * sobrescribe el mismo campo que ya usan esas pantallas, de modo que no existe
 * un sistema de personalización paralelo.
 */

inline fun <reified T : Enum<T>> enumByName(name: String): T? = enumValues<T>().find { it.name == name }

/** Presets globales que aplica un "Tema visual" comprado en la tienda. */
object VisualThemePresets {
    val presets: Map<String, AppAppearance> = mapOf(
        "Cosmico" to AppAppearance(accentHex = "8B5CF6", background = AppBackground.Aurora, font = AppFont.Modern),
        "Aqualux" to AppAppearance(accentHex = "22D3EE", background = AppBackground.Stars, font = AppFont.Mono)
    )

    fun matches(appearance: AppAppearance, name: String): Boolean {
        val preset = presets[name] ?: return false
        return appearance.accentHex.equals(preset.accentHex, ignoreCase = true) &&
            appearance.background == preset.background &&
            appearance.font == preset.font
    }
}

/** Estado visual actual necesario para saber qué cosméticos están equipados. */
data class CosmeticContext(
    val profile: PlayerProfile = PlayerProfile(),
    val appearance: AppAppearance = AppAppearance(),
    val teamIdentity: TeamIdentity? = null
)

/** ¿Está este artículo aplicado ahora mismo en el estado [ctx]? */
fun CosmeticBinding.isEquipped(ctx: CosmeticContext): Boolean = when (target) {
    EquipTarget.Profile -> when (field) {
        EquipField.AvatarFrame -> ctx.profile.frame.name == variant
        EquipField.ProfileBackground -> ctx.profile.background.name == variant
        EquipField.ProfileBanner -> ctx.profile.banner.name == variant
        EquipField.ProfileSticker -> ctx.profile.sticker.name == variant
        EquipField.ProfilePet -> ctx.profile.pet.name == variant
        EquipField.PlayerCard -> ctx.profile.card.name == variant
        EquipField.ProfileBadge -> ctx.profile.badges.any { it.name == variant }
        else -> false
    }
    EquipTarget.Appearance ->
        field == EquipField.VisualTheme && VisualThemePresets.matches(ctx.appearance, variant)
    EquipTarget.Team -> when (field) {
        EquipField.TeamFrame -> ctx.teamIdentity?.frame?.name == variant
        EquipField.TeamBackground -> ctx.teamIdentity?.background?.name == variant
        EquipField.TeamBadge -> ctx.teamIdentity?.badges?.any { it.name == variant } == true
        else -> false
    }
}

/**
 * Aplica o retira el cosmético sobre el perfil personal. Al retirarlo el campo
 * vuelve a su valor base de la etapa 3 (nunca deja el perfil sin estilo).
 */
fun CosmeticBinding.applyToProfile(profile: PlayerProfile, equip: Boolean): PlayerProfile = when (field) {
    EquipField.AvatarFrame ->
        profile.copy(frame = (if (equip) enumByName<AvatarFrame>(variant) else null) ?: AvatarFrame.Halo)
    EquipField.ProfileBackground ->
        profile.copy(background = (if (equip) enumByName<ProfileBackground>(variant) else null) ?: ProfileBackground.Nebula)
    EquipField.ProfileBanner ->
        profile.copy(banner = (if (equip) enumByName<ProfileBanner>(variant) else null) ?: ProfileBanner.None)
    EquipField.ProfileSticker ->
        profile.copy(sticker = (if (equip) enumByName<ProfileSticker>(variant) else null) ?: ProfileSticker.None)
    EquipField.ProfilePet ->
        profile.copy(pet = (if (equip) enumByName<ProfilePet>(variant) else null) ?: ProfilePet.None)
    EquipField.PlayerCard ->
        profile.copy(card = (if (equip) enumByName<ProfileCardStyle>(variant) else null) ?: ProfileCardStyle.Base)
    EquipField.ProfileBadge -> {
        val badge = enumByName<ProfileBadge>(variant)
        if (badge == null) profile
        else if (equip) profile.copy(badges = profile.badges + badge)
        else profile.copy(badges = profile.badges - badge)
    }
    else -> profile
}

/** Aplica o retira un tema visual global; al retirarlo vuelve a la apariencia base. */
fun CosmeticBinding.applyToAppearance(appearance: AppAppearance, equip: Boolean): AppAppearance {
    if (field != EquipField.VisualTheme) return appearance
    val preset = VisualThemePresets.presets[variant]
    if (!equip) return AppAppearance(mode = appearance.mode)
    return if (preset == null) appearance else appearance.copy(
        accentHex = preset.accentHex,
        background = preset.background,
        font = preset.font
    )
}

/** Aplica o retira un cosmético sobre la identidad de un equipo (capitán). */
fun CosmeticBinding.applyToTeamIdentity(identity: TeamIdentity, equip: Boolean): TeamIdentity = when (field) {
    EquipField.TeamFrame ->
        identity.copy(frame = (if (equip) enumByName<ProfileFrame>(variant) else null) ?: ProfileFrame.Ring)
    EquipField.TeamBackground ->
        identity.copy(background = (if (equip) enumByName<TeamBackground>(variant) else null) ?: TeamBackground.Dusk)
    EquipField.TeamBadge -> {
        val badge = enumByName<TeamBadge>(variant)
        if (badge == null) identity
        else if (equip) identity.copy(badges = identity.badges + badge)
        else identity.copy(badges = identity.badges - badge)
    }
    else -> identity
}

/**
 * Variantes del catálogo que todavía no son tuyas para el objetivo [target].
 * Los editores de perfil y equipo bloquean estas opciones para que ningún
 * cosmético pueda elegirse sin comprarlo antes.
 */
fun shopLockedVariants(catalog: List<ShopItem>, owned: Set<String>, target: EquipTarget): Set<String> =
    catalog.asSequence()
        .filter { it.binding?.target == target && it.id !in owned }
        .mapNotNull { it.binding?.variant }
        .toSet()

/** Variantes de [target] que ya posee el jugador (para mostrarlas como compradas). */
fun shopOwnedVariants(catalog: List<ShopItem>, owned: Set<String>, target: EquipTarget): Set<String> =
    catalog.asSequence()
        .filter { it.binding?.target == target && it.id in owned }
        .mapNotNull { it.binding?.variant }
        .toSet()
