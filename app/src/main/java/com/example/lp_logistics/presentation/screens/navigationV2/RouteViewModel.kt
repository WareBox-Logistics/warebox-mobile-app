package com.example.lp_logistics.presentation.screens.navigationV2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lp_logistics.data.local.UserManager
import com.example.lp_logistics.data.repository.DeliveryRepository
import com.example.lp_logistics.domain.model.Coordinate
import com.example.lp_logistics.domain.model.PolylinePath
import com.example.lp_logistics.domain.model.RouteDirection
import com.example.lp_logistics.domain.model.TollBooth
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    @ApplicationContext private val context: Context,
    private val repository: DeliveryRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val deliveryId = savedStateHandle.get<String>("deliveryId") ?: ""
    val truckId = savedStateHandle.get<String>("truckId") ?: ""

    private val _polylinePaths = MutableLiveData<List<PolylinePath>>()
    val polylinePaths: LiveData<List<PolylinePath>> = _polylinePaths

    private val _routeDirections = MutableLiveData<RouteDirection>()
    val routeDirections: LiveData<RouteDirection> = _routeDirections

    // LiveData for the current location
    private val _currentLocation = MutableLiveData<LatLng?>()
    val currentLocation: LiveData<LatLng?> get() = _currentLocation

    // LiveData for navigation instructions
    private val _instruction = MutableLiveData<String?>()
    val instruction: LiveData<String?> get() = _instruction

    // LiveData for navigation state
    private val _navigationStarted = MutableLiveData(false)
    val navigationStarted: LiveData<Boolean> = _navigationStarted

    // LiveData for off-route state
    private val _isOffRoute = MutableLiveData<Boolean>()
    val isOffRoute: LiveData<Boolean> get() = _isOffRoute

    // Location tracking
    private var locationCallback: LocationCallback? = null

    // Text-to-speech
    private var tts: TextToSpeech? = null
    private var lastInstructionTime = 0L
    private val instructionCooldown = 10000L // 10 seconds cooldown

    private val _origin = MutableLiveData<Coordinate>()
    val origin: LiveData<Coordinate> = _origin

    private val _destination = MutableLiveData<Coordinate>()
    val destination: LiveData<Coordinate> = _destination

    private val _progress = MutableLiveData(0f) // Initialize with 0
    val progress: LiveData<Float> = _progress

    private var driverIDViewModel: Int = 0

    // Load polyline and direction from backend
    fun loadRoute(routeJson: String) {
        viewModelScope.launch {
            println("here: $routeJson")
            driverIDViewModel = UserManager.getUser(context)!!.id
            val parsedData = parseRoute(routeJson)
            _polylinePaths.value = parsedData.first
            _routeDirections.value = parsedData.second
            _progress.value = 0f
            _origin.value = parsedData.second.points.firstOrNull()
            _destination.value = parsedData.second.points.lastOrNull()
        }
    }

    fun updateProgress(currentLocation: LatLng) {
        Log.d("PROGRESS_DEBUG", "==== New Progress Calculation ====")
        Log.d("PROGRESS_DEBUG", "Current location: ${currentLocation.latitude},${currentLocation.longitude}")

        routeDirections.value?.let { route ->
            // Log route metadata

            if (route.totalDistance <= 0.0) {
                Log.e("PROGRESS", "⚠️ Invalid totalDistance: ${route.totalDistance}")
                _progress.value = 0f
                return@let
            }

            Log.d("PROGRESS_DEBUG", "Route points count: ${route.points.size}")
            Log.d("PROGRESS_DEBUG", "Total distance: ${route.totalDistance}m")

            if (route.points.isEmpty()) {
                Log.e("PROGRESS_DEBUG", "Empty route points!")
                _progress.value = 0f
                return@let
            }

            if (route.totalDistance <= 0.0) {
                Log.e("PROGRESS_DEBUG", "Invalid total distance: ${route.totalDistance}")
                _progress.value = 0f
                return@let
            }

            // Log all route points with their remaining distances
            route.points.forEachIndexed { index, point ->
                Log.d("PROGRESS_DEBUG",
                    "Point $index: (${point.lat},${point.lng}) | Remaining: ${point.remainingDistance}m")
            }

            // Find closest point
            val closestPoint = route.points.minByOrNull { point ->
                calculateDistance(currentLocation, LatLng(point.lat, point.lng)).also { dist ->
                    Log.d("PROGRESS_DEBUG", "Distance to point (${point.lat},${point.lng}): ${dist}m")
                }
            }

            if (closestPoint == null) {
                Log.e("PROGRESS_DEBUG", "No closest point found!")
                _progress.value = 0f
                return@let
            }

            Log.d("PROGRESS_DEBUG", "Closest point: (${closestPoint.lat},${closestPoint.lng})")
            Log.d("PROGRESS_DEBUG", "Remaining distance at closest point: ${closestPoint.remainingDistance}m")

            val remainingDistance = closestPoint.remainingDistance ?: run {
                Log.e("PROGRESS_DEBUG", "Missing remaining distance at closest point!")
                _progress.value = 0f
                return@let
            }

            // Calculate progress
            val rawProgress = (route.totalDistance - remainingDistance) / route.totalDistance
            val clampedProgress = rawProgress.coerceIn(0.0, 1.0)

            Log.d("PROGRESS_DEBUG", "Raw progress: $rawProgress")
            Log.d("PROGRESS_DEBUG", "Clamped progress: $clampedProgress")

            _progress.value = clampedProgress.toFloat()
        } ?: run {
            Log.e("PROGRESS_DEBUG", "Route directions are null!")
        }
    }

    private fun calculateTraveledDistance(currentLocation: LatLng, points: List<Coordinate>): Double {
        var traveledDistance = 0.0
        var lastPoint = LatLng(points.first().lat, points.first().lng)

        for (i in 1 until points.size) {
            val startPoint = lastPoint
            val endPoint = LatLng(points[i].lat, points[i].lng)
            traveledDistance += calculateDistance(startPoint, endPoint)
            lastPoint = endPoint

            // Check if the current location is close to the end point
            if (calculateDistance(currentLocation, endPoint) < 50) {
                break
            }
        }

        // Log the traveled distance for debugging
        println("Traveled distance: $traveledDistance")
        return traveledDistance
    }

    private fun parseRoute(response: String): Pair<List<PolylinePath>, RouteDirection> {
        Log.d("API_RAW_JSON", "Full API Response: $response")
        val json = JSONObject(response)

        // Parse PolylinePath (keep your existing logic exactly as is)
        val polylinePathArray = json.getJSONArray("PolylinePath")
        val polylinePaths = mutableListOf<PolylinePath>()

        if (polylinePathArray.length() == 1) {
            val segmentArray = polylinePathArray.getJSONArray(0)
            val coordinates = mutableListOf<Coordinate>()
            for (j in 0 until segmentArray.length()) {
                val coordinateJson = segmentArray.getJSONObject(j)
                coordinates.add(Coordinate(
                    lat = coordinateJson.getDouble("lat"),
                    lng = coordinateJson.getDouble("lng")
                ))
            }
            polylinePaths.add(PolylinePath(coordinates))
        } else {
            for (i in 0 until polylinePathArray.length()) {
                val segmentArray = polylinePathArray.getJSONArray(i)
                val coordinates = mutableListOf<Coordinate>()
                for (j in 0 until segmentArray.length()) {
                    val coordinateJson = segmentArray.getJSONObject(j)
                    coordinates.add(Coordinate(
                        lat = coordinateJson.getDouble("lat"),
                        lng = coordinateJson.getDouble("lng")
                    ))
                }
                polylinePaths.add(PolylinePath(coordinates))
            }
        }

        // Parse RouteDirections with remaining distance support
        val routeDirectionsArray = json.getJSONArray("RouteDirections")
        val points = mutableListOf<Coordinate>()
        val instructions = mutableListOf<String>()
        val tollBooths = mutableListOf<TollBooth>()
        var maxDistance = 0.0
        var lastPoint: Coordinate? = null
        var isFirstPoint = true

        for (i in 0 until routeDirectionsArray.length()) {
            val directionJson = routeDirectionsArray.getJSONObject(i)
            val pointJson = directionJson.getJSONObject("point")
            val lat = pointJson.getDouble("lat")
            val lng = pointJson.getDouble("lng")
            var remainingDistance = directionJson.getDouble("long_m")

            // Find max distance (skip first point if it's 0)
            if (remainingDistance > maxDistance && (!isFirstPoint || remainingDistance != 0.0)) {
                maxDistance = remainingDistance
            }

            val currentPoint = Coordinate(
                lat = lat,
                lng = lng,
                remainingDistance = if (isFirstPoint && remainingDistance == 0.0) {
                    // Temporary placeholder - we'll fix this after finding maxDistance
                    -1.0
                } else {
                    remainingDistance
                }
            )

            // Deduplicate points
            if (currentPoint.lat == lastPoint?.lat && currentPoint.lng == lastPoint.lng) {
                continue
            }

            points.add(currentPoint)
            lastPoint = currentPoint
            isFirstPoint = false

            instructions.add(directionJson.getString("direccion").replace("+", " "))

            val costoCaseta = directionJson.getInt("costo_caseta")
            if (costoCaseta > 0) {
                tollBooths.add(TollBooth(currentPoint, costoCaseta))
            }
        }

        // Fix first point's remaining distance if it was 0
        if (points.isNotEmpty() && points[0].remainingDistance == -1.0) {
            points[0] = points[0].copy(remainingDistance = maxDistance)
        }

        Log.d("ROUTE_PARSE", """
        |Parsed Route Summary:
        |Total distance: $maxDistance meters
        |Points count: ${points.size}
        |First point remaining: ${points.firstOrNull()?.remainingDistance}
        |Last point remaining: ${points.lastOrNull()?.remainingDistance}
    """.trimMargin())

        return Pair(
            polylinePaths,
            RouteDirection(
                points = points,
                instructions = instructions,
                totalDistance = maxDistance,
                tollBooths = tollBooths
            )
        )
    }

    // Start GPS tracking
    @SuppressLint("MissingPermission")
    fun startTracking(context: Context) {
        _navigationStarted.postValue(true)

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            println("Tracking started")

            // Create a single instance of the callback
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.locations.firstOrNull()?.let {
                        val newLocation = LatLng(it.latitude, it.longitude)
                        _currentLocation.postValue(newLocation)
                        val currentTime = System.currentTimeMillis()

                        updateDriverLocation(newLocation.latitude, newLocation.longitude)


                        checkInstructions(newLocation)
                        if (isOffRoute(newLocation, 100.0)) {
                            _isOffRoute.postValue(true)
                            if (currentTime - lastInstructionTime > instructionCooldown) {
                                _instruction.postValue("Warning: you're wandering off-route.")
                                speakInstruction("Warning: you're wandering off-route.")
                                lastInstructionTime = currentTime
                            }
                        }
                    }
                }
            }

            fusedLocationProviderClient.requestLocationUpdates(
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000).build(),
                locationCallback!!, // Use the stored instance
                Looper.getMainLooper()
            )
        } else {
            println("Location permissions are not granted.")
            Toast.makeText(context, "Location permissions are not granted.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateDriverLocation(lat: Double, lng: Double) {
        viewModelScope.launch {
            try {
                val updateData = mutableMapOf(
                    "lat" to lat,
                    "lng" to lng,
                    "lastUpdated" to ServerValue.TIMESTAMP
                )

                deliveryId?.let { updateData["deliveryId"] = it }
                truckId?.let { updateData["truckId"] = it }

                Firebase.database.reference
                    .child("drivers")
                    .child(driverIDViewModel.toString())
                    .setValue(updateData)
            } catch (e: Exception) {
                Log.e("Firebase", "Failed to update location: ${e.message}")
            }
        }
    }

    // Check for nearby turns and give instructions
    private fun checkInstructions(currentLocation: LatLng) {
        routeDirections.value?.let { route ->
            val points = route.points
            val instructions = route.instructions

            for (i in points.indices) {
                val point = points[i]
                val pointLatLng = LatLng(point.lat, point.lng)

                val distanceToPoint = calculateDistance(currentLocation, pointLatLng)
                Log.d("checkInstructions", "Distance to point $i: $distanceToPoint meters")

                if (distanceToPoint < 100) { // Adjust the distance threshold as needed
                    val currentTime = System.currentTimeMillis()

                    // Check if enough time has passed since the last instruction
                    if (currentTime - lastInstructionTime > instructionCooldown) {
                        Log.d("checkInstructions", "Showing instruction: ${instructions[i]}")
                        _instruction.postValue(instructions[i])
                        speakInstruction(instructions[i])
                        lastInstructionTime = currentTime // Update last instruction time
                    }
                }
            }
        }
    }

    // Check if the user is off-route
    private fun isOffRoute(currentLocation: LatLng, threshold: Double): Boolean {
        val polylinePaths = _polylinePaths.value ?: return false

        // Flatten all coordinates from all polylines into a single list
        val allCoordinates = polylinePaths.flatMap { it.coordinates }

        // Find the closest point and its distance to the current location
        val closestPointAndDistance = allCoordinates.map {
            val point = LatLng(it.lat, it.lng)
            point to calculateDistance(currentLocation, point)
        }.minByOrNull { it.second }

        // Check if the closest point is farther than the threshold
        return closestPointAndDistance?.second?.let { it > threshold } ?: false
    }

    // Text-to-speech logic
    private fun speakInstruction(instruction: String) {
        if (tts == null) {
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.speak(instruction, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        } else {
            tts?.stop()
            tts?.speak(instruction, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    // Stop navigation and clean up resources
    fun stopNavigation() {
        locationCallback?.let {
            fusedLocationProviderClient.removeLocationUpdates(it)
            locationCallback = null
        }


        Firebase.database.reference.child("drivers/$driverIDViewModel").removeValue()

        _navigationStarted.postValue(false)
        _currentLocation.postValue(null)
        _instruction.postValue(null)
        lastInstructionTime = 0

        tts?.stop()
        tts?.shutdown()
        tts = null

        println("Navigation stopped and resources cleaned up.")
    }

    override fun onCleared() {
        tts?.stop()
        tts?.shutdown()
        super.onCleared()
    }

    // Helper function to calculate distance between two LatLng points
    private fun calculateDistance(point1: LatLng, point2: LatLng): Double {
        val results = FloatArray(1)
        Location.distanceBetween(
            point1.latitude, point1.longitude,
            point2.latitude, point2.longitude,
            results
        )
        return results[0].toDouble()
    }

     fun createReport( latitude: String, longitude: String, problem: Int, issue: Boolean, description: String) {
        viewModelScope.launch {
            try {
                val token = UserManager.getToken(context).toString()
                repository.postReportForDispatch(token, latitude, longitude, problem, issue, description, driverIDViewModel)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Report Created", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                println("Error creating report: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to create report", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Recalculate route
//    fun recalculateRoute(currentLocation: LatLng, destination: LatLng) {
//        viewModelScope.launch {
//            val newRouteResponse = fetchRouteFromApi() // Pass currentLocation and destination here
//            val parsedData = parseRoute(newRouteResponse)
//            _polylinePaths.postValue(parsedData.first)
//            _routeDirections.postValue(parsedData.second)
//            speakInstruction("Route recalculated. Please follow the new directions.")
//        }
//    }


    }

