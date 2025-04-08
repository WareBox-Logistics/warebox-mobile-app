package com.example.lp_logistics.presentation.screens.warehouse.arrivals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lp_logistics.presentation.components.TopBar
import com.example.lp_logistics.presentation.screens.home.HomeViewModel
import com.example.lp_logistics.presentation.theme.LightCreme
import com.example.lp_logistics.presentation.theme.Orange

@Composable
fun ConfirmArrival(confirmationCode: String, navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val loading by viewModel.loading.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.confirmDeliveryByDriver(context, confirmationCode)
    }

    Scaffold(
        topBar = { TopBar(title = "Delivery Confirmation", color = false) },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(color = LightCreme)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    if (loading) {
                        CircularProgressIndicator(
                            color = Orange,
                        )
                    } else {
                        Icon(
                            Icons.Rounded.TaskAlt,
                            contentDescription = "No deliveries",
                            tint = Color.White,
                            modifier = Modifier.size(80.dp)
                        )

                        Text(
                            text = "Arrival confirmed!",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )

                        navController.navigate("home")
                    }
                }
            }
        }
    )
}