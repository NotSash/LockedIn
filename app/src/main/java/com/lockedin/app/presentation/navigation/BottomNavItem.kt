package com.lockedin.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Bottom navigation items for the primary app sections.
 */
enum class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
) {
    HOME(
        screen = Screen.Home,
        label = "Home",
        icon = Icons.Rounded.Home
    ),
    GENERATOR(
        screen = Screen.Generator,
        label = "Generator",
        icon = Icons.Rounded.Lock
    ),
    CATEGORIES(
        screen = Screen.Categories,
        label = "Categories",
        icon = Icons.Rounded.Lock
    ),
    SETTINGS(
        screen = Screen.Settings,
        label = "Settings",
        icon = Icons.Rounded.Settings
    )
}

