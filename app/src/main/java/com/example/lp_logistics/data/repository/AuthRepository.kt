package com.example.lp_logistics.data.repository

import com.example.lp_logistics.data.remote.api.ApiService
import com.example.lp_logistics.data.remote.requests.LoginRequest
import com.example.lp_logistics.data.remote.responses.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiService: ApiService)
{
    suspend fun loginEmployee(email: String, password: String): LoginResponse {
        return withContext(Dispatchers.IO) {
            apiService.loginEmployee(LoginRequest(email, password))
        }
    }

    suspend fun logout(token: String) {
        withContext(Dispatchers.IO) {
            apiService.logout("Bearer $token")
        }
    }
}