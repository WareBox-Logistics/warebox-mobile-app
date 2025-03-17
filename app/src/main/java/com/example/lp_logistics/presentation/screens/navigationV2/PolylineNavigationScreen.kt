package com.example.lp_logistics.presentation.screens.navigationV2

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.lp_logistics.domain.model.FeatureCollection
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices.*
import com.google.android.gms.maps.model.LatLng

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lp_logistics.presentation.components.BottomSheet
import com.example.lp_logistics.presentation.theme.LightBlue
import com.example.lp_logistics.presentation.theme.LightCreme
import com.example.lp_logistics.presentation.theme.LightOrange
import com.example.lp_logistics.presentation.theme.Orange
import com.example.lp_logistics.presentation.theme.Warning
import com.example.lp_logistics.presentation.theme.WarningText
import com.google.android.gms.maps.CameraUpdateFactory

// Google Maps SDK for Android
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState

// Google Maps Compose
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: androidx.navigation.NavHostController,
    viewModel: RouteViewModel = hiltViewModel(),
) {

    val routeData by viewModel.routeData.observeAsState()
    val currentInstruction by viewModel.instruction.observeAsState()
    var isLoading by remember { mutableStateOf(true) }

    val navigationStarted by viewModel.navigationStarted.observeAsState()

    val cameraPositionState = rememberCameraPositionState {
        routeData?.features?.firstOrNull()?.properties?.waypoints?.firstOrNull()?.let { firstWaypoint ->
            position = CameraPosition.fromLatLngZoom(
                LatLng(firstWaypoint.location[1], firstWaypoint.location[0]),
                15f
            )
            isLoading = false
        }
    }

    val coroutineScope =rememberCoroutineScope()
    var mapLoaded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val fusedLocationProviderClient = getFusedLocationProviderClient(context)
    val currentLocation by viewModel.currentLocation.observeAsState()
    val isOffRoute by viewModel.isOffRoute.observeAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()


    LaunchedEffect(Unit) {
        viewModel.loadRoute()
    }

    LaunchedEffect(routeData) {
        routeData?.features?.firstOrNull()?.properties?.waypoints?.firstOrNull()?.let { firstWaypoint ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(firstWaypoint.location[1], firstWaypoint.location[0]),
                15f
            )
            isLoading = false
        }
    }

    LaunchedEffect(currentLocation) {
        currentLocation?.let { location ->
            coroutineScope.launch {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newCameraPosition(
                        CameraPosition(
                            LatLng(location.latitude, location.longitude),
                            19f,
                            60f,
                            0f
                        )
                    ),
                    durationMs = 1000
                )
            }
        }
    }



    Column(modifier = Modifier.fillMaxSize()) {
        // Header for navigation instructions
        Box( modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Orange)
            .padding(16.dp),
            contentAlignment = Alignment.Center){
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // Fixed height for the header
                .background(LightBlue) // Example: Blue background
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (currentInstruction.isNullOrEmpty()) "Click the button to start" else currentInstruction.toString(),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

    Box(modifier = Modifier.fillMaxSize()) {
        routeData?.let {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier
                    .align(Alignment.Center)
                    .background(Orange))
            } else {
                GoogleMapView(
                    routeData = it,
                    onMapLoaded = { mapLoaded = true },
                    fusedLocationProviderClient = fusedLocationProviderClient,
                    cameraPositionState = cameraPositionState
                )
            }
        }

        // Button to Start Trip
        if (mapLoaded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                ,
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Spacer(Modifier.weight(1f))
                    FloatingActionButton(
                        onClick = {
                            showBottomSheet = true
                        },
                        modifier = Modifier
                            .padding(16.dp),
                        containerColor = Warning,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Warning,
                            contentDescription = "Warning",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(16.dp),
                onClick = {
                    println("Clicked Start Trip")
                    if (!navigationStarted!!) {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            fusedLocationProviderClient.lastLocation
                                .addOnSuccessListener { location ->
                                    if (location != null) {
                                        coroutineScope.launch {
                                            try {
                                                // ✅ Animate camera to user's location with tilt
                                                cameraPositionState.animate(
                                                    update = CameraUpdateFactory.newCameraPosition(
                                                        CameraPosition(
                                                            LatLng(
                                                                location.latitude,
                                                                location.longitude
                                                            ),
                                                            19f, // Zoom closer
                                                            60f, // Tilt the camera
                                                            0f
                                                        )
                                                    ),
                                                    durationMs = 1000
                                                )

                                                // ✅ Start tracking after camera animation
                                                viewModel.startTracking(context)
                                            } catch (e: SecurityException) {
                                                Log.e(
                                                    "StartTrip",
                                                    "Permission denied: ${e.message}"
                                                )
                                            } catch (e: Exception) {
                                                Log.e(
                                                    "StartTrip",
                                                    "Error getting location: ${e.message}"
                                                )
                                            }
                                        }
                                    } else {
                                        Log.e("StartTrip", "Location is null")
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("StartTrip", "Failed to get location: ${e.message}")
                                }
                        } else {
                            // Request permission if not granted

                        }
                    }else{
                        //logic to close the navigation
                        viewModel.stopNavigation()
                        navController.navigate("home")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = LightOrange),
            ) {
                Text(
                    text = if(!navigationStarted!!) "Start Trip" else "Exit Navigation",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            //btn to recalculate route
            if(isOffRoute == true){

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(16.dp),
                        onClick = {

                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Warning),
                    ) {
                        Text(
                            text = "Recalculate Route",
                            color = WarningText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

            }

        }
    }
        if (showBottomSheet) {
            BottomSheet(
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet = false },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(LightCreme,RoundedCornerShape(10.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        //icons and text for basic things that could happen
                    }
                }
            }
        }
    }

}

@Composable
fun GoogleMapView(
    routeData: FeatureCollection,
    onMapLoaded: () -> Unit,
    fusedLocationProviderClient: FusedLocationProviderClient,
    cameraPositionState: CameraPositionState,
) {

    // Extract polyline points
    val polylinePoints = routeData.features[0].geometry.coordinates[0].map {
        LatLng(it[1], it[0]) // Convert to LatLng
    }

    // Extract waypoints
    val waypoints = routeData.features[0].properties.waypoints

    // Render GoogleMap Composable
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = true),
        uiSettings = MapUiSettings(compassEnabled = true, zoomControlsEnabled = true),
        onMapLoaded = {
            onMapLoaded()
        }
    ) {
        // Add Polyline
        Polyline(
            points = polylinePoints,
            color = Color.Blue,
            width = 10f
        )

        // Add Waypoints as Markers
        waypoints.forEach { waypoint ->
            val position = LatLng(waypoint.location[1], waypoint.location[0])
            Marker(
                state = MarkerState(position = position),
                title = "Waypoint ${waypoint.original_index}",
                snippet = "Lat: ${position.latitude}, Lng: ${position.longitude}"
            )
        }
    }
}









fun calculateDistance(start: LatLng, end: LatLng): Double {
    val earthRadius = 6371e3 // Earth's radius in meters

    // Convert latitude and longitude from degrees to radians
    val startLatRad = Math.toRadians(start.latitude)
    val endLatRad = Math.toRadians(end.latitude)
    val deltaLat = Math.toRadians(end.latitude - start.latitude)
    val deltaLon = Math.toRadians(end.longitude - start.longitude)

    // Haversine formula
    val a = sin(deltaLat / 2).pow(2) +
            cos(startLatRad) * cos(endLatRad) *
            sin(deltaLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return earthRadius * c // Result in meters
}

