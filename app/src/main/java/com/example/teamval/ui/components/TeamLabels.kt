package com.example.teamval.ui.components

import androidx.annotation.StringRes
import com.example.teamval.R
import com.example.teamval.domain.model.ScrimStatus
import com.example.teamval.domain.model.TeamRole
import com.example.teamval.domain.repository.TeamError

val TeamRole.labelRes: Int
    @StringRes get() = when (this) {
        TeamRole.Captain -> R.string.role_captain
        TeamRole.Member -> R.string.role_member
    }

val ScrimStatus.labelRes: Int
    @StringRes get() = when (this) {
        ScrimStatus.Proposed -> R.string.scrim_proposed
        ScrimStatus.Confirmed -> R.string.scrim_confirmed
        ScrimStatus.Completed -> R.string.scrim_completed
        ScrimStatus.Cancelled -> R.string.scrim_cancelled
    }

val TeamError.messageRes: Int
    @StringRes get() = when (this) {
        TeamError.InvalidTeam -> R.string.error_invalid_team
        TeamError.InvalidScrim -> R.string.error_invalid_scrim
        TeamError.InvalidResult -> R.string.error_invalid_result
        TeamError.NotFound -> R.string.error_not_found
        TeamError.AlreadyInTeam -> R.string.error_already_in_team
        TeamError.NoTeam -> R.string.error_no_team
        TeamError.OpponentIsOwn -> R.string.error_opponent_is_own
        TeamError.NotParticipant -> R.string.error_not_participant
        TeamError.WrongStatus -> R.string.error_wrong_status
        TeamError.DuplicateScrim -> R.string.error_duplicate_scrim
    }
