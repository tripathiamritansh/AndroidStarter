package com.sample.starter.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import kotlinx.coroutines.delay

data class Content(
    val backgroundColor: Color,
    val text: String,
    val textColor: Color
)

val content = listOf<Content>(
    Content(Color.LightGray, "Lets create!", Color.Yellow),
    Content(Color.Magenta, "Lets Design!", Color.Blue),
    Content(Color.Green, "Lets Code!", Color.White),
)

@Composable
fun LoginSplashScreen() {

    var currentKey by remember { mutableIntStateOf(0) }

    val backgroundColor by animateColorAsState(targetValue = content[currentKey].backgroundColor)
    val textColor by animateColorAsState(targetValue = content[currentKey].textColor)

    var animatedText by remember { mutableStateOf("") }

    LaunchedEffect(currentKey) {
        val targetText = content[currentKey].text

        animatedText = ""

        targetText.forEachIndexed { index, _ ->
            animatedText = targetText.substring(0, index + 1)
            delay(100)
        }
        delay(500)
        currentKey = (currentKey + 1) % content.size
    }

    Box(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = animatedText,
            modifier = Modifier.wrapContentSize(),
            color = textColor,
        )
    }

}

