package com.lockedin.app.presentation.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lockedin.app.core.ui.components.GlassmorphicSurface
import com.lockedin.app.core.ui.components.GradientButton
import com.lockedin.app.core.ui.components.NeumorphicButton
import kotlinx.coroutines.launch

data class OnboardingCallbacks(
    val onFinished: (wantsTour: Boolean) -> Unit
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel(),
    callbacks: OnboardingCallbacks
) {
    val state by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 3 }
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }

    val wantsTour = state.wantsGuidedTour
    if (state.isCompleted && wantsTour != null) {
        LaunchedEffect(state.isCompleted, wantsTour) {
            callbacks.onFinished(wantsTour)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Skip button
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .scale(1.0f)
                        .then(
                            Modifier
                                .padding(4.dp)
                                .neumorphicClickable {
                                    viewModel.onSkip()
                                }
                        )
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 0.dp),
                beyondBoundsPageCount = 1,
                userScrollEnabled = true
            ) { page ->
                when (page) {
                    0 -> OnboardingPage(
                        title = "Your passwords, locked in.",
                        subtitle = "Military-grade encryption keeps your passwords safe",
                        iconGradient = Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )

                    1 -> OnboardingPage(
                        title = "Generate unbreakable passwords",
                        subtitle = "Create strong, unique passwords with one tap",
                        iconGradient = Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.primary
                            )
                        )
                    )

                    else -> OnboardingPage(
                        title = "Autofill everywhere",
                        subtitle = "Sign in instantly across all your apps and browsers",
                        iconGradient = Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.tertiary,
                                MaterialTheme.colorScheme.primary
                            )
                        )
                    )
                }
            }

            OnboardingPagerIndicator(
                currentPage = pagerState.currentPage,
                pageCount = 3
            )

            val isLastPage = pagerState.currentPage == 2

            AnimatedVisibility(
                visible = !isLastPage,
                enter = fadeIn() + scaleIn(initialScale = 0.9f),
                exit = fadeOut() + scaleOut(targetScale = 0.9f)
            ) {
                NeumorphicButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 24.dp),
                    text = "Next",
                    onClick = {
                        if (pagerState.currentPage < 2) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                viewModel.onNext()
                            }
                        } else {
                            viewModel.onReachedLastPage()
                        }
                    }
                )
            }

            AnimatedVisibility(
                visible = isLastPage,
                enter = fadeIn() + scaleIn(initialScale = 0.9f),
                exit = fadeOut() + scaleOut(targetScale = 0.9f)
            ) {
                GradientButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 24.dp),
                    text = "Get Started",
                    onClick = {
                        viewModel.onReachedLastPage()
                    }
                )
            }
        }

        if (state.showTourSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val sheetScope = rememberCoroutineScope()

            ModalBottomSheet(
                onDismissRequest = {
                    sheetScope.launch { sheetState.hide() }.invokeOnCompletion {
                        viewModel.onTourChoice(false)
                    }
                },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Would you like a quick tour of all features?",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "We can guide you through the most powerful parts of LockedIn, or you can explore on your own.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    GradientButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Yes, show me around",
                        onClick = {
                            sheetScope.launch { sheetState.hide() }.invokeOnCompletion {
                                viewModel.onTourChoice(true)
                            }
                        }
                    )
                    NeumorphicButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = "No, I'll explore myself",
                        onClick = {
                            sheetScope.launch { sheetState.hide() }.invokeOnCompletion {
                                viewModel.onTourChoice(false)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingPage(
    title: String,
    subtitle: String,
    iconGradient: Brush,
    iconSize: Dp = 160.dp
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Placeholder animated lock / illustration using composable animation.
        GlassmorphicSurface(
            modifier = Modifier
                .size(iconSize)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    spotColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(iconGradient, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "\uD83D\uDD10",
                    fontSize = 56.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        )
    }
}

@Composable
private fun OnboardingPagerIndicator(
    currentPage: Int,
    pageCount: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pageCount) { index ->
                val isActive = index == currentPage
                val width = if (isActive) 24.dp else 8.dp
                val color = if (isActive) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                }
                Surface(
                    modifier = Modifier
                        .height(8.dp)
                        .width(width),
                    color = color,
                    shape = CircleShape
                ) {}
            }
        }
    }
}

/**
 * Simple clickable effect that can be replaced with a more complete neumorphic
 * interaction; kept minimal here to avoid circular dependency with components.
 */
private fun Modifier.neumorphicClickable(
    onClick: () -> Unit
): Modifier = this.then(
    androidx.compose.foundation.clickable(
        interactionSource = androidx.compose.foundation.interaction.MutableInteractionSource(),
        indication = null,
        onClick = onClick
    )
)

