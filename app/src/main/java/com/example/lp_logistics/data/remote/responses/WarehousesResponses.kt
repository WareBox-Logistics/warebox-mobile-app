package com.example.lp_logistics.data.remote.responses

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class CompanyResponse(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val service: Service,
)

data class Service(
    val id: Int,
    val name: String,
)

data class WarehouseResponse(
    val id: Int,
    val name: String,
    val altitude: String,
    val latitude: String,
)

data class ProductResponse(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val image: String,
    val category: Category,
    val company: CompanyResponse,
    val sku: String
)

data class SimpleProductResponse(
    val id: Int,
    val name: String,
    val sku: String
){
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            sku,
            name,
            "${name.first()}"
        )

        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}

data class Category(
    val id: Int,
    val name: String,
    val description: String,
)


data class PalletResponse(
    val id: Int,
    val company: String,
    val warehouse: String,
    val weight: String,
    val volume: String,
    val status: String,
    val boxes: List<BoxResponse>,
)


data class BoxResponse(//no pallet info
    val id: Int,
    val qty: Int,
    val weight: String,
    val volume: String,
    val pallet: Int,
    val product:  String //ProductResponse
)

data class BoxResponseWithPallet(
    val id: Int,
    val pallet: PalletResponse,
    val qty: Int,
    val weight: String,
    val volume: String,
    val product:  String //ProductResponse
)

data class Companies(
    val companies: List<CompanyResponse>
)

data class Warehouses(
    val warehouses: List<WarehouseResponse>
)

//data classes to get the response of delivery orders

data class DeliveryResponse(
    val message: String,
    val data: List<DeliveryData>
)

data class DeliveryData(
    val id: Int,
    val truck: Truck,
    val trailer: Trailer,
    val company: Company,
    val created_by: Int,
    val status: String,
    val shipping_date: String,
    val completed_date: String?,
    val route: RouteFromDelivery,
    val type: String,//make a scanner if deliveries of type warehouse - location
    val estimated_arrival: String,
    val estimated_duration_minutes: Int,
    val origin_id: Int,
    val origin_type: String,
    val destination_id: Int,
    val destination_type: String,
    val origin: Origin,
    val destination: Destination,
    val delivery_details: List<DeliveryDetail>,
    val dock: Dock
)

data class Truck(
    val id: Int,
    val plates: String,
    val vin: String,
    val model_id: Int,
    val volume: String,
    val driver_id: Int?,
    val type: String,
    val is_available: Boolean
)

data class Trailer(
    val id: Int,
    val plates: String,
    val vin: String,
    val model_id: Int,
    val volume: String,
    val driver_id: Int?,
    val type: String,
    val is_available: Boolean
)

data class Company(
    val id: Int,
    val name: String,
    val rfc: String,
    val email: String,
    val phone: String,
    val service: Int,
)

@Serializable
data class RouteFromDelivery(
    val PolylinePath: List<List<LatLng>>,
    val RouteDirections: List<RouteDirection>
)

@Serializable
data class LatLng(
    val lat: Double,
    val lng: Double
)

@Serializable
data class RouteDirection(
    val giro: Int,
    val point: LatLng,
    val long_m: Double,
    val geojson: String,
    val direccion: String,
    val tiempo_min: Double,
    val costo_caseta: Int,
    val punto_caseta: String?,
    val eje_excedente: Int
)

data class Origin(
    val id: Int,
    val name: String,
    val latitude: String,
    val longitude: String,
    val id_routing_net: String?,
    val source: String?,
    val target: String?
)

data class Destination(
    val id: Int,
    val name: String,
    val latitude: String,
    val longitude: String,
    val company: Int,
    val id_routing_net: String,
    val source: String,
    val target: String
)

data class DeliveryDetail(
    val id: Int,
    val delivery: Int,
    val pallet: Pallet,
)

data class Pallet(
    val id: Int,
    val company: Int,
    val warehouse: Int,
    val weight: String,
    val volume: String,
    val status: String,
    val verified: Boolean,
)

//parking lot location response

data class ParkingLotResponse(
    val vehicle_id: Int,
    val parking_location: ParkingLocation
)

data class ParkingLocation(
    val warehouse_name: String,
    val parking_lot_name: String,
    val spot_code: String,
    val lot_id: Int
)

data class DeliveryDock (
    val dock: Dock,
    val scheduled_time: String
)

data class Dock (
    val id: Int,
    val number: Int
)

data class ResponseMessage(
    val message: String
)

data class DeliveryStatusResponse(
    @SerializedName("delivery_id") val deliveryId: Int,
    @SerializedName("status") val status: String
)

data class ConfirmationByQR(
    @SerializedName("confirmation_code")val confirmationCode: String
)

data class FreeLot(
    @SerializedName("lot_id") val lotID: Int
)