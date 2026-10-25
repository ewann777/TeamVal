package com.example.teamval.ui.screens.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamval.R
import com.example.teamval.domain.model.BuiltinLogo
import com.example.teamval.domain.model.ImageCrop
import com.example.teamval.domain.model.IdentityDraftField
import com.example.teamval.domain.model.ImageLogo
import com.example.teamval.domain.model.LogoSource
import com.example.teamval.domain.model.Scrim
import com.example.teamval.domain.model.ScrimDraft
import com.example.teamval.domain.model.ScrimDraftField
import com.example.teamval.domain.model.ScrimResultDraft
import com.example.teamval.domain.model.ScrimResultField
import com.example.teamval.domain.model.Team
import com.example.teamval.domain.model.TeamDraft
import com.example.teamval.domain.model.TeamDraftField
import com.example.teamval.domain.model.TeamIdentity
import com.example.teamval.domain.model.TeamLogo
import com.example.teamval.domain.model.TeamSnapshot
import com.example.teamval.domain.repository.TeamRepository
import com.example.teamval.domain.repository.TeamResult
import com.example.teamval.ui.components.messageRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class TeamsNotice(val id: Long, val textRes: Int)

enum class HubSegment { Teams, Scrims }

data class TeamsLocalState(
    val segment: HubSegment = HubSegment.Teams,
    val teamDraft: TeamDraft = TeamDraft(),
    val teamInvalid: Set<TeamDraftField> = emptySet(),
    val scrimDraft: ScrimDraft = ScrimDraft(),
    val scrimInvalid: Set<ScrimDraftField> = emptySet(),
    val resultDraft: ScrimResultDraft = ScrimResultDraft(),
    val resultScrimId: String? = null,
    val resultInvalid: Set<ScrimResultField> = emptySet(),
    val notice: TeamsNotice? = null,
    // Stage 4.1 — identity editor (draft with live preview)
    val identityTeamId: String? = null,
    val identityDraft: TeamIdentity = TeamIdentity(),
    val identityInvalid: Set<IdentityDraftField> = emptySet(),
    val logoTab: LogoSource = LogoSource.Builtin,
    val pendingImageUri: String? = null,
    val pendingImageWidth: Int = 0,
    val pendingImageHeight: Int = 0
)

data class TeamsUiState(
    val snapshot: TeamSnapshot,
    val local: TeamsLocalState = TeamsLocalState(),
    val myTeamId: String? = null
) {
    val myTeam: Team? get() = myTeamId?.let { snapshot.team(it) }
    val myScrims: List<Scrim> get() = myTeamId?.let { snapshot.scrimsOf(it) } ?: emptyList()
    fun teamName(teamId: String): String = snapshot.team(teamId)?.name ?: "—"
    fun resultDraftFor(scrimId: String): ScrimResultDraft =
        if (local.resultScrimId == scrimId) local.resultDraft else ScrimResultDraft()
}

class TeamsViewModel(val repository: TeamRepository) : ViewModel() {
    val currentPlayer get() = repository.currentPlayer
    fun updatePlayerName(name: String) = repository.updatePlayerName(name)

    private val local = MutableStateFlow(TeamsLocalState())
    private var noticeId = 0L

    private val myTeamId: String?
        get() = repository.snapshot.value.teams.find { it.isMember(repository.currentPlayer.id) }?.id

    val uiState = combine(repository.snapshot, local) { snapshot, state ->
        TeamsUiState(
            snapshot = snapshot,
            local = state,
            myTeamId = snapshot.teams.find { it.isMember(repository.currentPlayer.id) }?.id
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TeamsUiState(repository.snapshot.value)
    )

    fun setSegment(segment: HubSegment) = local.update { it.copy(segment = segment) }

    // --- Teams -------------------------------------------------------------
    fun startCreatingTeam() = local.update {
        it.copy(teamDraft = TeamDraft(), teamInvalid = emptySet())
    }

    fun updateTeamDraft(draft: TeamDraft) = local.update {
        it.copy(teamDraft = draft, teamInvalid = it.teamInvalid.intersect(draft.invalidFields()))
    }

    fun createTeam(): Boolean {
        val draft = local.value.teamDraft
        val errors = draft.invalidFields()
        if (errors.isNotEmpty()) {
            local.update { it.copy(teamInvalid = errors) }
            return false
        }
        val result = repository.createTeam(draft)
        if (result is TeamResult.Success) {
            local.update { it.copy(teamDraft = TeamDraft(), teamInvalid = emptySet(), segment = HubSegment.Teams) }
        }
        report(result, R.string.team_created)
        return result is TeamResult.Success
    }

    // --- Scrims ------------------------------------------------------------
    fun startProposingScrim(opponentId: String? = null) = local.update {
        it.copy(
            scrimDraft = ScrimDraft(opponentTeamId = opponentId ?: ""),
            scrimInvalid = emptySet()
        )
    }

    fun updateScrimDraft(draft: ScrimDraft) = local.update {
        it.copy(scrimDraft = draft, scrimInvalid = it.scrimInvalid.intersect(draft.invalidFields(myTeamId)))
    }

    fun proposeScrim(): Boolean {
        val draft = local.value.scrimDraft
        val errors = draft.invalidFields(myTeamId)
        if (errors.isNotEmpty()) {
            local.update { it.copy(scrimInvalid = errors) }
            return false
        }
        val result = repository.proposeScrim(draft)
        if (result is TeamResult.Success) {
            local.update {
                it.copy(scrimDraft = ScrimDraft(), scrimInvalid = emptySet(), segment = HubSegment.Scrims)
            }
        }
        report(result, R.string.scrim_created)
        return result is TeamResult.Success
    }

    fun respondToScrim(scrimId: String, accept: Boolean) = report(
        repository.respondToScrim(scrimId, accept),
        if (accept) R.string.scrim_accepted else R.string.scrim_declined
    )

    fun cancelScrim(scrimId: String) = report(repository.cancelScrim(scrimId), R.string.scrim_cancelled_feedback)

    fun updateResult(scrimId: String, draft: ScrimResultDraft) = local.update {
        it.copy(resultScrimId = scrimId, resultDraft = draft, resultInvalid = emptySet())
    }

    fun completeScrim(scrimId: String): Boolean {
        val draft = local.value.resultDraft.takeIf { local.value.resultScrimId == scrimId } ?: ScrimResultDraft()
        val errors = draft.invalidFields()
        if (errors.isNotEmpty()) {
            local.update { it.copy(resultScrimId = scrimId, resultDraft = draft, resultInvalid = errors) }
            return false
        }
        val result = repository.completeScrim(scrimId, draft.homeScore.toInt(), draft.awayScore.toInt())
        if (result is TeamResult.Success) {
            local.update { it.copy(resultScrimId = null, resultDraft = ScrimResultDraft(), resultInvalid = emptySet()) }
        }
        report(result, R.string.scrim_completed_feedback)
        return result is TeamResult.Success
    }

    fun simulateIncomingScrim() = report(repository.simulateIncomingScrim(), R.string.scrim_simulated)

    // --- Identity (stage 4.1) ----------------------------------------------
    fun startEditingIdentity(teamId: String) {
        val saved = repository.snapshot.value.team(teamId)?.identity ?: TeamIdentity()
        local.update {
            it.copy(
                identityTeamId = teamId,
                identityDraft = saved,
                identityInvalid = emptySet(),
                logoTab = LogoSource.Builtin,
                pendingImageUri = null,
                pendingImageWidth = 0,
                pendingImageHeight = 0
            )
        }
    }

    fun updateIdentityDraft(draft: TeamIdentity) = local.update {
        it.copy(identityDraft = draft, identityInvalid = it.identityInvalid.intersect(draft.invalidFields()))
    }

    fun saveIdentity(): Boolean {
        val teamId = local.value.identityTeamId ?: return false
        if (repository.snapshot.value.team(teamId)?.captain?.id != currentPlayer.id) {
            notify(R.string.shop_error_no_permission)
            return false
        }
        val draft = local.value.identityDraft
        val errors = draft.invalidFields()
        if (errors.isNotEmpty()) {
            local.update { it.copy(identityInvalid = errors) }
            return false
        }
        repository.updateTeamIdentity(teamId, draft)
        local.update { it.copy(identityInvalid = emptySet(), pendingImageUri = null) }
        notify(R.string.identity_saved)
        return true
    }

    fun resetIdentity() {
        val teamId = local.value.identityTeamId ?: return
        val saved = repository.snapshot.value.team(teamId)?.identity ?: TeamIdentity()
        local.update { it.copy(identityDraft = saved, identityInvalid = emptySet()) }
        notify(R.string.identity_reset_done)
    }

    fun cancelIdentity() {
        local.update { it.copy(identityTeamId = null, identityInvalid = emptySet(), pendingImageUri = null) }
        notify(R.string.identity_discarded)
    }

    fun setLogoTab(tab: LogoSource) = local.update { it.copy(logoTab = tab) }

    fun updateBuiltinLogo(builtin: BuiltinLogo) {
        val draft = local.value.identityDraft
        updateIdentityDraft(
            draft.copy(logo = draft.logo.copy(source = LogoSource.Builtin, builtin = builtin))
        )
    }

    fun setPendingImage(uri: String, width: Int, height: Int) = local.update {
        it.copy(pendingImageUri = uri, pendingImageWidth = width, pendingImageHeight = height)
    }

    fun applyImageLogo(fileName: String, crop: ImageCrop) {
        val draft = local.value.identityDraft
        val settings = draft.logo.builtin
        updateIdentityDraft(
            draft.copy(
                logo = TeamLogo(
                    source = LogoSource.Image,
                    builtin = settings,
                    image = ImageLogo(
                        fileName = fileName,
                        shape = settings.shape,
                        crop = crop.clamped(),
                        frame = settings.frame
                    )
                )
            )
        )
        local.update { it.copy(logoTab = LogoSource.Image, pendingImageUri = null) }
    }

    fun removeImageLogo() {
        val draft = local.value.identityDraft
        updateIdentityDraft(draft.copy(logo = draft.logo.copy(source = LogoSource.Builtin, image = null)))
        local.update { it.copy(logoTab = LogoSource.Builtin) }
    }

    /**
     * Persists only the logo, on top of the saved identity, so the editor session
     * (and any pending profile edits) is never lost or clobbered.
     */
    fun saveLogo(): Boolean {
        val teamId = local.value.identityTeamId ?: return false
        if (repository.snapshot.value.team(teamId)?.captain?.id != currentPlayer.id) {
            notify(R.string.shop_error_no_permission)
            return false
        }
        val logo = local.value.identityDraft.logo
        if (logo.builtin.invalidFields().isNotEmpty()) {
            local.update { it.copy(identityInvalid = it.identityInvalid + IdentityDraftField.Logo) }
            return false
        }
        val saved = repository.snapshot.value.team(teamId)?.identity ?: return false
        repository.updateTeamIdentity(teamId, saved.copy(logo = logo))
        local.update { it.copy(identityInvalid = it.identityInvalid - IdentityDraftField.Logo, pendingImageUri = null) }
        notify(R.string.logo_saved)
        return true
    }

    /** Cancel action of the logo editor: restores the last saved logo. */
    fun revertLogo() {
        val teamId = local.value.identityTeamId ?: return
        val saved = repository.snapshot.value.team(teamId)?.identity ?: return
        local.update { it.copy(identityDraft = it.identityDraft.copy(logo = saved.logo)) }
    }

    fun dismissNotice(id: Long) = local.update {
        if (it.notice?.id == id) it.copy(notice = null) else it
    }

    fun notify(textRes: Int) {
        local.update { it.copy(notice = TeamsNotice(++noticeId, textRes)) }
    }

    private fun report(result: TeamResult, successRes: Int) {
        val textRes = when (result) {
            is TeamResult.Success -> successRes
            is TeamResult.Failure -> result.error.messageRes
        }
        local.update { it.copy(notice = TeamsNotice(++noticeId, textRes)) }
    }
}
