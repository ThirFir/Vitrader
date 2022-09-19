package com.example.vitrader.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


@SuppressLint("ConflictingOnColor")
private val DarkColorPalette = darkColors(
    primary = Blue2000,
    background = Blue2000,
    primaryVariant = Blue1600,
    secondary = Blue1200,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onPrimary = Blue200,
    surface = Blue1800

)

@SuppressLint("ConflictingOnColor")
private val LightColorPalette = lightColors(
    primary = Blue2000,
    background = Blue2000,
    primaryVariant = Blue1600,
    secondary = Blue1200,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onPrimary = Blue200,
    surface = Blue1800




    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun VitraderTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}