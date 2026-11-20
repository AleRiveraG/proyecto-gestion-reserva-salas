package com.example.gestiondereservas.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

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