package com.example.lp_logistics.data.repository

import com.example.lp_logistics.data.remote.api.ApiService
import com.example.lp_logistics.data.remote.responses.Route
import com.example.lp_logistics.data.remote.responses.RouteResponse

import javax.inject.Inject

class NavigationRepository @Inject constructor(private val apiService: ApiService){

    suspend fun getRoute(token: String, id: Int): Route {
        return apiService.getRoute("Bearer $token", id).route
    }
}