package com.example.teamval.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamval.R
import com.example.teamval.domain.model.AppAppearance
import com.example.teamval.domain.model.PlayerProfile
import com.example.teamval.domain.model.ProfileField
import com.example.teamval.domain.model.UserPreferences
import com.example.teamval.domain.model.ValorantStats
import com.example.teamval.domain.model.Reputation
import com.example.teamval.domain.repository.PreferencesRepository
import java.io.IOException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val saved: UserPreferences? = null,
    val profileDraft: PlayerProfile? = null,
    val appearanceDraft: AppAppearance? = null,
    val invalidFields: Set<ProfileField> = emptySet(),
    val invalidColor: Boolean = false,
    val saving: Boolean = false,
    val loadError: Boolean = false,
    val messageRes: Int? = null
)

class ProfileViewModel(
    private val repository: PreferencesRepository,
    val stats: ValorantStats,
    val reputation: Reputation
) : ViewModel() {
    private val state = MutableStateFlow(ProfileUiState())
    val uiState = state.asStateFlow()
    private var loadJob: Job? = null

    init { load() }

    fun load() {
        loadJob?.cancel()
        state.update { it.copy(loadError = false) }
        loadJob = viewModelScope.launch {
            try {
                repository.preferences.collect { saved -> state.update { it.copy(saved = saved, loadError = false) } }
            } catch (_: IOException) {
                state.update { it.copy(loadError = true) }
            }
        }
    }

    fun editProfile() = state.update { it.copy(profileDraft = it.saved?.profile, invalidFields = emptySet(), messageRes = null) }
    fun editAppearance() = state.update { it.copy(appearanceDraft = it.saved?.appearance, invalidColor = false, messageRes = null) }
    fun updateProfile(profile: PlayerProfile) = state.update { if (it.saving) it else it.copy(profileDraft = profile, messageRes = null) }
    fun updateAppearance(appearance: AppAppearance) = state.update { if (it.saving) it else it.copy(appearanceDraft = appearance, invalidColor = false, messageRes = null) }
    fun discardProfile() = state.update { it.copy(profileDraft = null, invalidFields = emptySet(), messageRes = null) }
    fun discardAppearance() = state.update { it.copy(appearanceDraft = null, invalidColor = false, messageRes = null) }
    fun resetAppearance() = updateAppearance(AppAppearance())

    fun saveProfile() {
        if (state.value.saving) return
        val draft = state.value.profileDraft ?: return
        val errors = draft.invalidFields()
        state.update { it.copy(invalidFields = errors, messageRes = if (errors.isEmpty()) null else R.string.profile_validation_error) }
        if (errors.isNotEmpty()) return
        save {
            repository.saveProfile(draft)
            state.update { it.copy(profileDraft = draft.normalized()) }
        }
    }

    fun saveAppearance() {
        if (state.value.saving) return
        val draft = state.value.appearanceDraft ?: return
        if (!draft.isValid()) {
            state.update { it.copy(invalidColor = true) }
            return
        }
        save {
            repository.saveAppearance(draft)
            state.update { it.copy(appearanceDraft = draft.normalized()) }
        }
    }

    private fun save(write: suspend () -> Unit) {
        state.update { it.copy(saving = true, messageRes = null) }
        viewModelScope.launch {
            try {
                write()
                state.update { it.copy(messageRes = R.string.preferences_saved) }
            } catch (_: IOException) {
                state.update { it.copy(messageRes = R.string.preferences_save_error) }
            } finally {
                state.update { it.copy(saving = false) }
            }
        }
    }
}
