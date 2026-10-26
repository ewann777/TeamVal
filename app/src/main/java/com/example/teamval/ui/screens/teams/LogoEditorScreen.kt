package com.example.teamval.ui.screens.teams

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.data.storage.ImageProbeResult
import com.example.teamval.data.storage.TeamLogoStorage
import com.example.teamval.domain.model.BuiltinLogo
import com.example.teamval.domain.model.ImageImportError
import com.example.teamval.domain.model.ImageLogo
import com.example.teamval.domain.model.LogoSource
import com.example.teamval.domain.model.TeamIdentity
import com.example.teamval.ui.components.AccentFilterChip
import com.example.teamval.ui.components.LogoEditorControls
import com.example.teamval.ui.components.PrimaryButton
import com.example.teamval.ui.components.StackPageHeader
import com.example.teamval.ui.components.StackPanel
import com.example.teamval.ui.components.TeamLogoView
import com.example.teamval.ui.components.labelRes
import com.example.teamval.ui.components.messageRes

/**
 * Logo editor with the two supported paths: option A creates an original logo
 * inside TeamVal, option B uploads an image with the Android Photo Picker (no
 * storage permissions requested).
 */
@Composable
fun LogoEditorScreen(
    identity: TeamIdentity,
    tab: LogoSource,
    onTabChange: (LogoSource) -> Unit,
    onBuiltinChange: (BuiltinLogo) -> Unit,
    onImagePicked: (Uri, Int, Int) -> Unit,
    onRemoveImage: () -> Unit,
    onSave: () -> Boolean,
    onDiscard: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val storage = remember(context) { TeamLogoStorage(context) }
    var pickError by remember { mutableStateOf<ImageImportError?>(null) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        when (val probe = storage.probe(uri)) {
            is ImageProbeResult.Err -> pickError = probe.error
            is ImageProbeResult.Ok -> {
                pickError = null
                onImagePicked(uri, probe.meta.width, probe.meta.height)
            }
        }
    }

    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 840.dp).fillMaxWidth()
                .verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            StackPageHeader(
                stringResource(R.string.logo_editor_title),
                stringResource(R.string.logo_editor_subtitle),
                onBack,
                R.string.back_to_scrims
            )

            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TeamLogoView(identity.logo, size = 160.dp, modifier = Modifier.testTag("logo_preview"))
                Text(
                    stringResource(
                        R.string.logo_summary,
                        stringResource(identity.logo.builtin.symbol.labelRes),
                        stringResource(identity.logo.builtin.shape.labelRes)
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.testTag("logo_summary")
                )
                Text(
                    stringResource(R.string.logo_preview),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AccentFilterChip(
                    selected = tab == LogoSource.Builtin,
                    onClick = { onTabChange(LogoSource.Builtin) },
                    label = stringResource(R.string.logo_option_create),
                    modifier = Modifier.testTag("tab_create")
                )
                AccentFilterChip(
                    selected = tab == LogoSource.Image,
                    onClick = { onTabChange(LogoSource.Image) },
                    label = stringResource(R.string.logo_option_upload),
                    modifier = Modifier.testTag("tab_upload")
                )
            }

            if (tab == LogoSource.Builtin) {
                LogoEditorControls(
                    logo = identity.logo.builtin,
                    onChange = onBuiltinChange,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                StackPanel {
                    Text(stringResource(R.string.image_rules_title), style = MaterialTheme.typography.titleSmall)
                    Text(
                        stringResource(R.string.image_rules_body),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    pickError?.let { error ->
                        Text(
                            stringResource(error.messageRes),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    val current: ImageLogo? = identity.logo.image
                    if (current != null) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TeamLogoView(identity.logo, size = 84.dp)
                            Text(
                                stringResource(R.string.image_crop_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                launcher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth().testTag("pick_image")
                        ) { Text(stringResource(R.string.image_pick)) }
                        OutlinedButton(
                            onClick = onRemoveImage,
                            modifier = Modifier.fillMaxWidth().testTag("remove_image")
                        ) { Text(stringResource(R.string.image_remove)) }
                    } else {
                        OutlinedButton(
                            onClick = {
                                launcher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth().testTag("pick_image")
                        ) { Text(stringResource(R.string.image_pick)) }
                    }
                    Text(
                        stringResource(R.string.image_moderation_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PrimaryButton(
                    text = stringResource(R.string.logo_save),
                    onClick = { onSave() },
                    modifier = Modifier.weight(1f).testTag("save_logo")
                )
                OutlinedButton(
                    onClick = {
                        onDiscard()
                        onBack()
                    },
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.identity_cancel)) }
            }
        }
    }
}
