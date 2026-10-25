package com.example.teamval.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.ui.theme.LocalTeamValVisuals
import java.util.Locale
import kotlin.math.roundToInt

/** Formats a rating with the Spanish decimal separator: 4.8 -> "4,8". */
fun formatStars(value: Float): String =
    String.format(Locale.US, "%.1f", value).replace('.', ',')

/** Interactive row of 1–5 stars for one rating category. */
@Composable
fun StarRatingRow(
    categoryLabel: String,
    hint: String,
    tag: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                categoryLabel,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )
            Text(
                stringResource(R.string.rating_value_short, value),
                style = MaterialTheme.typography.labelLarge,
                color = LocalTeamValVisuals.current.accentText
            )
        }
        Text(
            hint,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(5) { index ->
                val selected = index < value
                IconButton(
                    onClick = { onValueChange(index + 1) },
                    modifier = Modifier.size(44.dp).testTag("star_${index + 1}_$tag")
                ) {
                    Icon(
                        painter = painterResource(
                            if (selected) R.drawable.ic_star else R.drawable.ic_star_outline
                        ),
                        contentDescription = stringResource(
                            R.string.rating_star_content, categoryLabel, index + 1
                        ),
                        tint = if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

/** Read-only star row used for reputation summaries. */
@Composable
fun StarsDisplay(value: Float, modifier: Modifier = Modifier, size: Dp = 18.dp) {
    val filled = value.roundToInt().coerceIn(0, 5)
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(5) { index ->
            Icon(
                painter = painterResource(
                    if (index < filled) R.drawable.ic_star else R.drawable.ic_star_outline
                ),
                contentDescription = null,
                modifier = Modifier.size(size),
                tint = if (index < filled) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline
            )
        }
    }
}
