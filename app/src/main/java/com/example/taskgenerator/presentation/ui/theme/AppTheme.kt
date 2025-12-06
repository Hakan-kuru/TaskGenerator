// path: com.example.taskgenerator.presentation.ui.theme.AppTheme.kt

package com.example.taskgenerator.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// İLK KEZ kullanıyoruz: Uygulamada kullanabileceğimiz tema paletlerini temsil eden enum.
enum class AppPalette {
    SUNSET,
    NATURE,
    STONE_TEAL,
    COFFEE_CREAM,
    INDIGO_FOCUS
}

/**
 * Ana tema composable fonksiyonu.
 * Sistem temasına ve seçilen palete göre renk şemasını uygular.
 *
 * @param darkTheme Dark tema kullanılsın mı? Varsayılan olarak sistem temasını takip eder.
 * @param dynamicColor Android 12+ için dinamik renk desteği. Varsayılan: false (özel renk paletimizi kullanmak için).
 * @param palette Kullanılacak renk paleti (AppPalette enum).
 * @param content Tema içinde gösterilecek composable içerik.
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    palette: AppPalette = AppPalette.NATURE, // Varsayılan: Nature
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Android 12+ için dinamik renk desteği (isteğe bağlı)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> when (palette) {
            AppPalette.SUNSET ->
                if (darkTheme) DarkColorScheme else LightColorScheme

            AppPalette.NATURE ->
                if (darkTheme) NatureDarkColorScheme else NatureLightColorScheme

            AppPalette.STONE_TEAL ->
                if (darkTheme) StoneTealDarkColorScheme else StoneTealLightColorScheme

            AppPalette.COFFEE_CREAM ->
                if (darkTheme) CoffeeDarkColorScheme else CoffeeLightColorScheme

            AppPalette.INDIGO_FOCUS ->
                if (darkTheme) IndigoDarkColorScheme else IndigoLightColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
