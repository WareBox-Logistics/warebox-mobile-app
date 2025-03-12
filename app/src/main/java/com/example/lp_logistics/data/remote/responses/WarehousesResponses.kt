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

data class Companies(
    val companies: List<CompanyResponse>
)

data class Warehouses(
    val warehouses: List<WarehouseResponse>
)
