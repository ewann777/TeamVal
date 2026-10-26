package com.example.teamval.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.example.teamval.domain.model.*
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class LocalPreferencesRepositoryTest {
    @get:Rule val temporaryFolder = TemporaryFolder()

    private suspend fun withRepository(file: File, block: suspend (LocalPreferencesRepository) -> Unit) {
        val job = SupervisorJob()
        val store = PreferenceDataStoreFactory.create(scope = CoroutineScope(job + Dispatchers.IO), produceFile = { file })
        try { block(LocalPreferencesRepository(store)) } finally { job.cancelAndJoin() }
    }

    @Test
    fun profileAndAppearanceSurviveClosingAndReopeningTheStore() = runBlocking {
        val file = File(temporaryFolder.root, "preferences.preferences_pb")
        val profile = PlayerProfile(
            name = "  Astra#CL  ", headline = "  Mi equipo  ", biography = "Mi presentación personal",
            agent = FavoriteAgent.Sage, region = Region.Europe, avatar = AvatarStyle.Fox,
            frame = AvatarFrame.Double, background = ProfileBackground.Ocean, badges = emptySet(),
            banner = ProfileBanner.Estelar, sticker = ProfileSticker.GG,
            pet = ProfilePet.Bruno, card = ProfileCardStyle.Especial
        )
        val appearance = AppAppearance(AppThemeMode.Light, "#88dcc4", AppBackground.Aurora, AppFont.Editorial)
        withRepository(file) { repository ->
            assertEquals(UserPreferences(), repository.preferences.first())
            repository.saveProfile(profile)
            repository.saveAppearance(appearance)
        }
        withRepository(file) { repository ->
            assertEquals(UserPreferences(profile.normalized(), appearance.normalized()), repository.preferences.first())
            repository.saveProfile(profile.copy(name = "Astra nueva"))
            assertEquals(appearance.normalized(), repository.preferences.first().appearance)
        }
    }

    @Test
    fun invalidInputNeverOverwritesSavedPreferences() = runBlocking {
        withRepository(File(temporaryFolder.root, "invalid.preferences_pb")) { repository ->
            repository.saveProfile(PlayerProfile(name = "Jugador válido"))
            val original = repository.preferences.first()
            listOf(PlayerProfile(name = " "), PlayerProfile(headline = "x".repeat(81)), PlayerProfile(biography = "x".repeat(301))).forEach {
                try { repository.saveProfile(it); fail("Expected validation failure") } catch (_: IllegalArgumentException) { }
            }
            listOf("XYZ123", "FFF", "1234567", "").forEach {
                try { repository.saveAppearance(AppAppearance(accentHex = it)); fail("Expected invalid color") } catch (_: IllegalArgumentException) { }
            }
            assertEquals(original, repository.preferences.first())
        }
    }

    @Test
    fun unknownOptionsAndInvalidStoredColorUseSafeDefaults() = runBlocking {
        val file = File(temporaryFolder.root, "migration.preferences_pb")
        val job = SupervisorJob()
        val store = PreferenceDataStoreFactory.create(scope = CoroutineScope(job + Dispatchers.IO), produceFile = { file })
        try {
            store.edit {
                it[stringPreferencesKey("profile_avatar")] = "removed-avatar"
                it[stringPreferencesKey("appearance_mode")] = "removed-mode"
                it[stringPreferencesKey("appearance_accent")] = "invalid"
                it[stringSetPreferencesKey("profile_badges")] = setOf("Teamwork", "removed-badge")
            }
            val saved = LocalPreferencesRepository(store).preferences.first()
            assertEquals(AvatarStyle.Nova, saved.profile.avatar)
            assertEquals(setOf(ProfileBadge.Teamwork), saved.profile.badges)
            assertEquals(AppAppearance(accentHex = "C4B5FD", font = AppFont.Modern, palette = AppPalette.Legacy), saved.appearance)
        } finally { job.cancelAndJoin() }
    }

    @Test
    fun legacyAppearanceKeepsItsValuesWithoutWritingOrResettingProfile() = runBlocking {
        val file = File(temporaryFolder.root, "legacy.preferences_pb")
        val job = SupervisorJob()
        val store = PreferenceDataStoreFactory.create(scope = CoroutineScope(job + Dispatchers.IO), produceFile = { file })
        try {
            store.edit {
                it[stringPreferencesKey("appearance_accent")] = "88DCC4"
                it[stringPreferencesKey("appearance_mode")] = "Light"
                it[stringPreferencesKey("appearance_background")] = "Grid"
                it[stringPreferencesKey("appearance_font")] = "Editorial"
                it[stringPreferencesKey("profile_name")] = "Jugador existente"
            }
            val before = store.data.first()
            val repository = LocalPreferencesRepository(store)
            val saved = repository.preferences.first()
            assertEquals(AppAppearance(AppThemeMode.Light, "88DCC4", AppBackground.Grid, AppFont.Editorial, AppPalette.Legacy), saved.appearance)
            assertEquals("Jugador existente", saved.profile.name)
            assertEquals(before, store.data.first())
            repository.saveAppearance(saved.appearance)
            assertEquals(saved, repository.preferences.first())
        } finally { job.cancelAndJoin() }
    }

    @Test
    fun profileOnlyInstallGetsNewAppearanceAndPresetsSurviveReopening() = runBlocking {
        val file = File(temporaryFolder.root, "presets.preferences_pb")
        withRepository(file) { repo ->
            repo.saveProfile(PlayerProfile(name = "Perfil conservado"))
            assertEquals(AppAppearance(), repo.preferences.first().appearance)
            assertEquals("FF4655", repo.preferences.first().appearance.accentHex)
            assertEquals(AppFont.Gaming, repo.preferences.first().appearance.font)
        }
        AppearancePreset.entries.forEach { preset ->
            val appearance = preset.apply(AppAppearance(font = AppFont.Mono))
            withRepository(file) { it.saveAppearance(appearance) }
            withRepository(file) {
                assertEquals(appearance, it.preferences.first().appearance)
                assertEquals("Perfil conservado", it.preferences.first().profile.name)
            }
        }
    }
}
