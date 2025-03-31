package com.example.lp_logistics.data.repository

import com.example.lp_logistics.data.remote.api.ApiService
import com.example.lp_logistics.data.remote.requests.DriverIdRequest
import com.example.lp_logistics.data.remote.requests.ParkingLotRequest
import com.example.lp_logistics.data.remote.requests.ReportDispatchRequest
import com.example.lp_logistics.data.remote.responses.DeliveryData
import com.example.lp_logistics.data.remote.responses.DeliveryResponse
import com.example.lp_logistics.data.remote.responses.PalletResponse
import com.example.lp_logistics.data.remote.responses.ParkingLotResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class DeliveryRepository @Inject constructor(private val apiService: ApiService) {
    class DeliveryRepositoryException(message: String) : Exception(message)

    suspend fun getDeliveryOrders(token: String, driverId: Int): DeliveryResponse {
        return withContext(Dispatchers.IO) {
            try {
                apiService.getDeliveryOrders(
                    token = "Bearer $token",
                    request = DriverIdRequest(driverID = driverId)
                )
            } catch (e: HttpException) {
                println("HTTP Error: ${e.code()} - ${e.message()}")
                throw DeliveryRepositoryException("HTTP error: ${e.code()}")
            }
        }
    }

    suspend fun getParkingLot(token: String, vehicleId: Int): ParkingLotResponse {
        return withContext(Dispatchers.IO) {
            try {
                val request = ParkingLotRequest(vehicleID = vehicleId)
                apiService.getParkingLot(
                    token = "Bearer $token",
                    request = request
                )
        }catch (e: HttpException) {
            println("HTTP Error: ${e.code()} - ${e.message()}")
            throw DeliveryRepositoryException("HTTP error: ${e.code()}")
        }
            }
    }

    suspend fun getParkingLotTrailer(token: String, vehicleId: Int): ParkingLotResponse {
        return withContext(Dispatchers.IO) {
            try {
                val request = ParkingLotRequest(vehicleID = vehicleId)

                apiService.getParkingLotTrailer(
                    token = "Bearer $token",
                    request = request
                )
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                println("HTTP Error ${e.code()}: ${e.message()}\n$errorBody")
                throw DeliveryRepositoryException(
                    "Failed to get parking lot: ${e.code()} - ${errorBody ?: e.message()}"
                )
            } catch (e: IOException) {
                println("Network Error: ${e.message}")
                throw DeliveryRepositoryException("Network error: ${e.message}")
            } catch (e: Exception) {
                println("Unexpected Error: ${e.stackTraceToString()}")
                throw DeliveryRepositoryException("Unexpected error: ${e.message}")
            }
        }
    }

    suspend fun getDelivery(token: String, id: Int): DeliveryData {
        return try {
            withContext(Dispatchers.IO) {
                println("Getting delivery with id here: $id")
                apiService.getDelivery("Bearer $token", id)
            }
        } catch (e: HttpException) {
            println("HTTP Error: ${e.code()} - ${e.message()}")
            throw e
        }
    }


    suspend fun postReportForDispatch(token: String, latitude: String, longitude: String, problem: Int, issue: Boolean, description: String, driver: Int) {
        withContext(Dispatchers.IO) {
            try{
                apiService.postReportForDispatch("Bearer $token",ReportDispatchRequest(latitude, longitude, problem, issue, description, driver))

            }catch (e: HttpException) {
                println("HTTP Error: ${e.code()} - ${e.message()}")
                throw e
            }
        }

    }


}

