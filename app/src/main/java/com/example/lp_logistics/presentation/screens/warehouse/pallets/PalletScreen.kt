package com.example.lp_logistics.presentation.screens.warehouse.pallets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lp_logistics.R
import com.example.lp_logistics.presentation.components.BottomBar
import com.example.lp_logistics.presentation.components.IconMenuBtn
import com.example.lp_logistics.presentation.components.TopBar
import com.example.lp_logistics.presentation.theme.LightOrange

@Composable
fun PalletScreen(navController: NavController) {
    Scaffold (
        topBar = {
            TopBar(title = "Pallets", color = false)
        },
        content = { innerPadding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .background(color = LightOrange)){

                LazyColumn (contentPadding = innerPadding, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,modifier = Modifier
                    .padding(15.dp)
                    .align(Alignment.Center)) {
                    item{

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                            ){


                            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically ) {

                                IconMenuBtn(painterResource(R.drawable.pallet) , "Scan Pallet", onClick = {
                                    val isPallet = true
                                    try{
                                    navController.navigate("qr-scanner?isPallet=$isPallet")
                                    }catch (e: Exception){
                                        println("NAVIGATION ERROR $e")
                                    }
                                })

                                Spacer(modifier = Modifier.padding(12.dp))

                                IconMenuBtn(painterResource(R.drawable.pallet) , "Create Pallet", onClick = {navController.navigate("create-pallet?creating=true")})

                            }
                            Spacer(modifier = Modifier.padding(12.dp))
                            IconMenuBtn(painterResource(R.drawable.box) , "Scan Box", onClick = {
                                val isPallet = false
                                try{
                                    navController.navigate("qr-scanner?isPallet=$isPallet")
                                }catch (e: Exception) {
                                    println("NAVIGATION ERROR $e")
                                }
                            })

                        }

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
private fun PalletScreenPreview() {
    PalletScreen(navController = NavController(LocalContext.current))
}