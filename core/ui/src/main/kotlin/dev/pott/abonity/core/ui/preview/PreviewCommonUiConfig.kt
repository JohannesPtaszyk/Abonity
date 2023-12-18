package dev.pott.abonity.core.ui.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers

@Preview(
    name = "default light",
    group = "default",
)
@Preview(
    name = "default dark",
    group = "default",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    name = "small font",
    group = "font scales",
    fontScale = 0.5f,
)
@Preview(
    name = "medium font",
    group = "font scales",
    fontScale = 1.0f,
)
@Preview(
    name = "large font",
    group = "font scales",
    fontScale = 1.5f,
)
@Preview(
    name = "compact",
    group = "widths",
    widthDp = 300,
)
@Preview(
    name = "medium",
    group = "widths",
    widthDp = 800,
)
@Preview(
    name = "expanded",
    group = "widths",
    widthDp = 1100,
)
@Preview(
    name = "blue wallpaper",
    group = "wallpaper colors",
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE,
)
@Preview(
    name = "red wallpaper",
    group = "wallpaper colors",
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE,
)
@Preview(
    name = "green wallpaper",
    group = "wallpaper colors",
    wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE,
)
@Preview(
    name = "yellow wallpaper",
    group = "wallpaper colors",
    wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE,
)
@Preview(
    name = "blue wallpaper dark",
    group = "wallpaper colors",
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    name = "red wallpaper dark",
    group = "wallpaper colors",
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    name = "green wallpaper dark",
    group = "wallpaper colors",
    wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    name = "yellow wallpaper dark",
    group = "wallpaper colors",
    wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
annotation class PreviewCommonUiConfig

@Preview(
    name = "1. Mobile",
    device = Devices.DEFAULT,
)
@Preview(
    name = "2. Foldable",
    device = Devices.FOLDABLE,
)
@Preview(
    name = "3. Tablet",
    device = Devices.TABLET,
)
@Preview(
    name = "4. Desktop",
    device = Devices.DESKTOP,
)
annotation class PreviewCommonScreenConfig

@Preview(locale = "en-US")
@Preview(locale = "de-DE")
annotation class PreviewCommonLocaleConfig
