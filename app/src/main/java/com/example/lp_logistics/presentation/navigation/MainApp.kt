package com.example.lp_logistics.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lp_logistics.data.local.UserManager
import com.example.lp_logistics.data.remote.requests.User
import com.example.lp_logistics.presentation.screens.home.HomeScreen
import com.example.lp_logistics.presentation.screens.login.LoginScreen
import com.example.lp_logistics.presentation.screens.profile.ProfileScreen
import androidx.compose.material3.CircularProgressIndicator // Import for loading indicator
import androidx.compose.foundation.layout.* // Import for layout modifiers
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.lp_logistics.presentation.components.QR.Camera.ScannerNavigator
import com.example.lp_logistics.presentation.screens.navigationV2.MapScreen
import com.example.lp_logistics.presentation.screens.routes.SelectedRouteScreen
import com.example.lp_logistics.presentation.screens.warehouse.arrivals.ArrivalsScreen
import com.example.lp_logistics.presentation.screens.warehouse.pallets.BoxInfoScreen
import com.example.lp_logistics.presentation.screens.warehouse.pallets.CreatePalletScreen
import com.example.lp_logistics.presentation.screens.warehouse.pallets.PalletScreen


@RequiresApi(Build.VERSION_CODES.O)
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
    var startDestination by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        user = UserManager.getUser(context)
        isLoggedIn = user != null
        isLoading = false // Loading finished

    }

    LaunchedEffect(user) {
        startDestination = when {
            isLoggedIn && user?.role == "Chofer" -> "home"
            isLoggedIn && user?.role == "Almacenista" -> "arrivals"
            else -> "login"
        }
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
            startDestination = startDestination
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

            composable("navigation/{routeJson}",
                arguments = listOf(navArgument("routeJson") { type = NavType.StringType })) { backStackEntry ->
                val routeJson = backStackEntry.arguments?.getString("routeJson") ?: ""
//
                MapScreen(navController, routeJson)
            }

            composable("arrivals"){
                ArrivalsScreen(navController)
            }

            composable("pallets"){
                PalletScreen(navController)
            }

            composable(
                "create-pallet?palletIDNav={palletIDNav}&creating={creating}"
            ) { backStackEntry ->
                val creating = backStackEntry.arguments?.getString("creating")?.toBoolean() ?: false
                val palletIDNav = backStackEntry.arguments?.getString("palletIDNav")?.toInt() ?: 0
                CreatePalletScreen(navController, creating, palletIDNav)
            }

            composable("qr-scanner?isPallet={isPallet}"){ backStackEntry ->
                val isPallet = backStackEntry.arguments?.getString("isPallet")?.toBoolean() ?: false
                    ScannerNavigator(navController = navController, isPallet = isPallet)
                }

            composable("box-info?boxID={boxID}"){ backStackEntry ->
                val boxID = backStackEntry.arguments?.getString("boxID")?.toInt() ?: 0
                println("Box ID: $boxID")
                BoxInfoScreen(boxID, navController)
            }


            composable("selected-delivery?deliveryID={deliveryID}"){ backStackEntry ->
                val deliveryID = backStackEntry.arguments?.getString("deliveryID")?.toInt() ?: 0
                println("Box ID: $deliveryID")
                SelectedRouteScreen( navController, deliveryID)
            }


        }
    }
}



