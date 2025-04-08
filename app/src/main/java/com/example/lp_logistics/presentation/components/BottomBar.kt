package com.example.lp_logistics.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lp_logistics.R
import com.example.lp_logistics.presentation.theme.Orange

@Composable
fun BottomBar(navController: NavController, isWarehouse: Boolean = false) {
    BottomAppBar(modifier = Modifier.background(color = Color.White)){
        Row {
            if (!isWarehouse) {
                Column(verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                        .weight(1f)
                        .clickable {
                            navController.navigate("vehicle")
                        }) {
                    Icon(
                        painter = painterResource(R.drawable.delivery_truck),
                        tint = Orange,
                        contentDescription = "Semi-Truck",
                        modifier = Modifier.size(30.dp)
                    )

                    Text(
                        text = if (!isWarehouse) "Semi-Truck" else "Arrivals",
                        color = Orange,
                        fontSize = 12.sp

                    )
                }
            }

            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        if (!isWarehouse) navController.navigate("home") else navController.navigate(
                            "pallets"
                        )
                    }) {
                if (isWarehouse) {
                    Icon(
                        painter = painterResource(R.drawable.pallet),
                        tint = Orange,
                        contentDescription = "pallet",
                        modifier = Modifier.size(30.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.routes),
                        tint = Orange,
                        contentDescription = "deliveries",
                        modifier = Modifier.size(30.dp)
                    )
                }

                Text(
                    text = if (!isWarehouse) "Deliveries" else "Pallets",
                    color = Orange,
                    fontSize = 12.sp

                )
            }
                Column(verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if(isWarehouse){
                                navController.navigate("profile/${isWarehouse}")
                            }else {
                                navController.navigate("profile")
                            }
                        }) {
                    Icon(
                        painter = painterResource(R.drawable.person),
                        tint = Orange,
                        contentDescription = "Profile",
                        modifier = Modifier.size(30.dp)
                    )

                    Text(
                        text = "Profile",
                        color = Orange,
                        fontSize = 12.sp

                    )
                }

        }
    }

}

@Preview
@Composable
private fun PreviewBottomBar() {
    BottomBar(navController = NavController(LocalContext.current), isWarehouse = true)
}