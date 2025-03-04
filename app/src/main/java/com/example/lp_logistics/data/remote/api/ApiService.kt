package com.example.lp_logistics.data.remote.api

import com.example.lp_logistics.data.remote.requests.CreateBoxRequest
import com.example.lp_logistics.data.remote.requests.CreatePalletRequest
import com.example.lp_logistics.data.remote.requests.LoginRequest
import com.example.lp_logistics.data.remote.responses.BoxResponse
import com.example.lp_logistics.data.remote.responses.Companies
import com.example.lp_logistics.data.remote.responses.CompanyResponse
import com.example.lp_logistics.data.remote.responses.LoginResponse
import com.example.lp_logistics.data.remote.responses.PalletResponse
import com.example.lp_logistics.data.remote.responses.ProductResponse
import com.example.lp_logistics.data.remote.responses.RouteResponse
import com.example.lp_logistics.data.remote.responses.SimpleProductResponse
import com.example.lp_logistics.data.remote.responses.WarehouseResponse
import com.example.lp_logistics.data.remote.responses.Warehouses
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    //AUTH
    @POST("loginEmployee")
    suspend fun loginEmployee(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("logout")
    suspend fun logout(
        @Header("Authorization") token: String
    )

    //Routes testing
    @GET("route/{id}")
    suspend fun getRoute(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): RouteResponse

    //Create a Box
    @POST("box-inventory")
    suspend fun createBox(
        @Header("Authorization") token: String,
        @Body request: CreateBoxRequest
    )

    //Create a Pallet
    @POST("pallet")
    suspend fun createPallet(
        @Header("Authorization") token: String,
        @Body request: CreatePalletRequest
    )

    //get warehouse
    @GET("warehouse/{id}")
    suspend fun getWarehouse(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): WarehouseResponse

    //get company
    @GET("company/{id}")
    suspend fun getCompany(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): CompanyResponse

    //get product
    @GET("product/{id}")
    suspend fun getProduct(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ):ProductResponse

    //get pallet
    @GET("pallet/{id}")
    suspend fun getPallet(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): PalletResponse

    //get box
    @GET("box-inventory/{id}")
    suspend fun getBox(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): BoxResponse

    //get all companies
    @GET("company")
    suspend fun getAllCompanies(
        @Header("Authorization") token: String
    ): Companies

    //get all warehouses
    @GET("warehouse")
    suspend fun getAllWarehouses(
        @Header("Authorization") token: String
    ): Warehouses

    @GET("product/company/{company}")
    suspend fun getProductsByCompany(
        @Header("Authorization") token: String,
        @Path("company") company: Int
    ): List<SimpleProductResponse>
}