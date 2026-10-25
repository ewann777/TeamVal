package com.example.teamval.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Stage 4.1 rules: proportional image crop across aspect ratios, comment policy,
 * anti-abuse rating rules and local image validation.
 */
class ImageCropMathTest {

    private val aspectRatios = listOf(
        1080f to 1080f, // 1:1
        1920f to 1080f, // 16:9 landscape
        1080f to 1920f, // 9:16 portrait
        800f to 500f, // 8:5 landscape
        500f to 800f // 5:8 portrait
    )

    @Test
    fun sourceRectKeepsSquareWindowForEveryAspectRatio() {
        aspectRatios.forEach { (width, height) ->
            listOf(1f, 1.5f, 2f, 3f, 4f).forEach { zoom ->
                val crop = ImageCrop(scale = zoom, offsetX = 0.5f, offsetY = -0.5f)
                val rect = ImageCropMath.sourceRect(width, height, 512f, crop)
                // The frame is 1:1, so the visible window must be square: no distortion.
                assertEquals(
                    "Square window for ${width}x$height at zoom $zoom",
                    rect.width, rect.height, 0.01f
                )
                assertTrue(rect.x >= 0f)
                assertTrue(rect.y >= 0f)
                assertTrue(rect.x + rect.width <= width + 0.01f)
                assertTrue(rect.y + rect.height <= height + 0.01f)
            }
        }
    }

    @Test
    fun centerCropIsCenteredByDefault() {
        aspectRatios.forEach { (width, height) ->
            val rect = ImageCropMath.sourceRect(width, height, 512f, ImageCrop())
            assertEquals((width - rect.width) / 2f, rect.x, 0.5f)
            assertEquals((height - rect.height) / 2f, rect.y, 0.5f)
        }
    }

    @Test
    fun zoomShrinksTheVisibleWindow() {
        val wide = 1920f to 1080f
        val small = ImageCropMath.sourceRect(wide.first, wide.second, 512f, ImageCrop(scale = 1f))
        val large = ImageCropMath.sourceRect(wide.first, wide.second, 512f, ImageCrop(scale = 4f))
        assertTrue(large.width < small.width)
        assertEquals(small.width / 4f, large.width, 0.5f)
    }

    @Test
    fun draggingRightMovesTheWindowLeft() {
        val width = 1920f
        val height = 1080f
        val start = ImageCrop()
        val dragged = ImageCropMath.pan(start, width, height, 512f, panXPx = 120f, panYPx = 0f)
        val before = ImageCropMath.sourceRect(width, height, 512f, start)
        val after = ImageCropMath.sourceRect(width, height, 512f, dragged)
        assertTrue(after.x < before.x)
        // Vertical offset must not move on a horizontal drag.
        assertEquals(before.y, after.y, 0.01f)
    }

    @Test
    fun zoomAndPanAreClamped() {
        val zoomed = ImageCropMath.zoom(ImageCrop(), 50f)
        assertEquals(4f, zoomed.scale, 0f)
        val unzoomed = ImageCropMath.zoom(ImageCrop(scale = 4f), 0.01f)
        assertEquals(1f, unzoomed.scale, 0f)

        var crop = ImageCrop()
        repeat(50) { crop = ImageCropMath.pan(crop, 800f, 600f, 512f, 400f, 400f) }
        assertTrue(crop.offsetX in -1f..1f)
        assertTrue(crop.offsetY in -1f..1f)
        val rect = ImageCropMath.sourceRect(800f, 600f, 512f, crop)
        assertTrue(rect.x + rect.width <= 800.01f)
        assertTrue(rect.y + rect.height <= 600.01f)
    }

    @Test
    fun panDoesNothingWhenTheImageMatchesTheFrame() {
        val square = ImageCropMath.pan(ImageCrop(), 512f, 512f, 512f, panXPx = 90f, panYPx = 90f)
        assertEquals(0f, square.offsetX, 0.001f)
        assertEquals(0f, square.offsetY, 0.001f)
    }
}

class CommentPolicyTest {

    @Test
    fun blankCommentsAreAllowed() {
        assertNull(CommentPolicy.validate(""))
        assertNull(CommentPolicy.validate("   "))
    }

    @Test
    fun commentsRespectLengthRules() {
        assertEquals(CommentError.TooShort, CommentPolicy.validate("corto"))
        assertEquals(CommentError.TooLong, CommentPolicy.validate("a".repeat(501)))
        assertNull(CommentPolicy.validate("Buen trato y puntualidad impecable."))
    }

    @Test
    fun linksAndBlockedTermsAreRejected() {
        assertEquals(CommentError.Link, CommentPolicy.validate("mira esto http://ejemplo.com"))
        assertEquals(CommentError.Link, CommentPolicy.validate("entra a www.ejemplo.com"))
        assertEquals(CommentError.BlockedContent, CommentPolicy.validate("ustedes son unos idiotas"))
        assertEquals(CommentError.BlockedContent, CommentPolicy.validate("Esto es una estafa"))
    }
}

class RatingRulesTest {

    private fun scrim(status: ScrimStatus) = Scrim(
        id = "scrim-1",
        homeTeamId = "team-a",
        awayTeamId = "team-b",
        mode = GameMode.Competitive,
        schedule = "Sábado 19:00",
        notes = "",
        status = status
    )

    @Test
    fun blocksFollowTheAntiAbusePolicy() {
        assertEquals(
            RatingBlock.NoTeam,
            RatingRules.check(scrim(ScrimStatus.Completed), null, null, "team-b")
        )
        assertEquals(
            RatingBlock.NoScrim,
            RatingRules.check(null, "team-a", null, "team-b")
        )
        assertEquals(
            RatingBlock.NotParticipant,
            RatingRules.check(scrim(ScrimStatus.Completed), "team-z", null, "team-b")
        )
        assertEquals(
            RatingBlock.NotCompleted,
            RatingRules.check(scrim(ScrimStatus.Proposed), "team-a", null, "team-b")
        )
        assertEquals(
            RatingBlock.NotCompleted,
            RatingRules.check(scrim(ScrimStatus.Confirmed), "team-a", null, "team-b")
        )
        assertEquals(
            RatingBlock.OwnTeam,
            RatingRules.check(scrim(ScrimStatus.Completed), "team-a", null, "team-a")
        )
        val existing = TeamRating(
            id = "r1", scrimId = "scrim-1", fromTeamId = "team-a", toTeamId = "team-b",
            captainName = "Cap", scores = RatingCategory.entries.associateWith { 5 }
        )
        assertEquals(
            RatingBlock.AlreadyRated,
            RatingRules.check(scrim(ScrimStatus.Completed), "team-a", existing, "team-b")
        )
        assertNull(
            RatingRules.check(scrim(ScrimStatus.Completed), "team-a", null, "team-b")
        )
    }

    @Test
    fun scoresRequireFiveCategoriesInRange() {
        assertTrue(RatingRules.validScores(RatingCategory.entries.associateWith { 5 }))
        assertTrue(RatingRules.validScores(RatingCategory.entries.associateWith { 1 }))
        assertFalse(
            RatingRules.validScores(RatingCategory.entries.associateWith { 6 })
        )
        assertFalse(
            RatingRules.validScores(
                RatingCategory.entries.associateWith { 3 } - RatingCategory.Sportsmanship
            )
        )
    }
}

class ImageFileRulesTest {

    @Test
    fun supportedFormatsAreAccepted() {
        assertNull(ImageFileRules.validate("foto.JPG", "image/jpeg", 500_000))
        assertNull(ImageFileRules.validate("logo.png", "image/png", 900_000))
        assertNull(ImageFileRules.validate("subida.webp", "image/webp", 100))
        // Extension fallback when the mime type is unknown.
        assertNull(ImageFileRules.validate("logo.jpeg", null, 100))
    }

    @Test
    fun unsupportedFormatsAreRejected() {
        assertEquals(
            ImageImportError.UnsupportedFormat,
            ImageFileRules.validate("anim.gif", "image/gif", 100)
        )
        assertEquals(
            ImageImportError.UnsupportedFormat,
            ImageFileRules.validate("documento.pdf", "application/pdf", 100)
        )
        assertEquals(
            ImageImportError.UnsupportedFormat,
            ImageFileRules.validate("sin_extension", null, 100)
        )
    }

    @Test
    fun sizeLimitsAreEnforced() {
        assertEquals(
            ImageImportError.TooLarge,
            ImageFileRules.validate("grande.jpg", "image/jpeg", ImageFileRules.MAX_BYTES + 1)
        )
        assertEquals(
            ImageImportError.Empty,
            ImageFileRules.validate("vacio.jpg", "image/jpeg", 0)
        )
    }

    @Test
    fun recommendationsCheckSidesAndSquareness() {
        assertTrue(ImageFileRules.isRecommended(512, 512))
        assertTrue(ImageFileRules.isRecommended(1920, 1080))
        assertFalse(ImageFileRules.isRecommended(500, 800))
        assertTrue(ImageFileRules.isSquare(900, 900))
        assertFalse(ImageFileRules.isSquare(900, 800))
    }
}
