package com.example.teamval.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.teamval.domain.model.AppAppearance
import com.example.teamval.domain.model.AppBackground
import com.example.teamval.domain.model.AppFont
import com.example.teamval.domain.model.AppThemeMode
import com.example.teamval.domain.model.AppPalette
import com.example.teamval.domain.model.PlayerProfile
import com.example.teamval.domain.model.ProfileBadge
import com.example.teamval.domain.model.UserPreferences
import com.example.teamval.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.map

val Context.teamValPreferences by preferencesDataStore(name = "teamval_personalization")

class LocalPreferencesRepository(private val store: DataStore<Preferences>) : PreferencesRepository {
    private object Keys {
        val name = stringPreferencesKey("profile_name")
        val headline = stringPreferencesKey("profile_headline")
        val biography = stringPreferencesKey("profile_biography")
        val agent = stringPreferencesKey("profile_agent")
        val region = stringPreferencesKey("profile_region")
        val avatar = stringPreferencesKey("profile_avatar")
        val frame = stringPreferencesKey("profile_frame")
        val background = stringPreferencesKey("profile_background")
        val badges = stringSetPreferencesKey("profile_badges")
        val banner = stringPreferencesKey("profile_banner")
        val sticker = stringPreferencesKey("profile_sticker")
        val pet = stringPreferencesKey("profile_pet")
        val card = stringPreferencesKey("profile_card")
        val mode = stringPreferencesKey("appearance_mode")
        val accent = stringPreferencesKey("appearance_accent")
        val appBackground = stringPreferencesKey("appearance_background")
        val font = stringPreferencesKey("appearance_font")
        val palette = stringPreferencesKey("appearance_palette")
    }

    override val preferences = store.data.map { values ->
        val default = PlayerProfile()
        val profile = PlayerProfile(
            name = values[Keys.name]?.takeIf { it.trim().length in 3..24 } ?: default.name,
            headline = values[Keys.headline]?.take(80) ?: default.headline,
            biography = values[Keys.biography]?.take(300) ?: default.biography,
            agent = enumOrDefault(values[Keys.agent], default.agent),
            region = enumOrDefault(values[Keys.region], default.region),
            avatar = enumOrDefault(values[Keys.avatar], default.avatar),
            frame = enumOrDefault(values[Keys.frame], default.frame),
            background = enumOrDefault(values[Keys.background], default.background),
            badges = values[Keys.badges]?.mapNotNull { name -> ProfileBadge.entries.find { it.name == name } }?.toSet() ?: default.badges,
            banner = enumOrDefault(values[Keys.banner], default.banner),
            sticker = enumOrDefault(values[Keys.sticker], default.sticker),
            pet = enumOrDefault(values[Keys.pet], default.pet),
            card = enumOrDefault(values[Keys.card], default.card)
        )
        // Solo una configuración de apariencia anterior activa los valores de
        // compatibilidad. Tener un perfil guardado no impide el nuevo tema base.
        val legacy = values[Keys.palette] == null &&
            listOf(Keys.mode, Keys.accent, Keys.appBackground, Keys.font).any { values[it] != null }
        val defaults = if (legacy) AppAppearance(accentHex = "C4B5FD", font = AppFont.Modern, palette = AppPalette.Legacy)
            else AppAppearance()
        val appearance = AppAppearance(
            mode = enumOrDefault(values[Keys.mode], AppThemeMode.Dark),
            accentHex = values[Keys.accent]?.takeIf { AppAppearance(accentHex = it).isValid() } ?: defaults.accentHex,
            background = backgroundOrDefault(values[Keys.appBackground]),
            font = enumOrDefault(values[Keys.font], defaults.font),
            palette = enumOrDefault(values[Keys.palette], defaults.palette)
        ).normalized()
        UserPreferences(profile, appearance)
    }

    override suspend fun saveProfile(profile: PlayerProfile) {
        require(profile.invalidFields().isEmpty())
        val normalized = profile.normalized()
        store.edit {
            it[Keys.name] = normalized.name
            it[Keys.headline] = normalized.headline
            it[Keys.biography] = normalized.biography
            it[Keys.agent] = normalized.agent.name
            it[Keys.region] = normalized.region.name
            it[Keys.avatar] = normalized.avatar.name
            it[Keys.frame] = normalized.frame.name
            it[Keys.background] = normalized.background.name
            it[Keys.badges] = normalized.badges.map { badge -> badge.name }.toSet()
            it[Keys.banner] = normalized.banner.name
            it[Keys.sticker] = normalized.sticker.name
            it[Keys.pet] = normalized.pet.name
            it[Keys.card] = normalized.card.name
        }
    }

    override suspend fun saveAppearance(appearance: AppAppearance) {
        require(appearance.isValid())
        val normalized = appearance.normalized()
        store.edit {
            it[Keys.mode] = normalized.mode.name
            it[Keys.accent] = normalized.accentHex
            it[Keys.appBackground] = normalized.background.name
            it[Keys.font] = normalized.font.name
            it[Keys.palette] = normalized.palette.name
        }
    }

    private inline fun <reified T : Enum<T>> enumOrDefault(value: String?, default: T): T =
        enumValues<T>().find { it.name == value } ?: default

    /**
     * Lectura segura del fondo guardado. «Grid» (Cuadrícula) se retiró de la
     * interfaz y se sustituyó por «Stars» (Estrellas), de modo que las
     * preferencias antiguas siguen siendo un fondo decorado válido; cualquier
     * otro valor desconocido vuelve al predeterminado. La migración es de solo
     * lectura: no escribe ni resetea lo que hay guardado.
     */
    private fun backgroundOrDefault(value: String?): AppBackground =
        enumOrDefault(if (value == "Grid") AppBackground.Stars.name else value, AppBackground.Plain)
}
