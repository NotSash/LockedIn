package com.lockedin.app.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Base shape system matching the spacing spec:
 * - Cards: 20dp
 * - Buttons: 16dp
 * - Inputs: 12dp
 */
val LockedInShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),   // Inputs
    medium = RoundedCornerShape(16.dp),  // Buttons
    large = RoundedCornerShape(20.dp),   // Cards
    extraLarge = RoundedCornerShape(28.dp)
)

