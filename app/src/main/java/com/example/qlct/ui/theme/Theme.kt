package com.example.qlct.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PeachPrimary,
    secondary = PeachSecondary,
    tertiary = PeachTertiary
)

private val LightColorScheme = lightColorScheme(
    primary = PeachPrimary,
    secondary = PeachSecondary,
    tertiary = PeachTertiary

)

@Composable
fun QLCTTheme(
    darkTheme: Boolean = false, // Luôn ép về giao diện sáng
    dynamicColor: Boolean = false, // Tắt màu động để giữ màu sắc chủ đạo đồng nhất
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}