package com.example.lp_logistics.presentation.screens.navigationV2

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.rounded.CarCrash
import androidx.compose.material.icons.rounded.Dangerous
import androidx.compose.material.icons.rounded.LocalPolice
import androidx.compose.material.icons.rounded.MinorCrash
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices.*
import com.google.android.gms.maps.model.LatLng

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.lp_logistics.domain.model.Coordinate
import com.example.lp_logistics.domain.model.PolylinePath
import com.example.lp_logistics.domain.model.TollBooth
import com.example.lp_logistics.presentation.components.BottomSheet
import com.example.lp_logistics.presentation.theme.LightBlue
import com.example.lp_logistics.presentation.components.QR.generateQRCodeBitmap
import com.example.lp_logistics.presentation.theme.LightOrange
import com.example.lp_logistics.presentation.theme.Orange
import com.example.lp_logistics.presentation.theme.Report
import com.example.lp_logistics.presentation.theme.Warning
import com.example.lp_logistics.presentation.theme.WarningText
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavHostController,
    routeJson: String,
    deliveryID: String,
    deliveryType: String,
    viewModel: RouteViewModel = hiltViewModel(),
) {
    val deliveryId = viewModel.deliveryId
    val truckId = viewModel.truckId

    // Observe LiveData from ViewModel
    val polylinePaths by viewModel.polylinePaths.observeAsState()
    val routeDirections by viewModel.routeDirections.observeAsState()
    val currentInstruction by viewModel.instruction.observeAsState()
    val navigationStarted by viewModel.navigationStarted.observeAsState()
    val currentLocation by viewModel.currentLocation.observeAsState()
    val isOffRoute by viewModel.isOffRoute.observeAsState()
    val origin by viewModel.origin.observeAsState()
    val destination by viewModel.destination.observeAsState()
    val progress by viewModel.progress.observeAsState(0f)

    // UI State
    var isLoading by remember { mutableStateOf(true) }
    var mapLoaded by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    var showBottomSheetQR by remember { mutableStateOf(false) }
    val sheetStateQR = rememberModalBottomSheetState()

    Log.d("MapScreen", "polylinePath: $polylinePaths")
    // Camera Position State
    val cameraPositionState = rememberCameraPositionState {
        polylinePaths?.firstOrNull()?.coordinates?.takeIf { it.isNotEmpty() }?.firstOrNull()?.let { firstCoordinate ->
            position = CameraPosition.fromLatLngZoom(
                LatLng(firstCoordinate.lat, firstCoordinate.lng),
                15f
            )
        }
    }

    // Coroutine Scope
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val fusedLocationProviderClient = getFusedLocationProviderClient(context)

    // Load route when the screen is launched
    LaunchedEffect(Unit) {
        viewModel.loadRoute(routeJson)
    }

    // Update camera position when polylinePath is loaded
    LaunchedEffect(polylinePaths) {
        println("Origin: ${viewModel.origin.value}")
        println("Destination: ${viewModel.destination.value}")
        polylinePaths?.firstOrNull()?.coordinates?.firstOrNull()?.let { firstCoordinate ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(firstCoordinate.lat, firstCoordinate.lng),
                15f
            )
            isLoading = false
        }
    }
    Log.d("MapScreen", "routeDirections: $routeDirections")

    LaunchedEffect(currentLocation) {
        if (currentLocation != null) {
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
                    viewModel.updateProgress(location)
                }
            }
        } else {
            Log.w("MapScreen", "currentLocation is null, skipping camera update.")
        }
    }


    // Main UI
    Column(modifier = Modifier.fillMaxSize()) {
        // Header for navigation instructions
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(Orange)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
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

        // Map and Buttons
        Box(modifier = Modifier.fillMaxSize()) {
            polylinePaths?.let { paths ->
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier
                        .align(Alignment.Center)
                        .background(Orange))
                } else {
                    GoogleMapView(
                        polylinePaths = paths,
                        origin = origin,
                        destination = destination,
                        tollBooths = routeDirections?.tollBooths ?: emptyList(),
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
                        .align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Spacer(Modifier.weight(1f))
                        FloatingActionButton(
                            onClick = { showBottomSheet = true },
                            modifier = Modifier.padding(16.dp),
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

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Spacer(Modifier.weight(1f))
                        FloatingActionButton(
                            onClick = {

                                if(deliveryType == "warehouse_to_location"){
                                    navController.navigate("qr-scanner?driver=${true}")
                                }else{
                                showBottomSheetQR = true
                                    println("THis isnthe type: $deliveryType")
                                }
                                      },
                            modifier = Modifier.padding(16.dp),
                            containerColor = LightBlue,
                            contentColor = Color.White,
                            shape = RoundedCornerShape(50.dp)
                        ) {
                            Icon(
                                Icons.Rounded.QrCode,
                                contentDescription = "QR for delivery",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .padding(horizontal = 16.dp),
                    )

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(16.dp),
                        onClick = {
                            if (navigationStarted == false) {//its not activating the navigation
                                println("Starting navigation")
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    fusedLocationProviderClient.lastLocation
                                        .addOnSuccessListener { location ->
                                            if (location != null) {
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
                                                    viewModel.startTracking(context)
                                                }
                                            }
                                        }
                                }
                            } else {
                                println("Stopping navigation")
                                viewModel.stopNavigation()
                                navController.navigate("home")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LightOrange),
                    ) {
                        Text(
                            text = if (navigationStarted == true) "Stop Trip" else "Start Trip", // cahnges ere
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Recalculate Route Button
                    if (isOffRoute == true) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(16.dp),
                            onClick = {
                                // TODO: Implement recalculate route logic
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

        // Bottom Sheet
        if (showBottomSheet) {
            BottomSheet(
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet = false },
            ) {
                Text(
                    text = "Quick Notification Action",
                    color = Orange,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        top = 20.dp,
                        bottom = 5.dp
                    )

                )
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(16.dp),
                            onClick = {
                                viewModel.createReport(latitude = currentLocation!!.latitude.toString(), longitude = currentLocation!!.longitude.toString(), problem = 2, issue = true, description = "Police Checkpoint")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Warning),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.LocalPolice,
                                    contentDescription = "Warning",
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)

                                )

                                Spacer(modifier=Modifier.weight(1f))

                                Text(
                                    text = "Police Checkpoint",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(16.dp),
                            onClick = {
                                viewModel.createReport(latitude = currentLocation!!.latitude.toString(), longitude = currentLocation!!.longitude.toString(), problem = 3, issue = true, description = "Vehicle Crash Up ahead")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Orange),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.MinorCrash,
                                    contentDescription = "crash",
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)

                                )
                                Spacer(modifier=Modifier.weight(1f))

                                Text(
                                    text = "Vehicle Crash Up ahead",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(16.dp),
                            onClick = {
                                viewModel.createReport(latitude = currentLocation!!.latitude.toString(), longitude = currentLocation!!.longitude.toString(), problem = 5, issue = false, description = "Truck breakdown")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Orange),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.Dangerous,
                                    contentDescription = "crash",
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)

                                )
                                Spacer(modifier=Modifier.weight(1f))

                                Text(
                                    text = "Truck breakdown",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(16.dp),
                            onClick = {
                                viewModel.createReport(latitude = currentLocation!!.latitude.toString(), longitude = currentLocation!!.longitude.toString(), problem = 4, issue = false, description = "I have crashed")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Report),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.CarCrash,
                                    contentDescription = "crash",
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)

                                )
                                Spacer(modifier=Modifier.weight(1f))

                                Text(
                                    text = "I have crashed",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

        if (showBottomSheetQR) {
            BottomSheet(
                sheetState = sheetStateQR,
                onDismissRequest = { showBottomSheetQR = false },
            ) {
                Spacer(modifier = Modifier.fillMaxWidth().height(20.dp))
                val qrCodeBitmap = generateQRCodeBitmap(deliveryID)
                Text(
                    text = "Delivery QR",
                    color = Orange,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        top = 20.dp,
                        bottom = 20.dp
                    )
                )

                Image(
                    bitmap = qrCodeBitmap.asImageBitmap(),
                    contentDescription = "QR Code for the delivery",
                    modifier = Modifier.size(200.dp)
                )
            }
        }
    }
}

@Composable
fun GoogleMapView(
    polylinePaths: List<PolylinePath>,
    origin: Coordinate?,
    destination: Coordinate?,
    tollBooths: List<TollBooth>,
    onMapLoaded: () -> Unit,
    fusedLocationProviderClient: FusedLocationProviderClient,
    cameraPositionState: CameraPositionState,
) {
    // Render GoogleMap
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = true),
        uiSettings = MapUiSettings(compassEnabled = true, zoomControlsEnabled = true),
        onMapLoaded = { onMapLoaded() }
    ) {
        // Add Polylines
        polylinePaths.forEach { polylinePath ->
            val polylinePoints = polylinePath.coordinates.map {
                LatLng(it.lat, it.lng)
            }
            Polyline(
                points = polylinePoints,
                color = Color.Blue,
                width = 10f
            )
        }

        tollBooths.forEach { tollBooth ->
            Marker(
                state = MarkerState(position = LatLng(tollBooth.coordinate.lat, tollBooth.coordinate.lng)),
                title = "Toll Booth",
                snippet = "Cost: ${tollBooth.cost} pesos",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            )
        }

        Marker(
            state = MarkerState(position = LatLng(origin!!.lat, origin.lng)),
            title = "Origin",
            snippet = "Origin",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
        )

        Marker(
            state = MarkerState(position = LatLng(destination!!.lat, destination.lng)),
            title = "Destination",
            snippet = "Destination",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)
        )//skibidi
    }
}

