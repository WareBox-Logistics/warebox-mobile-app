package com.example.lp_logistics.presentation.screens.navigation

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Gravity
import android.widget.FrameLayout
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.lp_logistics.BuildConfig
import com.example.lp_logistics.presentation.components.showToast
import com.example.lp_logistics.presentation.theme.LightBlue
import com.example.lp_logistics.presentation.theme.LightOrange
import com.example.lp_logistics.presentation.theme.Orange
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.navigation.CustomControlPosition
import com.google.android.libraries.navigation.DisplayOptions
import com.google.android.libraries.navigation.NavigationApi
import com.google.android.libraries.navigation.NavigationView
import com.google.android.libraries.navigation.Navigator
import com.google.android.libraries.navigation.SimulationOptions
import com.google.android.libraries.navigation.Waypoint
import com.google.android.libraries.navigation.StylingOptions
import com.google.android.libraries.navigation.SupportNavigationFragment

@SuppressLint("MissingPermission")
fun initializeNavigationApi(activity: FragmentActivity, navigationView: NavigationView?, containerId: Int?, onFragmentReady: (Navigator, SupportNavigationFragment) -> Unit) {
    println("Initializing Navigation API...")

    if (mNavigator != null) {
        println("🚨 Navigator already initialized, skipping re-initialization.")
        return
    }

    println("Activity found: ${activity::class.java.simpleName}")

    NavigationApi.getNavigator(
        activity,
        object : NavigationApi.NavigatorListener {
            @SuppressLint("MissingPermission")
            override fun onNavigatorReady(navigator: Navigator) {
                println("✅ Navigator is ready!")

                mNavigator = navigator
                registerNavigationListeners(navigator, activity)

                val navFragment = SupportNavigationFragment.newInstance()

                if (containerId != null) {
                    println("Attempting to attach navFragment to container ID: $containerId")
                    activity.supportFragmentManager.beginTransaction()
                        .replace(containerId, navFragment)
                        .commitNowAllowingStateLoss()
                    println("✅ navFragment attached to container ID: $containerId")
                } else {
                    println("❌ Invalid container ID!")
                }

                navigationView?.post {
                    println("✅ navigationView exists, forcing redraw and calling getMapAsync...")
                    navigationView.getMapAsync { googleMap ->
                        println("✅ getMapAsync callback fired, setting map to follow user location")
                        googleMap.followMyLocation(GoogleMap.CameraPerspective.TILTED)
                    }
                }

                println("✅ navFragment successfully attached, calling onFragmentReady")
                onFragmentReady(navigator, navFragment)
            }

            override fun onError(@NavigationApi.ErrorCode errorCode: Int) {
                when (errorCode) {
                    NavigationApi.ErrorCode.NOT_AUTHORIZED -> {
                        showToast(activity, "Invalid API Key: Check AndroidManifest.xml")
                        println("Invalid API Key: Check AndroidManifest.xml")
                    }
                    NavigationApi.ErrorCode.TERMS_NOT_ACCEPTED -> {
                        showToast(activity, "User did not accept Navigation Terms.")
                        println("User did not accept Navigation Terms.")
                    }
                    else -> {
                        showToast(activity, "Error loading Navigation API: $errorCode")
                        println("Error loading Navigation API: $errorCode")
                    }
                }
            }
        },
    )
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private var mNavigator: Navigator? = null

 fun registerNavigationListeners(navigator: Navigator, context: Context) {
    val arrivalListener = Navigator.ArrivalListener {
        showToast(context, "User has arrived at the destination!")
        navigator.clearDestinations()
    }
    navigator.addArrivalListener(arrivalListener)

    val routeChangedListener = Navigator.RouteChangedListener {
        showToast(context, "onRouteChanged: the driver's route changed")
    }
    navigator.addRouteChangedListener(routeChangedListener)
}


 fun navigateToLocation(navigator: Navigator?, destination: LatLng, context: Context) {
     val waypoint: Waypoint = Waypoint.builder()
         .setLatLng(destination.latitude, destination.longitude)
         .build()

    val pendingRoute = navigator?.setDestination(waypoint)
     DisplayOptions().showStopSigns(true).showTrafficLights(true)


     pendingRoute?.setOnResultListener { code ->
        when (code) {
            Navigator.RouteStatus.OK -> {
                // Start navigation guidance
                navigator.startGuidance()
            }
            Navigator.RouteStatus.ROUTE_CANCELED -> showToast(context, "Route guidance canceled.")
            Navigator.RouteStatus.NO_ROUTE_FOUND,
            Navigator.RouteStatus.NETWORK_ERROR -> {
                showToast(context, "Error starting guidance: $code")
            }
            else -> showToast(context, "Error starting guidance: $code")
        }
    }


    // Enable voice guidance
    navigator?.setAudioGuidance(Navigator.AudioGuidance.VOICE_ALERTS_AND_GUIDANCE)


     // Simulate route for debugging
    if (BuildConfig.DEBUG) {
        navigator?.simulator?.simulateLocationsAlongExistingRoute(SimulationOptions().speedMultiplier(5f))
    }
}

fun customizeNavigationUI(navFragment: SupportNavigationFragment, navigator: Navigator) {
    if (!navFragment.isAdded) {
        println("❌ navFragment is not attached!")
        return
    }

    navFragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
        if (viewLifecycleOwner != null) {
            println("✅ viewLifecycleOwner is available!")

            val stylingOptions = StylingOptions()
                .primaryDayModeThemeColor(LightOrange.toArgb())
                .primaryNightModeThemeColor(LightBlue.toArgb())
                .secondaryNightModeThemeColor(LightBlue.toArgb())
                .headerLargeManeuverIconColor(Color.White.toArgb())

            navFragment.setStylingOptions(stylingOptions)
            navFragment.setTripProgressBarEnabled(true)
            navFragment.setSpeedometerEnabled(true)
            navFragment.setSpeedLimitIconEnabled(true)
            DisplayOptions().showStopSigns(true).showTrafficLights(true)

            val stopButton = android.widget.Button(navFragment.requireContext()).apply {
                text = "Stop Trip"
                setBackgroundColor(Orange.toArgb())
                textSize = 16f
                setPadding(20, 10, 20, 10)
                setOnClickListener {
                    println("🛑 Stopping navigation...")
                    navigator.stopGuidance()
                }
            }

            val buttonContainer = FrameLayout(navFragment.requireContext()).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.BOTTOM
                }
                addView(stopButton)
            }

            navFragment.setCustomControl(buttonContainer, CustomControlPosition.FOOTER)

            println("✅ Styling options applied successfully!")
        } else {
            println("❌ viewLifecycleOwner is NULL!")
        }
    }

    println("🔹 Finished setting up observer")
}


