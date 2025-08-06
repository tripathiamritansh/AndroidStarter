package com.sample.starter.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalFeedColors = staticCompositionLocalOf { DefaultColor }

private val DefaultColor = FeedColors(
    isLikedTint = Color.Red,
    cardBackgroundColor = Color.White
)

private val FunkyColor = FeedColors(
    isLikedTint = Color.Cyan,
    cardBackgroundColor = Color.DarkGray
)

data class FeedColors(
    val isLikedTint: Color,
    val cardBackgroundColor: Color
)

@Composable
fun FeedTheme(
    isFunkyTheme: Boolean,
    content: @Composable () -> Unit
) {
    val color = if (isFunkyTheme) FunkyColor else DefaultColor
    CompositionLocalProvider(LocalFeedColors provides color) {
        content()
    }
}
