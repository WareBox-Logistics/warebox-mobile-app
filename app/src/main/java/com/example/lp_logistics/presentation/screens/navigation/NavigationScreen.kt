package com.example.lp_logistics.presentation.screens.navigation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import com.example.lp_logistics.presentation.components.TopBar
import com.example.lp_logistics.presentation.navigation.LocalComponentActivity
import com.example.lp_logistics.presentation.navigation.LocalFragmentActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.navigation.NavigationView
import com.google.android.libraries.navigation.Navigator
import com.google.android.libraries.navigation.SupportNavigationFragment

@SuppressLint("MissingPermission")
@Composable
fun NavigationScreen(activity: FragmentActivity, destination: LatLng) {
    val navigator = remember { mutableStateOf<Navigator?>(null) }
    val navigationView = remember { mutableStateOf<NavigationView?>(null) }
    val navFragment = remember { mutableStateOf<SupportNavigationFragment?>(null) }
    val navFragmentContainerId = remember { mutableStateOf<Int?>(null) }

    activity.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    LaunchedEffect(navigator.value) {
        val containerId = navFragmentContainerId.value
        if (navigator.value == null && containerId != null) {
            initializeNavigationApi(activity, navigationView.value, containerId) { nav, fragment ->
                navigator.value = nav
                navFragment.value = fragment
                registerNavigationListeners(nav, activity)

                nav.setTaskRemovedBehavior(Navigator.TaskRemovedBehavior.QUIT_SERVICE)

                navigationView.value?.getMapAsync { googleMap ->
                    googleMap.followMyLocation(GoogleMap.CameraPerspective.TILTED)
                }

                navigateToLocation(nav, destination, activity)
            }
        }
    }

    LaunchedEffect(navFragment.value) {
        navFragment.value?.let { fragment ->
            fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                if (viewLifecycleOwner != null) {
                    customizeNavigationUI(fragment, navigator.value!!)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            navigator.value?.let {
                it.removeArrivalListener { }
                it.removeRouteChangedListener { }
                it.simulator?.unsetUserLocation()
                it.cleanup()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(title = "Navigating to Destination", color = true)

        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    FrameLayout(context).apply {
                        val generatedId = View.generateViewId()
                        id = generatedId
                        navFragmentContainerId.value = generatedId

                        navigationView.value = NavigationView(context).apply {
                            onCreate(null)
                            onStart()
                            onResume()
                        }

                        addView(navigationView.value)
                    }
                }
            )
        }
    }
}