package com.example.teamval.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import com.example.teamval.domain.model.ImageFileRules
import com.example.teamval.domain.model.ImageImportError
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

sealed interface ImageProbeResult {
    data class Ok(val meta: ImageMeta) : ImageProbeResult
    data class Err(val error: ImageImportError) : ImageProbeResult
}

data class ImageMeta(val width: Int, val height: Int, val sizeBytes: Long, val displayName: String?, val mimeType: String?)

sealed interface ImageImportResult {
    data class Ok(val fileName: String, val width: Int, val height: Int) : ImageImportResult
    data class Err(val error: ImageImportError) : ImageImportResult
}

/**
 * Keeps an optimized copy of uploaded logos in the app's private storage
 * (`filesDir/team_logos`). Images are only downscaled (never upscaled) and always
 * stored preserving their aspect ratio; the visible crop is applied at render time.
 */
class TeamLogoStorage(private val context: Context) {

    private val directory: File
        get() = File(context.filesDir, "team_logos").apply { mkdirs() }

    private val cache = HashMap<String, Bitmap>()

    /** Validates format, size and decodability without keeping any data. */
    fun probe(uri: Uri): ImageProbeResult {
        var displayName: String? = null
        var sizeBytes = 0L
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
                if (nameIndex >= 0) displayName = cursor.getString(nameIndex)
                if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) sizeBytes = cursor.getLong(sizeIndex)
            }
        }
        val mimeType = context.contentResolver.getType(uri)
        ImageFileRules.validate(displayName, mimeType, sizeBytes)?.let { return ImageProbeResult.Err(it) }

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        runCatching {
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            return ImageProbeResult.Err(ImageImportError.CannotDecode)
        }
        return ImageProbeResult.Ok(
            ImageMeta(
                width = bounds.outWidth,
                height = bounds.outHeight,
                sizeBytes = sizeBytes,
                displayName = displayName,
                mimeType = mimeType
            )
        )
    }

    /** Imports the picked image into private storage as an optimized copy. */
    fun import(uri: Uri): ImageImportResult {
        val probe = probe(uri)
        if (probe is ImageProbeResult.Err) return ImageImportResult.Err(probe.error)
        val bitmap = decodeScaled(uri, MAX_SIDE) ?: return ImageImportResult.Err(ImageImportError.CannotDecode)
        val fileName = persistBitmap(bitmap)
        val (width, height) = readMeta(fileName)
        bitmap.recycle()
        return ImageImportResult.Ok(fileName, width, height)
    }

    /** Persists a bitmap (used by the pipeline and by tests with synthetic images). */
    fun persistBitmap(bitmap: Bitmap): String {
        val scaled = scaleDown(bitmap)
        val file = File(directory, "${UUID.randomUUID()}.$EXTENSION")
        FileOutputStream(file).use { output ->
            scaled.compress(compressionFormat(), QUALITY, output)
        }
        if (scaled !== bitmap) scaled.recycle()
        return file.name
    }

    fun bitmapFor(fileName: String): Bitmap? {
        cache[fileName]?.let { return it }
        val file = File(directory, fileName)
        if (!file.exists()) return null
        return decodeFile(file)?.also { cache[fileName] = it }
    }

    fun bitmapForUri(uri: Uri): Bitmap? = decodeScaled(uri, MAX_PREVIEW_SIDE)

    fun delete(fileName: String): Boolean {
        cache.remove(fileName)
        return File(directory, fileName).delete()
    }

    fun exists(fileName: String): Boolean = File(directory, fileName).exists()

    private fun readMeta(fileName: String): Pair<Int, Int> {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(File(directory, fileName).absolutePath, options)
        return (options.outWidth to options.outHeight)
    }

    private fun decodeFile(file: File): Bitmap? = runCatching {
        BitmapFactory.decodeFile(file.absolutePath)
    }.getOrNull()

    private fun decodeScaled(uri: Uri, maxSide: Int): Bitmap? = runCatching {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
        var sample = 1
        while (bounds.outWidth / (sample * 2) >= maxSide || bounds.outHeight / (sample * 2) >= maxSide) {
            sample *= 2
        }
        val options = BitmapFactory.Options().apply { inSampleSize = sample }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
    }.getOrNull()

    /** Preview images are only downscaled, never upscaled (KTX style kept explicit). */
    @Suppress("UseKtx")
    private fun scaleDown(bitmap: Bitmap): Bitmap {
        val longest = maxOf(bitmap.width, bitmap.height)
        if (longest <= MAX_SIDE) return bitmap
        val ratio = MAX_SIDE.toFloat() / longest
        val width = (bitmap.width * ratio).toInt().coerceAtLeast(1)
        val height = (bitmap.height * ratio).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    private fun compressionFormat(): Bitmap.CompressFormat =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSY
        else @Suppress("DEPRECATION") Bitmap.CompressFormat.WEBP

    companion object {
        const val MAX_SIDE = 1024
        const val MAX_PREVIEW_SIDE = 2048
        const val EXTENSION = "webp"
        private const val QUALITY = 90
    }
}
