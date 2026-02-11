package es.mundodolphins.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.White

private val LightColorScheme =
    lightColorScheme(
        primary = orange,
        secondary = aqua,
        tertiary = orange,
        background = White,
        surface = White,
        onSecondary = White,
        onSecondaryContainer = White,
        onBackground = gray900,
        onSurface = gray900,
        primaryContainer = White,
        secondaryContainer = aqua,
        tertiaryContainer = orange,
    /*
    Other default colors to override
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
     */
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = orange,
        secondary = aqua,
        tertiary = orange,
        background = gray900,
        surface = gray900,
        onPrimary = White,
        onSecondary = White,
        onTertiary = White,
        onBackground = gray100,
        onSurface = gray100,
        onSecondaryContainer = gray100,
        primaryContainer = gray800,
        secondaryContainer = gray800,
        tertiaryContainer = gray800,
    )

@Composable
fun MundoDolphinsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
