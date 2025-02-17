package com.example.lp_logistics.presentation.navigation

import androidx.compose.material3.Text  // Import for Text
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lp_logistics.data.local.UserManager
import com.example.lp_logistics.data.remote.requests.User
import com.example.lp_logistics.presentation.screens.home.HomeScreen
import com.example.lp_logistics.presentation.screens.login.LoginScreen
import com.example.lp_logistics.presentation.screens.navigation.NavigationScreen
import com.example.lp_logistics.presentation.screens.profile.ProfileScreen
import com.google.android.gms.maps.model.LatLng
import androidx.compose.material3.CircularProgressIndicator // Import for loading indicator
import androidx.compose.foundation.layout.* // Import for layout modifiers
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity


@Composable
fun MainApp(
    navController: androidx.navigation.NavHostController,
    locationPermissionGranted: Boolean,
    context: android.content.Context,
) {
    val activity = LocalContext.current as FragmentActivity
    var isLoggedIn by remember { mutableStateOf(false) }
    var user by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) } // Loading state

    LaunchedEffect(Unit) {
        user = UserManager.getUser(context)
        isLoggedIn = user != null
        isLoading = false // Loading finished
    }

    if (isLoading) {
        // Show a loading indicator while checking login status
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) "home" else "login",
        ) {
            composable("login") {
                LoginScreen(context, navController)
            }
            composable("home") {
                HomeScreen(navController, context)
            }

            composable("profile") {
                ProfileScreen(navController, context)
            }

            composable("navigation") {
                if (locationPermissionGranted) {
                    NavigationScreen(activity,destination = LatLng(19.432608, -99.133209)) //check if id need context or not
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Location permission is required for navigation.")
                    }
                }
            }
        }
    }
}