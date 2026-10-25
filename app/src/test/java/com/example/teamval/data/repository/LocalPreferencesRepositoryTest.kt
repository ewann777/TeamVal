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
            assertEquals(AppAppearance(), saved.appearance)
        } finally { job.cancelAndJoin() }
    }
}
