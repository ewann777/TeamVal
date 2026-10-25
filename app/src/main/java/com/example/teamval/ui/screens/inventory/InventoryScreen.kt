package com.example.teamval.ui.screens.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.ShopItem
import com.example.teamval.ui.components.StackPageHeader
import com.example.teamval.ui.components.glassSurface
import com.example.teamval.ui.screens.shop.CategoryChips
import com.example.teamval.ui.screens.shop.CoinAmount
import com.example.teamval.ui.screens.shop.EconomyUiState
import com.example.teamval.ui.screens.shop.ProductCard
import kotlinx.coroutines.delay

/**
 * Inventario de la etapa 5: todos los artículos con su estado (Bloqueado,
 * Disponible, Adquirido, Equipado), filtros por categoría, detalles y acciones
 * de equipar/desequipar.
 */
@Composable
fun InventoryScreen(
    state: EconomyUiState,
    catalog: List<ShopItem>,
    onBack: () -> Unit,
    onBuy: (ShopItem) -> Unit,
    onEquip: (ShopItem) -> Unit,
    onUnequip: (ShopItem) -> Unit,
    onOpenTeamEditor: () -> Unit,
    onClearUnlocked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var category by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmItem by androidx.compose.runtime.remember { mutableStateOf<ShopItem?>(null) }
    var detailsItem by androidx.compose.runtime.remember { mutableStateOf<ShopItem?>(null) }

    LaunchedEffect(state.unlockedItemId) {
        if (state.unlockedItemId != null) {
            delay(1_800)
            onClearUnlocked()
        }
    }

    val filtered = category?.let { name -> catalog.filter { it.category.name == name } } ?: catalog

    LazyVerticalGrid(
        columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(220.dp),
        modifier = modifier.fillMaxWidth().testTag("inventory_grid"),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                StackPageHeader(
                    title = stringResource(R.string.inventory_title),
                    subtitle = stringResource(R.string.inventory_subtitle),
                    onBack = onBack,
                    backLabel = R.string.shop_title
                )
                Box(
                    Modifier.fillMaxWidth().glassSurface(MaterialTheme.shapes.large).testTag("inventory_summary")
                ) {
                    Row(
                        Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        CoinAmount(state.wallet.balance)
                        Text(
                            stringResource(R.string.inventory_count, state.owned.size),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (state.owned.isEmpty()) {
                    Text(
                        stringResource(R.string.inventory_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("inventory_empty_hint")
                    )
                }
                Text(
                    stringResource(R.string.inventory_blocked_note),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    stringResource(R.string.inventory_filter),
                    style = MaterialTheme.typography.titleSmall
                )
                CategoryChips(category) { category = it }
            }
        }

        items(filtered, key = { it.id }) { item ->
            ProductCard(
                item = item,
                state = state.stateOf(item),
                unlocked = state.unlockedItemId == item.id,
                onDetails = { detailsItem = item },
                onBuy = { confirmItem = item },
                onEquip = { onEquip(item) },
                onUnequip = { onUnequip(item) },
                onOpenTeamEditor = onOpenTeamEditor
            )
        }

        if (filtered.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    stringResource(R.string.shop_no_items),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    confirmItem?.let { item ->
        com.example.teamval.ui.screens.shop.BuyConfirmDialog(
            item = item,
            onConfirm = {
                onBuy(item)
                confirmItem = null
            },
            onDismiss = { confirmItem = null }
        )
    }

    detailsItem?.let { item ->
        com.example.teamval.ui.screens.shop.ItemDetailsDialog(
            item = item,
            state = state.stateOf(item),
            catalog = catalog,
            onBuy = {
                confirmItem = item
                detailsItem = null
            },
            onEquip = {
                onEquip(item)
                detailsItem = null
            },
            onUnequip = {
                onUnequip(item)
                detailsItem = null
            },
            onDismiss = { detailsItem = null },
            onOpenTeamEditor = {
                detailsItem = null
                onOpenTeamEditor()
            }
        )
    }
}
