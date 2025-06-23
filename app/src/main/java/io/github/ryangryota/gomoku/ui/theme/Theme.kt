package io.github.ryangryota.gomoku.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = AccentBlue,
    onPrimary = White,
    primaryContainer = SurfaceVariantLight,
    onPrimaryContainer = White,
    secondary = AccentGreen,
    onSecondary = White,
    secondaryContainer = AccentGreen,
    onSecondaryContainer = White,
    tertiary = AccentYellow,
    onTertiary = Black,
    tertiaryContainer = AccentYellow,
    onTertiaryContainer = Black,
    background = SurfaceLight,
    onBackground = Black,
    surface = SurfaceLight,
    onSurface = Black,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Black,
    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRed,
    onErrorContainer = White,
    outline = OutlineLight,
    outlineVariant = Gray90,
    inversePrimary = AccentPurple,
    scrim = Black
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = White,
    primaryContainer = SurfaceVariantDark,
    onPrimaryContainer = White,
    secondary = AccentGreen,
    onSecondary = Black,
    secondaryContainer = AccentGreen,
    onSecondaryContainer = Black,
    tertiary = AccentYellow,
    onTertiary = Black,
    tertiaryContainer = AccentYellow,
    onTertiaryContainer = Black,
    background = SurfaceDark,
    onBackground = White,
    surface = SurfaceDark,
    onSurface = White,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = White,
    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRed,
    onErrorContainer = White,
    outline = OutlineDark,
    outlineVariant = Gray70,
    inversePrimary = AccentPurple,
    scrim = Black
)

@Composable
fun OneColorGomokuTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        // Android 12以上ならダイナミックカラーを利用
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        // それ以外は従来のカラースキーム
        else -> if (useDarkTheme) DarkColorScheme else LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
