package com.example.lp_logistics.data.remote.responses

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
)

data class Category(
    val id: Int,
    val name: String,
    val description: String,
)

data class PalletResponse(
    val id: Int,
    val company: CompanyResponse,
    val warehouse: WarehouseResponse,
    val weight: Float,
    val volume: Float,
    val pallet: String
)

data class BoxResponse(
    val id: Int,
    val qty: Int,
    val weight: Float,
    val volume: Float,
    val pallet: PalletResponse,
    val product: ProductResponse
)

data class Companies(
    val companies: List<CompanyResponse>
)

data class Warehouses(
    val warehouses: List<WarehouseResponse>
)
