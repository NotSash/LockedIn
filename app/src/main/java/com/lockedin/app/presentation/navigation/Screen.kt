package com.lockedin.app.presentation.navigation

/**
 * Central definition of all navigation routes in the app.
 */
sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object MasterPasswordSetup : Screen("auth_setup_master")
    data object PinSetup : Screen("auth_setup_pin")
    data object BiometricSetup : Screen("auth_setup_biometric")
    data object Login : Screen("auth_login")

    data object Home : Screen("home")
    data object Generator : Screen("generator")
    data object Categories : Screen("categories")
    data object Settings : Screen("settings")

    data object AddPassword : Screen("add_password")
    data object EditPassword : Screen("edit_password/{passwordId}") {
        fun create(passwordId: Long) = "edit_password/$passwordId"
    }

    data object Detail : Screen("detail/{passwordId}") {
        fun create(passwordId: Long) = "detail/$passwordId"
    }

    data object SecurityReport : Screen("security_report")
    data object Import : Screen("settings_import")
    data object ChangeMasterPassword : Screen("settings_change_master")
    data object ChangePin : Screen("settings_change_pin")
    data object ExcludedApps : Screen("settings_excluded_apps")
}

