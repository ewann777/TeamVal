package com.example.teamval.ui.components

import androidx.annotation.StringRes
import com.example.teamval.R
import com.example.teamval.domain.model.GameMode
import com.example.teamval.domain.model.Region
import com.example.teamval.domain.model.RequestStatus
import com.example.teamval.domain.model.StackCategory
import com.example.teamval.domain.model.ValorantRank
import com.example.teamval.domain.repository.StackError

val ValorantRank.labelRes: Int
    @StringRes get() = when (this) {
        ValorantRank.Unranked -> R.string.rank_unranked
        ValorantRank.Iron -> R.string.rank_iron
        ValorantRank.Bronze -> R.string.rank_bronze
        ValorantRank.Silver -> R.string.rank_silver
        ValorantRank.Gold -> R.string.rank_gold
        ValorantRank.Platinum -> R.string.rank_platinum
        ValorantRank.Diamond -> R.string.rank_diamond
        ValorantRank.Ascendant -> R.string.rank_ascendant
        ValorantRank.Immortal -> R.string.rank_immortal
        ValorantRank.Radiant -> R.string.rank_radiant
    }

val Region.labelRes: Int
    @StringRes get() = when (this) {
        Region.LatamNorth -> R.string.region_lan
        Region.LatamSouth -> R.string.region_las
        Region.Brazil -> R.string.region_br
        Region.NorthAmerica -> R.string.region_na
        Region.Europe -> R.string.region_eu
        Region.AsiaPacific -> R.string.region_ap
        Region.Korea -> R.string.region_kr
    }

val GameMode.labelRes: Int
    @StringRes get() = when (this) {
        GameMode.Competitive -> R.string.mode_competitive
        GameMode.Unrated -> R.string.mode_unrated
        GameMode.Swiftplay -> R.string.mode_swiftplay
        GameMode.SpikeRush -> R.string.mode_spike_rush
        GameMode.TeamDeathmatch -> R.string.mode_team_deathmatch
    }

val StackCategory.labelRes: Int
    @StringRes get() = when (this) {
        StackCategory.Duo -> R.string.stack_tab_duo
        StackCategory.Trio -> R.string.stack_tab_trio
        StackCategory.Stack -> R.string.stack_tab_stack
    }

val RequestStatus.labelRes: Int
    @StringRes get() = when (this) {
        RequestStatus.Pending -> R.string.request_pending
        RequestStatus.Accepted -> R.string.request_accepted
        RequestStatus.Rejected -> R.string.request_rejected
    }

val StackError.messageRes: Int
    @StringRes get() = when (this) {
        StackError.InvalidDraft -> R.string.error_invalid_draft
        StackError.NotFound -> R.string.error_not_found
        StackError.NotOwner -> R.string.error_not_owner
        StackError.OwnStack -> R.string.error_own_stack
        StackError.Full -> R.string.error_stack_full
        StackError.DuplicateRequest -> R.string.error_duplicate_request
        StackError.AlreadyMember -> R.string.error_already_member
        StackError.RequestResolved -> R.string.error_request_resolved
        StackError.NoCandidates -> R.string.error_no_candidates
    }
