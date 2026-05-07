package es.mundodolphins.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White

private val LightColorScheme =
    lightColorScheme(
        primary = coral,
        onPrimary = White,
        secondary = aqua,
        onSecondary = White,
        tertiary = orange,
        background = pageBackground,
        surface = White,
        surfaceVariant = cardTint,
        onBackground = aquaDark,
        onSurface = gray900,
        onSurfaceVariant = gray700,
        primaryContainer = aqua,
        onPrimaryContainer = White,
        secondaryContainer = aquaMuted,
        onSecondaryContainer = White,
        tertiaryContainer = orange,
        outline = Color(0xFFDCE8EA),
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
        surface = gray800,
        surfaceVariant = gray800,
        onPrimary = White,
        onSecondary = White,
        onTertiary = White,
        onBackground = gray100,
        onSurface = gray100,
        onSurfaceVariant = gray100,
        onSecondaryContainer = gray100,
        primaryContainer = aquaDark,
        onPrimaryContainer = White,
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
