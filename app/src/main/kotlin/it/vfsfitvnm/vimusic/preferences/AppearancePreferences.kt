package it.vfsfitvnm.vimusic.preferences

import it.vfsfitvnm.vimusic.GlobalPreferencesHolder
import it.vfsfitvnm.vimusic.enums.ColorPaletteMode
import it.vfsfitvnm.vimusic.enums.ColorPaletteName
import it.vfsfitvnm.vimusic.enums.ThumbnailRoundness
import it.vfsfitvnm.vimusic.ui.styling.BuiltInFontFamily

object AppearancePreferences : GlobalPreferencesHolder() {
    private val useSystemFont by boolean(false)

    var colorPaletteName by enum(ColorPaletteName.Dynamic)
    var colorPaletteMode by enum(ColorPaletteMode.System)
    var thumbnailRoundness by enum(ThumbnailRoundness.Light)
    var applyFontPadding by boolean(false)
    var fontFamily by enum(if (useSystemFont) BuiltInFontFamily.System else BuiltInFontFamily.Poppins)
    val isShowingThumbnailInLockscreenProperty = boolean(false)
    var isShowingThumbnailInLockscreen by isShowingThumbnailInLockscreenProperty
    var swipeToHideSong by boolean(false)
    var swipeToHideSongConfirm by boolean(true)
    var maxThumbnailSize by int(1920)
}
