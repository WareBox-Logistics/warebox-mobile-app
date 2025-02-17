package com.example.lp_logistics.data.remote.responses

import com.example.lp_logistics.data.remote.requests.User

data class LoginResponse(
    val user: User,
    val token: String
)
