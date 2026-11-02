package com.aaryan.pulse.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer

/** Soft accent glow that fades in on press and out on release. Apply before `clickable`. */
@Composable
fun Modifier.pressGlow(
    interactionSource: InteractionSource,
    color: Color = MaterialTheme.colorScheme.primary,
    maxAlpha: Float = 0.16f
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val glow by animateFloatAsState(
        targetValue = if(pressed) maxAlpha else 0f,
        animationSpec = tween(durationMillis = if(pressed) 60 else 300),
        label = "pressGlow"
    )
    return this.drawBehind {
        if(glow > 0f) drawRect(color.copy(alpha = glow))
    }
}

/** Springy press-down scale, for icons and labels inside a button. */
@Composable
fun Modifier.pressScale(
    interactionSource: InteractionSource,
    pressedScale: Float = 0.84f
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if(pressed) pressedScale else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "pressScale"
    )
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
