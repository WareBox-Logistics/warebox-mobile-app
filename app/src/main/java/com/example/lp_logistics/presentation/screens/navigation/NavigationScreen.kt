package com.example.lp_logistics.presentation.screens.navigation
//
//import android.annotation.SuppressLint
//
//import android.view.View
//import android.view.WindowManager
//import android.widget.FrameLayout
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.AddLocationAlt
//import androidx.compose.material.icons.filled.CarCrash
//import androidx.compose.material.icons.filled.Warning
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.material3.rememberModalBottomSheetState
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.ImeAction
//import androidx.compose.ui.text.input.TextFieldValue
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.fragment.app.FragmentActivity
//import androidx.navigation.NavController
//import com.example.lp_logistics.presentation.components.BottomSheet
//import com.example.lp_logistics.presentation.theme.LightBlue
//import com.example.lp_logistics.presentation.theme.LightOrange
//import com.example.lp_logistics.presentation.theme.Orange
//import com.example.lp_logistics.presentation.theme.Report
//import com.example.lp_logistics.presentation.theme.Warning
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.libraries.navigation.NavigationView
//import com.google.android.libraries.navigation.Navigator
//import com.google.android.libraries.navigation.SupportNavigationFragment
//
//@SuppressLint("MissingPermission")
//@Composable
//fun NavigationScreen(activity: FragmentActivity, destination: LatLng, navController: NavController) {
//    val navigator = remember { mutableStateOf<Navigator?>(null) }
//    val navigationView = remember { mutableStateOf<NavigationView?>(null) }
//    val navFragment = remember { mutableStateOf<SupportNavigationFragment?>(null) }
//    val navFragmentContainerId = remember { mutableStateOf<Int?>(null) }
//
//
//    activity.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//
//
//    LaunchedEffect(navigator.value) {
//        val containerId = navFragmentContainerId.value
//        if (navigator.value == null && containerId != null) {
//            initializeNavigationApi(activity, navigationView.value, containerId,
//                onFragmentReady = { nav, fragment ->
//                    navigator.value = nav
//                    navFragment.value = fragment
//                    registerNavigationListeners(nav, activity)
//                    nav.setTaskRemovedBehavior(Navigator.TaskRemovedBehavior.QUIT_SERVICE)
//
//                    navigationView.value?.getMapAsync { map ->
//                        map.followMyLocation(GoogleMap.CameraPerspective.TILTED)
//
//                    }
//                    navigateToLocation(nav, destination, activity)
//                },
//
//            )
//        }
//    }
//
//
//    LaunchedEffect(navFragment.value) {
//        navFragment.value?.let { fragment ->
//            fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
//                if (viewLifecycleOwner != null) {
//                    customizeNavigationUI(fragment, navigator.value!!, navController)
//                }
//            }
//        }
//    }
//
//    DisposableEffect(Unit) {
//        onDispose {
//            navigator.value?.apply {
//                stopGuidance()
//                clearDestinations()
//                removeArrivalListener { }
//                removeRouteChangedListener { }
//                simulator?.unsetUserLocation()
//                cleanup()
//            }
//            navigationView.value?.apply {
//                onPause()
//                onStop()
//                onDestroy()
//            }
//            navFragment.value?.let { fragment ->
//                activity.supportFragmentManager.beginTransaction().remove(fragment).commit()
//            }
//        }
//    }
//
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        Box(modifier = Modifier
//            .fillMaxWidth()
//            .height(40.dp)
//            .background(Orange))
//
//        Box(modifier = Modifier.weight(1f)) {
//            AndroidView(
//                modifier = Modifier.fillMaxSize(),
//                factory = { context ->
//                    FrameLayout(context).apply {
//                        val generatedId = View.generateViewId()
//                        id = generatedId
//                        navFragmentContainerId.value = generatedId
//
//                        navigationView.value = NavigationView(context).apply {
//                            onCreate(null)
//                            onStart()
//                            onResume()
//                        }
//
//                        addView(navigationView.value)
//                    }
//                }
//            )
//        }
//        Spacer(modifier = Modifier.height(10.dp))
//    }
//}
//
//@Composable
//fun MarkerHandlerWarning(navFragment: SupportNavigationFragment) {
//    var location by remember { mutableStateOf<LatLng?>(null) }
//    var issue by remember { mutableStateOf("warning") }
//
//    if (location != null) {
//        AddMarker(coords = location!!, issue = issue)
//    }
//
//    CircularButtonWithImage(
//        issue = "warning",
//        navFragment = navFragment,
//        onLocationReceived = { loc, iss ->
//            location = loc
//            issue = iss
//        }
//    )
//}
//
//@Composable
//fun MarkerHandlerReport(navFragment: SupportNavigationFragment) {
//    var location by remember { mutableStateOf<LatLng?>(null) }
//    var issue by remember { mutableStateOf("report") }
//
//    if (location != null) {
//        AddMarker(coords = location!!, issue = issue)
//    }
//
//    CircularButtonWithImage(
//        issue = "report",
//        navFragment = navFragment,
//        onLocationReceived = { loc, iss ->
//            location = loc
//            issue = iss
//        }
//    )
//}
//
//
//@Composable
//fun CircularButtonWithImage(
//    issue: String,
//    navFragment: SupportNavigationFragment,
//    onLocationReceived: (LatLng, String) -> Unit
//) {
//    Button(
//        onClick = {
//            navFragment.activity?.let { activity ->
//                getCurrentLocation(activity) { location ->
//                    if (location != null) {
//                        onLocationReceived(location, issue)
//                    } else {
//                        println("❌ Failed to get location!")
//                    }
//                }
//            }
//        },
//        shape = CircleShape,
//        colors = if (issue === "warning") ButtonDefaults.buttonColors(containerColor = Warning) else ButtonDefaults.buttonColors(containerColor = Report),
//        contentPadding = PaddingValues(0.dp),
//        modifier = Modifier
//            .clip(CircleShape)
//            .background(LightBlue)
//            .size(72.dp)
//    ) {
//        Icon(
//            if (issue === "warning") Icons.Default.Warning else Icons.Default.CarCrash,
//            tint = Color.White,
//            contentDescription = "btn navigation",
//            modifier = Modifier.size(50.dp)
//        )
//    }
//}
//
//
////check why it doesnt work once i get back to navigation once i exit it, it just stops everything, i want to also conserve my custom styles
//@Composable
//fun ExitNavigation(
//    navigator: Navigator,
//    navController: NavController
//) {
//    Button(
//        onClick = {
//            navigator.stopGuidance()
//            navigator.clearDestinations()
//            navigator.cleanup()
//            navController.popBackStack()
//        },
//        colors = ButtonDefaults.buttonColors(
//            containerColor = LightOrange
//        ),
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(50.dp)
//    ) {
//        Text(
//            text = "Exit Navigation",
//            color = Color.White,
//            fontSize = 18.sp,
//            fontWeight = FontWeight.SemiBold
//        )
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AddMarker(
//    coords: LatLng,
//    issue: String
//) {
//    var showBottomSheet by remember { mutableStateOf(true) }
//    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
//    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
//
//
//    if (showBottomSheet) {
//        BottomSheet(
//            sheetState = sheetState,
//            onDismissRequest = {
//                showBottomSheet = false
//            }
//        ) {
//            Text(
//                text = "Add a $issue marker",
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold,
//                color = Orange,
//                modifier = Modifier.padding(bottom = 16.dp)
//            )
//
//            Text(
//                text = "Warning, you'll add a marker for dispatch at the following coordinates, latitude: ${coords.latitude}, longitude: ${coords.longitude}",
//                fontSize = 16.sp,
//                color = Color.Black,
//                modifier = Modifier.padding(bottom = 16.dp)
//            )
//
//            //this should be a selector to select a problem
//            TextField(
//                value = textFieldValue,
//                onValueChange = { textFieldValue = it },
//                leadingIcon = {
//                    Icon(
//                        Icons.Default.AddLocationAlt,
//                        contentDescription = "Location icon",
//                        tint = Orange
//                    )
//                },
//                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
//                placeholder = { Text("Description of the issue") },
//                label = { Text("Issue") },
//                singleLine = true,
//                modifier = Modifier.padding(bottom = 16.dp)
//            )
//
//            Button(
//                onClick = {
//                    //todo
//                    //use a viewmodel to send this to the back
//                },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = LightOrange
//                ),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp)
//            ) {
//                Text(
//                    text = "Add Marker",
//                    color = Color.White,
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.SemiBold
//                )
//            }
//        }
//    }
//}
//
//
//
//
//
