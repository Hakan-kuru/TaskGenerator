// path: com.example.taskgenerator.presentation.ui.theme.ColorScheme.kt

package com.example.taskgenerator.presentation.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Temel renk tanımları - tüm paletler
object AppColors {
    // --- Sunset (mevcut palet) ---
    val Primary = Color(0xFFFF6B6B)
    val PrimaryVariant = Color(0xFFFF3B7F)
    val Secondary = Color(0xFFFFD93D)

    val BackgroundDark = Color(0xFF1B1B1D)
    val SurfaceDark = Color(0xFF2B2B2E)
    val OnBackgroundDark = Color(0xFFF5F5F5)
    val OnSurfaceDark = Color(0xFFF5F5F5)

    val OnPrimary = Color(0xFFFFFFFF)
    val Error = Color(0xFFD64550)

    val BackgroundLight = Color(0xFFFFFBFE)
    val SurfaceLight = Color(0xFFFFFBFE)
    val OnBackgroundLight = Color(0xFF1C1B1F)
    val OnSurfaceLight = Color(0xFF1C1B1F)

    val PrimaryLight = Color(0xFFE55A5A)
    val SecondaryLight = Color(0xFFE5C235)

    // --- Nature paleti (daha önce eklediğimiz) ---
    val NaturePrimary = Color(0xFF6BAF92)
    val NaturePrimaryDark = Color(0xFF4E8C73)
    val NatureSecondary = Color(0xFFF2D7B6)
    val NatureAccent = Color(0xFFC39EA0)

    val NatureBackgroundLight = Color(0xFFFAF8F6)
    val NatureSurfaceLight = Color(0xFFFFFFFF)
    val NatureOnBackgroundLight = Color(0xFF2D2A26)
    val NatureOnSurfaceLight = Color(0xFF2D2A26)

    val NatureBackgroundDark = Color(0xFF121413)
    val NatureSurfaceDark = Color(0xFF1C1F1D)
    val NatureOnBackgroundDark = Color(0xFFECE7DE)
    val NatureOnSurfaceDark = Color(0xFFECE7DE)

    val NatureError = Color(0xFFD4837B)

    // İLK KEZ: Stone & Teal paleti için renkler.
    val StoneTealPrimary = Color(0xFF2D6A73)
    val StoneTealPrimaryDark = Color(0xFF1F4C54)
    val StoneTealSecondary = Color(0xFFF2B880)
    val StoneTealAccent = Color(0xFFFF9F1C)

    val StoneTealBackgroundLight = Color(0xFFF5F4F2)
    val StoneTealSurfaceLight = Color(0xFFFFFFFF)
    val StoneTealOnBackgroundLight = Color(0xFF222222)
    val StoneTealOnSurfaceLight = Color(0xFF222222)

    val StoneTealBackgroundDark = Color(0xFF141617)
    val StoneTealSurfaceDark = Color(0xFF1E2122)
    val StoneTealOnBackgroundDark = Color(0xFFEDEDED)
    val StoneTealOnSurfaceDark = Color(0xFFEDEDED)

    val StoneTealError = Color(0xFFD64550)

    // İLK KEZ: Coffee & Cream paleti için renkler.
    val CoffeePrimary = Color(0xFF6B4F3F)
    val CoffeePrimaryDark = Color(0xFF4A3528)
    val CoffeeSecondary = Color(0xFFD8BFAA)
    val CoffeeAccent = Color(0xFFC27A4B)

    val CoffeeBackgroundLight = Color(0xFFF7F3EE)
    val CoffeeSurfaceLight = Color(0xFFFFFFFF)
    val CoffeeOnBackgroundLight = Color(0xFF241C16)
    val CoffeeOnSurfaceLight = Color(0xFF241C16)

    val CoffeeBackgroundDark = Color(0xFF15110F)
    val CoffeeSurfaceDark = Color(0xFF201A16)
    val CoffeeOnBackgroundDark = Color(0xFFEEE4DB)
    val CoffeeOnSurfaceDark = Color(0xFFEEE4DB)

    val CoffeeError = Color(0xFFC34F4F)

    // İLK KEZ: Indigo Focus paleti için renkler.
    val IndigoPrimary = Color(0xFF3949AB)
    val IndigoPrimaryDark = Color(0xFF283593)
    val IndigoSecondary = Color(0xFFFFB74D)
    val IndigoAccent = Color(0xFF26C6DA)

    val IndigoBackgroundLight = Color(0xFFF5F6FA)
    val IndigoSurfaceLight = Color(0xFFFFFFFF)
    val IndigoOnBackgroundLight = Color(0xFF1F2230)
    val IndigoOnSurfaceLight = Color(0xFF1F2230)

    val IndigoBackgroundDark = Color(0xFF121321)
    val IndigoSurfaceDark = Color(0xFF1C1D2A)
    val IndigoOnBackgroundDark = Color(0xFFE5E7F5)
    val IndigoOnSurfaceDark = Color(0xFFE5E7F5)

    val IndigoError = Color(0xFFE53935)
}

// --- Sunset tabanlı ColorScheme (eski palet) ---
val LightColorScheme: ColorScheme = lightColorScheme(
    primary = AppColors.PrimaryLight,
    onPrimary = AppColors.OnPrimary,
    primaryContainer = AppColors.PrimaryVariant,
    onPrimaryContainer = AppColors.OnPrimary,

    secondary = AppColors.SecondaryLight,
    onSecondary = Color(0xFF1C1B1F),
    secondaryContainer = AppColors.Secondary,
    onSecondaryContainer = Color(0xFF1C1B1F),

    tertiary = AppColors.PrimaryVariant,
    onTertiary = AppColors.OnPrimary,

    error = AppColors.Error,
    onError = AppColors.OnPrimary,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = AppColors.BackgroundLight,
    onBackground = AppColors.OnBackgroundLight,

    surface = AppColors.SurfaceLight,
    onSurface = AppColors.OnSurfaceLight,
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),

    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),

    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = AppColors.Primary,
    surfaceTint = AppColors.Primary
)

val DarkColorScheme: ColorScheme = darkColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.OnPrimary,
    primaryContainer = AppColors.PrimaryVariant,
    onPrimaryContainer = AppColors.OnPrimary,

    secondary = AppColors.Secondary,
    onSecondary = Color(0xFF1C1B1F),
    secondaryContainer = Color(0xFFE5C235),
    onSecondaryContainer = Color(0xFF1C1B1F),

    tertiary = AppColors.PrimaryVariant,
    onTertiary = AppColors.OnPrimary,

    error = AppColors.Error,
    onError = AppColors.OnPrimary,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = AppColors.BackgroundDark,
    onBackground = AppColors.OnBackgroundDark,

    surface = AppColors.SurfaceDark,
    onSurface = AppColors.OnSurfaceDark,
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),

    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),

    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE6E1E5),
    inverseOnSurface = Color(0xFF313033),
    inversePrimary = AppColors.PrimaryLight,
    surfaceTint = AppColors.Primary
)

// --- Nature ColorScheme (kullandığın tema) ---
val NatureLightColorScheme: ColorScheme = lightColorScheme(
    primary = AppColors.NaturePrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = AppColors.NaturePrimaryDark,
    onPrimaryContainer = Color(0xFFFFFFFF),

    secondary = AppColors.NatureSecondary,
    onSecondary = AppColors.NatureOnBackgroundLight,
    secondaryContainer = AppColors.NatureAccent,
    onSecondaryContainer = AppColors.NatureOnBackgroundLight,

    tertiary = AppColors.NatureAccent,
    onTertiary = Color(0xFFFFFFFF),

    error = AppColors.NatureError,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = AppColors.NatureBackgroundLight,
    onBackground = AppColors.NatureOnBackgroundLight,

    surface = AppColors.NatureSurfaceLight,
    onSurface = AppColors.NatureOnSurfaceLight,
    surfaceVariant = Color(0xFFE3DED7),
    onSurfaceVariant = Color(0xFF494540),

    outline = Color(0xFF827569),
    outlineVariant = Color(0xFFC9BBAE),

    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF2E322F),
    inverseOnSurface = Color(0xFFF3EEE7),
    inversePrimary = AppColors.NaturePrimaryDark,
    surfaceTint = AppColors.NaturePrimary
)

val NatureDarkColorScheme: ColorScheme = darkColorScheme(
    primary = AppColors.NaturePrimaryDark,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = AppColors.NaturePrimary,
    onPrimaryContainer = Color(0xFFFFFFFF),

    secondary = AppColors.NatureSecondary,
    onSecondary = AppColors.NatureOnBackgroundDark,
    secondaryContainer = AppColors.NatureAccent,
    onSecondaryContainer = AppColors.NatureOnBackgroundDark,

    tertiary = AppColors.NatureAccent,
    onTertiary = Color(0xFFFFFFFF),

    error = AppColors.NatureError,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFFFDAD6),

    background = AppColors.NatureBackgroundDark,
    onBackground = AppColors.NatureOnBackgroundDark,

    surface = AppColors.NatureSurfaceDark,
    onSurface = AppColors.NatureOnSurfaceDark,
    surfaceVariant = Color(0xFF3A3D3A),
    onSurfaceVariant = Color(0xFFCDC7BF),

    outline = Color(0xFF8D857C),
    outlineVariant = Color(0xFF3A3D3A),

    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE5DFD7),
    inverseOnSurface = Color(0xFF2E322F),
    inversePrimary = AppColors.NaturePrimary,
    surfaceTint = AppColors.NaturePrimaryDark
)

// --- Stone & Teal ColorScheme ---
val StoneTealLightColorScheme: ColorScheme = lightColorScheme(
    primary = AppColors.StoneTealPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = AppColors.StoneTealPrimaryDark,
    onPrimaryContainer = Color(0xFFFFFFFF),

    secondary = AppColors.StoneTealSecondary,
    onSecondary = AppColors.StoneTealOnBackgroundLight,
    secondaryContainer = AppColors.StoneTealAccent,
    onSecondaryContainer = AppColors.StoneTealOnBackgroundLight,

    tertiary = AppColors.StoneTealAccent,
    onTertiary = Color(0xFFFFFFFF),

    error = AppColors.StoneTealError,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = AppColors.StoneTealBackgroundLight,
    onBackground = AppColors.StoneTealOnBackgroundLight,

    surface = AppColors.StoneTealSurfaceLight,
    onSurface = AppColors.StoneTealOnSurfaceLight,
    surfaceVariant = Color(0xFFE0DDDA),
    onSurfaceVariant = Color(0xFF494847),

    outline = Color(0xFF807C78),
    outlineVariant = Color(0xFFC7C2BD),

    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF2F3133),
    inverseOnSurface = Color(0xFFF3F1EE),
    inversePrimary = AppColors.StoneTealPrimaryDark,
    surfaceTint = AppColors.StoneTealPrimary
)

val StoneTealDarkColorScheme: ColorScheme = darkColorScheme(
    primary = AppColors.StoneTealPrimaryDark,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = AppColors.StoneTealPrimary,
    onPrimaryContainer = Color(0xFFFFFFFF),

    secondary = AppColors.StoneTealSecondary,
    onSecondary = AppColors.StoneTealOnBackgroundDark,
    secondaryContainer = AppColors.StoneTealAccent,
    onSecondaryContainer = AppColors.StoneTealOnBackgroundDark,

    tertiary = AppColors.StoneTealAccent,
    onTertiary = Color(0xFFFFFFFF),

    error = AppColors.StoneTealError,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFFFDAD6),

    background = AppColors.StoneTealBackgroundDark,
    onBackground = AppColors.StoneTealOnBackgroundDark,

    surface = AppColors.StoneTealSurfaceDark,
    onSurface = AppColors.StoneTealOnSurfaceDark,
    surfaceVariant = Color(0xFF3A3C3D),
    onSurfaceVariant = Color(0xFFCFD1D2),

    outline = Color(0xFF8A8D8E),
    outlineVariant = Color(0xFF3A3C3D),

    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE4E5E6),
    inverseOnSurface = Color(0xFF2F3133),
    inversePrimary = AppColors.StoneTealPrimary,
    surfaceTint = AppColors.StoneTealPrimaryDark
)

// --- Coffee & Cream ColorScheme ---
val CoffeeLightColorScheme: ColorScheme = lightColorScheme(
    primary = AppColors.CoffeePrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = AppColors.CoffeePrimaryDark,
    onPrimaryContainer = Color(0xFFFFFFFF),

    secondary = AppColors.CoffeeSecondary,
    onSecondary = AppColors.CoffeeOnBackgroundLight,
    secondaryContainer = AppColors.CoffeeAccent,
    onSecondaryContainer = AppColors.CoffeeOnBackgroundLight,

    tertiary = AppColors.CoffeeAccent,
    onTertiary = Color(0xFFFFFFFF),

    error = AppColors.CoffeeError,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = AppColors.CoffeeBackgroundLight,
    onBackground = AppColors.CoffeeOnBackgroundLight,

    surface = AppColors.CoffeeSurfaceLight,
    onSurface = AppColors.CoffeeOnSurfaceLight,
    surfaceVariant = Color(0xFFE5DAD0),
    onSurfaceVariant = Color(0xFF4C4037),

    outline = Color(0xFF837469),
    outlineVariant = Color(0xFFCDBFB3),

    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF302721),
    inverseOnSurface = Color(0xFFF4ECE4),
    inversePrimary = AppColors.CoffeePrimaryDark,
    surfaceTint = AppColors.CoffeePrimary
)

val CoffeeDarkColorScheme: ColorScheme = darkColorScheme(
    primary = AppColors.CoffeePrimaryDark,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = AppColors.CoffeePrimary,
    onPrimaryContainer = Color(0xFFFFFFFF),

    secondary = AppColors.CoffeeSecondary,
    onSecondary = AppColors.CoffeeOnBackgroundDark,
    secondaryContainer = AppColors.CoffeeAccent,
    onSecondaryContainer = AppColors.CoffeeOnBackgroundDark,

    tertiary = AppColors.CoffeeAccent,
    onTertiary = Color(0xFFFFFFFF),

    error = AppColors.CoffeeError,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFFFDAD6),

    background = AppColors.CoffeeBackgroundDark,
    onBackground = AppColors.CoffeeOnBackgroundDark,

    surface = AppColors.CoffeeSurfaceDark,
    onSurface = AppColors.CoffeeOnSurfaceDark,
    surfaceVariant = Color(0xFF3A2F28),
    onSurfaceVariant = Color(0xFFD7C8BC),

    outline = Color(0xFF8C7E73),
    outlineVariant = Color(0xFF3A2F28),

    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE7DDD3),
    inverseOnSurface = Color(0xFF302721),
    inversePrimary = AppColors.CoffeePrimary,
    surfaceTint = AppColors.CoffeePrimaryDark
)

// --- Indigo Focus ColorScheme ---
val IndigoLightColorScheme: ColorScheme = lightColorScheme(
    primary = AppColors.IndigoPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = AppColors.IndigoPrimaryDark,
    onPrimaryContainer = Color(0xFFFFFFFF),

    secondary = AppColors.IndigoSecondary,
    onSecondary = AppColors.IndigoOnBackgroundLight,
    secondaryContainer = AppColors.IndigoAccent,
    onSecondaryContainer = AppColors.IndigoOnBackgroundLight,

    tertiary = AppColors.IndigoAccent,
    onTertiary = Color(0xFFFFFFFF),

    error = AppColors.IndigoError,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = AppColors.IndigoBackgroundLight,
    onBackground = AppColors.IndigoOnBackgroundLight,

    surface = AppColors.IndigoSurfaceLight,
    onSurface = AppColors.IndigoOnSurfaceLight,
    surfaceVariant = Color(0xFFE0E1F0),
    onSurfaceVariant = Color(0xFF434666),

    outline = Color(0xFF7C7FA0),
    outlineVariant = Color(0xFFC4C6DE),

    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF292B3D),
    inverseOnSurface = Color(0xFFF0F1FB),
    inversePrimary = AppColors.IndigoPrimaryDark,
    surfaceTint = AppColors.IndigoPrimary
)

val IndigoDarkColorScheme: ColorScheme = darkColorScheme(
    primary = AppColors.IndigoPrimaryDark,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = AppColors.IndigoPrimary,
    onPrimaryContainer = Color(0xFFFFFFFF),

    secondary = AppColors.IndigoSecondary,
    onSecondary = AppColors.IndigoOnBackgroundDark,
    secondaryContainer = AppColors.IndigoAccent,
    onSecondaryContainer = AppColors.IndigoOnBackgroundDark,

    tertiary = AppColors.IndigoAccent,
    onTertiary = Color(0xFFFFFFFF),

    error = AppColors.IndigoError,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFFFDAD6),

    background = AppColors.IndigoBackgroundDark,
    onBackground = AppColors.IndigoOnBackgroundDark,

    surface = AppColors.IndigoSurfaceDark,
    onSurface = AppColors.IndigoOnSurfaceDark,
    surfaceVariant = Color(0xFF343552),
    onSurfaceVariant = Color(0xFFD1D2F0),

    outline = Color(0xFF8B8EC0),
    outlineVariant = Color(0xFF343552),

    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE2E3F5),
    inverseOnSurface = Color(0xFF292B3D),
    inversePrimary = AppColors.IndigoPrimary,
    surfaceTint = AppColors.IndigoPrimaryDark
)
