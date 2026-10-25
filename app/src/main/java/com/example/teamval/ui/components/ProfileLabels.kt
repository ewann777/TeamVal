package com.example.teamval.ui.components

import com.example.teamval.R
import com.example.teamval.domain.model.AppBackground
import com.example.teamval.domain.model.AppFont
import com.example.teamval.domain.model.AppThemeMode
import com.example.teamval.domain.model.AvatarFrame
import com.example.teamval.domain.model.AvatarStyle
import com.example.teamval.domain.model.ProfileBackground
import com.example.teamval.domain.model.ProfileBadge
import com.example.teamval.domain.model.ProfileBanner
import com.example.teamval.domain.model.ProfileCardStyle
import com.example.teamval.domain.model.ProfilePet
import com.example.teamval.domain.model.ProfileSticker

val AvatarStyle.labelRes: Int get() = when (this) {
    AvatarStyle.Nova -> R.string.avatar_nova
    AvatarStyle.Orbit -> R.string.avatar_orbit
    AvatarStyle.Fox -> R.string.avatar_fox
    AvatarStyle.Sentinel -> R.string.avatar_sentinel
}
val AvatarFrame.labelRes: Int get() = when (this) {
    AvatarFrame.None -> R.string.frame_none
    AvatarFrame.Halo -> R.string.frame_halo
    AvatarFrame.Double -> R.string.frame_double
    AvatarFrame.Lavanda -> R.string.frame_lavanda
    AvatarFrame.Solar -> R.string.frame_solar
    AvatarFrame.Neon -> R.string.frame_neon
}
val ProfileBackground.labelRes: Int get() = when (this) {
    ProfileBackground.Nebula -> R.string.background_nebula
    ProfileBackground.Ocean -> R.string.background_ocean
    ProfileBackground.Sunset -> R.string.background_sunset
    ProfileBackground.Graphite -> R.string.background_graphite
    ProfileBackground.Pastel -> R.string.background_pastel
    ProfileBackground.Aurora -> R.string.background_aurora_soft
    ProfileBackground.Galaxia -> R.string.background_galaxia
}
val ProfileBadge.labelRes: Int get() = when (this) {
    ProfileBadge.Teamwork -> R.string.badge_teamwork
    ProfileBadge.Strategist -> R.string.badge_strategist
    ProfileBadge.Positive -> R.string.badge_positive
    ProfileBadge.Explorer -> R.string.badge_explorer
    ProfileBadge.Estrella -> R.string.badge_estrella
    ProfileBadge.Comunidad -> R.string.badge_comunidad
    ProfileBadge.Veterano -> R.string.badge_veterano
}
val ProfileBanner.labelRes: Int get() = when (this) {
    ProfileBanner.None -> R.string.option_none
    ProfileBanner.Diurno -> R.string.banner_diurno
    ProfileBanner.Noche -> R.string.banner_noche
    ProfileBanner.Estelar -> R.string.banner_estelar
}
val ProfileSticker.labelRes: Int get() = when (this) {
    ProfileSticker.None -> R.string.option_none
    ProfileSticker.GG -> R.string.sticker_gg
    ProfileSticker.Fuego -> R.string.sticker_fuego
}
val ProfilePet.labelRes: Int get() = when (this) {
    ProfilePet.None -> R.string.option_none
    ProfilePet.Bruno -> R.string.pet_bruno
    ProfilePet.Nube -> R.string.pet_nube
}
val ProfileCardStyle.labelRes: Int get() = when (this) {
    ProfileCardStyle.Base -> R.string.card_base
    ProfileCardStyle.Especial -> R.string.card_especial
    ProfileCardStyle.Cambio -> R.string.card_cambio
}
val AppThemeMode.labelRes: Int get() = when (this) {
    AppThemeMode.Dark -> R.string.theme_dark
    AppThemeMode.Light -> R.string.theme_light
    AppThemeMode.System -> R.string.theme_system
}
val AppBackground.labelRes: Int get() = when (this) {
    AppBackground.Plain -> R.string.app_background_plain
    AppBackground.Aurora -> R.string.app_background_aurora
    AppBackground.Grid -> R.string.app_background_grid
}
val AppFont.labelRes: Int get() = when (this) {
    AppFont.Modern -> R.string.font_modern
    AppFont.Editorial -> R.string.font_editorial
    AppFont.Mono -> R.string.font_mono
}
