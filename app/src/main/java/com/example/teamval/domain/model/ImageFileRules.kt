package com.example.teamval.domain.model

/**
 * Local validation for images picked from the gallery (demo rules). Real content
 * moderation only becomes possible once the backend review flow exists.
 */
object ImageFileRules {
    const val MAX_BYTES = 10L * 1024 * 1024
    const val RECOMMENDED_SIDE = 512

    val supportedExtensions = listOf("jpg", "jpeg", "png", "webp")
    private val supportedMimes = listOf("image/jpeg", "image/png", "image/webp")

    fun validate(displayName: String?, mimeType: String?, sizeBytes: Long): ImageImportError? {
        if (sizeBytes <= 0L) return ImageImportError.Empty
        if (sizeBytes > MAX_BYTES) return ImageImportError.TooLarge
        val mime = mimeType?.trim()?.lowercase().orEmpty()
        val extension = displayName?.substringAfterLast('.', "")?.trim()?.lowercase().orEmpty()
        return when {
            mime.isNotEmpty() && mime !in supportedMimes -> ImageImportError.UnsupportedFormat
            mime.isEmpty() && extension !in supportedExtensions -> ImageImportError.UnsupportedFormat
            else -> null
        }
    }

    fun isRecommended(width: Int, height: Int): Boolean =
        width >= RECOMMENDED_SIDE && height >= RECOMMENDED_SIDE

    fun isSquare(width: Int, height: Int): Boolean = width == height
}

enum class ImageImportError { UnsupportedFormat, TooLarge, Empty, CannotDecode }
