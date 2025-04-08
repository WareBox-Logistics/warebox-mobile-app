package com.example.lp_logistics.data.repository

import com.example.lp_logistics.data.remote.api.ApiService
import com.example.lp_logistics.data.remote.requests.DriverIdRequest
import com.example.lp_logistics.data.remote.requests.ParkingLotRequest
import com.example.lp_logistics.data.remote.requests.ReportDispatchRequest
import com.example.lp_logistics.data.remote.responses.ConfirmationByQR
import com.example.lp_logistics.data.remote.responses.DeliveryData
import com.example.lp_logistics.data.remote.responses.DeliveryDock
import com.example.lp_logistics.data.remote.responses.DeliveryResponse
import com.example.lp_logistics.data.remote.responses.DeliveryStatusResponse
import com.example.lp_logistics.data.remote.responses.FreeLot
import com.example.lp_logistics.data.remote.responses.PalletResponse
import com.example.lp_logistics.data.remote.responses.ParkingLotResponse
import com.example.lp_logistics.data.remote.responses.ResponseMessage
import com.example.lp_logistics.domain.model.DeliveryDetails
import com.example.lp_logistics.domain.model.SetLoadingRequest
import com.example.lp_logistics.domain.model.Vehicle
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

    suspend fun getDeliveryDetails(token: String, id: Int): DeliveryDetails {
        return try {
            withContext(Dispatchers.IO) {
                println("Getting delivery with id here: $id")
                apiService.getDeliveryDetails("Bearer $token", id)
            }
        } catch (e: HttpException) {
            println("HTTP Error: ${e.code()} - ${e.message()}")
            throw e
        }
    }

    suspend fun getReservedDock(token: String, deliveryId: Int): DeliveryDock {
        return try {
            withContext(Dispatchers.IO) {
                println("Getting delivery with id here: $deliveryId")
                apiService.getReservedDock("Bearer $token", deliveryId)
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

    suspend fun setLoadingDock(token: String, deliveryID: Int): ResponseMessage {
         return try {
             println("Attempting to call: PATCH $deliveryID")

             withContext(Dispatchers.IO) {
                apiService.setLoadingDock(
                    token = "Bearer $token",
                    id = deliveryID,
                )
            }
        } catch (e: HttpException) {
            println("HTTP Error: ${e.code()} - ${e.message()}")
            throw e
        } catch (e: Exception) {
            println("Error: ${e.message}")
            throw e
        }
    }

    suspend fun startDelivering(token: String, deliveryId: Int):ResponseMessage {
        return try {
            withContext(Dispatchers.IO) {
                apiService.startDelivering("Bearer $token", deliveryId)
            }
        } catch (e: HttpException) {
            println("HTTP Error: ${e.code()} - ${e.message()}")
            throw e
        }
    }

    suspend fun setToDocking(token: String, deliveryId: Int): ResponseMessage { // check this one
        return apiService.setToDocking("Bearer $token", deliveryId)
    }

    suspend fun getDeliveryStatus(token: String, deliveryId: Int): DeliveryStatusResponse {
        return apiService.getDeliveryStatus("Bearer $token", deliveryId)
    }

    suspend fun confirmDeliveryArrival(token: String, deliveryId: Int): ResponseMessage {
        return apiService.confirmDeliveryArrival("Bearer $token", deliveryId)
    }

    suspend fun getDriverVehicle(token: String, driverId: Int):Vehicle {
        return try {
            withContext(Dispatchers.IO) {
                apiService.getDriverVehicle("Bearer $token", driverId)
            }
        } catch (e: HttpException) {
            println("HTTP Error: ${e.code()} - ${e.message()}")
            throw e
        }
    }


    suspend fun confirmDeliveryByCode(token: String, confirmationCode: String): ResponseMessage {
           return try{
               withContext(Dispatchers.IO) {
                   apiService.confirmDeliveryByCode(
                       "Bearer $token",
                       ConfirmationByQR(confirmationCode = confirmationCode)
                   )
               }
            }catch (e: HttpException) {
                println("HTTP Error: ${e.code()} - ${e.message()}")
                throw e
            }
        }

    suspend fun freeLot(token: String, lotID: Int){
        return try{
            withContext(Dispatchers.IO) {
                apiService.freeLot(
                    "Bearer $token",
                    FreeLot(lotID = lotID)
                    )
            }
        }catch (e: HttpException) {
            println("HTTP Error: ${e.code()} - ${e.message()}")
            throw e
        }
    }

}

