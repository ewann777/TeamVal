package com.example.teamval.domain.model

/**
 * Identity and visual customization of a team (stage 4.1).
 *
 * Colors are stored as packed ARGB [Long] values so the domain layer keeps no
 * dependency on Compose or Android types; the UI converts them to Color.
 */
enum class LogoSymbol { Star, Bolt, Hexagon, Shield, Diamond, Triangle, Crosshair, Orbit }
enum class LogoShape { Circle, Square }
enum class LogoFrame { None, Solid, Glow, Double }
enum class GradientStyle { None, Linear, Radial }
enum class BannerStyle { Solid, Gradient, Aurora, Grid, Graphite }
enum class TeamBackground { Plain, Dusk, Aurora, Grid, Atardecer }
enum class TeamBadge { FairPlay, Punctual, Communicative, Veteran, Rising, Organized, Flame }
enum class ProfileFrame { None, Ring, Glow, Gradient, Orbit, Prism }
enum class SectionLayout { Standard, Compact, Spotlight }
enum class MemberStyle { Grid, List, Cards }

/** Logo drawn inside TeamVal: symbol, colors, initials, background, frame and size. */
data class BuiltinLogo(
    val symbol: LogoSymbol = LogoSymbol.Star,
    val initials: String = "",
    val primaryColor: Long = 0xFF8B5CF6,
    val secondaryColor: Long = 0xFF22D3EE,
    val gradient: GradientStyle = GradientStyle.Linear,
    val background: Long = 0xFF171528,
    val frame: LogoFrame = LogoFrame.Glow,
    val shape: LogoShape = LogoShape.Circle,
    val scale: Float = 1f
) {
    fun invalidFields(): Set<LogoDraftField> = buildSet {
        if (initials.length > 4) add(LogoDraftField.Initials)
        if (scale < 0.6f || scale > 1.4f) add(LogoDraftField.Scale)
    }
}

/**
 * Logo uploaded from the gallery. [fileName] points at the optimized copy kept in
 * the app's private storage; [crop] keeps the pan/zoom applied inside the square
 * frame so rendering never stretches the image.
 */
data class ImageLogo(
    val fileName: String,
    val shape: LogoShape = LogoShape.Circle,
    val crop: ImageCrop = ImageCrop(),
    val frame: LogoFrame = LogoFrame.None
)

enum class LogoDraftField { Initials, Scale }

enum class LogoSource { Builtin, Image }

data class TeamLogo(
    val source: LogoSource = LogoSource.Builtin,
    val builtin: BuiltinLogo = BuiltinLogo(),
    val image: ImageLogo? = null
)

/**
 * Normalized crop of an image inside a square frame.
 *
 * [scale] >= 1 is the zoom factor (1 keeps the cover fit) and [offsetX]/[offsetY]
 * are expressed in frame units in the range -1..1. All geometry is proportional so
 * the source keeps its aspect ratio at every size.
 */
data class ImageCrop(val scale: Float = 1f, val offsetX: Float = 0f, val offsetY: Float = 0f) {
    fun clamped(): ImageCrop = ImageCrop(
        scale = scale.coerceIn(1f, 4f),
        offsetX = offsetX.coerceIn(-1f, 1f),
        offsetY = offsetY.coerceIn(-1f, 1f)
    )
}

/**
 * Pure crop math (unit tested). Given an image of [imageWidth] x [imageHeight]
 * pixels shown inside a square frame of [frameSize] with a cover fit, returns the
 * visible source rectangle in pixels. The rectangle always keeps the source aspect
 * ratio, so images are cropped proportionally and never stretched.
 */
object ImageCropMath {
    fun coverScale(imageWidth: Float, imageHeight: Float, frameSize: Float): Float {
        if (imageWidth <= 0f || imageHeight <= 0f || frameSize <= 0f) return 1f
        return maxOf(frameSize / imageWidth, frameSize / imageHeight)
    }

    /** Width in pixels of the scaled image (frame units) for a cover fit. */
    fun scaledWidth(imageWidth: Float, imageHeight: Float, frameSize: Float): Float =
        imageWidth * coverScale(imageWidth, imageHeight, frameSize)

    fun scaledHeight(imageWidth: Float, imageHeight: Float, frameSize: Float): Float =
        imageHeight * coverScale(imageWidth, imageHeight, frameSize)

    /**
     * Visible source rectangle (x, y, width, height in pixels) after zooming by
     * [crop.scale] and panning by [crop.offsetX]/[crop.offsetY]. The result is
     * always a square window of the source with the frame's aspect ratio (1:1).
     */
    fun sourceRect(
        imageWidth: Float,
        imageHeight: Float,
        frameSize: Float,
        crop: ImageCrop
    ): RectPx {
        val base = coverScale(imageWidth, imageHeight, frameSize)
        val zoom = crop.clamped().scale
        val window = frameSize / (base * zoom) // visible source size in px (square)
        val maxX = (imageWidth - window).coerceAtLeast(0f)
        val maxY = (imageHeight - window).coerceAtLeast(0f)
        // offset -1..1 maps to the full pan range on each axis.
        val x = (imageWidth - window) / 2f + crop.clamped().offsetX * maxX / 2f
        val y = (imageHeight - window) / 2f + crop.clamped().offsetY * maxY / 2f
        return RectPx(
            x = x.coerceIn(0f, maxX),
            y = y.coerceIn(0f, maxY),
            width = window.coerceAtMost(imageWidth),
            height = window.coerceAtMost(imageHeight)
        )
    }

    /** Applies a pinch factor, keeping the zoom inside the supported 1x–4x range. */
    fun zoom(crop: ImageCrop, factor: Float): ImageCrop =
        crop.copy(scale = (crop.clamped().scale * factor).coerceIn(1f, 4f)).clamped()

    /**
     * Converts a drag in frame pixels into an equivalent pan. Dragging right moves
     * the image right, which means the visible window travels left in the source.
     */
    fun pan(
        crop: ImageCrop,
        imageWidth: Float,
        imageHeight: Float,
        frameSize: Float,
        panXPx: Float,
        panYPx: Float
    ): ImageCrop {
        val current = crop.clamped()
        val base = coverScale(imageWidth, imageHeight, frameSize)
        if (base <= 0f || frameSize <= 0f) return current
        val window = frameSize / (base * current.scale)
        val maxX = (imageWidth - window).coerceAtLeast(0f)
        val maxY = (imageHeight - window).coerceAtLeast(0f)
        val dx = -panXPx * window / frameSize
        val dy = -panYPx * window / frameSize
        val deltaX = if (maxX > 0f) dx * 2f / maxX else 0f
        val deltaY = if (maxY > 0f) dy * 2f / maxY else 0f
        return ImageCrop(
            scale = current.scale,
            offsetX = (current.offsetX + deltaX).coerceIn(-1f, 1f),
            offsetY = (current.offsetY + deltaY).coerceIn(-1f, 1f)
        )
    }
}

data class RectPx(val x: Float, val y: Float, val width: Float, val height: Float) {
    val aspectRatio: Float get() = if (height == 0f) 0f else width / height
}

/** Full visual identity of a team: editable with live preview in the team editor. */
data class TeamIdentity(
    val logo: TeamLogo = TeamLogo(),
    val primaryColor: Long = 0xFF8B5CF6,
    val secondaryColor: Long = 0xFF22D3EE,
    val gradient: GradientStyle = GradientStyle.Linear,
    val banner: BannerStyle = BannerStyle.Gradient,
    val background: TeamBackground = TeamBackground.Dusk,
    val frame: ProfileFrame = ProfileFrame.Ring,
    val badges: Set<TeamBadge> = setOf(TeamBadge.FairPlay),
    val motto: String = "",
    val description: String = "",
    val layout: SectionLayout = SectionLayout.Standard,
    val memberStyle: MemberStyle = MemberStyle.Cards
) {
    fun invalidFields(): Set<IdentityDraftField> = buildSet {
        if (motto.trim().length > 80) add(IdentityDraftField.Motto)
        if (description.trim().length !in 10..300) add(IdentityDraftField.Description)
        if (logo.builtin.invalidFields().isNotEmpty()) add(IdentityDraftField.Logo)
    }
}

enum class IdentityDraftField { Motto, Description, Logo }

/** Ordered sections used by the live preview and the team profile. */
fun TeamIdentity.sectionOrder(): List<IdentitySection> = when (layout) {
    SectionLayout.Standard -> listOf(IdentitySection.Banner, IdentitySection.Motto, IdentitySection.Description, IdentitySection.Members)
    SectionLayout.Compact -> listOf(IdentitySection.Motto, IdentitySection.Banner, IdentitySection.Members, IdentitySection.Description)
    SectionLayout.Spotlight -> listOf(IdentitySection.Members, IdentitySection.Banner, IdentitySection.Motto, IdentitySection.Description)
}

enum class IdentitySection { Banner, Motto, Description, Members }
