package com.example.teamval.domain.model

enum class AvatarStyle { Nova, Orbit, Fox, Sentinel }
enum class AvatarFrame { None, Halo, Double, Lavanda, Solar, Neon }
enum class ProfileBackground { Nebula, Ocean, Sunset, Graphite, Pastel, Aurora, Galaxia }
enum class ProfileBadge { Teamwork, Strategist, Positive, Explorer, Estrella, Comunidad, Veterano }
/** Decoración de perfil de la etapa 5: se equipa desde el inventario. */
enum class ProfileBanner { None, Diurno, Noche, Estelar }
enum class ProfileSticker { None, GG, Fuego }
enum class ProfilePet { None, Bruno, Nube }
enum class ProfileCardStyle { Base, Especial, Cambio }
enum class FavoriteAgent { Jett, Sage, Omen, Killjoy, Raze, Sova }
enum class AppThemeMode { Dark, Light, System }
enum class AppBackground { Plain, Aurora, Grid }
enum class AppFont { Modern, Editorial, Mono }
enum class ProfileField { Name, Headline, Biography }

data class PlayerProfile(
    val name: String = "LosFichas#777",
    val headline: String = "Buena comunicación. Mejores partidas.",
    val biography: String = "Juego para mejorar y conocer gente con buena actitud. Me gusta coordinar al equipo y celebrar cada pequeña victoria.",
    val agent: FavoriteAgent = FavoriteAgent.Omen,
    val region: Region = Region.LatamSouth,
    val avatar: AvatarStyle = AvatarStyle.Nova,
    val frame: AvatarFrame = AvatarFrame.Halo,
    val background: ProfileBackground = ProfileBackground.Nebula,
    val badges: Set<ProfileBadge> = setOf(ProfileBadge.Teamwork, ProfileBadge.Positive),
    /** Cosméticos equipados desde el inventario (etapa 5). */
    val banner: ProfileBanner = ProfileBanner.None,
    val sticker: ProfileSticker = ProfileSticker.None,
    val pet: ProfilePet = ProfilePet.None,
    val card: ProfileCardStyle = ProfileCardStyle.Base
) {
    fun invalidFields(): Set<ProfileField> = buildSet {
        if (name.trim().length !in 3..24) add(ProfileField.Name)
        if (headline.trim().length > 80) add(ProfileField.Headline)
        if (biography.trim().length > 300) add(ProfileField.Biography)
    }

    fun normalized() = copy(name = name.trim(), headline = headline.trim(), biography = biography.trim())
}

data class AppAppearance(
    val mode: AppThemeMode = AppThemeMode.Dark,
    val accentHex: String = "C4B5FD",
    val background: AppBackground = AppBackground.Plain,
    val font: AppFont = AppFont.Modern
) {
    fun isValid(): Boolean = accentHex.removePrefix("#").matches(Regex("[0-9a-fA-F]{6}"))
    fun normalized() = copy(accentHex = accentHex.removePrefix("#").uppercase())
}

data class UserPreferences(
    val profile: PlayerProfile = PlayerProfile(),
    val appearance: AppAppearance = AppAppearance()
)

data class ValorantStats(val matches: Int, val wins: Int, val kd: Double, val headshotPercent: Int, val rank: ValorantRank) {
    val winRate: Int get() = if (matches == 0) 0 else wins * 100 / matches
}

data class PlayerReview(val author: String, val stars: Int, val comment: String)

data class Reputation(val reviews: List<PlayerReview>, val communication: Int, val teamwork: Int, val attitude: Int) {
    val average: Double get() = if (reviews.isEmpty()) 0.0 else reviews.map { it.stars }.average()
}
