package com.aaryan.pulse.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

private const val ANIMATED_CONTENT_DURATION = 300

@Composable
fun <T> FadeAnimatedContent(
    targetState: T,
    content: @Composable (targetState: T) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            (fadeIn(animationSpec = tween(ANIMATED_CONTENT_DURATION, delayMillis = 60, easing = FastOutSlowInEasing)) +
                scaleIn(initialScale = 0.92f, animationSpec = tween(ANIMATED_CONTENT_DURATION, delayMillis = 60, easing = FastOutSlowInEasing))
                ) togetherWith (
                fadeOut(animationSpec = tween(180)) +
                    scaleOut(targetScale = 0.97f, animationSpec = tween(180))
                )
        },
        contentAlignment = Alignment.Center,
        label = "FadeAnimatedContent"
    ) { currentTargetState: T ->
        content(currentTargetState)
    }
}
