package com.example.lp_logistics.presentation.screens.warehouse.pallets

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lp_logistics.data.local.UserManager
import com.example.lp_logistics.data.remote.responses.CompanyResponse
import com.example.lp_logistics.data.remote.responses.SimpleProductResponse
import com.example.lp_logistics.data.remote.responses.WarehouseResponse
import com.example.lp_logistics.data.repository.WarehouseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePalletViewModel @Inject constructor(private val warehouseRepository: WarehouseRepository) : ViewModel(){
    private val _companies = mutableStateOf<List<CompanyResponse>>(emptyList())
    val companies: State<List<CompanyResponse>> = _companies

    private val _warehouses = mutableStateOf<List<WarehouseResponse>>(emptyList())
    val warehouses: State<List<WarehouseResponse>> = _warehouses

    private val _products = mutableStateOf<List<SimpleProductResponse>>(emptyList())
    val products: State<List<SimpleProductResponse>> = _products

    fun createPallet(
        context: Context,
        company: Int,
        warehouse: Int,
        weight: Float,
        volume: Float,
        status: String,
        verified: Boolean) {
        viewModelScope.launch {
            try {
                val token = UserManager.getToken(context).toString()

                warehouseRepository.createPallet(
                    token,
                    company,
                    warehouse,
                    weight,
                    volume,
                    status,
                    verified
                )
            } catch (e: Exception) {
                println("Error creating pallet: ${e.message}")
            }
        }
    }

    fun getPallet(context: Context, id: Int){
        viewModelScope.launch {
            try {
                val token = UserManager.getToken(context).toString()
                warehouseRepository.getPallet(token, id)
            } catch (e: Exception) {
                println("Error getting pallet: ${e.message}")
            }
        }
    }

    fun createBox(
        context: Context,
        qty: Int,
        weight: Float,
        volume: Float,
        pallet: Int,
        inventory: Int
    ){
        viewModelScope.launch {
            try{
                val token = UserManager.getToken(context).toString()
                warehouseRepository.createBox(token, qty, weight, volume, pallet, inventory)
            } catch (e: Exception){
                println("Error creating box: ${e.message}")
            }
        }
    }

    fun getWarehouse(context: Context, id: Int){
        viewModelScope.launch {
            try{
                val token = UserManager.getToken(context).toString()
                warehouseRepository.getWarehouse(token, id)
            } catch (e: Exception){
                println("Error getting warehouse: ${e.message}")

            }
        }
    }

    fun getCompany(context: Context, id: Int){
        viewModelScope.launch {
            val token = UserManager.getToken(context).toString()
            try{
                warehouseRepository.getCompany(token, id)
            } catch (e: Exception){
                println("Error getting company: ${e.message}")
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
                warehouseRepository.getBox(token, id)
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
            } catch (e: Exception){
                println("Error getting all warehouses: ${e.message}")
            }
        }
    }
    fun getProductsByCompany(context: Context, company: Int){
        viewModelScope.launch {
            val token = UserManager.getToken(context).toString()
            try{
                val result = warehouseRepository.getProductsByCompany(token, company)
                _products.value = result
            } catch (e: Exception){
                println("Error getting products by company: ${e.message}")
            }
        }
    }

}