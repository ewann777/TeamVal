package com.example.teamval.ui.components

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.example.teamval.R
import com.example.teamval.domain.model.GameMode
import com.example.teamval.domain.model.Region
import com.example.teamval.domain.model.StackFilters
import com.example.teamval.domain.model.ValorantRank

@Composable
fun StackFiltersPanel(filters: StackFilters, onChange: (StackFilters) -> Unit) {
    StackPanel(Modifier.testTag("stack_filters")) {
        SelectionField(
            label = stringResource(R.string.filter_rank),
            selected = filters.rank,
            options = listOf(null) + ValorantRank.entries,
            optionLabel = { stringResource(it?.labelRes ?: R.string.filter_all_ranks) },
            onSelect = { onChange(filters.copy(rank = it)) }
        )
        SelectionField(
            label = stringResource(R.string.filter_region),
            selected = filters.region,
            options = listOf(null) + Region.entries,
            optionLabel = { stringResource(it?.labelRes ?: R.string.filter_all_regions) },
            onSelect = { onChange(filters.copy(region = it)) }
        )
        SelectionField(
            label = stringResource(R.string.filter_mode),
            selected = filters.mode,
            options = listOf(null) + GameMode.entries,
            optionLabel = { stringResource(it?.labelRes ?: R.string.filter_all_modes) },
            onSelect = { onChange(filters.copy(mode = it)) }
        )
        TextButton(onClick = { onChange(StackFilters()) }, enabled = filters.isActive) {
            Text(stringResource(R.string.clear_filters))
        }
    }
}
