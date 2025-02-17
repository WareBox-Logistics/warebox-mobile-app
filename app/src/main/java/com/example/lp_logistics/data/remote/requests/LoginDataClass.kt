package com.example.lp_logistics.data.remote.requests

data class LoginRequest(
    val email: String,
    val password: String
)

data class  User(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val email: String,
    val role: String,
    val created_at: String,
    val updated_at: String
)

