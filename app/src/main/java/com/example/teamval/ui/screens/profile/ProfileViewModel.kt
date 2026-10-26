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
    val messageRes: Int? = null,
    /** Distingue la confirmación de guardado correcto de los avisos de error. */
    val messageIsError: Boolean = false
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

    fun editProfile() = state.update { it.copy(profileDraft = it.saved?.profile, invalidFields = emptySet(), messageRes = null, messageIsError = false) }
    fun editAppearance() = state.update { it.copy(appearanceDraft = it.saved?.appearance, invalidColor = false, messageRes = null, messageIsError = false) }
    fun updateProfile(profile: PlayerProfile) = state.update { if (it.saving) it else it.copy(profileDraft = profile, messageRes = null, messageIsError = false) }
    fun updateAppearance(appearance: AppAppearance) = state.update { if (it.saving) it else it.copy(appearanceDraft = appearance, invalidColor = false, messageRes = null, messageIsError = false) }
    fun discardProfile() = state.update { it.copy(profileDraft = null, invalidFields = emptySet(), messageRes = null, messageIsError = false) }
    fun discardAppearance() = state.update { it.copy(appearanceDraft = null, invalidColor = false, messageRes = null, messageIsError = false) }
    fun resetAppearance() = updateAppearance(AppAppearance())

    fun saveProfile() {
        if (state.value.saving) return
        val draft = state.value.profileDraft ?: return
        val errors = draft.invalidFields()
        state.update {
            it.copy(
                invalidFields = errors,
                messageRes = if (errors.isEmpty()) null else R.string.profile_validation_error,
                messageIsError = errors.isNotEmpty()
            )
        }
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
        // La confirmación específica de apariencia solo se muestra tras guardar.
        save(successRes = R.string.appearance_updated) {
            repository.saveAppearance(draft)
            state.update { it.copy(appearanceDraft = draft.normalized()) }
        }
    }

    private fun save(successRes: Int = R.string.preferences_saved, write: suspend () -> Unit) {
        state.update { it.copy(saving = true, messageRes = null, messageIsError = false) }
        viewModelScope.launch {
            try {
                write()
                state.update { it.copy(messageRes = successRes, messageIsError = false) }
            } catch (_: IOException) {
                state.update { it.copy(messageRes = R.string.preferences_save_error, messageIsError = true) }
            } finally {
                state.update { it.copy(saving = false) }
            }
        }
    }
}
