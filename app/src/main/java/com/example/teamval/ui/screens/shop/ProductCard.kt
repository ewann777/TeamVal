package com.example.teamval.ui.screens.shop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.EquipTarget
import com.example.teamval.domain.model.ItemState
import com.example.teamval.domain.model.ShopItem
import com.example.teamval.ui.components.PrimaryButton
import com.example.teamval.ui.components.glassSurface
import com.example.teamval.ui.components.labelRes

/**
 * Tarjeta de producto de la tienda/inventario: arte, metadatos, precio y
 * acciones según el estado (Bloqueado / Disponible / Adquirido / Equipado).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductCard(
    item: ShopItem,
    state: ItemState,
    unlocked: Boolean,
    onDetails: () -> Unit,
    onBuy: () -> Unit,
    onEquip: () -> Unit,
    onUnequip: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenTeamEditor: (() -> Unit)? = null
) {
    val shape = MaterialTheme.shapes.large
    Box(modifier.fillMaxWidth().glassSurface(shape).testTag("product_${item.id}")) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box {
                CosmeticArt(item)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RarityBadge(item.rarity)
                ItemStateBadge(state)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetaText(item.category.labelRes, Modifier.weight(1f))
                if (item.binding?.target == EquipTarget.Team) {
                    Text(
                        stringResource(R.string.shop_team_item),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (item.binding?.target == EquipTarget.Profile) {
                    Text(
                        stringResource(R.string.shop_personal_item),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(item.name, style = MaterialTheme.typography.titleMedium)
            Text(
                item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item.isPremium) {
                    Text(
                        stringResource(R.string.shop_clp_price, item.premiumPriceClp ?: 0),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        stringResource(R.string.shop_premium),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    CoinAmount(item.priceCoins ?: 0)
                }
                if (item.isBundle) {
                    Text(
                        stringResource(R.string.shop_bundle),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                when (state) {
                    ItemState.Blocked -> OutlinedButton(onClick = {}, enabled = false) {
                        Text(stringResource(R.string.shop_premium))
                    }
                    ItemState.Available -> PrimaryButton(text = stringResource(R.string.shop_buy), onClick = onBuy)
                    ItemState.Owned -> {
                        if (item.binding?.target == EquipTarget.Team && onOpenTeamEditor != null) {
                            PrimaryButton(text = stringResource(R.string.inventory_open_team_editor), onClick = onOpenTeamEditor)
                        } else if (item.binding != null) {
                            PrimaryButton(text = stringResource(R.string.shop_equip), onClick = onEquip)
                        }
                    }
                    ItemState.Equipped -> {
                        if (item.binding?.target == EquipTarget.Team && onOpenTeamEditor != null) {
                            OutlinedButton(onClick = onOpenTeamEditor) { Text(stringResource(R.string.inventory_open_team_editor)) }
                        } else OutlinedButton(onClick = onUnequip) {
                            Text(stringResource(R.string.shop_unequip))
                        }
                    }
                }
                TextButton(onClick = onDetails) {
                    Text(stringResource(R.string.shop_details))
                }
            }
        }
        if (unlocked) UnlockOverlay(item.name)
    }
}
