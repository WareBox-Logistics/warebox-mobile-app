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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lp_logistics.R
import com.example.lp_logistics.data.remote.requests.CreateBoxRequest
import com.example.lp_logistics.data.remote.responses.BoxResponse
import com.example.lp_logistics.data.remote.responses.CompanyResponse
import com.example.lp_logistics.data.remote.responses.Service
import com.example.lp_logistics.data.remote.responses.SimpleProductResponse
import com.example.lp_logistics.data.remote.responses.WarehouseResponse
import com.example.lp_logistics.presentation.components.BottomBar
import com.example.lp_logistics.presentation.components.BottomSheet
import com.example.lp_logistics.presentation.components.ItemBox
import com.example.lp_logistics.presentation.components.NumericOrangeTextField
import com.example.lp_logistics.presentation.components.NumericOrangeTextFieldContainers
import com.example.lp_logistics.presentation.components.OrangeTextFields
import com.example.lp_logistics.presentation.components.SearchBar
import com.example.lp_logistics.presentation.components.SelectableDropdown
import com.example.lp_logistics.presentation.components.SlimButton
import com.example.lp_logistics.presentation.components.TopBar
import com.example.lp_logistics.presentation.components.VolumeGeneratorInMeters
import com.example.lp_logistics.presentation.components.adding2Floats
import com.example.lp_logistics.presentation.components.subtracting2Floats
import com.example.lp_logistics.presentation.theme.Create
import com.example.lp_logistics.presentation.theme.LightCreme
import com.example.lp_logistics.presentation.theme.LightGray
import com.example.lp_logistics.presentation.theme.LightOrange
import com.example.lp_logistics.presentation.theme.Orange
import com.example.lp_logistics.presentation.theme.RedText
import com.example.lp_logistics.presentation.theme.Report
import com.example.lp_logistics.presentation.theme.Warning
import com.example.lp_logistics.presentation.theme.WarningText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePalletScreen(navController: NavController, creating: Boolean = false, palletIDNav: Int = 0, viewModel: CreatePalletViewModel = hiltViewModel()) {
    val palletID = remember { mutableStateOf("") }
    var client by remember { mutableStateOf<CompanyResponse?>(null) }
    var origin = remember { mutableStateOf<WarehouseResponse?>(null) }
    val status = remember { mutableStateOf("") }
    val boxesState = remember { mutableIntStateOf(0) }


    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    //boxbottomsheet
    var showBoxBottomSheet by remember { mutableStateOf(false) }
    val boxSheetState = rememberModalBottomSheetState()

    val context = LocalContext.current

    //viewmodel vals
    val companies = viewModel.companies
    val warehouses = viewModel.warehouses
    val searchText by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val companyProducts by viewModel.companyProducts.collectAsState()

    //Box vals
    val selectedProduct = remember { mutableStateOf<SimpleProductResponse?>(null) }
    val boxQuantity = remember { mutableStateOf("") }
    val boxWeight = remember { mutableStateOf("") }
    val boxHeight = remember { mutableStateOf("") }
    val boxWidth = remember { mutableStateOf("") }
    val boxDepth = remember { mutableStateOf("") }

    //List of Boxes
    val boxes = remember { mutableListOf<CreateBoxRequest>() }
    val boxesResponseList = remember { mutableListOf<BoxResponse>() }
    val boxStates = remember { mutableStateMapOf<Int, Boolean>() }
    val palletVolume = remember { mutableFloatStateOf(0.0f) }
    val volumeState = remember { mutableStateOf(palletVolume.floatValue.toString()) }
    val palletWeight = remember { mutableFloatStateOf(0.0f) }
    val weightState = remember { mutableStateOf(palletWeight.floatValue.toString())}

    //success
    val success by viewModel.success.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val isCreating by viewModel.isCreating

    //pallet
    val palletInfo by viewModel.pallet
    val palletId by viewModel.palletId
    val palletCompany = remember { mutableStateOf("") }
    val palletWarehouse = remember { mutableStateOf("") }

    if(creating)(LaunchedEffect(Unit) {
        try {
            viewModel.getAllCompanies(context)
        } catch (e: Exception) {
            Toast.makeText(context, "Error getting all companies: ${e.message}", Toast.LENGTH_LONG).show()
        }

        try {
            viewModel.getAllWarehouses(context)
        } catch (e: Exception) {
            Toast.makeText(context, "Error getting all warehouses: ${e.message}", Toast.LENGTH_LONG).show()
        }
    })

    if(!creating){

        LaunchedEffect(palletIDNav) {
            println("Start: palletIDNav = $palletIDNav")
            viewModel.getPallet(context, palletIDNav)
            println("After getPallet")
        }

        LaunchedEffect(palletInfo) {
            if (isSearching) {
                println("waiting")
            } else if (palletInfo != null) {
                println(palletInfo)
                palletID.value = palletInfo?.id.toString()
                println("Set palletID = ${palletID.value}")

                palletCompany.value = palletInfo!!.company
                println("Set palletCompany = ${palletCompany.value}")

                palletWarehouse.value = palletInfo!!.warehouse
                println("Set palletWarehouse = ${palletWarehouse.value}")

                status.value = palletInfo!!.status
                println("Set status = ${status.value}")

                boxesState.intValue = palletInfo!!.boxes.size
                println("Set boxesState = ${boxesState.intValue}")

                palletInfo!!.boxes.forEach { box ->
                    boxesResponseList.add(box)
                }
                println("Finished adding boxes to boxesResponseList")

                volumeState.value = palletInfo!!.volume
                println("Set palletVolume = ${palletVolume.floatValue}")

                weightState.value = palletInfo!!.weight
                println("Set palletWeight = ${palletWeight.floatValue}")
            }
        }

    }

    if(creating) {
        LaunchedEffect(client) {
            if (client != null) {
                viewModel.getProductsByCompany(context, client!!.id)
            }
        }


        LaunchedEffect(palletVolume.floatValue) {
            volumeState.value = palletVolume.floatValue.toString()
        }

        LaunchedEffect(palletWeight.floatValue) {
            weightState.value = palletWeight.floatValue.toString()
        }
    }


    Scaffold(
        topBar = { TopBar(title = "Pallets", color = false) }
        ,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(color = LightOrange)

            ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = LightOrange)
            ) {


                if (success) {
                    LaunchedEffect(success) {
                        snackbarHostState.showSnackbar("Pallet & Boxes created successfully")
                        viewModel.resetSuccess()
                        //if this works i should clear the filters and show the information in each filed, i can use the get pallet function and just pass the ID of the pallet
                        println("This is the ID we got back: $palletId")
                        navController.navigate("create-pallet?palletIDNav=$palletId")
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Absolute.SpaceAround,
                        modifier = Modifier.padding(start = 20.dp)
                    ) {

                        Box(modifier = Modifier
                            .background(LightCreme, RoundedCornerShape(10.dp))
                            .width(130.dp)
                            .height(55.dp)
                            .padding(start = 10.dp)
                            .clickable {
                                showBoxBottomSheet = true
                                println("Clicked")
                            }) {

                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.align(Alignment.Center)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.box),
                                    contentDescription = "Box Icon",
                                    tint = LightOrange,
                                    modifier = Modifier
                                        .size(40.dp)

                                )

                                Spacer(modifier = Modifier.weight(1f))


                                Text(
                                    text = boxesState.intValue.toString(),
                                    color = LightOrange,
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 10.dp, top = 5.dp)

                                )
                            }
                        }
                    if(creating){
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .height(55.dp)
                                .width(200.dp)
                                .background(Create, RoundedCornerShape(10.dp))
                                .clickable {
                                    println("CREATE PALLET WAS CLICKED")

                                    // Validate data
                                    val isDataValid = client != null && origin.value != null &&
                                            palletWeight.floatValue > 0f && palletVolume.floatValue > 0f && boxes.isNotEmpty()

                                    if (isDataValid) {
                                        // All data is valid, send to ViewModel
                                        viewModel.createPalletAndBoxes(
                                            context = context,
                                            client!!.id,
                                            origin.value!!.id,
                                            palletWeight.floatValue,
                                            palletVolume.floatValue,
                                            false, // Example
                                            boxes = boxes
                                        )
                                    } else {
                                        // Handle invalid data (e.g., show a toast or snackbar to inform the user)
                                        Toast
                                            .makeText(
                                                context,
                                                "Please fill in all required fields.",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                                }
                        ) {
                            Text(
                                text = "Create Pallet & Boxes",
                                color = Color(0xFF009045),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.align(Alignment.Center)

                            )
                        }
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
                                        if(creating) {
                                        Text(
                                            text = "Company",
                                            fontSize = 12.sp,
                                            color = Orange,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.align(Alignment.Start)
                                        )

                                            SelectableDropdown(
                                                selectedText = client?.name ?: "Select a Company",
                                                items = companies.value.map { it.name },
                                                onItemSelected = { selectedText ->
                                                    val selectedCompany =
                                                        companies.value.find { it.name == selectedText }
                                                    if (selectedCompany != null) {
                                                        client = selectedCompany
                                                    }
                                                }
                                            )
                                        }else{
                                            OrangeTextFields(
                                                palletCompany,
                                                "Company",
                                                KeyboardOptions(imeAction = ImeAction.Next),
                                                readOnly = true,
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier
                                            .height(150.dp)
                                            .offset(y = 12.dp)
                                    ) {

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
                                                        Toast
                                                            .makeText(
                                                                context,
                                                                "Please select a company",
                                                                Toast.LENGTH_SHORT
                                                            )
                                                            .show()
                                                    }
                                                }
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center,
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                Icon(
                                                    if(creating) Icons.Rounded.Add else Icons.Rounded.Receipt,
                                                    contentDescription = "Add Icon",
                                                    tint = Color.White,
                                                    modifier = Modifier
                                                        .height(80.dp)
                                                        .width(80.dp)
                                                )
                                                Text(
                                                    text = if(creating)"Add Box" else "Print QR Codes",
                                                    color = Color.White,
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                            }
                                        }
                                    }
                                }


                                Spacer(modifier = Modifier.height(10.dp))

                                if(creating) {
                                    Text(
                                    text = "Warehouse",
                                    fontSize = 12.sp,
                                    color = Orange,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.align(Alignment.Start)
                                )

                                    SelectableDropdown(
                                        selectedText = origin.value?.name ?: "Select a Warehouse",
                                        items = warehouses.value.map { it.name },
                                        onItemSelected = { selectedText ->
                                            val selectedWarehouse =
                                                warehouses.value.find { it.name == selectedText }
                                            if (selectedWarehouse != null) {
                                                origin.value = selectedWarehouse
                                            }
                                        },
                                        dropdownWidth = 320,
                                    )
                                }else{
                                    OrangeTextFields(
                                        palletWarehouse,
                                        "Warehouse",
                                        KeyboardOptions(imeAction = ImeAction.Next),
                                        readOnly = true,
                                    )
                                }


                                OrangeTextFields(
                                    weightState,
                                    "Weight (kg)",
                                    KeyboardOptions(imeAction = ImeAction.Next),
                                    placeholder = "In KG",
                                    readOnly = true,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                OrangeTextFields(
                                    volumeState,
                                    "Volume (m³)",
                                    KeyboardOptions(imeAction = ImeAction.Next),
                                    placeholder = "Volume in M3",
                                    readOnly = true,
                                    modifier = Modifier.padding(top = 8.dp)
                                )

                                OrangeTextFields(
                                    status,
                                    "Status",
                                    KeyboardOptions(imeAction = ImeAction.Next),
                                    readOnly = true,
                                    placeholder = if (creating) "Status will be 'created'" else "Status",
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }

            }
                //show this when its creating the pallet
                if (isCreating) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable(enabled = false) { }
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Orange
                        )
                    }
                }
        }
            if (showBottomSheet) {
                BottomSheet(
                    sheetState = sheetState,
                    onDismissRequest = { showBottomSheet = false },
                ) {

                    SearchBar(
                        textSearch = searchText,
                        onTextChange = viewModel::onSearchTextChange,
                        placeholder = "Search for company products by name or SKU",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedProduct.value != null) {

                        Text(
                            text = "${selectedProduct.value!!.name} Box",
                            fontSize = 14.sp,
                            color = Orange,
                            fontWeight = FontWeight.Bold
                        )

                       NumericOrangeTextField(
                           value = boxQuantity,
                           label = "Quantity",
                           modifier = Modifier
                               .fillMaxWidth()
                               .padding(horizontal = 10.dp, vertical = 5.dp),
                           placeholder = "Number of items in the box"
                       )

                        NumericOrangeTextField(
                            value = boxWeight,
                            label = "Weight (kg)",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            placeholder = "Weight of the box"
                        )

                        NumericOrangeTextField(
                            value = boxHeight,
                            label = "Height (cm)",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            placeholder = "Height of the box"
                        )

                        NumericOrangeTextField(
                            value = boxWidth,
                            label = "Width (cm)",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            placeholder = "Width of the box"
                        )

                        NumericOrangeTextField(
                            value = boxDepth,
                            label = "Length (cm)",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            placeholder = "Depth of the box",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                        )

                      SlimButton(
                          onClick = {
                              if (boxQuantity.value.isNotEmpty() && boxWeight.value.isNotEmpty() && boxHeight.value.isNotEmpty() && boxWidth.value.isNotEmpty() && boxDepth.value.isNotEmpty()) {
                                  val volume = VolumeGeneratorInMeters(boxDepth.value.toFloat(),boxWidth.value.toFloat(),boxHeight.value.toFloat())
                                  boxes.add(
                                    CreateBoxRequest(
                                        boxQuantity.value.toInt(),
                                        boxWeight.value.toFloat(),
                                        volume,
                                        -1,
                                        selectedProduct.value!!.id,
                                    )
                                    )
                                    boxesState.intValue++
                                    palletVolume.floatValue = adding2Floats(palletVolume.floatValue,volume)
                                    palletWeight.floatValue = adding2Floats(palletWeight.floatValue,boxWeight.value.toFloat())

                                  //hide form
                                  selectedProduct.value = null
                                  //clean inputs
                                  boxQuantity.value = ""
                                  boxDepth.value = ""
                                  boxWidth.value = ""
                                  boxWeight.value = ""
                                  boxHeight.value = ""
                                  //close bottomsheet
                                  showBottomSheet = false

                                  //show toast
                                  Toast.makeText(context, "Box added!", Toast.LENGTH_SHORT).show()

                              }else{
                                  Toast.makeText(context, "Please fill the form", Toast.LENGTH_SHORT).show()
                              }
                          },
                          iconRes = R.drawable.box,
                          iconColor = Color(0xFF009045),
                          primaryText = "Add Box",
                          modifier = Modifier
                              .fillMaxWidth()
                      )

                    SlimButton(
                        onClick = {
                            selectedProduct.value = null
                        },
                        iconRes = R.drawable.hide,
                        iconColor = WarningText,
                        textColor = WarningText,
                        primaryText = "Hide form",
                        containerColor = Warning,
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                        HorizontalDivider(thickness = 3.dp, color = LightGray, modifier = Modifier.padding(horizontal = 10.dp))
                    }


                    if (isSearching) {
                        println("Is searching")
                        Box(modifier = Modifier.fillMaxSize()){
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = Orange
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            println("companyProducts: ${companyProducts}")
                            items(companyProducts) { products ->
                                TextButton(
                                    onClick = {
                                        selectedProduct.value = products

                                    }
                                ){
                                    Text(
                                        text = products.name,
                                        color = Orange,
                                        fontSize = 16.sp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp)
                                    )
                                }
                            }
                        }
                }

                }
            }

            //box bottomsheet
            if (showBoxBottomSheet) {
                BottomSheet(
                    sheetState = boxSheetState,
                    onDismissRequest = { showBoxBottomSheet = false },
                ) {
                    Text(
                        text = "Boxes ready to register",
                        fontSize = 18.sp,
                        color = Orange,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .imePadding()
                            .weight(1f)
                    ) {
                        if (creating){
                            itemsIndexed(boxes) { index, box ->

                                SlimButton(
                                    onClick = {

                                        boxStates[index] = !(boxStates[index] ?: false)
                                    },
                                    iconRes = R.drawable.box,
                                    iconColor = LightCreme,
                                    textColor = LightCreme,
                                    primaryText = companyProducts.find { it.id == box.product }?.name
                                        ?: "Product not found",
                                    containerColor = LightOrange,
                                )

                                if (boxStates[index] == true) {
                                    NumericOrangeTextFieldContainers(
                                        value = companyProducts.find { it.id == box.product }?.name
                                            ?: "Product not found",
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
                                        value = box.weight.toString(),
                                        label = "Weight",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 5.dp),
                                    )

                                    NumericOrangeTextFieldContainers(
                                        value = box.volume.toString(),
                                        label = "Volume",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 5.dp),
                                    )

                                    SlimButton(
                                        onClick = {
                                            boxes.remove(box)
                                            //subtract counter, weight and volume
                                            boxesState.intValue--
                                            palletVolume.floatValue = subtracting2Floats(
                                                palletVolume.floatValue,
                                                box.volume
                                            )
                                            palletWeight.floatValue = subtracting2Floats(
                                                palletWeight.floatValue,
                                                box.weight
                                            )

                                            Toast.makeText(
                                                context,
                                                "Box removed!",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        },
                                        iconRes = R.drawable.trash,
                                        iconColor = RedText,
                                        textColor = RedText,
                                        primaryText = "Remove Box",
                                        containerColor = Report,
                                    )

                                    SlimButton(
                                        onClick = {
                                            boxStates[index] = false
                                        },
                                        iconRes = R.drawable.hide,
                                        iconColor = WarningText,
                                        textColor = WarningText,
                                        primaryText = "Hide Info",
                                        containerColor = Warning,
                                    )
                                }
                            }
                    } else {
                            itemsIndexed(boxesResponseList) { index, box ->

                                SlimButton(
                                    onClick = {

                                        boxStates[index] = !(boxStates[index] ?: false)
                                    },
                                    iconRes = R.drawable.box,
                                    iconColor = LightCreme,
                                    textColor = LightCreme,
                                    primaryText = box.product,
                                    containerColor = LightOrange,
                                )

                                if (boxStates[index] == true) {
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

                                    SlimButton(
                                        onClick = {
                                            boxStates[index] = false
                                        },
                                        iconRes = R.drawable.hide,
                                        iconColor = WarningText,
                                        textColor = WarningText,
                                        primaryText = "Hide Info",
                                        containerColor = Warning,
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


@Preview
@Composable
private fun CreatePalletScreenPreview() {
    CreatePalletScreen(navController = NavController(LocalContext.current))
}