package com.example.teamval.ui.screens.wallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.CoinTransaction
import com.example.teamval.domain.model.TransactionKind
import com.example.teamval.domain.model.WalletSnapshot
import com.example.teamval.ui.components.StackPageHeader
import com.example.teamval.ui.components.glassSurface
import com.example.teamval.ui.components.labelRes
import com.example.teamval.ui.screens.shop.CoinAmount
import com.example.teamval.ui.theme.LocalTeamValVisuals
import java.text.DateFormat
import java.util.Date

/**
 * Monedero de TeamVal Coins: saldo, monedas obtenidas y gastadas, historial de
 * movimientos y recompensas recibidas. Las monedas no tienen valor real.
 */
@Composable
fun WalletScreen(
    wallet: WalletSnapshot,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val visuals = LocalTeamValVisuals.current
    val received = wallet.received

    LazyColumn(
        modifier = modifier.fillMaxWidth().testTag("wallet_list"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            StackPageHeader(
                title = stringResource(R.string.wallet_title),
                subtitle = stringResource(R.string.wallet_subtitle),
                onBack = onBack,
                backLabel = R.string.shop_title
            )
        }
        item {
            Column(
                Modifier.fillMaxWidth().glassSurface(MaterialTheme.shapes.large).padding(18.dp).testTag("wallet_summary"),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    stringResource(R.string.wallet_balance),
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
                        modifier = Modifier.size(28.dp),
                        tint = visuals.accentText
                    )
                    Text(
                        wallet.balance.toString(),
                        style = MaterialTheme.typography.headlineLarge,
                        color = visuals.accentText
                    )
                    Text(
                        stringResource(R.string.coin_name),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            wallet.earned.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            stringResource(R.string.wallet_earned),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        Text(wallet.spent.toString(), style = MaterialTheme.typography.titleLarge)
                        Text(
                            stringResource(R.string.wallet_spent),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    stringResource(R.string.wallet_disclaimer),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Text(stringResource(R.string.wallet_received), style = MaterialTheme.typography.titleMedium)
        }
        if (received.isEmpty()) {
            item {
                Text(
                    stringResource(R.string.wallet_received_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(received, key = { it.id }) { tx -> TransactionRow(tx) }
        }

        item {
            Text(stringResource(R.string.wallet_history), style = MaterialTheme.typography.titleMedium)
        }
        if (wallet.transactions.isEmpty()) {
            item {
                Text(
                    stringResource(R.string.wallet_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(wallet.transactions, key = { "history_${it.id}" }) { tx -> TransactionRow(tx) }
        }
    }
}

@Composable
private fun TransactionRow(tx: CoinTransaction) {
    val visuals = LocalTeamValVisuals.current
    val icon = when (tx.kind) {
        TransactionKind.Purchase -> R.drawable.ic_store
        TransactionKind.Reward -> R.drawable.ic_gift
        TransactionKind.StartingBonus -> R.drawable.ic_coin
    }
    val amountColor = if (tx.amount >= 0) visuals.accentText else MaterialTheme.colorScheme.onSurface
    Row(
        Modifier
            .fillMaxWidth()
            .glassSurface(MaterialTheme.shapes.medium)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = stringResource(tx.kind.labelRes),
            modifier = Modifier.size(22.dp),
            tint = visuals.accentText
        )
        Column(Modifier.weight(1f)) {
            Text(tx.description, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
            Text(
                stringResource(tx.kind.labelRes) + " · " +
                    DateFormat.getDateInstance(DateFormat.SHORT).format(Date(tx.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            (if (tx.amount > 0) "+" else "") + tx.amount.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = amountColor
        )
    }
}
