package com.example.teamval.ui.screens.shop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.ItemState
import com.example.teamval.domain.model.EquipTarget
import com.example.teamval.domain.model.ShopItem
import com.example.teamval.ui.components.PrimaryButton
import com.example.teamval.ui.components.labelRes

/** Confirmación visual antes de comprar con monedas ficticias. */
@Composable
fun BuyConfirmDialog(item: ShopItem, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.shop_confirm_title)) },
        text = {
            Text(stringResource(R.string.shop_confirm_message, item.name, item.priceCoins ?: 0))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(R.string.shop_confirm_action)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.shop_close)) }
        }
    )
}

/** Detalle y vista previa ampliada de un artículo. */
@Composable
fun ItemDetailsDialog(
    item: ShopItem,
    state: ItemState,
    catalog: List<ShopItem>,
    onBuy: () -> Unit,
    onEquip: () -> Unit,
    onUnequip: () -> Unit,
    onDismiss: () -> Unit,
    onOpenTeamEditor: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(item.name) },
        text = {
            Column(
                Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CosmeticArt(item)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RarityBadge(item.rarity)
                    ItemStateBadge(state)
                }
                Text(
                    stringResource(item.category.labelRes),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(item.description, style = MaterialTheme.typography.bodyMedium)
                if (item.isBundle && item.contains.isNotEmpty()) {
                    val names = item.contains.mapNotNull { id -> catalog.find { it.id == id }?.name }
                    if (names.isNotEmpty()) {
                        Text(
                            stringResource(R.string.shop_contains, names.joinToString(", ")),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.isPremium) {
                        Text(
                            stringResource(R.string.shop_clp_price, item.premiumPriceClp ?: 0),
                            style = MaterialTheme.typography.titleMedium
                        )
                    } else {
                        CoinAmount(item.priceCoins ?: 0)
                    }
                }
                if (item.isPremium) {
                    Text(
                        stringResource(R.string.shop_premium_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    stringResource(R.string.shop_no_lock_loot),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            when {
                state == ItemState.Blocked -> OutlinedButton(onClick = {}, enabled = false) {
                    Text(stringResource(R.string.shop_premium))
                }
                state == ItemState.Available -> PrimaryButton(text = stringResource(R.string.shop_buy), onClick = onBuy)
                item.binding?.target == EquipTarget.Team ->
                    PrimaryButton(text = stringResource(R.string.inventory_open_team_editor), onClick = onOpenTeamEditor)
                state == ItemState.Owned && item.binding != null ->
                    PrimaryButton(text = stringResource(R.string.shop_equip), onClick = onEquip)
                state == ItemState.Equipped ->
                    OutlinedButton(onClick = onUnequip) { Text(stringResource(R.string.shop_unequip)) }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.shop_close)) }
        }
    )
}
