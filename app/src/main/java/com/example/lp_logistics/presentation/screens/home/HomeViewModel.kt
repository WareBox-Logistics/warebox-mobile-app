package com.example.lp_logistics.presentation.screens.home

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lp_logistics.data.local.UserManager
import com.example.lp_logistics.data.remote.responses.DeliveryData
import com.example.lp_logistics.data.remote.responses.DeliveryResponse
import com.example.lp_logistics.data.remote.responses.ParkingLotResponse
import com.example.lp_logistics.data.repository.DeliveryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel  @Inject constructor(private val deliveryRepository: DeliveryRepository) : ViewModel(){

    private val _deliveries = mutableStateOf<List<DeliveryData>>(emptyList())
    val deliveries: State<List<DeliveryData>> = _deliveries

    private val _parkingLotResponse = MutableLiveData<ParkingLotResponse?>(null)
    val parkingLotResponse: LiveData<ParkingLotResponse?> = _parkingLotResponse

    private val _parkingLotResponseTrailer = MutableLiveData<ParkingLotResponse?>(null)
    val parkingLotResponseTrailer: LiveData<ParkingLotResponse?> = _parkingLotResponseTrailer

    private val _errorMessage = MutableStateFlow<String?>("")
    val errorMessage: MutableStateFlow<String?> = _errorMessage

    private val _loading = MutableStateFlow(false)
    val loading: MutableStateFlow<Boolean> = _loading

    private val _delivery = mutableStateOf<DeliveryData?>(null)
    val delivery: State<DeliveryData?> = _delivery

    val isRefreshing = mutableStateOf(false)

    fun loadDeliveries(context: Context) {
        viewModelScope.launch {
            println("🔵 Coroutine started")
            isRefreshing.value = true
            _loading.value = true

            try {
                println("🟢 Fetching token...")
                val token = UserManager.getToken(context).toString()
                val driverId = UserManager.getUser(context)!!.id
                println("🟢 Token: ${token.take(5)}... | DriverID: $driverId")

                println("🟡 Calling API...")
                val response = deliveryRepository.getDeliveryOrders(token, driverId)
                println("🟢 API Response: ${response.message} ")

                _deliveries.value = response.data
                println("✅ Deliveries: ${_deliveries.value?.size ?: 0} items")

            } catch (e: Exception) {
                println("🔴 ERROR: ${e.stackTraceToString()}")
            }finally {
                _loading.value = false
                isRefreshing.value = false
            }
        }
    }

    fun findTruckParkingLocation(context:Context,vehicleId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null

            try {
                println(vehicleId)
                val token = UserManager.getToken(context).toString()
                val response = deliveryRepository.getParkingLot(token, vehicleId)
                _parkingLotResponse.value = response
            } catch (e: DeliveryRepository.DeliveryRepositoryException) {
                _errorMessage.value = "Error finding parking location: ${e.message}"
                println("Error: ${e.message}")
            } catch (e: Exception) {
                _errorMessage.value = "An unexpected error occurred"
                println("Unexpected error: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    fun findTrailerParkingLocation(context:Context,vehicleId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null

            try {
                println(vehicleId)
                val token = UserManager.getToken(context).toString()
                val response = deliveryRepository.getParkingLotTrailer(token, vehicleId)
                _parkingLotResponseTrailer.value = response
            } catch (e: DeliveryRepository.DeliveryRepositoryException) {
                _errorMessage.value = "Error finding parking location: ${e.message}"
                println("Error: ${e.message}")
            } catch (e: Exception) {
                _errorMessage.value = "An unexpected error occurred"
                println("Unexpected error: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearParkingLocation() {
        _parkingLotResponse.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun getDelivery(id: Int, context: Context){
        viewModelScope.launch {
            _loading.value = true
            try {
                val token = UserManager.getToken(context).toString()
                val response = deliveryRepository.getDelivery(token, id)
                _delivery.value = response
            }catch (e: Exception){
                    e.message ?: "Unknown error occurred"
            }finally {
                _loading.value = false
            }
        }
    }

}
