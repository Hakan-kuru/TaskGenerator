// path: com.example.taskgenerator.presentation.ui.theme.Theme.kt

package com.example.taskgenerator.presentation.ui.theme

import androidx.compose.runtime.Composable

/**
 * Eski tema fonksiyonu - geriye dönük uyumluluk için AppTheme'i çağırıyor.
 * Yeni kodlarda AppTheme kullanılması önerilir.
 */
@Deprecated(
    message = "AppTheme kullanın",
    replaceWith = ReplaceWith("AppTheme(darkTheme, dynamicColor, content)", "com.example.taskgenerator.presentation.ui.theme.AppTheme")
)
@Composable
fun TaskGeneratorTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AppTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        content = content
    )
}