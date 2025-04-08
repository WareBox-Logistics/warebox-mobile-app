package com.example.lp_logistics.data.repository

import com.example.lp_logistics.data.remote.api.ApiService
import com.example.lp_logistics.data.remote.requests.LoginRequest
import com.example.lp_logistics.data.remote.responses.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiService: ApiService)
{
    suspend fun loginEmployee(email: String, password: String): LoginResponse {
        return try {
            val response = apiService.loginEmployee(LoginRequest(email, password))

            // Check if role is allowed (2 or 3)
            if (response.user.roleID !in listOf(2, 3)) {
                throw SecurityException("Unauthorized role access")
            }
            return response
        } catch (e: HttpException) {
            // Handle HTTP errors (like 401 for wrong credentials)
            when (e.code()) {
                401 -> {
                    // Try to parse the error message from response
                    val errorResponse = try {
                        e.response()?.errorBody()?.string()?.let {
                            Json.decodeFromString<ErrorResponse>(it)
                        }
                    } catch (parseError: Exception) {
                        null
                    }
                    throw CredentialsException(errorResponse?.message ?: "The provided credentials are incorrect")
                }
                else -> throw e // Re-throw other HTTP errors
            }
        }
    }

    suspend fun updateFcmToken(userId: Int, token: String) {
        withContext(Dispatchers.IO) {
            apiService.updateFcmToken(userId, FcmTokenRequest(token))
        }
    }

    suspend fun logout(token: String) {
        withContext(Dispatchers.IO) {
            apiService.logout("Bearer $token")
        }
    }
}

data class FcmTokenRequest(
    val fcm_token: String
)

data class ErrorResponse(
    val message: String?,
    @SerialName("error") val error: String?
)

class CredentialsException(message: String) : Exception(message)
