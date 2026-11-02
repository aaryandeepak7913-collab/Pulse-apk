package com.aaryan.pulse.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.min
import kotlin.math.sin

private val SplashBackground = Color(0xFF0B0B0E)
private val SplashAccent = Color(0xFF8FB0FF)

/**
 * Minimal launch splash: ripples bloom from a dot, a wave packet "pulses" across the
 * screen, the wordmark fades in, then everything dissolves into the app.
 */
@Composable
fun PulseSplash(
    visible: Boolean,
    onFinished: () -> Unit
) {
    if(!visible) return

    val draw = remember { Animatable(0f) }
    val reveal = remember { Animatable(0f) }
    var exit by remember { mutableStateOf(false) }

    val infinite = rememberInfiniteTransition(label = "splash")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Restart),
        label = "phase"
    )
    val ripple by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Restart),
        label = "ripple"
    )

    LaunchedEffect(Unit) {
        draw.animateTo(1f, tween(950, easing = FastOutSlowInEasing))
        reveal.animateTo(1f, tween(450, easing = FastOutSlowInEasing))
        delay(450)
        exit = true
    }

    val alpha by animateFloatAsState(
        targetValue = if(exit) 0f else 1f,
        animationSpec = tween(450),
        finishedListener = { if(exit) onFinished() },
        label = "splashAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { this.alpha = alpha }
            .background(SplashBackground),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f - size.height * 0.03f
            val minSide = min(size.width, size.height)

            // Ripples
            for(i in 0 until 3) {
                val t = (ripple + i / 3f) % 1f
                val radius = minSide * (0.05f + 0.40f * t)
                drawCircle(
                    color = SplashAccent.copy(alpha = (1f - t) * 0.30f * draw.value),
                    radius = radius,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // Wave packet
            val left = size.width * 0.10f
            val width = size.width * 0.80f
            val amplitude = minSide * 0.12f
            val steps = 180
            val shown = (steps * draw.value).toInt().coerceAtLeast(1)
            val path = Path()
            var lastX = left
            var lastY = cy
            for(i in 0..shown) {
                val x = i / steps.toFloat()
                val d = x - 0.5f
                val envelope = exp(-(d * d) / (2f * 0.11f * 0.11f))
                val y = cy - sin(x * 2f * PI.toFloat() * 4.5f - phase) * envelope * amplitude
                val px = left + x * width
                if(i == 0) path.moveTo(px, y) else path.lineTo(px, y)
                lastX = px
                lastY = y
            }
            // soft glow
            drawPath(
                path = path,
                color = SplashAccent.copy(alpha = 0.20f),
                style = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            drawPath(
                path = path,
                color = Color.White,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            // head
            if(draw.value < 1f) {
                drawCircle(SplashAccent, radius = 4.dp.toPx(), center = Offset(lastX, lastY))
            }
        }

        Text(
            text = "PULSE",
            color = Color.White.copy(alpha = 0.92f),
            fontSize = 20.sp,
            fontWeight = FontWeight.Light,
            letterSpacing = 10.sp,
            modifier = Modifier
                .offset(y = 110.dp)
                .graphicsLayer {
                    this.alpha = reveal.value
                    translationY = (1f - reveal.value) * 14.dp.toPx()
                }
        )
    }
}

/** Small ripple loader, used while connecting. */
@Composable
fun PulseLoader(
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    color: Color = SplashAccent
) {
    val infinite = rememberInfiniteTransition(label = "loader")
    val ripple by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing), RepeatMode.Restart),
        label = "loaderRipple"
    )

    Canvas(modifier = modifier.size(size)) {
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        val maxRadius = this.size.minDimension / 2f
        for(i in 0 until 3) {
            val t = (ripple + i / 3f) % 1f
            drawCircle(
                color = color.copy(alpha = (1f - t) * 0.8f),
                radius = maxRadius * (0.15f + 0.85f * t),
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
        }
        drawCircle(color = color, radius = maxRadius * 0.14f, center = center)
    }
}
