package com.example.teamval.ui.components

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.teamval.R
import com.example.teamval.domain.model.ItemCategory
import com.example.teamval.domain.model.ItemRarity
import com.example.teamval.domain.model.ItemState
import com.example.teamval.domain.model.TransactionKind

val ItemCategory.labelRes: Int
    @StringRes get() = when (this) {
        ItemCategory.AvatarFrame -> R.string.cat_avatar_frame
        ItemCategory.ProfileBackground -> R.string.cat_profile_background
        ItemCategory.Banner -> R.string.cat_banner
        ItemCategory.Badge -> R.string.cat_badge
        ItemCategory.Sticker -> R.string.cat_sticker
        ItemCategory.Pet -> R.string.cat_pet
        ItemCategory.PlayerCard -> R.string.cat_player_card
        ItemCategory.TeamDecoration -> R.string.cat_team_decoration
        ItemCategory.TeamLogoFrame -> R.string.cat_team_logo_frame
        ItemCategory.VisualTheme -> R.string.cat_visual_theme
        ItemCategory.Bundle -> R.string.cat_bundle
    }

val ItemRarity.labelRes: Int
    @StringRes get() = when (this) {
        ItemRarity.Common -> R.string.rarity_common
        ItemRarity.Rare -> R.string.rarity_rare
        ItemRarity.Epic -> R.string.rarity_epic
        ItemRarity.Legendary -> R.string.rarity_legendary
    }

val ItemState.labelRes: Int
    @StringRes get() = when (this) {
        ItemState.Blocked -> R.string.state_blocked
        ItemState.Available -> R.string.state_available
        ItemState.Owned -> R.string.state_owned
        ItemState.Equipped -> R.string.state_equipped
    }

val TransactionKind.labelRes: Int
    @StringRes get() = when (this) {
        TransactionKind.StartingBonus -> R.string.tx_bonus
        TransactionKind.Reward -> R.string.tx_reward
        TransactionKind.Purchase -> R.string.tx_purchase
    }

/**
 * Etiqueta de opción de personalización; cuando la variante es un cosmético de
 * la tienda que aún no posees, añade el aviso de bloqueo.
 */
@Composable
fun shopChipLabel(label: String, variant: String, lockedVariants: Set<String>): String =
    if (variant in lockedVariants) label + " · " + stringResource(R.string.shop_option_locked) else label
