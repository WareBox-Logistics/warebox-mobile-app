package com.example.lp_logistics.presentation.screens.home

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lp_logistics.data.remote.responses.DeliveryData
import com.example.lp_logistics.presentation.components.BottomBar
import com.example.lp_logistics.presentation.components.DateTimeUtils
import com.example.lp_logistics.presentation.components.TopBar
import com.example.lp_logistics.presentation.components.TripsCard
import com.example.lp_logistics.presentation.theme.LightBlue
import com.example.lp_logistics.presentation.theme.LightOrange


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavController,
    context: Context,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val loading by viewModel.loading.collectAsState()
    val deliveries by viewModel.deliveries
    val isRefreshing by viewModel.isRefreshing // Add this to your ViewModel

    LaunchedEffect(Unit) {
        println("Loading deliveries...")
        viewModel.loadDeliveries(context)
    }

    Scaffold(
        topBar = {
                TopBar(title = "Daily Route", color = false)
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = LightOrange)
            ) {
                // Combined pull-to-refresh and content

                when {
                    loading && deliveries.isEmpty() -> {
                        // Show full-screen loading only if there's no data
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.White
                        )
                    }

                    else -> {
                        PullToRefreshBox(
                            isRefreshing = isRefreshing,
                            onRefresh = { viewModel.loadDeliveries(context) }
                        ) {
                            DeliveryList(
                                deliveries,
                                modifier = Modifier.padding(15.dp),
                                contentPadding = innerPadding,
                                navController = navController
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            BottomBar(navController)
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeliveryList(
    deliveries: List<DeliveryData>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    navController: NavController
) {

    LazyColumn(modifier = modifier,horizontalAlignment = Alignment.CenterHorizontally, contentPadding = contentPadding) {
        if(deliveries.isEmpty()){
            item{
                Spacer(modifier = Modifier.height(200.dp))
            }
            item{
                Icon(
                    Icons.Rounded.TaskAlt,
                    contentDescription = "No deliveries",
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
            }
            item{
                Text(
                    text = "No future or current deliveries found",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }else {
            items(deliveries) { delivery ->
                DeliveryItem(delivery = delivery, navController = navController)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeliveryItem(delivery: DeliveryData, navController: NavController) {
    val formattedTime = remember(delivery.shipping_date) {
        DateTimeUtils.formatToTime(delivery.shipping_date)
    }

    TripsCard(
        origin = delivery.origin.name,
        destination = delivery.destination.name,
        date = delivery.shipping_date,
        image = "1",
        navController = navController,
        time = formattedTime,
        deliveryID = delivery.id
    )

    Spacer(modifier = Modifier.height(16.dp))
}
