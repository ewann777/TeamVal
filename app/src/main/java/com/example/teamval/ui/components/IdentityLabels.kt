package com.example.teamval.ui.components

import androidx.annotation.StringRes
import com.example.teamval.R
import com.example.teamval.domain.model.BannerStyle
import com.example.teamval.domain.model.CommentError
import com.example.teamval.domain.model.GradientStyle
import com.example.teamval.domain.model.ImageImportError
import com.example.teamval.domain.model.LogoFrame
import com.example.teamval.domain.model.LogoShape
import com.example.teamval.domain.model.LogoSymbol
import com.example.teamval.domain.model.MemberStyle
import com.example.teamval.domain.model.ProfileFrame
import com.example.teamval.domain.model.RatingBlock
import com.example.teamval.domain.model.RatingCategory
import com.example.teamval.domain.model.ReputationBadge
import com.example.teamval.domain.model.SectionLayout
import com.example.teamval.domain.model.TeamBackground
import com.example.teamval.domain.model.TeamBadge
import com.example.teamval.domain.repository.RatingError

val RatingCategory.labelRes: Int
    @StringRes get() = when (this) {
        RatingCategory.Respect -> R.string.rating_respect
        RatingCategory.Seriousness -> R.string.rating_seriousness
        RatingCategory.Punctuality -> R.string.rating_punctuality
        RatingCategory.Communication -> R.string.rating_communication
        RatingCategory.Sportsmanship -> R.string.rating_sportsmanship
    }

val RatingCategory.hintRes: Int
    @StringRes get() = when (this) {
        RatingCategory.Respect -> R.string.rating_respect_hint
        RatingCategory.Seriousness -> R.string.rating_seriousness_hint
        RatingCategory.Punctuality -> R.string.rating_punctuality_hint
        RatingCategory.Communication -> R.string.rating_communication_hint
        RatingCategory.Sportsmanship -> R.string.rating_sportsmanship_hint
    }

val LogoSymbol.labelRes: Int
    @StringRes get() = when (this) {
        LogoSymbol.Star -> R.string.symbol_star
        LogoSymbol.Bolt -> R.string.symbol_bolt
        LogoSymbol.Hexagon -> R.string.symbol_hexagon
        LogoSymbol.Shield -> R.string.symbol_shield
        LogoSymbol.Diamond -> R.string.symbol_diamond
        LogoSymbol.Triangle -> R.string.symbol_triangle
        LogoSymbol.Crosshair -> R.string.symbol_crosshair
        LogoSymbol.Orbit -> R.string.symbol_orbit
    }

val GradientStyle.labelRes: Int
    @StringRes get() = when (this) {
        GradientStyle.None -> R.string.option_none
        GradientStyle.Linear -> R.string.option_linear
        GradientStyle.Radial -> R.string.option_radial
    }

val LogoFrame.labelRes: Int
    @StringRes get() = when (this) {
        LogoFrame.None -> R.string.option_none
        LogoFrame.Solid -> R.string.frame_solid
        LogoFrame.Glow -> R.string.frame_glow
        LogoFrame.Double -> R.string.logo_frame_double
    }

val LogoShape.labelRes: Int
    @StringRes get() = when (this) {
        LogoShape.Circle -> R.string.shape_circle
        LogoShape.Square -> R.string.shape_square
    }

val BannerStyle.labelRes: Int
    @StringRes get() = when (this) {
        BannerStyle.Solid -> R.string.banner_solid
        BannerStyle.Gradient -> R.string.banner_gradient
        BannerStyle.Aurora -> R.string.banner_aurora
        BannerStyle.Grid -> R.string.banner_grid
        BannerStyle.Graphite -> R.string.banner_graphite
    }

val TeamBackground.labelRes: Int
    @StringRes get() = when (this) {
        TeamBackground.Plain -> R.string.background_plain
        TeamBackground.Dusk -> R.string.background_dusk
        TeamBackground.Aurora -> R.string.background_aurora
        TeamBackground.Grid -> R.string.background_grid
        TeamBackground.Atardecer -> R.string.background_atardecer
    }

val ProfileFrame.labelRes: Int
    @StringRes get() = when (this) {
        ProfileFrame.None -> R.string.option_none
        ProfileFrame.Ring -> R.string.frame_ring
        ProfileFrame.Glow -> R.string.frame_glow
        ProfileFrame.Gradient -> R.string.frame_gradient
        ProfileFrame.Orbit -> R.string.frame_orbit
        ProfileFrame.Prism -> R.string.frame_prism
    }

val SectionLayout.labelRes: Int
    @StringRes get() = when (this) {
        SectionLayout.Standard -> R.string.layout_standard
        SectionLayout.Compact -> R.string.layout_compact
        SectionLayout.Spotlight -> R.string.layout_spotlight
    }

val MemberStyle.labelRes: Int
    @StringRes get() = when (this) {
        MemberStyle.Grid -> R.string.member_grid
        MemberStyle.List -> R.string.member_list
        MemberStyle.Cards -> R.string.member_cards
    }

val TeamBadge.labelRes: Int
    @StringRes get() = when (this) {
        TeamBadge.FairPlay -> R.string.badge_fair_play
        TeamBadge.Punctual -> R.string.badge_punctual
        TeamBadge.Communicative -> R.string.badge_communicative
        TeamBadge.Veteran -> R.string.badge_veteran
        TeamBadge.Rising -> R.string.badge_rising
        TeamBadge.Organized -> R.string.badge_organized
        TeamBadge.Flame -> R.string.badge_flame
    }

val ReputationBadge.labelRes: Int
    @StringRes get() = when (this) {
        ReputationBadge.Trusted -> R.string.badge_trusted
        ReputationBadge.FairPlay -> R.string.badge_fair_play
        ReputationBadge.Punctual -> R.string.badge_punctual
        ReputationBadge.Communicative -> R.string.badge_communicative
        ReputationBadge.Active -> R.string.badge_active
    }

val RatingError.messageRes: Int
    @StringRes get() = when (this) {
        RatingError.NoTeam -> R.string.rating_block_no_team
        RatingError.NoScrim -> R.string.rating_block_no_scrim
        RatingError.NotCompleted -> R.string.rating_block_not_completed
        RatingError.NotParticipant -> R.string.rating_block_not_participant
        RatingError.OwnTeam -> R.string.rating_block_own_team
        RatingError.AlreadyRated -> R.string.rating_block_already
        RatingError.InvalidScores -> R.string.rating_block_invalid_scores
        RatingError.CommentRejected -> R.string.rating_error_generic
        RatingError.NotFound -> R.string.rating_error_generic
        RatingError.NoPermission -> R.string.rating_error_generic
    }

val RatingBlock.messageRes: Int
    @StringRes get() = when (this) {
        RatingBlock.NoTeam -> R.string.rating_block_no_team
        RatingBlock.NoScrim -> R.string.rating_block_no_scrim
        RatingBlock.NotCompleted -> R.string.rating_block_not_completed
        RatingBlock.NotParticipant -> R.string.rating_block_not_participant
        RatingBlock.OwnTeam -> R.string.rating_block_own_team
        RatingBlock.AlreadyRated -> R.string.rating_block_already
    }

val ImageImportError.messageRes: Int
    @StringRes get() = when (this) {
        ImageImportError.UnsupportedFormat -> R.string.image_pick_error_format
        ImageImportError.TooLarge -> R.string.image_pick_error_size
        ImageImportError.Empty -> R.string.image_pick_error_empty
        ImageImportError.CannotDecode -> R.string.image_pick_error_decode
    }

val CommentError.messageRes: Int
    @StringRes get() = when (this) {
        CommentError.TooShort -> R.string.rating_comment_too_short
        CommentError.TooLong -> R.string.rating_comment_too_long
        CommentError.Link -> R.string.rating_comment_link
        CommentError.BlockedContent -> R.string.rating_comment_blocked
    }
