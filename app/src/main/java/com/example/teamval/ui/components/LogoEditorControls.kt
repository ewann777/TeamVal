package com.example.teamval.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.BuiltinLogo
import com.example.teamval.domain.model.GradientStyle
import com.example.teamval.domain.model.LogoFrame
import com.example.teamval.domain.model.LogoShape
import com.example.teamval.domain.model.LogoSymbol
import com.example.teamval.ui.theme.LocalTeamValVisuals

public val primarySwatches = listOf(
    0xFF8B5CF6, 0xFF22D3EE, 0xFFF59E0B, 0xFFEF4444, 0xFF10B981, 0xFFF472B6, 0xFF38BDF8, 0xFFE5E7EB
)
public val secondarySwatches = listOf(
    0xFF22D3EE, 0xFFA78BFA, 0xFFFBBF24, 0xFFF472B6, 0xFF34D399, 0xFF60A5FA, 0xFFFF8A65, 0xFF111827
)
public val backgroundSwatches = listOf(
    0xFF171528, 0xFF0F172A, 0xFF1F2937, 0xFF3B0764, 0xFF064E3B, 0xFF450A0A, 0xFF111827, 0xFFF8FAFC
)

/** All controls of option A: creating a logo inside Teamval with live preview. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LogoEditorControls(
    logo: BuiltinLogo,
    onChange: (BuiltinLogo) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        SectionBlock(title = stringResource(R.string.logo_symbol)) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LogoSymbol.entries.forEach { symbol ->
                    AccentFilterChip(
                        selected = logo.symbol == symbol,
                        onClick = { onChange(logo.copy(symbol = symbol)) },
                        label = stringResource(symbol.labelRes)
                    )
                }
            }
        }

        SectionBlock(title = stringResource(R.string.logo_initials)) {
            OutlinedTextField(
                value = logo.initials,
                onValueChange = { input ->
                    if (input.length <= 4) onChange(logo.copy(initials = input))
                },
                label = { Text(stringResource(R.string.logo_initials)) },
                supportingText = { Text(stringResource(R.string.logo_initials_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("logo_initials")
            )
        }

        SectionBlock(title = stringResource(R.string.logo_gradient)) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GradientStyle.entries.forEach { style ->
                    AccentFilterChip(
                        selected = logo.gradient == style,
                        onClick = { onChange(logo.copy(gradient = style)) },
                        label = stringResource(style.labelRes)
                    )
                }
            }
        }

        SectionBlock(title = stringResource(R.string.logo_primary_color)) {
            ColorSwatchRow(
                swatches = primarySwatches,
                selected = logo.primaryColor,
                tag = "primary",
                onSelect = { onChange(logo.copy(primaryColor = it)) }
            )
        }
        SectionBlock(title = stringResource(R.string.logo_secondary_color)) {
            ColorSwatchRow(
                swatches = secondarySwatches,
                selected = logo.secondaryColor,
                tag = "secondary",
                onSelect = { onChange(logo.copy(secondaryColor = it)) }
            )
        }
        SectionBlock(title = stringResource(R.string.logo_background)) {
            ColorSwatchRow(
                swatches = backgroundSwatches,
                selected = logo.background,
                tag = "background",
                onSelect = { onChange(logo.copy(background = it)) }
            )
        }

        SectionBlock(title = stringResource(R.string.logo_frame)) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LogoFrame.entries.forEach { frame ->
                    AccentFilterChip(
                        selected = logo.frame == frame,
                        onClick = { onChange(logo.copy(frame = frame)) },
                        label = stringResource(frame.labelRes)
                    )
                }
            }
        }

        SectionBlock(title = stringResource(R.string.logo_shape)) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LogoShape.entries.forEach { shape ->
                    AccentFilterChip(
                        selected = logo.shape == shape,
                        onClick = { onChange(logo.copy(shape = shape)) },
                        label = stringResource(shape.labelRes)
                    )
                }
            }
        }

        SectionBlock(title = stringResource(R.string.logo_scale)) {
            Slider(
                value = logo.scale,
                onValueChange = { onChange(logo.copy(scale = it)) },
                valueRange = 0.6f..1.4f,
                steps = 15,
                modifier = Modifier.testTag("logo_scale")
            )
        }
    }
}

@Composable
private fun SectionBlock(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall)
        content()
    }
}

@Composable
fun ColorSwatchRow(
    swatches: List<Long>,
    selected: Long,
    tag: String,
    onSelect: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalTeamValVisuals.current.accentText
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        swatches.forEachIndexed { index, value ->
            val isSelected = value == selected
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(argb(value), CircleShape)
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) accent else MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape
                    )
                    .clickable { onSelect(value) }
                    .testTag("${tag}_swatch_$index"),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Text("✓", color = readableOnSwatch(argb(value)), style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

private fun readableOnSwatch(color: Color): Color {
    val luminance = 0.299f * color.red + 0.587f * color.green + 0.114f * color.blue
    return if (luminance > 0.55f) Color(0xFF15131F) else Color.White
}
