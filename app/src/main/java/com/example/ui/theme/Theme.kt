package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

/**
 * Extended institutional semantic palette for Uchch Madhyamik Vidyalaya Gangauli, Simri, Buxar.
 * Complements Material 3 [ColorScheme] with domain-specific tokens for board merit badges,
 * urgent official circulars, smart-lab highlights, and heritage masthead gradients.
 */
@Immutable
data class SchoolExtendedColors(
    val mastheadNavy: Color,
    val mastheadNavyDeep: Color,
    val crestGold: Color,
    val crestGoldContainer: Color,
    val onCrestGoldContainer: Color,
    val meritEmerald: Color,
    val meritEmeraldContainer: Color,
    val urgentCrimson: Color,
    val urgentCrimsonContainer: Color,
    val smartLabTeal: Color,
    val parchmentSurface: Color,
    val heroOverlayGradient: Brush
)

val LightSchoolExtendedColors = SchoolExtendedColors(
    mastheadNavy = RoyalNavyPrimary,
    mastheadNavyDeep = RoyalNavyDark,
    crestGold = SaffronGoldBright,
    crestGoldContainer = SaffronGoldContainer,
    onCrestGoldContainer = OnSaffronGoldContainer,
    meritEmerald = EmeraldSuccess,
    meritEmeraldContainer = EmeraldSuccessBg,
    urgentCrimson = CrimsonUrgent,
    urgentCrimsonContainer = CrimsonUrgentBg,
    smartLabTeal = GangaTealTertiary,
    parchmentSurface = SandstoneBackgroundLight,
    heroOverlayGradient = Brush.verticalGradient(
        colors = listOf(
            RoyalNavyDark.copy(alpha = 0.55f),
            RoyalNavyDark.copy(alpha = 0.78f),
            RoyalNavyPrimary.copy(alpha = 0.96f)
        )
    )
)

val DarkSchoolExtendedColors = SchoolExtendedColors(
    mastheadNavy = RoyalNavyPrimary,
    mastheadNavyDeep = RoyalNavyDark,
    crestGold = GoldPrimaryDark,
    crestGoldContainer = Color(0xFF422704),
    onCrestGoldContainer = SaffronGoldContainer,
    meritEmerald = Color(0xFF4ADE80),
    meritEmeraldContainer = Color(0xFF064E3B),
    urgentCrimson = Color(0xFFF87171),
    urgentCrimsonContainer = Color(0xFF7F1D1D),
    smartLabTeal = TealTertiaryDark,
    parchmentSurface = MidnightSurfaceDark,
    heroOverlayGradient = Brush.verticalGradient(
        colors = listOf(
            MidnightBackgroundDark.copy(alpha = 0.60f),
            MidnightBackgroundDark.copy(alpha = 0.84f),
            MidnightSurfaceDark.copy(alpha = 0.98f)
        )
    )
)

val LocalSchoolExtendedColors = staticCompositionLocalOf { LightSchoolExtendedColors }

/**
 * Light Material 3 ColorScheme inspired by Oxford Academic Navy, Saraswati Saffron Gold,
 * Sacred Ganga Teal, and Warm Sandstone Parchment.
 */
val InstitutionalLightColorScheme: ColorScheme = lightColorScheme(
    primary = RoyalNavyPrimary,
    onPrimary = Color.White,
    primaryContainer = RoyalNavyContainer,
    onPrimaryContainer = OnRoyalNavyContainer,
    inversePrimary = GoldPrimaryDark,
    secondary = SaffronGoldSecondary,
    onSecondary = Color.White,
    secondaryContainer = SaffronGoldContainer,
    onSecondaryContainer = OnSaffronGoldContainer,
    tertiary = GangaTealTertiary,
    onTertiary = Color.White,
    tertiaryContainer = GangaTealContainer,
    onTertiaryContainer = OnGangaTealContainer,
    background = SandstoneBackgroundLight,
    onBackground = InkTextPrimaryLight,
    surface = SandstoneSurfaceLight,
    onSurface = InkTextPrimaryLight,
    surfaceVariant = SandstoneSurfaceVariantLight,
    onSurfaceVariant = InkTextSecondaryLight,
    surfaceTint = RoyalNavyPrimary,
    inverseSurface = RoyalNavyDark,
    inverseOnSurface = Color(0xFFF8FAFC),
    error = CrimsonUrgent,
    onError = Color.White,
    errorContainer = CrimsonUrgentBg,
    onErrorContainer = Color(0xFF450A0A),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
    scrim = Color(0x99081829),
    surfaceBright = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFEAE3D5),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFAF7F2),
    surfaceContainer = Color(0xFFF4EFE6),
    surfaceContainerHigh = Color(0xFFEDE6DA),
    surfaceContainerHighest = Color(0xFFE5DDD0)
)

/**
 * Dark Material 3 ColorScheme ("Midnight Academic Observatory") with luminous gold accents,
 * deep navy slate surfaces, and high-contrast typography for comfortable evening reading.
 */
val InstitutionalDarkColorScheme: ColorScheme = darkColorScheme(
    primary = GoldPrimaryDark,
    onPrimary = OnGoldPrimaryDark,
    primaryContainer = NavyPrimaryContainerDark,
    onPrimaryContainer = OnNavyPrimaryContainerDark,
    inversePrimary = RoyalNavyPrimary,
    secondary = SaffronGoldBright,
    onSecondary = Color(0xFF281500),
    secondaryContainer = Color(0xFF422704),
    onSecondaryContainer = SaffronGoldContainer,
    tertiary = TealTertiaryDark,
    onTertiary = Color(0xFF002727),
    tertiaryContainer = Color(0xFF0E4747),
    onTertiaryContainer = GangaTealContainer,
    background = MidnightBackgroundDark,
    onBackground = Color(0xFFF1F5F9),
    surface = MidnightSurfaceDark,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = MidnightSurfaceVariantDark,
    onSurfaceVariant = Color(0xFFCBD5E1),
    surfaceTint = GoldPrimaryDark,
    inverseSurface = Color(0xFFF1F5F9),
    inverseOnSurface = MidnightBackgroundDark,
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2),
    outline = Color(0xFF334155),
    outlineVariant = Color(0xFF1E293B),
    scrim = Color(0xCC000000),
    surfaceBright = Color(0xFF1E3652),
    surfaceDim = Color(0xFF071320),
    surfaceContainerLowest = Color(0xFF050E18),
    surfaceContainerLow = Color(0xFF0B1929),
    surfaceContainer = Color(0xFF0E2034),
    surfaceContainerHigh = Color(0xFF152A42),
    surfaceContainerHighest = Color(0xFF1C3552)
)

/**
 * Architectural shape system balancing classical academic authority with modern card ergonomics.
 */
val SchoolShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

/**
 * Convenience accessor for institutional theme tokens alongside standard [MaterialTheme].
 */
object SchoolTheme {
    val colors: SchoolExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalSchoolExtendedColors.current

    val colorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    val shapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes
}

/**
 * Primary institutional theme for Uchch Madhyamik Vidyalaya Gangauli, Simri, Buxar (Bihar).
 * Applies the Royal Navy & Saraswati Saffron Gold palette, Playfair Display + Plus Jakarta Sans
 * typography hierarchy, and edge-to-edge system bar appearance.
 */
@Composable
fun GangauliSchoolTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) InstitutionalDarkColorScheme else InstitutionalLightColorScheme
    val extendedColors = if (darkTheme) DarkSchoolExtendedColors else LightSchoolExtendedColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity
            activity?.window?.let { window ->
                val insetsController = WindowCompat.getInsetsController(window, view)
                // The top institutional masthead is always deep Royal Navy, so status bar icons stay light
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalSchoolExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = SchoolShapes,
            content = content
        )
    }
}

/**
 * Standard application theme entry point delegating to [GangauliSchoolTheme].
 */
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    GangauliSchoolTheme(
        darkTheme = darkTheme,
        content = content
    )
}
