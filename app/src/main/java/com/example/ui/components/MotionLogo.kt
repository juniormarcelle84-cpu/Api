package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppBlue
import com.example.ui.theme.AppCoral
import com.example.ui.theme.AppWhite
import com.example.ui.theme.AppYellow
import com.example.ui.theme.ArchivoFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class MotionLogoMode {
    NORMAL,
    LOADING,
    SUCCESS
}

@Composable
fun MotionLogo(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    mode: MotionLogoMode = MotionLogoMode.NORMAL,
    showWordmark: Boolean = true,
    onTap: (() -> Unit)? = null
) {
    var animationKey by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    // Keyframe progress: 0f to 1f over 1400ms
    val animProgress = remember { Animatable(0f) }
    val wordmarkAlpha = remember { Animatable(0f) }
    val wordmarkSpacing = remember { Animatable(0.3f) }

    // Success checkmark progress
    val checkmarkProgress = remember { Animatable(0f) }

    // Loading loop transition
    val infiniteTransition = rememberInfiniteTransition(label = "loading_loop")
    val loadingOffset by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loading_brackets"
    )
    val loadingPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loading_pulse"
    )
    val loadingPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loading_pulse_alpha"
    )

    // Handle normal play-once and success transitions
    LaunchedEffect(animationKey, mode) {
        if (mode == MotionLogoMode.NORMAL) {
            animProgress.snapTo(0f)
            wordmarkAlpha.snapTo(0f)
            wordmarkSpacing.snapTo(0.3f)
            checkmarkProgress.snapTo(0f)

            // Play 1.4s animation
            animProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1400, easing = LinearEasing)
            )

            // Wordmark fades in right after, letter-spacing from .3em to .02em
            launch {
                wordmarkAlpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
            }
            launch {
                wordmarkSpacing.animateTo(0.02f, tween(600, easing = FastOutSlowInEasing))
            }
        } else if (mode == MotionLogoMode.SUCCESS) {
            animProgress.snapTo(1f)
            wordmarkAlpha.snapTo(1f)
            wordmarkSpacing.snapTo(0.02f)
            checkmarkProgress.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (mode == MotionLogoMode.NORMAL) {
                        animationKey++
                    }
                    onTap?.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val scale = size.toPx() / 120f

                scale(scale, pivot = Offset(0f, 0f)) {
                    when (mode) {
                        MotionLogoMode.LOADING -> {
                            // Infinite alternate looping state
                            drawLogoBrackets(
                                leftOffset = loadingOffset,
                                rightOffset = -loadingOffset
                            )
                            // Looping pulse
                            drawCircle(
                                color = AppYellow.copy(alpha = loadingPulseAlpha),
                                radius = 12f * loadingPulseScale,
                                center = Offset(60f, 60f),
                                style = Stroke(width = 3f)
                            )
                            // Center dot
                            drawCircle(
                                color = AppYellow,
                                radius = 9f,
                                center = Offset(60f, 60f)
                            )
                        }

                        MotionLogoMode.SUCCESS -> {
                            // Brackets settled
                            drawLogoBrackets(leftOffset = 0f, rightOffset = 0f)

                            // Yellow dot morphing into checkmark
                            val morphFactor = checkmarkProgress.value
                            val circleRadius = 9f + (6f * morphFactor) // expands to r=15

                            drawCircle(
                                color = AppYellow,
                                radius = circleRadius,
                                center = Offset(60f, 60f)
                            )

                            if (morphFactor > 0.1f) {
                                // Draw checkmark in dark background color inside yellow circle
                                val checkPath = Path().apply {
                                    val startX = 53f
                                    val startY = 60f
                                    val midX = 58f
                                    val midY = 65f
                                    val endX = 68f
                                    val endY = 55f

                                    moveTo(startX, startY)
                                    lineTo(
                                        startX + (midX - startX) * (morphFactor * 2f).coerceAtMost(1f),
                                        startY + (midY - startY) * (morphFactor * 2f).coerceAtMost(1f)
                                    )
                                    if (morphFactor > 0.5f) {
                                        val secondProgress = ((morphFactor - 0.5f) * 2f).coerceAtMost(1f)
                                        lineTo(
                                            midX + (endX - midX) * secondProgress,
                                            midY + (endY - midY) * secondProgress
                                        )
                                    }
                                }
                                drawPath(
                                    path = checkPath,
                                    color = Color(0xFF1B1A1B),
                                    style = Stroke(width = 3.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            }
                        }

                        MotionLogoMode.NORMAL -> {
                            val p = animProgress.value

                            // Left bracket: translateX(-24px to 0px) with cubic-bezier(.65,0,.25,1)
                            val leftEase = cubicBezierEase(0.65f, 0f, 0.25f, 1f, p)
                            val leftOffset = -24f * (1f - leftEase)

                            // Right bracket: translateX(24px to 0px) with cubic-bezier(.65,0,.25,1)
                            val rightOffset = 24f * (1f - leftEase)

                            drawLogoBrackets(leftOffset = leftOffset, rightOffset = rightOffset)

                            // Pulse circle .p: 0% to 55%: scale(0.6), opacity 0. 65%: opacity 0.9. to: scale(2.0), opacity 0
                            if (p > 0.55f) {
                                val pulseP = (p - 0.55f) / 0.45f // 0f to 1f
                                val pulseScale = 0.6f + (1.4f * pulseP) // scale 0.6 to 2.0
                                val pulseAlpha = when {
                                    p <= 0.65f -> ((p - 0.55f) / 0.10f) * 0.9f
                                    else -> (1f - pulseP) * 0.9f
                                }.coerceIn(0f, 1f)

                                drawCircle(
                                    color = AppYellow.copy(alpha = pulseAlpha),
                                    radius = 12f * pulseScale,
                                    center = Offset(60f, 60f),
                                    style = Stroke(width = 3f)
                                )
                            }

                            // Center dot .d: 0% to 55%: scale(0). 55% to 100%: cubic-bezier(.3,1.6,.5,1)
                            if (p > 0.55f) {
                                val dotP = (p - 0.55f) / 0.45f
                                val dotScale = cubicBezierOvershoot(0.3f, 1.6f, 0.5f, 1f, dotP).coerceAtLeast(0f)
                                scale(dotScale, pivot = Offset(60f, 60f)) {
                                    drawCircle(
                                        color = AppYellow,
                                        radius = 9f,
                                        center = Offset(60f, 60f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showWordmark) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "APIMATCH",
                color = AppWhite.copy(
                    alpha = if (mode == MotionLogoMode.LOADING) 1f else wordmarkAlpha.value
                ),
                fontSize = 20.sp,
                fontFamily = ArchivoFontFamily,
                fontWeight = FontWeight.Black,
                letterSpacing = if (mode == MotionLogoMode.LOADING) 0.02.em else wordmarkSpacing.value.em
            )
        }
    }
}

/**
 * Draws the left and right brackets using exact SVG path data:
 * Left: M44 26c-10 0-14 4-14 14v10c0 6-3 10-8 10 5 0 8 4 8 10v10c0 10 4 14 14 14
 * Right: M76 26c10 0 14 4 14 14v10c0 6 3 10 8 10-5 0-8 4-8 10v10c0 10-4 14-14 14
 */
private fun DrawScope.drawLogoBrackets(leftOffset: Float, rightOffset: Float) {
    // Left blue bracket
    val leftPath = Path().apply {
        moveTo(44f, 26f)
        relativeCubicTo(-10f, 0f, -14f, 4f, -14f, 14f)
        relativeLineTo(0f, 10f)
        relativeCubicTo(0f, 6f, -3f, 10f, -8f, 10f)
        relativeCubicTo(5f, 0f, 8f, 4f, 8f, 10f)
        relativeLineTo(0f, 10f)
        relativeCubicTo(0f, 10f, 4f, 14f, 14f, 14f)
    }

    translate(left = leftOffset, top = 0f) {
        drawPath(
            path = leftPath,
            color = AppBlue,
            style = Stroke(
                width = 10f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }

    // Right coral bracket
    val rightPath = Path().apply {
        moveTo(76f, 26f)
        relativeCubicTo(10f, 0f, 14f, 4f, 14f, 14f)
        relativeLineTo(0f, 10f)
        relativeCubicTo(0f, 6f, 3f, 10f, 8f, 10f)
        relativeCubicTo(-5f, 0f, -8f, 4f, -8f, 10f)
        relativeLineTo(0f, 10f)
        relativeCubicTo(0f, 10f, -4f, 14f, -14f, 14f)
    }

    translate(left = rightOffset, top = 0f) {
        drawPath(
            path = rightPath,
            color = AppCoral,
            style = Stroke(
                width = 10f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

/**
 * Approximate cubic bezier evaluation for .65, 0, .25, 1
 */
private fun cubicBezierEase(p1x: Float, p1y: Float, p2x: Float, p2y: Float, t: Float): Float {
    // Cubic bezier parametric calculation with binary search for x -> y
    val clampedT = t.coerceIn(0f, 1f)
    var low = 0f
    var high = 1f
    var s = clampedT

    // 8 iterations binary search for parameter 's' where B_x(s) == clampedT
    for (i in 0..7) {
        val currentX = 3 * (1 - s) * (1 - s) * s * p1x + 3 * (1 - s) * s * s * p2x + s * s * s
        if (currentX < clampedT) {
            low = s
        } else {
            high = s
        }
        s = (low + high) / 2f
    }

    return 3 * (1 - s) * (1 - s) * s * p1y + 3 * (1 - s) * s * s * p2y + s * s * s
}

/**
 * Overshoot cubic bezier evaluation for .3, 1.6, .5, 1
 */
private fun cubicBezierOvershoot(p1x: Float, p1y: Float, p2x: Float, p2y: Float, t: Float): Float {
    val clampedT = t.coerceIn(0f, 1f)
    var low = 0f
    var high = 1f
    var s = clampedT

    for (i in 0..7) {
        val currentX = 3 * (1 - s) * (1 - s) * s * p1x + 3 * (1 - s) * s * s * p2x + s * s * s
        if (currentX < clampedT) {
            low = s
        } else {
            high = s
        }
        s = (low + high) / 2f
    }

    return 3 * (1 - s) * (1 - s) * s * p1y + 3 * (1 - s) * s * s * p2y + s * s * s
}
