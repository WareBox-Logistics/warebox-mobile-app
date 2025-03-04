package com.example.lp_logistics.presentation.screens.warehouse.pallets

import android.widget.Toast
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lp_logistics.R
import com.example.lp_logistics.data.remote.responses.CompanyResponse
import com.example.lp_logistics.data.remote.responses.WarehouseResponse
import com.example.lp_logistics.presentation.components.BottomBar
import com.example.lp_logistics.presentation.components.BottomSheet
import com.example.lp_logistics.presentation.components.ItemBox
import com.example.lp_logistics.presentation.components.OrangeTextFields
import com.example.lp_logistics.presentation.components.TopBar
import com.example.lp_logistics.presentation.theme.Create
import com.example.lp_logistics.presentation.theme.LightCreme
import com.example.lp_logistics.presentation.theme.LightGray
import com.example.lp_logistics.presentation.theme.LightOrange
import com.example.lp_logistics.presentation.theme.Orange
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePalletScreen(navController: NavController, viewModel: CreatePalletViewModel = hiltViewModel()) {
    val palletID = remember { mutableStateOf("") }
    var client by remember { mutableStateOf<CompanyResponse?>(null) }
    var origin = remember { mutableStateOf<WarehouseResponse?>(null) }
    val height = remember { mutableStateOf("") }
    val width = remember { mutableStateOf("") }
    val depth = remember { mutableStateOf("") }
    val weight = remember { mutableStateOf("") }
    val status = remember { mutableStateOf("") }
    var boxes: Int = 0

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var expanded by remember { mutableStateOf(false) }

    var expandedOrigin by remember { mutableStateOf(false) }


    val context = LocalContext.current

    //viewmodel vals
    val companies = viewModel.companies
    val warehouses = viewModel.warehouses
    val products = viewModel.products

    LaunchedEffect(Unit) {
        viewModel.getAllCompanies(context)
        viewModel.getAllWarehouses(context)
    }

    LaunchedEffect(client) {
        if (client != null) {
            viewModel.getProductsByCompany(context, client!!.id)
        }
    }


    Scaffold(
        topBar = { TopBar(title = "Pallets", color = false) },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = LightOrange)
                    .padding(innerPadding)
            ) {
                Box(modifier = Modifier
                    .offset(y = -(55).dp, x = -(30).dp)
                    .align(Alignment.TopEnd)
                    .background(LightCreme, RoundedCornerShape(10.dp))
                    .width(130.dp)
                    .padding(start=10.dp,top=5.dp,bottom=5.dp)){

                    Row(  horizontalArrangement = Arrangement.Center) {
                        Icon(
                            painter = painterResource(R.drawable.box),
                            contentDescription = "Box Icon",
                            tint = LightOrange,
                            modifier = Modifier
                                .size(40.dp)

                        )

                        Spacer(modifier = Modifier.weight(1f))


                        Text(
                            text= boxes.toString(),
                            color = LightOrange,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 10.dp, top = 5.dp)

                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .height(600.dp)
                            .width(352.dp)
                            .padding()
                            .background(LightCreme, RoundedCornerShape(10.dp))
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically,

                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    OrangeTextFields(
                                        palletID,
                                        "Pallet ID",
                                        KeyboardOptions(imeAction = ImeAction.Next),
                                        readOnly = true
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "Company",
                                        fontSize = 12.sp,
                                        color = Orange,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.align(Alignment.Start)
                                    )
                                    Box(modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = Color.White,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .height(60.dp)
                                        .clickable { expanded = true }){
                                            Text(
                                                text =client?.name ?: "Select a Company",
                                                fontSize = 12.sp,
                                                color = LightGray,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier
                                                    .align(Alignment.CenterStart)
                                                    .padding(start = 10.dp)
                                            )

                                        DropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false },
                                            modifier = Modifier
                                                .background(
                                                    LightOrange,
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .width(152.dp)
                                        ) {
                                            companies.value.forEach { company ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            text = company.name,
                                                            fontSize = 14.sp,
                                                            color = Color.White,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                           },
                                                    onClick = {
                                                    client = company
                                                    expanded = false
                                                }
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .height(150.dp)
                                        .offset(y = 12.dp)
                                ){

                                    Box(
                                        modifier = Modifier
                                            .height(150.dp)
                                            .width(150.dp)
                                            .background(LightOrange, RoundedCornerShape(10.dp))
                                            .weight(1f)
                                            .clickable {
                                                if (client != null) {
                                                    showBottomSheet = true
                                                } else {
                                                    Toast.makeText(context, "Please select a company", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Icon(
                                                Icons.Rounded.Add,
                                                contentDescription = "Add Icon",
                                                tint = Color.White,
                                                modifier = Modifier
                                                    .height(80.dp)
                                                    .width(80.dp)
                                            )
                                            Text(
                                                text = "Add Box",
                                                color = Color.White,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }
                                }
                            }


                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Warehouse",
                                fontSize = 12.sp,
                                color = Orange,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .background(color = Color.White, shape = RoundedCornerShape(10.dp))
                                .height(60.dp)
                                .clickable { expandedOrigin = true }){
                                Text(
                                    text =origin.value?.name ?: "Select an Origin",
                                    fontSize = 12.sp,
                                    color = LightGray,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .padding(start = 10.dp)
                                )

                                DropdownMenu(
                                    expanded = expandedOrigin,
                                    onDismissRequest = { expandedOrigin = false },
                                    modifier = Modifier
                                        .background(LightOrange, shape = RoundedCornerShape(10.dp))
                                        .width(320.dp)
                                ) {
                                    warehouses.value.forEach { warehouse ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = warehouse.name,
                                                    fontSize = 14.sp,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            },
                                            onClick = {
                                                origin = mutableStateOf(warehouse)
                                                expandedOrigin = false
                                            }
                                        )
                                    }
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                                    .height(80.dp)
                            ) {
                                OrangeTextFields(height, "Height", KeyboardOptions(imeAction = ImeAction.Next), modifier = Modifier.weight(1f), placeholder = "In CM")
                                Spacer(modifier = Modifier.width(16.dp))
                                OrangeTextFields(width, "Width", KeyboardOptions(imeAction = ImeAction.Next), modifier = Modifier.weight(1f), placeholder = "In CM")
                            }

                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                                    .height(80.dp)
                            ) {
                                OrangeTextFields(depth, "Depth", KeyboardOptions(imeAction = ImeAction.Next), modifier = Modifier.weight(1f),placeholder = "In CM")
                                Spacer(modifier = Modifier.width(16.dp))
                                OrangeTextFields(weight, "Weight", KeyboardOptions(imeAction = ImeAction.Next), modifier = Modifier.weight(1f),placeholder = "In KG")
                            }

                            OrangeTextFields(status, "Status", KeyboardOptions(imeAction = ImeAction.Next))
                        }
                    }

                    Button(
                        onClick = { /* create the pallet */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Create),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 18.dp)
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Create Pallet",
                            color = Color(0xFF009045),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            if (showBottomSheet) {
                BottomSheet(
                    sheetState = sheetState,
                    onDismissRequest = { showBottomSheet = false },
                ) {

                    Button(onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    }) {
                        Text("Hide bottom sheet")
                    }
                }
            }
        },
        bottomBar = { BottomBar(navController, isWarehouse = true) }
    )
}


@Preview
@Composable
private fun CreatePalletScreenPreview() {
    CreatePalletScreen(navController = NavController(LocalContext.current))
}