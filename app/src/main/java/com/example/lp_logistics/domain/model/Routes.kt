package com.example.lp_logistics.domain.model


data class FeatureCollection(
    val features: List<Feature>,
    val type: String // "FeatureCollection"
)

data class Feature(
    val type: String, // "Feature"
    val properties: Properties,
    val geometry: Geometry
)

// Properties of the route
data class Properties(
    val mode: String, // "truck"
    val waypoints: List<Waypoint>,
    val units: String, // "metric"
    val distance: Double, // Total route distance
    val distance_units: String, // "meters"
    val time: Double, // Total route time
    val legs: List<Leg> // Route legs
)

// Waypoints for the route
data class Waypoint(
    val location: List<Double>, // Longitude and latitude
    val original_index: Int
)

// Legs of the route, including steps
data class Leg(
    val distance: Double, // Distance of this leg
    val time: Double, // Time for this leg
    val steps: List<Step> // Navigation steps within this leg
)

// Individual step in the route
data class Step(
    val from_index: Int,
    val to_index: Int,
    val distance: Double,
    val time: Double,
    val instruction: Instruction // Step-by-step navigation instruction
)

// Instruction for a step
data class Instruction(
    val text: String // Instruction text like "Turn left onto Avenida Emiliano Zapata."
)

// Geometry of the route (coordinates for the polyline)
data class Geometry(
    val type: String, // "MultiLineString"
    val coordinates: List<List<List<Double>>> // List of points (longitude, latitude)
)



data class Coordinate(
    val lat: Double,
    val lng: Double,
    val remainingDistance: Double? = null
)

data class PolylinePath(
    val coordinates: List<Coordinate>
)

data class RouteDirection(
    val points: List<Coordinate>, // List of coordinates representing the route
    val instructions: List<String>, // List of instructions for the route
    val totalDistance: Double, // Length of the route in meters
    val tollBooths: List<TollBooth>, // List of toll booths along the route
    val origin: Coordinate,
    val destination: Coordinate
)

data class TollBooth(
    val coordinate: Coordinate,
    val cost: Int
)