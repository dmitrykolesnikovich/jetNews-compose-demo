package com.example.jetnews.widget

import androidx.compose.ui.unit.sp
import androidx.glance.color.ColorProvider
import androidx.glance.color.ColorProviders
import androidx.glance.material3.ColorProviders
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.jetnews.darkColorScheme
import com.example.jetnews.lightColorScheme

object AppWidgetTheme {
    val colors: ColorProviders = ColorProviders(light = lightColorScheme, dark = darkColorScheme)
    val outlineVariant: ColorProvider = ColorProvider(day = lightColorScheme.onSurface.copy(alpha = 0.1f), night = darkColorScheme.onSurface.copy(alpha = 0.1f))
    val bodyLarge: TextStyle = TextStyle(fontSize = 16.sp)
    val bodySmall: TextStyle = TextStyle(fontSize = 12.sp)
}
