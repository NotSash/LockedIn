package com.lockedin.app.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lockedin.app.core.security.BiometricHelper
import com.lockedin.app.core.security.SecureClipboardManager
import com.lockedin.app.core.ui.components.AnimatedBottomNavBar
import com.lockedin.app.core.ui.components.BottomNavItemUi
import com.lockedin.app.presentation.addEdit.AddEditScreen
import com.lockedin.app.presentation.addEdit.AddEditViewModel
import com.lockedin.app.presentation.auth.setup.MasterPasswordSetupScreen
import com.lockedin.app.presentation.auth.setup.PinSetupScreen
import com.lockedin.app.presentation.auth.setup.SetupViewModel
import com.lockedin.app.presentation.auth.login.LoginScreen
import com.lockedin.app.presentation.generator.GeneratorScreen
import com.lockedin.app.presentation.home.HomeScreen
import com.lockedin.app.presentation.categories.CategoriesScreen
import com.lockedin.app.presentation.categories.CategoryPasswordsScreen
import com.lockedin.app.presentation.detail.DetailScreen
import com.lockedin.app.presentation.onboarding.OnboardingScreen
import com.lockedin.app.presentation.security.SecurityReportScreen
import com.lockedin.app.presentation.settings.SettingsScreen
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun LockedInRootNav(
    clipboardManager: SecureClipboardManager,
    clipboardTimeoutMillis: Long,
    biometricHelper: BiometricHelper
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                if (currentRoute in BottomNavItem.values().map { it.screen.route }) {
                    val items = BottomNavItem.values().map {
                        BottomNavItemUi(
                            key = it.screen.route,
                            label = it.label,
                            icon = it.icon,
                            selectedIcon = it.icon
                        )
                    }
                    AnimatedBottomNavBar(
                        items = items,
                        selectedKey = currentRoute ?: Screen.Home.route,
                        onItemSelected = { route ->
                            navController.navigate(route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(Screen.Home.route) { saveState = true }
                            }
                        }
                    )
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.fillMaxSize(),
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(durationMillis = 300, easing = EaseInOut)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(durationMillis = 200, easing = EaseInOut)
                    ) + fadeOut(animationSpec = tween(200))
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(durationMillis = 300, easing = EaseInOut)
                    ) + fadeIn(animationSpec = tween(300))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(durationMillis = 200, easing = EaseInOut)
                    ) + fadeOut(animationSpec = tween(200))
                }
            ) {
                addCoreGraph(
                    navController = navController,
                    clipboardManager = clipboardManager,
                    clipboardTimeoutMillis = clipboardTimeoutMillis,
                    biometricHelper = biometricHelper
                )
            }
        }
    }
}

@VisibleForTesting
fun NavGraphBuilder.addCoreGraph(
    navController: NavHostController,
    clipboardManager: SecureClipboardManager,
    clipboardTimeoutMillis: Long,
    biometricHelper: BiometricHelper
) {
    composable(Screen.Home.route) {
        HomeScreen(
            callbacks = com.lockedin.app.presentation.home.HomeCallbacks(
                onNavigateToGenerator = { navController.navigate(Screen.Generator.route) },
                onNavigateToAddPassword = { navController.navigate(Screen.AddPassword.route) },
                onNavigateToHistory = { /* history bottom sheet handled in Generator screen */ },
                onNavigateToSearch = { /* search overlay handled in Home screen */ },
                onNavigateToSecurityReport = { navController.navigate(Screen.SecurityReport.route) },
                onNavigateToDetail = { id -> navController.navigate(Screen.Detail.create(id)) },
                onCopyPassword = { value ->
                    clipboardManager.copyWithTimeout(
                        id = System.currentTimeMillis(),
                        text = value,
                        timeoutMillis = clipboardTimeoutMillis
                    )
                }
            )
        )
    }

    composable(Screen.Generator.route) {
        GeneratorScreen(
            secureClipboardManager = clipboardManager,
            clipboardTimeoutMillis = clipboardTimeoutMillis,
            callbacks = com.lockedin.app.presentation.generator.GeneratorCallbacks(
                onSaveToVault = { /* wired from Add/Edit sheet in a later phase */ }
            )
        )
    }

    composable(Screen.Categories.route) {
        CategoriesScreen(
            callbacks = com.lockedin.app.presentation.categories.CategoriesCallbacks(
                onBack = { navController.popBackStack() },
                onCategoryClick = { name ->
                    navController.navigate("category_passwords/$name")
                }
            )
        )
    }

    composable(
        route = "category_passwords/{categoryName}"
    ) { backStack ->
        val categoryName = backStack.arguments?.getString("categoryName") ?: ""
        CategoryPasswordsScreen(
            categoryName = categoryName,
            secureClipboardManager = clipboardManager,
            clipboardTimeoutMillis = clipboardTimeoutMillis,
            callbacks = com.lockedin.app.presentation.categories.CategoryPasswordsCallbacks(
                onBack = { navController.popBackStack() },
                onPasswordClick = { id -> navController.navigate(Screen.Detail.create(id)) }
            )
        )
    }

    composable(Screen.Settings.route) {
        SettingsScreen(
            callbacks = com.lockedin.app.presentation.settings.SettingsCallbacks(
                onBack = { navController.popBackStack() },
                onChangeMasterPassword = { navController.navigate(Screen.ChangeMasterPassword.route) },
                onChangePin = { navController.navigate(Screen.ChangePin.route) },
                onOpenExport = { /* export sheet handled by Settings host */ },
                onOpenImport = { navController.navigate(Screen.Import.route) },
                onOpenExcludedApps = { navController.navigate(Screen.ExcludedApps.route) }
            )
        )
    }

    composable(Screen.AddPassword.route) {
        AddEditScreen(
            passwordId = null,
            callbacks = com.lockedin.app.presentation.addEdit.AddEditCallbacks(
                onBack = { navController.popBackStack() },
                onSaved = { id ->
                    navController.popBackStack()
                    navController.navigate(Screen.Detail.create(id))
                }
            )
        )
    }

    composable(Screen.Detail.route) { backStack ->
        val id = backStack.arguments?.getString("passwordId")?.toLongOrNull() ?: 0L
        DetailScreen(
            passwordId = id,
            secureClipboardManager = clipboardManager,
            clipboardTimeoutMillis = clipboardTimeoutMillis,
            callbacks = com.lockedin.app.presentation.detail.DetailCallbacks(
                onBack = { navController.popBackStack() },
                onDeleted = {
                    navController.popBackStack()
                },
                onEdit = { editId ->
                    navController.navigate(Screen.EditPassword.create(editId))
                }
            )
        )
    }

    composable(Screen.EditPassword.route) { backStack ->
        val id = backStack.arguments?.getString("passwordId")?.toLongOrNull()
        AddEditScreen(
            passwordId = id,
            callbacks = com.lockedin.app.presentation.addEdit.AddEditCallbacks(
                onBack = { navController.popBackStack() },
                onSaved = { savedId ->
                    navController.popBackStack()
                    navController.navigate(Screen.Detail.create(savedId))
                }
            )
        )
    }

    composable(Screen.SecurityReport.route) {
        SecurityReportScreen(
            callbacks = com.lockedin.app.presentation.security.SecurityReportCallbacks(
                onBack = { navController.popBackStack() },
                onPasswordClick = { id -> navController.navigate(Screen.Detail.create(id)) }
            )
        )
    }
}

