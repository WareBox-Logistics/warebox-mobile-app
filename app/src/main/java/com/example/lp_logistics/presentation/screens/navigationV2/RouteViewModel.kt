package com.example.lp_logistics.presentation.screens.navigationV2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lp_logistics.domain.model.FeatureCollection
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var lastInstructionTime: Long = 0
    private val instructionCooldown = 5000L

    private val _routeData = MutableLiveData<FeatureCollection?>()
    val routeData: LiveData<FeatureCollection?> = _routeData

    private val _currentLocation = MutableLiveData<LatLng?>()
    val currentLocation: LiveData<LatLng?> = _currentLocation

    private val _instruction = MutableLiveData<String?>()
    val instruction: LiveData<String?> = _instruction

    private val _navigationStarted = MutableLiveData(false)
    val navigationStarted: LiveData<Boolean> = _navigationStarted

    private val _isOffRoute = MutableLiveData(false)
    val isOffRoute: LiveData<Boolean> = _isOffRoute

    private var locationCallback: LocationCallback? = null



    // Load polyline and direction from backend
    fun loadRoute() {
        viewModelScope.launch {
            // Example of loading from an API (replace with actual request)
            val jsonResponse = withContext(Dispatchers.IO) {
                // Mocking an API call or use a real endpoint
                val response = fetchRouteFromApi()
                parseRoute(response)
            }
            _routeData.value = jsonResponse
        }
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

                        checkInstructions(newLocation)
                        if (isOffRoute(newLocation, 50.0)) {
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

    // Parse and load route data from API
    private fun parseRoute(response: String): FeatureCollection {
        return Gson().fromJson(response, FeatureCollection::class.java)
    }

    // Check for nearby turns and give instructions
    private fun checkInstructions(currentLocation: LatLng) {
        routeData.value?.let { route ->
            val steps = route.features[0].properties.legs[0].steps
            for (step in steps) {
                val startPoint = route.features[0].geometry.coordinates[0][step.from_index]
                val startPointLatLng = LatLng(startPoint[1], startPoint[0])

                val distanceToTurn = calculateDistance(currentLocation, startPointLatLng)
                if (distanceToTurn < 50) {//leave 30
                    val currentTime = System.currentTimeMillis()

                    // Check if enough time has passed since the last instruction
                    if (currentTime - lastInstructionTime > instructionCooldown) {
                        _instruction.postValue(step.instruction.text)
                        speakInstruction(step.instruction.text)
                        lastInstructionTime = currentTime // Update last instruction time
                    }
                }
            }
        }
    }

    private fun isOffRoute(currentLocation: LatLng, threshold: Double): Boolean {
        val coordinates = routeData.value?.features?.get(0)?.geometry?.coordinates?.get(0) ?: return false

        val closestPointAndDistance = coordinates.map {
            val point = LatLng(it[1], it[0])
            point to calculateDistance(currentLocation, point)
        }.minByOrNull { it.second }

        return closestPointAndDistance?.second?.let { it > threshold } ?: false
    }




    private var tts: TextToSpeech? = null

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


    override fun onCleared() {
        tts?.stop()
        tts?.shutdown()
        super.onCleared()
    }

    // Stop navigation and clean up resources
    fun stopNavigation() {
        // Stop location updates using the stored callback
        locationCallback?.let {
            fusedLocationProviderClient.removeLocationUpdates(it)
            locationCallback = null
        }

        // Reset state variables
        _navigationStarted.postValue(false)
        _currentLocation.postValue(null)
        _instruction.postValue(null)
        lastInstructionTime = 0

        // Stop and release TTS
        tts?.stop()
        tts?.shutdown()
        tts = null

        println("Navigation stopped and resources cleaned up.")
    }



    fun recalculateRoute(currentLocation: LatLng, destination: LatLng) {
        viewModelScope.launch {
            val newRouteResponse = fetchRouteFromApi()//pass currentLocation and destination here
            _routeData.postValue(parseRoute(newRouteResponse))
            speakInstruction("Route recalculated. Please follow the new directions.")
        }
    }





    fun fetchRouteFromApi(): String {
        return """
   {
     "features": [
       {
         "type": "Feature",
         "properties": {
           "mode": "truck",
           "waypoints": [
             {
               "location": [
                 -117.06964,
                 32.379858
               ],
               "original_index": 0
             },
             {
               "location": [
                 -117.063325,
                 32.382183
               ],
               "original_index": 1
             }
           ],
           "units": "metric",
           "distance": 927,
           "distance_units": "meters",
           "time": 97.818,
           "legs": [
             {
               "distance": 927,
               "time": 97.818,
               "steps": [
                 {
                   "from_index": 0,
                   "to_index": 3,
                   "distance": 313,
                   "time": 32.29,
                   "instruction": {
                     "text": "Drive north on Avenida General Antonio León."
                   }
                 },
                 {
                   "from_index": 3,
                   "to_index": 13,
                   "distance": 548,
                   "time": 58.537,
                   "instruction": {
                     "text": "Turn right onto Calle Mártires De Tacubaya."
                   }
                 },
                 {
                   "from_index": 13,
                   "to_index": 16,
                   "distance": 65,
                   "time": 6.99,
                   "instruction": {
                     "text": "Turn right onto Calle Alfredo Serratos."
                   }
                 },
                 {
                   "from_index": 16,
                   "to_index": 16,
                   "distance": 0,
                   "time": 0,
                   "instruction": {
                     "text": "You have arrived at your destination."
                   }
                 }
               ]
             }
           ]
         },
         "geometry": {
           "type": "MultiLineString",
           "coordinates": [
             [
               [
                 -117.069649,
                 32.379859
               ],
               [
                 -117.069649,
                 32.380477
               ],
               [
                 -117.069647,
                 32.381658
               ],
               [
                 -117.069626,
                 32.382679
               ],
               [
                 -117.069053,
                 32.382669
               ],
               [
                 -117.068528,
                 32.382659
               ],
               [
                 -117.067897,
                 32.38266
               ],
               [
                 -117.06731,
                 32.38266
               ],
               [
                 -117.06673,
                 32.382656
               ],
               [
                 -117.066136,
                 32.382647
               ],
               [
                 -117.065552,
                 32.382651
               ],
               [
                 -117.064973,
                 32.382637
               ],
               [
                 -117.064422,
                 32.382628
               ],
               [
                 -117.063792,
                 32.382616
               ],
               [
                 -117.063552,
                 32.382369
               ],
               [
                 -117.063358,
                 32.382184
               ],
               [
                 -117.063341,
                 32.38217
               ]
             ]
           ]
         }
       }
     ],
     "properties": {
       "mode": "truck",
       "waypoints": [
         {
           "lat": 32.37985896776382,
           "lon": -117.06964090083619
         },
         {
           "lat": 32.38218305720365,
           "lon": -117.063325921363
         }
       ],
       "units": "metric"
     },
     "type": "FeatureCollection"
   }
        """.trimIndent()
    }

}
