package com.example.jetnews

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.color.ColorProvider
import androidx.glance.color.ColorProviders
import androidx.glance.material3.ColorProviders
import androidx.glance.unit.ColorProvider

object AppTheme {

    @Composable
    fun colorScheme(isDark: Boolean = false) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context: Context = LocalContext.current
        if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (isDark) darkColorScheme else lightColorScheme
    }

    val shapes: Shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(8.dp)
    )

    val typography: Typography = Typography(
        displayLarge = defaultTextStyle.copy(fontSize = 57.sp, lineHeight = 64.sp, letterSpacing = (-0.25).sp),
        displayMedium = defaultTextStyle.copy(fontSize = 45.sp, lineHeight = 52.sp, letterSpacing = 0.sp),
        displaySmall = defaultTextStyle.copy(fontSize = 36.sp, lineHeight = 44.sp, letterSpacing = 0.sp),
        headlineLarge = defaultTextStyle.copy(fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = 0.sp, lineBreak = LineBreak.Heading),
        headlineMedium = defaultTextStyle.copy(fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = 0.sp, lineBreak = LineBreak.Heading),
        headlineSmall = defaultTextStyle.copy(fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = 0.sp, lineBreak = LineBreak.Heading),
        titleLarge = defaultTextStyle.copy(fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = 0.sp, lineBreak = LineBreak.Heading),
        titleMedium = defaultTextStyle.copy(fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp, fontWeight = FontWeight.Medium, lineBreak = LineBreak.Heading),
        titleSmall = defaultTextStyle.copy(fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp, fontWeight = FontWeight.Medium, lineBreak = LineBreak.Heading),
        labelLarge = defaultTextStyle.copy(fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp, fontWeight = FontWeight.Medium),
        labelMedium = defaultTextStyle.copy(fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp, fontWeight = FontWeight.Medium),
        labelSmall = defaultTextStyle.copy(fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp, fontWeight = FontWeight.Medium),
        bodyLarge = defaultTextStyle.copy(fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp, lineBreak = LineBreak.Paragraph),
        bodyMedium = defaultTextStyle.copy(fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp, lineBreak = LineBreak.Paragraph),
        bodySmall = defaultTextStyle.copy(fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp, lineBreak = LineBreak.Paragraph),
    )

}

@Composable
fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(AppTheme.colorScheme(darkTheme), AppTheme.shapes, AppTheme.typography, content)
}

val ColorScheme.codeBlockBackground: Color get() = onSurface.copy(alpha = .15f)

/*values*/

private val MontserratFontFamily: FontFamily = FontFamily(Font(R.font.montserrat_regular), Font(R.font.montserrat_medium, FontWeight.W500))

@Suppress("DEPRECATION")
private val defaultTextStyle: TextStyle = TextStyle(
    fontFamily = MontserratFontFamily,
    platformStyle = PlatformTextStyle(includeFontPadding = false),
    lineHeightStyle = LineHeightStyle(alignment = LineHeightStyle.Alignment.Center, trim = LineHeightStyle.Trim.None)
)

private val md_theme_light_primary: Color = Color(0xFFBF0031)
private val md_theme_light_onPrimary: Color = Color(0xFFFFFFFF)
private val md_theme_light_primaryContainer: Color = Color(0xFFFFDAD9)
private val md_theme_light_onPrimaryContainer: Color = Color(0xFF40000A)
private val md_theme_light_secondary: Color = Color(0xFF775656)
private val md_theme_light_onSecondary: Color = Color(0xFFFFFFFF)
private val md_theme_light_secondaryContainer: Color = Color(0xFFFFDAD9)
private val md_theme_light_onSecondaryContainer: Color = Color(0xFF2C1516)
private val md_theme_light_tertiary: Color = Color(0xFF755A2F)
private val md_theme_light_onTertiary: Color = Color(0xFFFFFFFF)
private val md_theme_light_tertiaryContainer: Color = Color(0xFFFFDDAF)
private val md_theme_light_onTertiaryContainer: Color = Color(0xFF281800)
private val md_theme_light_error: Color = Color(0xFFBA1A1A)
private val md_theme_light_errorContainer: Color = Color(0xFFFFDAD6)
private val md_theme_light_onError: Color = Color(0xFFFFFFFF)
private val md_theme_light_onErrorContainer: Color = Color(0xFF410002)
private val md_theme_light_background: Color = Color(0xFFFFFBFF)
private val md_theme_light_onBackground: Color = Color(0xFF201A1A)
private val md_theme_light_surface: Color = Color(0xFFFFFBFF)
private val md_theme_light_onSurface: Color = Color(0xFF201A1A)
private val md_theme_light_surfaceVariant: Color = Color(0xFFF4DDDD)
private val md_theme_light_onSurfaceVariant: Color = Color(0xFF524343)
private val md_theme_light_outline: Color = Color(0xFF857373)
private val md_theme_light_inverseOnSurface: Color = Color(0xFFFBEEED)
private val md_theme_light_inverseSurface: Color = Color(0xFF362F2F)
private val md_theme_light_inversePrimary: Color = Color(0xFFFFB3B4)
private val md_theme_light_surfaceTint: Color = Color(0xFFBF0031)

private val md_theme_dark_primary: Color = Color(0xFFFFB3B4)
private val md_theme_dark_onPrimary: Color = Color(0xFF680016)
private val md_theme_dark_primaryContainer: Color = Color(0xFF920023)
private val md_theme_dark_onPrimaryContainer: Color = Color(0xFFFFDAD9)
private val md_theme_dark_secondary: Color = Color(0xFFE6BDBC)
private val md_theme_dark_onSecondary: Color = Color(0xFF44292A)
private val md_theme_dark_secondaryContainer: Color = Color(0xFF5D3F3F)
private val md_theme_dark_onSecondaryContainer: Color = Color(0xFFFFDAD9)
private val md_theme_dark_tertiary: Color = Color(0xFFE5C18D)
private val md_theme_dark_onTertiary: Color = Color(0xFF422C05)
private val md_theme_dark_tertiaryContainer: Color = Color(0xFF5B421A)
private val md_theme_dark_onTertiaryContainer: Color = Color(0xFFFFDDAF)
private val md_theme_dark_error: Color = Color(0xFFFFB4AB)
private val md_theme_dark_errorContainer: Color = Color(0xFF93000A)
private val md_theme_dark_onError: Color = Color(0xFF690005)
private val md_theme_dark_onErrorContainer: Color = Color(0xFFFFDAD6)
private val md_theme_dark_background: Color = Color(0xFF201A1A)
private val md_theme_dark_onBackground: Color = Color(0xFFECE0DF)
private val md_theme_dark_surface: Color = Color(0xFF201A1A)
private val md_theme_dark_onSurface: Color = Color(0xFFECE0DF)
private val md_theme_dark_surfaceVariant: Color = Color(0xFF524343)
private val md_theme_dark_onSurfaceVariant: Color = Color(0xFFD7C1C1)
private val md_theme_dark_outline: Color = Color(0xFFA08C8C)
private val md_theme_dark_inverseOnSurface: Color = Color(0xFF201A1A)
private val md_theme_dark_inverseSurface: Color = Color(0xFFECE0DF)
private val md_theme_dark_inversePrimary: Color = Color(0xFFBF0031)
private val md_theme_dark_surfaceTint: Color = Color(0xFFFFB3B4)

val lightColorScheme: ColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
)

val darkColorScheme: ColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
)
