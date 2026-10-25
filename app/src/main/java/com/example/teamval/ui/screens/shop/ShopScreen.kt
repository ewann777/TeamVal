package com.example.teamval.ui.screens.shop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.ItemCategory
import com.example.teamval.domain.model.ItemState
import com.example.teamval.domain.model.ShopItem
import com.example.teamval.ui.components.AccentFilterChip
import com.example.teamval.ui.components.StackPageHeader
import com.example.teamval.ui.components.glassSurface
import com.example.teamval.ui.components.labelRes
import com.example.teamval.ui.theme.LocalTeamValVisuals
import kotlinx.coroutines.delay

/**
 * Tienda de cosméticos (etapa 5): saldo, destacados, categorías deslizables,
 * productos con vista previa, confirmación de compra y animación de
 * desbloqueo. Todas las operaciones delegan en [EconomyViewModel].
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShopScreen(
    state: EconomyUiState,
    catalog: List<ShopItem>,
    onBuy: (ShopItem) -> Unit,
    onEquip: (ShopItem) -> Unit,
    onUnequip: (ShopItem) -> Unit,
    onOpenInventory: () -> Unit,
    onOpenWallet: () -> Unit,
    onOpenRewards: () -> Unit,
    onOpenTeamEditor: () -> Unit,
    onClearUnlocked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val visuals = LocalTeamValVisuals.current
    var category by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmItem by remember { mutableStateOf<ShopItem?>(null) }
    var detailsItem by remember { mutableStateOf<ShopItem?>(null) }

    LaunchedEffect(state.unlockedItemId) {
        if (state.unlockedItemId != null) {
            delay(1_800)
            onClearUnlocked()
        }
    }

    val featured = catalog.filter { it.featured }
    val filtered = category?.let { name -> catalog.filter { it.category.name == name } } ?: catalog

    LazyVerticalGrid(
        columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(220.dp),
        modifier = modifier.fillMaxWidth().testTag("shop_grid"),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                StackPageHeader(
                    title = stringResource(R.string.shop_title),
                    subtitle = stringResource(R.string.shop_subtitle)
                )
                androidx.compose.foundation.layout.Box(
                    Modifier.fillMaxWidth().glassSurface(MaterialTheme.shapes.large).testTag("shop_balance")
                ) {
                    Column(
                        Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            stringResource(R.string.shop_balance),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_coin),
                                contentDescription = null,
                                modifier = Modifier.size(26.dp),
                                tint = visuals.accentText
                            )
                            Text(
                                state.wallet.balance.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                color = visuals.accentText
                            )
                            Text(
                                stringResource(R.string.coin_name),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = onOpenWallet) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_coin),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(stringResource(R.string.wallet_open))
                            }
                            OutlinedButton(onClick = onOpenInventory) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_inventory),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(stringResource(R.string.shop_open_inventory))
                            }
                            OutlinedButton(onClick = onOpenRewards) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_gift),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(stringResource(R.string.shop_open_rewards))
                            }
                        }
                    }
                }
            }
        }

        if (featured.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(stringResource(R.string.shop_featured), style = MaterialTheme.typography.titleMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(featured, key = { it.id }) { item ->
                            val itemState = state.stateOf(item)
                            Column(
                                Modifier
                                    .width(230.dp)
                                    .glassSurface(MaterialTheme.shapes.large)
                                    .padding(12.dp)
                                    .testTag("featured_${item.id}"),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CosmeticArt(item, compact = true)
                                Text(
                                    item.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!item.isPremium) CoinAmount(item.priceCoins ?: 0)
                                    else Text(
                                        stringResource(R.string.shop_clp_price, item.premiumPriceClp ?: 0),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(onClick = { detailsItem = item }, Modifier.weight(1f)) {
                                        Text(stringResource(R.string.shop_details), maxLines = 1)
                                    }
                                    if (itemState == ItemState.Available) {
                                        OutlinedButton(onClick = { confirmItem = item }, Modifier.weight(1f)) {
                                            Text(stringResource(R.string.shop_buy), maxLines = 1)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) { CategoryChips(category) { category = it } }

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
        BuyConfirmDialog(
            item = item,
            onConfirm = {
                onBuy(item)
                confirmItem = null
            },
            onDismiss = { confirmItem = null }
        )
    }

    detailsItem?.let { item ->
        ItemDetailsDialog(
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
