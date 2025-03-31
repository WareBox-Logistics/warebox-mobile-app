package com.example.lp_logistics.presentation.screens.warehouse.pallets

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lp_logistics.data.remote.responses.BoxResponse
import com.example.lp_logistics.presentation.components.BottomBar
import com.example.lp_logistics.presentation.components.NumericOrangeTextFieldContainers
import com.example.lp_logistics.presentation.components.TopBar
import com.example.lp_logistics.presentation.theme.LightCreme
import com.example.lp_logistics.presentation.theme.LightOrange
import com.example.lp_logistics.presentation.theme.Orange

@Composable
fun BoxInfoScreen(boxID: Int, navController: NavController, viewModel: CreatePalletViewModel = hiltViewModel() ) {
    val context = LocalContext.current
    val boxInfo = viewModel.singleBox
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        println("Getting box info for box ID: $boxID")
        viewModel.getBox(context, boxID)
    }

    Scaffold(
        topBar = { TopBar(title = "Box Info", color = false) }
        ,
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(color = LightCreme)

            ) {
                if (boxInfo.value == null) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = LightOrange
                    )
                } else {
                    val box = boxInfo.value!!

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 10.dp).verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Basic Info:",
                            fontSize = 16.sp,
                            color = Orange,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                        NumericOrangeTextFieldContainers(
                            value = box.product,
                            label = "Product",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )

                        NumericOrangeTextFieldContainers(
                            value = box.qty.toString(),
                            label = "Quantity",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )

                        NumericOrangeTextFieldContainers(
                            value = box.weight,
                            label = "Weight",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )

                        NumericOrangeTextFieldContainers(
                            value = box.volume,
                            label = "Volume",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )

                        Text(
                            text = "Belongs to Pallet:",
                            fontSize = 16.sp,
                            color = Orange,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )

                        NumericOrangeTextFieldContainers(
                            value = box.pallet.id.toString(),
                            label = "ID",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )

                        NumericOrangeTextFieldContainers(
                            value = box.pallet.company,
                            label = "Company",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )

                        NumericOrangeTextFieldContainers(
                            value = box.pallet.warehouse,
                            label = "Warehouse",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )

                        NumericOrangeTextFieldContainers(
                            value = box.pallet.volume,
                            label = "Volume",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )

                        NumericOrangeTextFieldContainers(
                            value = box.pallet.weight,
                            label = "Weight",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )

                        NumericOrangeTextFieldContainers(
                            value = box.pallet.status,
                            label = "Status",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                    }
                }
            }
        },bottomBar = { BottomBar(navController, isWarehouse = true) }
            )
}
