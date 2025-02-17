package com.example.lp_logistics.presentation.navigation

// ActivityProvider.kt
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.fragment.app.FragmentActivity

val LocalFragmentActivity = staticCompositionLocalOf<FragmentActivity> {
    error("No FragmentActivity provided")
}
