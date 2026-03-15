package com.lockedin.app.presentation.auth

/**
 * High-level authentication state for the app.
 *
 * Used by Splash / Main navigation to decide whether to show onboarding,
 * setup flow, or login vs. home.
 */
sealed class AuthState {

    /**
     * No master password has been configured yet.
     * User must go through onboarding + setup.
     */
    data object SetupRequired : AuthState()

    /**
     * User has a configured vault but is not currently authenticated.
     */
    data object Locked : AuthState()

    /**
     * User is authenticated and the vault is unlocked.
     */
    data object Unlocked : AuthState()
}

