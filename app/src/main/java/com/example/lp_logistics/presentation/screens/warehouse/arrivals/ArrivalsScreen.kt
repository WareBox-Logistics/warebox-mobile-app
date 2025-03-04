package com.example.lp_logistics.presentation.screens.warehouse.arrivals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lp_logistics.presentation.components.BottomBar
import com.example.lp_logistics.presentation.components.TopBar
import com.example.lp_logistics.presentation.components.TripsCard
import com.example.lp_logistics.presentation.theme.LightOrange

@Composable
fun ArrivalsScreen(navController: NavController) {

    //should make a launchEffect to get the arrivals if any are available
    Scaffold (
        topBar = {
            TopBar(title = "Your Arrivals", color = false)
        },
        content = { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().background(color = LightOrange)){
                LazyColumn (contentPadding = innerPadding, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                    .padding(15.dp)) {
                    item{
                        //this should render how many arrivals are going to arrive also the ones that are planned already
                        TripsCard(
                            origin = "Test",
                            destination = "Test",
                            date = "January 10th",
                            time = "10:00 AM",
                            image = "1",
                            navController = navController
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                }
            }
        },
        bottomBar = {
            // Bottom navigation
            BottomBar(navController, isWarehouse = true)
        }
    )
}

@Preview
@Composable
private fun ArrivalsScreenPreview() {
    ArrivalsScreen(navController = NavController(LocalContext.current))
}