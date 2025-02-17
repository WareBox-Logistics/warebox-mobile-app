package com.example.lp_logistics.presentation.screens.home

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.lp_logistics.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomAppBar
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lp_logistics.presentation.components.BottomBar
import com.example.lp_logistics.presentation.components.TopBar
import com.example.lp_logistics.presentation.components.TripsCard
import com.example.lp_logistics.presentation.theme.LightOrange


@Composable
fun HomeScreen( navController: NavController, context: Context) {

    Scaffold (
        topBar = {
            TopBar(title = "Daily Route", color = false)
        },
        content = { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().background(color = LightOrange)){

                LazyColumn (contentPadding = innerPadding, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                    .padding(15.dp)) {
                   item{
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
            BottomBar(navController)
        }
    )
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(navController = NavController(LocalContext.current), context = LocalContext.current)
}