package com.lockedin.app.core.ui.animation

import androidx.compose.animation.core.EaseInBack
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

// Durations
const val DurationShort = 100
const val DurationMedium = 250
const val DurationLong = 400
const val DurationXL = 500
const val DurationSecurityGauge = 1200
const val DurationShimmer = 1500

// Springs
val SpringButtonPress = spring<Float>(
    dampingRatio = 0.6f,
    stiffness = Spring.StiffnessMedium
)

val SpringNavPill = spring<Float>(
    dampingRatio = 0.7f,
    stiffness = Spring.StiffnessMedium
)

val SpringBottomSheet = spring<Float>(
    dampingRatio = 0.8f,
    stiffness = Spring.StiffnessMediumLow
)

// Tweens / easings
val TweenPageEnter = tween<Float>(
    durationMillis = DurationLong + 100,
    easing = EaseInOut
)

val TweenPageExit = tween<Float>(
    durationMillis = DurationMedium,
    easing = EaseInOut
)

val TweenDialogEnter = tween<Float>(
    durationMillis = DurationMedium,
    easing = EaseOutBack
)

val TweenDeleteEaseInBack = tween<Float>(
    durationMillis = DurationLong,
    easing = EaseInBack
)

