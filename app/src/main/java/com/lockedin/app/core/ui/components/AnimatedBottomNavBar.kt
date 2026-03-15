package com.lockedin.app.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lockedin.app.core.ui.animation.SpringNavPill

/**
 * Bottom nav bar with sliding pill indicator, icon morphs, and haptic feedback.
 */
data class BottomNavItemUi(
    val key: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

@Composable
fun AnimatedBottomNavBar(
    items: List<BottomNavItemUi>,
    selectedKey: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val background = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val selected = item.key == selectedKey
                    val scale by animateFloatAsState(
                        targetValue = if (selected) 1.1f else 1f,
                        animationSpec = SpringNavPill,
                        label = "navIconScale"
                    )
                    val alpha by animateFloatAsState(
                        targetValue = if (selected) 1f else 0.6f,
                        animationSpec = SpringNavPill,
                        label = "navIconAlpha"
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (!selected) {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onItemSelected(item.key)
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            if (selected) {
                                NavPill(
                                    width = 64.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                )
                            }
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.icon,
                                contentDescription = item.label,
                                tint = if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .scale(scale)
                                    .alpha(alpha)
                            )
                        }
                        AnimatedVisibility(
                            visible = selected,
                            enter = expandHorizontally(),
                            exit = shrinkHorizontally()
                        ) {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NavPill(
    width: Dp,
    color: Color
) {
    // Use spring<Dp> here instead of SpringNavPill (which is SpringSpec<Float>)
    val animatedWidth by animateDpAsState(
        targetValue = width,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = 300f
        ),
        label = "navPillWidth"
    )

    Box(
        modifier = Modifier
            .offset(y = 16.dp)
            .height(32.dp)
            .width(animatedWidth)
            .clip(RoundedCornerShape(999.dp))
            .background(color)
    )
}