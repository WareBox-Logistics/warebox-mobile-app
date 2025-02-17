package com.example.lp_logistics.data.remote.responses

data class RouteResponse(
    val route: Route
)

data class Route(
    val id: Int,
    val name: String,
    val company: Int,
    val origin: String,
    val destination: String,
    val polyline: String
)

