package com.example.lp_logistics.presentation.screens.warehouse.pallets

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lp_logistics.data.local.UserManager
import com.example.lp_logistics.data.remote.requests.CreateBoxRequest
import com.example.lp_logistics.data.remote.responses.BoxResponseWithPallet
import com.example.lp_logistics.data.remote.responses.CompanyResponse
import com.example.lp_logistics.data.remote.responses.PalletResponse
import com.example.lp_logistics.data.remote.responses.SimpleProductResponse
import com.example.lp_logistics.data.remote.responses.WarehouseResponse
import com.example.lp_logistics.data.repository.WarehouseRepository
import com.example.lp_logistics.presentation.components.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class CreatePalletViewModel @Inject constructor(private val warehouseRepository: WarehouseRepository) : ViewModel(){
    private val _companies = mutableStateOf<List<CompanyResponse>>(emptyList())
    val companies: State<List<CompanyResponse>> = _companies

    private val _warehouses = mutableStateOf<List<WarehouseResponse>>(emptyList())
    val warehouses: State<List<WarehouseResponse>> = _warehouses

    private val _products = mutableStateOf<List<SimpleProductResponse>>(emptyList())
    val products: State<List<SimpleProductResponse>> = _products

    //for the pallets
    private val _pallet = mutableStateOf<PalletResponse?>(null)
    val pallet: State<PalletResponse?> = _pallet

    private val _palletId = mutableIntStateOf(0)
    val palletId: State<Int> = _palletId

    //for the searchbar
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success = _success.asStateFlow()

    private val _isCreating = mutableStateOf(false)
    val isCreating: State<Boolean> get() = _isCreating

    private val _singleBox = mutableStateOf<BoxResponseWithPallet?>(null)
    val singleBox: State<BoxResponseWithPallet?> = _singleBox

    fun setCreating(isCreating: Boolean) {
        _isCreating.value = isCreating
    }

    val companyProducts = searchText
        .debounce(500L)
        .onEach { _isSearching.update { true } }
        .combine(snapshotFlow { _products.value }) { text, products ->
            println("Products: $text")
            if (text.isBlank()) {
                products
            } else {
                delay(2000L)
                products.filter{
                    it.doesMatchSearchQuery(text)
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _products.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }



    fun getPallet(context: Context, id: Int) {
        viewModelScope.launch {
            try {
                println("Getting pallet with id: $id")
                _isSearching.value = true
                val token = UserManager.getToken(context).toString()
                val pallet = withContext(Dispatchers.IO) {
                    warehouseRepository.getPallet(token, id)
                }
                if (pallet == null) {
                    println("Pallet is null!")
                    return@launch
                }
                println("Pallet retrieved: $pallet")
                _pallet.value = pallet
            } catch (e: Exception) {
                println("Error getting pallet: ${e.message}")
            } finally {
                _isSearching.value = false
            }
        }
    }


private fun createBox(
        context: Context,
        qty: Int,
        weight: Float,
        volume: Float,
        pallet: Int,
        product: Int
    ){
        viewModelScope.launch {
            try{
                val token = UserManager.getToken(context).toString()
                warehouseRepository.createBox(token, qty, weight, volume, pallet, product)
            } catch (e: Exception){
                println("Error creating box: ${e.message}")
            }
        }
    }

    fun getProduct(context: Context, id: Int){
        viewModelScope.launch {
            val token = UserManager.getToken(context).toString()
            try{
                warehouseRepository.getProduct(token, id)
            } catch (e: Exception){
                println("Error getting product: ${e.message}")
            }
        }
    }

    fun getBox(context: Context, id: Int){
        viewModelScope.launch {
            val token = UserManager.getToken(context).toString()
            try{
                val box = warehouseRepository.getBox(token, id)
                _singleBox.value = box
                println("Box: ${_singleBox.value}")
            } catch (e: Exception){
                println("Error getting box: ${e.message}")
            }
        }
    }

        fun getAllCompanies(context: Context) {
            viewModelScope.launch {
                val token = UserManager.getToken(context).toString()
                try {
                    val result = warehouseRepository.getAllCompanies(token)
                    _companies.value = result.companies
                } catch (e: Exception) {
                    println("Error getting all companies: ${e.message}")
                }
            }
        }

    fun getAllWarehouses(context: Context){
        viewModelScope.launch {
            val token = UserManager.getToken(context).toString()
            try{
                val result = warehouseRepository.getAllWarehouses(token)
                _warehouses.value = result.warehouses
                println("Warehouses: ${_warehouses.value}")
            } catch (e: Exception){
                println("Error getting all warehouses: ${e.message}")
            }
        }
    }
    fun getProductsByCompany(context: Context, company: Int) {
        viewModelScope.launch {
            val token = UserManager.getToken(context).toString()
            try {
                _isSearching.value = true
                val result = warehouseRepository.getProductsByCompany(token, company)
                _products.value = result
                if (_products.value.isEmpty()){
                    showToast(context,"No products found for this company")
                }
                println("Products: ${_products.value}")
                _isSearching.value = false
            } catch (e: Exception) {
                println("Error getting products by company: ${e.message}")
            }
        }
    }

    fun createPalletAndBoxes(
        context: Context,
        company: Int,
        warehouse: Int,
        weight: Float,
        volume: Float,
        verified: Boolean,
        boxes: List<CreateBoxRequest>,
    ) {
        viewModelScope.launch {
            try {
                setCreating(true)
                val token = UserManager.getToken(context).toString()
                val status = "Created"

                // Create the pallet and retrieve its ID
                val palletResponse = warehouseRepository.createPallet(
                    token,
                    company,
                    warehouse,
                    weight,
                    volume,
                    status,
                    verified
                )
                val palletId = palletResponse.id
                _palletId.intValue = palletId

                for (box in boxes) {
                    createBox(
                        context,
                        box.qty,
                        box.weight,
                        box.volume,
                        palletId, // the pallet ID
                        box.product
                    )
                }
                _success.value = true
            } catch (e: Exception) {
                println("Error creating pallet and boxes: ${e.message}")
                _success.value = false
                showToast(context, "Error creating pallet and boxes: ${e.message}")
            } finally {
                setCreating(false)
            }
        }
    }

    fun resetSuccess() {
        _success.value = false
    }


}