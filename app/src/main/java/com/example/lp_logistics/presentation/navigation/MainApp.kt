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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.lp_logistics.presentation.components.QR.Camera.ScannerNavigator
import com.example.lp_logistics.presentation.screens.navigationV2.MapScreen
import com.example.lp_logistics.presentation.screens.profile.VehicleScreen
import com.example.lp_logistics.presentation.screens.routes.SelectedRouteScreen
import com.example.lp_logistics.presentation.screens.warehouse.arrivals.ArrivalsScreen
import com.example.lp_logistics.presentation.screens.warehouse.arrivals.ConfirmArrival
import com.example.lp_logistics.presentation.screens.warehouse.arrivals.DeliveryScreen
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
    var authState by remember { mutableStateOf<AuthState>(AuthState.Loading) }
    var currentUser by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Use LaunchedEffect to safely call suspend function
    LaunchedEffect(Unit) {
        val user = UserManager.getUser(context) // This is now properly called in coroutine
        authState = when {
            user == null -> AuthState.Unauthenticated
            user.roleID in listOf(2, 3) -> AuthState.Authenticated(user)
            else -> {
                UserManager.clearUser(context)
                AuthState.Unauthenticated
            }
        }
        currentUser = user
        isLoading = false
    }
    val startDestination = remember(currentUser) {
        when (currentUser?.roleID) {
            2 -> "pallets" // Warehouse
            3 -> "home"    // Driver
            else -> "login"
        }
    }

    // Navigation logic
    LaunchedEffect(authState, currentUser) {
        if (authState is AuthState.Authenticated) {
            // Only navigate if we're not already on the correct screen
            if (navController.currentDestination?.route != startDestination) {
                navController.navigate(startDestination) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    if (isLoading) {
        LoadingScreen()
    } else {
        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {
            composable("login") {
                LoginScreen(context, navController)
            }
            composable("home") {
                HomeScreen(navController, context)
            }

            // In your NavHost declaration:
            composable(
                "profile?isWarehouse={isWarehouse}",
                ) { backStackEntry ->
                    val isWarehouse = backStackEntry.arguments?.getBoolean("isWarehouse") ?: false
                    ProfileScreen(navController, context)
                }

            composable("navigation/{deliveryId}/{deliveryType}/{routeJson}",
                arguments = listOf(
                navArgument("deliveryId") { type = NavType.StringType }, // or NavType.LongType if it's numeric
                navArgument("deliveryType") { type = NavType.StringType },    // or NavType.LongType if it's numeric
                navArgument("routeJson") { type = NavType.StringType }
            )
            ) { backStackEntry ->
                val deliveryId = backStackEntry.arguments?.getString("deliveryId") ?: ""
                val deliveryType = backStackEntry.arguments?.getString("deliveryType") ?: ""
                val routeJson = backStackEntry.arguments?.getString("routeJson") ?: ""
//
                MapScreen(navController, routeJson, deliveryId, deliveryType)
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

            composable(
                "qr-scanner?isPallet={isPallet}&delivery={delivery}&driver={driver}",
                arguments = listOf(
                    navArgument("isPallet") { type = NavType.BoolType; defaultValue = false },
                    navArgument("delivery") { type = NavType.BoolType; defaultValue = false },
                    navArgument("driver") { type = NavType.BoolType; defaultValue = false }
                )
            ) { backStackEntry ->
                val isPallet = backStackEntry.arguments?.getBoolean("isPallet") ?: false
                val delivery = backStackEntry.arguments?.getBoolean("delivery") ?: false
                val driver = backStackEntry.arguments?.getBoolean("driver") ?: false

                ScannerNavigator(navController, isPallet, delivery, driver)
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

            composable("delivery-info?deliveryID={deliveryID}"){ backStackEntry ->
                val deliveryID = backStackEntry.arguments?.getString("deliveryID")?.toInt() ?: 0
                println("deliveryID in Main app: $deliveryID")
                DeliveryScreen(deliveryID, navController)
            }

            composable("vehicle"){
                VehicleScreen(navController)
            }

            composable("confirming-delivery?code={code}") { backStackEntry ->
                val code = backStackEntry.arguments?.getString("code") ?: ""
                ConfirmArrival(code, navController)
            }

        }
    }
}

@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    object Unauthenticated : AuthState()
}



