package com.example.lp_logistics.data.repository

import com.example.lp_logistics.data.remote.api.ApiService
import com.example.lp_logistics.data.remote.requests.CreateBoxRequest
import com.example.lp_logistics.data.remote.requests.CreatePalletRequest
import com.example.lp_logistics.data.remote.responses.BoxResponse
import com.example.lp_logistics.data.remote.responses.BoxResponseWithPallet
import com.example.lp_logistics.data.remote.responses.Companies
import com.example.lp_logistics.data.remote.responses.CompanyResponse
import com.example.lp_logistics.data.remote.responses.PalletResponse
import com.example.lp_logistics.data.remote.responses.ProductResponse
import com.example.lp_logistics.data.remote.responses.SimpleProductResponse
import com.example.lp_logistics.data.remote.responses.WarehouseResponse
import com.example.lp_logistics.data.remote.responses.Warehouses
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import retrofit2.HttpException


class WarehouseRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun createBox(token: String, qty: Int, weight: Float, volume: Float, pallet: Int, product: Int): Unit = withContext(
        Dispatchers.IO) {
        try{
            apiService.createBox("Bearer $token", CreateBoxRequest(qty, weight, volume, pallet, product))
        } catch(e: HttpException) {
            println("HTTP Error: ${e.code()} - ${e.message()}")
        }
    }

    suspend fun createPallet(
        token: String,
        company: Int,
        warehouse: Int,
        weight: Float,
        volume: Float,
        status: String,
        verified: Boolean
    ): PalletResponse = withContext(Dispatchers.IO) {
        try {
            apiService.createPallet(
                "Bearer $token",
                CreatePalletRequest(company, warehouse, weight, volume, status, verified)
            )
        } catch (e: HttpException) {
            println("HTTP Error: ${e.code()} - ${e.message()}")
            throw e
        }
    }


    suspend fun getWarehouse(token: String, id: Int): WarehouseResponse{
        return try {
             withContext(Dispatchers.IO) {
            apiService.getWarehouse("Bearer $token", id)
                }
        } catch (e: HttpException) {
            println("HTTP Error: ${e.code()} - ${e.message()}")
            throw e
        }
    }

    suspend fun getCompany(token: String, id: Int): CompanyResponse {
        return try{
            withContext(Dispatchers.IO) {
                apiService.getCompany("Bearer $token", id)
            }
        } catch (e: HttpException) {
            println("HTTP Error: ${e.code()} - ${e.message()}")
            throw e
        }
    }

    suspend fun getProduct(token: String, id: Int): ProductResponse{
        return try {
            withContext(Dispatchers.IO) {
                apiService.getProduct("Bearer $token", id)
            }
            } catch (e: HttpException) {
            println("HTTP Error: ${e.code()} - ${e.message()}")
            throw e
        }
    }

    suspend fun getPallet(token: String, id: Int): PalletResponse{
        return try {
            withContext(Dispatchers.IO) {
                println("Getting pallet with id here: $id")
                apiService.getPallet("Bearer $token", id)
            }
            } catch (e: HttpException) {
            println("HTTP Error: ${e.code()} - ${e.message()}")
            throw e
        }
    }

    suspend fun getBox(token: String, id: Int): BoxResponseWithPallet {
        return try{
            withContext(Dispatchers.IO) {
                apiService.getBox("Bearer $token", id)
            }
        } catch (e: HttpException) {
            println("HTTP Error: ${e.code()} - ${e.message()}")
            throw e
        }
    }

    suspend fun getAllCompanies(token: String): Companies {
        return try{
            withContext(Dispatchers.IO) {
                apiService.getAllCompanies("Bearer $token")
            }
        } catch (e: HttpException) {
            println("HTTP Error: ${e.code()} - ${e.message()}")
            throw e
        }
    }

    suspend fun getAllWarehouses(token: String): Warehouses {
        return try{
            withContext(Dispatchers.IO) {
                apiService.getAllWarehouses("Bearer $token")
            }
            } catch (e: HttpException) {
            println("HTTP Error: ${e.code()} - ${e.message()}")
            throw e
        }
    }

    suspend fun getProductsByCompany(token: String, company: Int): List<SimpleProductResponse> {
        return try{
            withContext(Dispatchers.IO) {
                apiService.getProductsByCompany("Bearer $token", company)
            }
        } catch (e: HttpException) {
            println("HTTP Error: ${e.code()} - ${e.message()}")
            throw e
        }
    }



}