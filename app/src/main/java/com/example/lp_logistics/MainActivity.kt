package com.example.lp_logistics

import android.Manifest.permission
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.lp_logistics.presentation.navigation.MainApp
import com.example.lp_logistics.presentation.theme.LP_LogisticsTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.CompositionLocalProvider
import com.example.lp_logistics.presentation.navigation.LocalComponentActivity
import androidx.fragment.app.FragmentActivity

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private val locationPermissionGranted = mutableStateOf(false)  // Make it a val
    private val postNotificationPermissionGranted = mutableStateOf(false) // Make it a val



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        permissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            locationPermissionGranted.value = permissions.getOrDefault(permission.ACCESS_FINE_LOCATION, false)
            if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
                postNotificationPermissionGranted.value = permissions.getOrDefault(permission.POST_NOTIFICATIONS, false)
            }
        }

        setContent {
            LP_LogisticsTheme {
                val navController = rememberNavController()
                val context = LocalContext.current

                CompositionLocalProvider(LocalComponentActivity provides this) {
                    CheckAndRequestPermissions(locationPermissionGranted, postNotificationPermissionGranted) {
                        MainApp(navController, locationPermissionGranted.value, context)
                    }
                }
            }
        }
    }

    @Composable
    fun CheckAndRequestPermissions(
        locationPermissionGranted: MutableState<Boolean>,
        postNotificationPermissionGranted: MutableState<Boolean>,
        content: @Composable () -> Unit
    ) {
        val permissions = if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            arrayOf(permission.ACCESS_FINE_LOCATION, permission.POST_NOTIFICATIONS)
        } else {
            arrayOf(permission.ACCESS_FINE_LOCATION)
        }

        var shouldShowRationale by remember { mutableStateOf(false) } // State for rationale

        if (permissions.any { !checkPermissionGranted(it) }) {
            // Check if we should show a rationale
            shouldShowRationale = permissions.any { shouldShowRequestPermissionRationale(it) }

            if (shouldShowRationale) {

                val snackbarHostState = remember { SnackbarHostState() }
                LaunchedEffect(Unit) {
                    snackbarHostState.showSnackbar("Location permission is needed for navigation.")
                }
                SnackbarHost(hostState = snackbarHostState)

            }

            // Launch permission request
            LaunchedEffect(Unit) { // Launch only once
                permissionsLauncher.launch(permissions)
            }

        } else {
            locationPermissionGranted.value = true
            if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
                postNotificationPermissionGranted.value = true
            }
        }

        // Only show content if location permission is granted
        if (locationPermissionGranted.value) {
            content()
        } else if (shouldShowRationale) {
            // Show something while waiting for permission after showing rationale
            Text("Waiting for location permission...")
        } else {
            // Show something initially when permission is not granted and no rationale shown yet
            Text("Location permission is required for navigation.")
        }
    }

    private fun checkPermissionGranted(permissionToCheck: String): Boolean =
        ContextCompat.checkSelfPermission(this, permissionToCheck) == PackageManager.PERMISSION_GRANTED
}