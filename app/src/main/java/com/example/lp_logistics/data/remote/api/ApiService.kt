package com.example.lp_logistics.data.remote.api

import com.example.lp_logistics.data.remote.requests.LoginRequest
import com.example.lp_logistics.data.remote.responses.LoginResponse
import com.example.lp_logistics.data.remote.responses.RouteResponse
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

}