package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GovBlueDark,
    secondary = GovBlueSecondaryDark,
    tertiary = GovGoldDark,
    background = GovBgDark,
    surface = GovSurfaceDark,
    onBackground = GovOnBgDark,
    onSurface = GovOnBgDark,
    primaryContainer = GovBlueDark.copy(alpha = 0.2f),
    secondaryContainer = GovBlueSecondaryDark.copy(alpha = 0.2f)
)

private val LightColorScheme = lightColorScheme(
    primary = GovBlueLight,
    secondary = GovBlueSecondaryLight,
    tertiary = GovGoldLight,
    background = GovBgLight,
    surface = GovSurfaceLight,
    onBackground = GovOnBgLight,
    onSurface = GovOnBgLight,
    primaryContainer = GovBlueLight.copy(alpha = 0.1f),
    secondaryContainer = GovBlueSecondaryLight.copy(alpha = 0.1f)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // We prioritize our professional civic branding theme!
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
