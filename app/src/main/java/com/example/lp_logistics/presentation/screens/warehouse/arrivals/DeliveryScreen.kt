package com.example.lp_logistics.presentation.screens.warehouse.arrivals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lp_logistics.domain.model.BoxUIState
import com.example.lp_logistics.domain.model.PalletUIState
import com.example.lp_logistics.presentation.components.BottomBar
import com.example.lp_logistics.presentation.components.TopBar
import com.example.lp_logistics.presentation.screens.home.HomeViewModel
import com.example.lp_logistics.presentation.theme.Create
import com.example.lp_logistics.presentation.theme.LightBlue
import com.example.lp_logistics.presentation.theme.LightCreme
import com.example.lp_logistics.presentation.theme.LightOrange
import com.example.lp_logistics.presentation.theme.Orange


@Composable
fun DeliveryScreen(deliveryID: Int, navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val uiState = viewModel.uiState.value
    val loadingStatus by viewModel.loadingStatus.collectAsState()
    val deliveryStatus by viewModel.deliveryStatus
    val deliveryDetails by viewModel.deliveryDetails

    LaunchedEffect(Unit) {
        viewModel.getDeliveryDetails(deliveryID, context)
        println("Delivery ID (in confirmationScreen): $deliveryID")
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
                if (uiState.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = LightOrange
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .padding( 20.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Basic Info
                        Text(
                            text = "Basic Info:",
                            style = MaterialTheme.typography.titleMedium.copy(color = Orange)
                        )
                        InfoField("Origin", viewModel.deliveryDetails.value?.origin ?: "")
                        InfoField("Destination", viewModel.deliveryDetails.value?.destination ?: "")

                        // Pallets
                        Text(
                            text = "Pallets:",
                            style = MaterialTheme.typography.titleMedium.copy(color = Orange),
                            modifier = Modifier.padding(vertical = 10.dp)
                        )

                        uiState.forEach { palletState ->
                            PalletItem(
                                palletState = palletState,
                                onPalletClick = { viewModel.togglePalletExpanded(palletState.pallet.pallet_id) },
                                onBoxClick = { boxId ->
                                    viewModel.toggleBoxExpanded(palletState.pallet.pallet_id, boxId)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (deliveryStatus == "Delivering"){
                            Box(
                                modifier = Modifier
                                    .height(55.dp)
                                    .background(Create, RoundedCornerShape(10.dp))
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.confirmArrival(context,deliveryID)
                                    }) {

                                if(loadingStatus){
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.align(Alignment.Center),
                                    )
                                } else {

                                    Text(
                                        text = "Confirm Arrival",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = { BottomBar(navController, isWarehouse = true) }
    )
}

@Composable
fun PalletItem(
    palletState: PalletUIState,
    onPalletClick: () -> Unit,
    onBoxClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Pallet header (clickable)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPalletClick() }
                .padding(vertical = 8.dp)
                .background(LightOrange, RoundedCornerShape(10.dp))
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pallet ID: ${palletState.pallet.pallet_id}",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                modifier = Modifier.weight(1f).padding(start = 16.dp)
            )
            Icon(
                imageVector = if (palletState.isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (palletState.isExpanded) "Collapse" else "Expand",
                tint = Color.White
            )
        }

        // Pallet content (shown when expanded)
        if (palletState.isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            ) {
                palletState.boxes.forEach { boxState ->
                    BoxItem(
                        boxState = boxState,
                        onClick = { onBoxClick(boxState.box.box_id) },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BoxItem(
    boxState: BoxUIState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Box header (clickable)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 8.dp)
                .background(LightBlue, RoundedCornerShape(10.dp))
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Box ID: ${boxState.box.box_id}",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                modifier = Modifier.weight(1f).padding(start = 16.dp)
            )
            Icon(
                imageVector = if (boxState.isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (boxState.isExpanded) "Collapse" else "Expand",
                tint = Color.White
            )
        }

        // Box content (shown when expanded)
        if (boxState.isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp)
            ) {
                InfoField("Product", boxState.box.product_name)
                InfoField("Quantity", boxState.box.quantity.toString())
                InfoField("SKU", boxState.box.product_sku)
            }
        }
    }
}

@Composable
fun InfoField(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Orange,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(10.dp))
                .padding(12.dp)
        )
    }
}