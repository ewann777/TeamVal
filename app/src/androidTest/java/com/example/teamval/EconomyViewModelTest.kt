package com.example.teamval

import androidx.lifecycle.ViewModelStore
import com.example.teamval.data.fake.DemoRewardData
import com.example.teamval.data.fake.DemoShopCatalog
import com.example.teamval.data.repository.InMemoryEconomyStorage
import com.example.teamval.data.repository.InMemoryTeamRepository
import com.example.teamval.data.repository.LocalEconomyRepository
import com.example.teamval.domain.model.*
import com.example.teamval.domain.repository.PreferencesRepository
import com.example.teamval.ui.screens.shop.EconomyViewModel
import com.example.teamval.ui.screens.teams.TeamsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Test

class EconomyViewModelTest {
    private class Preferences : PreferencesRepository {
        override val preferences = MutableStateFlow(UserPreferences())
        override suspend fun saveProfile(profile: PlayerProfile) {
            delay(20) // Escritura suspendida: dos equipados no deben pisarse.
            preferences.value = preferences.value.copy(profile = profile)
        }
        override suspend fun saveAppearance(appearance: AppAppearance) {
            preferences.value = preferences.value.copy(appearance = appearance)
        }
    }

    private suspend fun withModel(block: suspend (EconomyViewModel, LocalEconomyRepository, Preferences) -> Unit) {
        val store = ViewModelStore()
        val preferences = Preferences()
        val repo = LocalEconomyRepository(InMemoryEconomyStorage(), DemoShopCatalog.items, DemoRewardData.missions)
        val model = withContext(Dispatchers.Main) {
            EconomyViewModel(repo, preferences, InMemoryTeamRepository(), 500).also { store.put("economy", it) }
        }
        try {
            withTimeout(5_000) { model.uiState.first { it.wallet.balance == 500 } }
            block(model, repo, preferences)
        } finally {
            withContext(Dispatchers.Main) { store.clear() }
        }
    }

    @Test
    fun equipRequiresOwnershipAndUsesCatalogBinding() = runBlocking {
        withModel { model, repo, preferences ->
            val frame = DemoShopCatalog.find("frame_lavanda")!!
            withContext(Dispatchers.Main) { model.equip(frame) }
            withTimeout(5_000) { model.uiState.first { it.notice?.textRes == R.string.shop_error_not_owned } }
            assertEquals(PlayerProfile(), preferences.preferences.value.profile)
            repo.purchase(frame)
            withContext(Dispatchers.Main) {
                model.equip(frame.copy(binding = CosmeticBinding(EquipTarget.Profile, EquipField.ProfilePet, "Bruno")))
            }
            val saved = withTimeout(5_000) { preferences.preferences.first { it.profile.frame == AvatarFrame.Lavanda } }
            assertEquals(ProfilePet.None, saved.profile.pet)
            assertEquals(400, repo.transactions.first().sumOf { it.amount })
        }
    }

    @Test
    fun concurrentEquipsKeepBothBadgesAndThemePreservesMode() = runBlocking {
        withModel { model, repo, preferences ->
            val star = DemoShopCatalog.find("badge_estrella")!!
            val community = DemoShopCatalog.find("badge_comunidad")!!
            val theme = DemoShopCatalog.find("theme_cosmico")!!
            listOf(star, community, theme).forEach { repo.purchase(it) }
            preferences.saveAppearance(AppAppearance(mode = AppThemeMode.Light))
            withContext(Dispatchers.Main) {
                model.equip(star)
                model.equip(community)
                model.equip(theme)
            }
            val saved = withTimeout(5_000) { preferences.preferences.first { it.appearance.accentHex == "8B5CF6" } }
            assertTrue(saved.profile.badges.containsAll(setOf(ProfileBadge.Estrella, ProfileBadge.Comunidad)))
            assertEquals(AppThemeMode.Light, saved.appearance.mode)
            withContext(Dispatchers.Main) { model.unequip(theme) }
            withTimeout(5_000) { preferences.preferences.first { it.appearance == AppAppearance(mode = AppThemeMode.Light) } }
        }
    }

    @Test
    fun teamEditorRejectsSavingAnotherCaptainsIdentity() = runBlocking {
        val store = ViewModelStore()
        val teams = InMemoryTeamRepository()
        val original = teams.snapshot.value
        withContext(Dispatchers.Main) {
            val model = TeamsViewModel(teams).also { store.put("teams", it) }
            try {
                val rival = original.teams.first()
                model.startEditingIdentity(rival.id)
                model.updateIdentityDraft(rival.identity.copy(frame = ProfileFrame.Prism))
                assertFalse(model.saveIdentity())
                assertFalse(model.saveLogo())
                assertEquals(original, teams.snapshot.value)
            } finally {
                store.clear()
            }
        }
    }
}
