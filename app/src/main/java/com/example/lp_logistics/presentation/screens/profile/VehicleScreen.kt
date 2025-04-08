package com.example.lp_logistics.presentation.screens.profile

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lp_logistics.R
import com.example.lp_logistics.domain.model.Vehicle
import com.example.lp_logistics.presentation.components.BottomBar
import com.example.lp_logistics.presentation.components.TopBar
import com.example.lp_logistics.presentation.screens.home.HomeViewModel
import com.example.lp_logistics.presentation.theme.LightCreme
import com.example.lp_logistics.presentation.theme.LightOrange
import com.example.lp_logistics.presentation.theme.Orange

@Composable
fun VehicleScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val context = navController.context
    val vehicle by viewModel.driverVehicle.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getDriverVehicle(context)
    }

    Scaffold(
        topBar = {
            TopBar(title = "Driver's Vehicle", color = false)
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = LightOrange)
                    .padding(innerPadding)
            ) {
                when {
                    loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.White
                        )
                    }

                    error != null -> {
                        // Show error message
                        Text(
                            text = "Error loading vehicle data: ${error}",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Red
                        )
                    }

                    vehicle != null -> {
                        VehicleDetailsContent(vehicle = vehicle!!)
                    }

                    else -> {
                        Text(
                            text = "No vehicle data available",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        },
        bottomBar = {
            BottomBar(navController)
        }
    )
}

@Composable
private fun VehicleDetailsContent(vehicle: Vehicle) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(680.dp)
            .padding(15.dp)
            .background(
                LightCreme,
                shape = RoundedCornerShape(10.dp)
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Text(
                text = "Semi Truck",
                fontSize = 36.sp,
                fontWeight  = FontWeight.SemiBold,
                color = Orange,
                modifier = Modifier.padding(top = 58.dp, start = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.truck2),
                contentDescription = null,
                modifier = Modifier
                    .height(122.dp)
                    .width(158.dp)
            )
        }
        LazyColumn(
            modifier = Modifier.padding(
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { VehicleDetailItem("Plates", vehicle.plates) }
            item { VehicleDetailItem("Vin", vehicle.vin) }
            item { VehicleDetailItem("Model", vehicle.modell.name) }
            item { VehicleDetailItem("Brand", vehicle.modell.brand.name) }
        }
    }
}

@Composable
private fun VehicleDetailItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(vertical = 20.dp)
    ) {
        Text(
            text = label,
            color = Orange,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(10.dp))
                .padding(16.dp)
        )
    }
}