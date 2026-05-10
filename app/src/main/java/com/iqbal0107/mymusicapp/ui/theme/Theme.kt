package com.iqbal0107.mymusicapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MyMusicAppTheme(
    darkTheme: Boolean = false,
    seedColor: Color? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        seedColor != null && darkTheme -> darkColorScheme(
            primary = seedColor,
            secondary = seedColor.copy(alpha = 0.7f)
        )
        seedColor != null -> lightColorScheme(
            primary = seedColor,
            secondary = seedColor.copy(alpha = 0.7f)
        )
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}