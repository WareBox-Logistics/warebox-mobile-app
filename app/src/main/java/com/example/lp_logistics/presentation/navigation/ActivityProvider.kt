package com.example.lp_logistics.presentation.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.activity.ComponentActivity

val LocalComponentActivity = staticCompositionLocalOf<ComponentActivity> {
    error("No ComponentActivity provided")
}
