package com.example.gestiondereservas.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Black,
    secondary = White,
    tertiary = Yellow
)

private val EsquemaDuoc = lightColorScheme(
    primary = Black,
    onPrimary = White,
    secondary = White,
    onSecondary = Black,
    tertiary = Yellow,
    onTertiary = Black,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = Black
)

@Composable
fun GestionDeReservasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EsquemaDuoc,
        typography = Typography(),
        content = content
    )
}