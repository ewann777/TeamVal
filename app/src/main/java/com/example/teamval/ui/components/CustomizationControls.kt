package com.example.teamval.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.ui.theme.LocalTeamValVisuals

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> ChoiceChips(
    options: List<T>,
    selected: (T) -> Boolean,
    onSelect: (T) -> Unit,
    label: @Composable (T) -> String,
    enabled: (T) -> Boolean = { true }
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { value ->
            AccentFilterChip(
                selected = selected(value),
                onClick = { onSelect(value) },
                label = label(value),
                enabled = enabled(value)
            )
        }
    }
}

/** Sección con panel de vidrio y encabezado marcado por la barra de acento. */
@Composable
fun SettingsSection(title: String, description: String? = null, content: @Composable ColumnScope.() -> Unit) {
    StackPanel {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AccentBar(height = 22.dp)
            Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { heading() })
        }
        description?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        content()
    }
}

@Composable
fun CustomizationPage(
    title: String,
    saving: Boolean,
    messageRes: Int?,
    onDiscard: () -> Unit,
    onSave: () -> Unit,
    messageIsError: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    BackHandler { if (!saving) onDiscard() }
    val visuals = LocalTeamValVisuals.current
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(Modifier.widthIn(max = 840.dp).fillMaxSize()) {
            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(24.dp).testTag("customization_content"),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AccentBar(height = 30.dp)
                    Text(title, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.semantics { heading() })
                }
                Text(
                    stringResource(R.string.preview_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp)
                )
                content()
            }
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                modifier = Modifier.shadow(
                    elevation = 14.dp,
                    shape = RectangleShape,
                    ambientColor = visuals.shadowColor,
                    spotColor = visuals.shadowColor
                )
            ) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HairlineDivider()
                    val message = messageRes?.let { stringResource(it) }
                    AnimatedVisibility(
                        visible = message != null,
                        enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
                    ) {
                        SaveMessage(message.orEmpty(), isError = messageIsError)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(onClick = onDiscard, enabled = !saving, modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.editor_close))
                        }
                        PrimaryButton(
                            text = stringResource(if (saving) R.string.preferences_saving else R.string.preferences_save),
                            onClick = onSave,
                            enabled = !saving,
                            modifier = Modifier.weight(1f).testTag("save_preferences")
                        )
                    }
                }
            }
        }
    }
}

/**
 * Confirmación breve y elegante que aparece —con una animación de entrada—
 * únicamente cuando el guardado se completó correctamente; los avisos de error
 * se distinguen por su color. Al volver a modificar cualquier cosa el estado
 * limpia el mensaje y la tira desaparece.
 */
@Composable
private fun SaveMessage(message: String, isError: Boolean) {
    val colors = MaterialTheme.colorScheme
    val shape = MaterialTheme.shapes.medium
    Surface(
        color = if (isError) colors.errorContainer else colors.primaryContainer,
        contentColor = if (isError) colors.onErrorContainer else colors.onPrimaryContainer,
        shape = shape,
        border = BorderStroke(
            1.dp,
            if (isError) colors.error.copy(alpha = 0.45f) else colors.primary.copy(alpha = 0.45f)
        ),
        modifier = Modifier.fillMaxWidth().testTag("preferences_message")
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AccentBar(height = 16.dp)
            Text(message, style = MaterialTheme.typography.labelLarge)
        }
    }
}
