package com.example.teamval.ui.screens.teams

import android.net.Uri
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.data.storage.ImageImportResult
import com.example.teamval.data.storage.TeamLogoStorage
import com.example.teamval.domain.model.ImageCrop
import com.example.teamval.domain.model.ImageFileRules
import com.example.teamval.domain.model.LogoShape
import com.example.teamval.ui.components.ImageCropView
import com.example.teamval.ui.components.PrimaryButton
import com.example.teamval.ui.components.StackPageHeader
import com.example.teamval.ui.components.StackPanel
import com.example.teamval.ui.components.messageRes

/**
 * Proportional crop screen for uploaded images: pan and zoom inside the square
 * frame, with circular and square previews. The import keeps an optimized copy in
 * private storage; nothing is ever stretched.
 */
@Composable
fun ImageCropScreen(
    uri: String?,
    imageWidth: Int,
    imageHeight: Int,
    shape: LogoShape,
    onApplied: (String, ImageCrop) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val storage = remember(context) { TeamLogoStorage(context) }
    val parsedUri = remember(uri) { uri?.let(Uri::parse) }
    val bitmap = remember(uri) { parsedUri?.let { storage.bitmapForUri(it)?.asImageBitmap() } }
    var crop by remember(uri) { mutableStateOf(ImageCrop()) }
    var errorRes by remember { mutableStateOf<Int?>(null) }

    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 840.dp).fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            StackPageHeader(
                stringResource(R.string.image_crop_title),
                stringResource(R.string.image_crop_subtitle),
                onBack,
                R.string.back_to_scrims
            )
            Text(
                stringResource(R.string.image_crop_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (imageWidth > 0 && imageHeight > 0) {
                Text(
                    stringResource(
                        R.string.image_size_note,
                        imageWidth,
                        imageHeight,
                        stringResource(
                            if (ImageFileRules.isRecommended(imageWidth, imageHeight)) {
                                R.string.image_status_ok
                            } else {
                                R.string.image_status_low
                            }
                        )
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                stringResource(R.string.image_current),
                style = MaterialTheme.typography.labelLarge
            )

            ImageCropView(
                image = bitmap,
                crop = crop,
                shape = shape,
                frameSize = 260.dp,
                onCropChange = { crop = it },
                modifier = Modifier.testTag("crop_view")
            )

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ImageCropView(
                        image = bitmap,
                        crop = crop,
                        shape = LogoShape.Circle,
                        frameSize = 116.dp,
                        onCropChange = {},
                        modifier = Modifier.testTag("preview_circle"),
                        interactive = false
                    )
                    Text(
                        stringResource(R.string.image_preview_circle),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ImageCropView(
                        image = bitmap,
                        crop = crop,
                        shape = LogoShape.Square,
                        frameSize = 116.dp,
                        onCropChange = {},
                        modifier = Modifier.testTag("preview_square"),
                        interactive = false
                    )
                    Text(
                        stringResource(R.string.image_preview_square),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            errorRes?.let { res ->
                Text(
                    stringResource(res),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            StackPanel(Modifier.fillMaxWidth()) {
                Text(
                    stringResource(R.string.image_moderation_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { crop = ImageCrop() },
                        modifier = Modifier.weight(1f).testTag("reset_crop")
                    ) { Text(stringResource(R.string.image_crop_reset)) }
                    PrimaryButton(
                        text = stringResource(R.string.image_crop_apply),
                        onClick = {
                            val target = parsedUri
                            if (target == null) {
                                onBack()
                                return@PrimaryButton
                            }
                            when (val result = storage.import(target)) {
                                is ImageImportResult.Err -> errorRes = result.error.messageRes
                                is ImageImportResult.Ok -> onApplied(result.fileName, crop)
                            }
                        },
                        modifier = Modifier.weight(1f).testTag("apply_image")
                    )
                }
            }
        }
    }
}
