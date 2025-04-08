package com.example.lp_logistics.presentation.screens.home

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lp_logistics.data.local.UserManager
import com.example.lp_logistics.data.remote.responses.DeliveryData
import com.example.lp_logistics.data.remote.responses.DeliveryDock
import com.example.lp_logistics.data.remote.responses.DeliveryStatusResponse
import com.example.lp_logistics.data.remote.responses.ParkingLotResponse
import com.example.lp_logistics.data.repository.DeliveryRepository
import com.example.lp_logistics.domain.model.BoxUIState
import com.example.lp_logistics.domain.model.DeliveryDetails
import com.example.lp_logistics.domain.model.PalletUIState
import com.example.lp_logistics.domain.model.Vehicle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.lp_logistics.presentation.theme.Create
import com.example.lp_logistics.presentation.theme.LightBlue
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class HomeViewModel  @Inject constructor(private val deliveryRepository: DeliveryRepository) : ViewModel(){

    private val _deliveries = mutableStateOf<List<DeliveryData>>(emptyList())
    val deliveries: State<List<DeliveryData>> = _deliveries

    private val _deliveryDetails = mutableStateOf<DeliveryDetails?>(null)
    val deliveryDetails: State<DeliveryDetails?> = _deliveryDetails

    private val _uiState = mutableStateOf<List<PalletUIState>>(emptyList())
    val uiState: State<List<PalletUIState>> = _uiState

    private val _parkingLotResponse = MutableLiveData<ParkingLotResponse?>(null)
    val parkingLotResponse: LiveData<ParkingLotResponse?> = _parkingLotResponse

    private val _parkingLotResponseTrailer = MutableLiveData<ParkingLotResponse?>(null)
    val parkingLotResponseTrailer: LiveData<ParkingLotResponse?> = _parkingLotResponseTrailer

    private val _errorMessage = MutableStateFlow<String?>("")
    val errorMessage: MutableStateFlow<String?> = _errorMessage

    private val _loading = MutableStateFlow(false)
    val loading: MutableStateFlow<Boolean> = _loading

    private val _loadingStatus = MutableStateFlow(false)
    val loadingStatus: MutableStateFlow<Boolean> = _loadingStatus

    private val _delivery = mutableStateOf<DeliveryData?>(null)
    val delivery: State<DeliveryData?> = _delivery

    val isRefreshing = mutableStateOf(false)

    private val _routeUiState = mutableStateOf<RouteUiState>(RouteUiState.Loading)
    val routeUiState: State<RouteUiState> = _routeUiState

    private val _driverVehicle = MutableStateFlow<Vehicle?>(null)
    val driverVehicle: StateFlow<Vehicle?> = _driverVehicle

    private val _deliveryStatus = mutableStateOf<String?>(null)
    val deliveryStatus: State<String?> = _deliveryStatus

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

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

    fun getDelivery(id: Int, context: Context) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val token = UserManager.getToken(context).toString()
                val response = deliveryRepository.getDelivery(token, id)
                _delivery.value = response
                _deliveryStatus.value = response.status // Track status
            } catch (e: Exception) {
                e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getDeliveryDetails(id: Int, context: Context) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val token = UserManager.getToken(context).toString()
                val response = deliveryRepository.getDeliveryDetails(token, id)
                _deliveryDetails.value = response

                val palletsUI = response.pallets.map { pallet ->
                    val boxStates = pallet.boxes.map { box ->
                        BoxUIState(
                            box = box,
                            isExpanded = false
                        )
                    }

                    PalletUIState(
                        pallet = pallet,
                        isExpanded = false,
                        boxes = boxStates
                    )
                }
                _deliveryStatus.value =  _deliveryDetails.value!!.status
                _uiState.value = palletsUI
            } catch (e: Exception) {
                // Handle error
                e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun togglePalletExpanded(palletId: Int) {
        _uiState.value = _uiState.value.map { pallet ->
            if (pallet.pallet.pallet_id == palletId) {
                pallet.copy(isExpanded = !pallet.isExpanded)
            } else pallet
        }
    }

    fun toggleBoxExpanded(palletId: Int, boxId: Int) {
        _uiState.value = _uiState.value.map { pallet ->
            if (pallet.pallet.pallet_id == palletId) {
                pallet.copy(
                    boxes = pallet.boxes.map { box ->
                        if (box.box.box_id == boxId) {
                            box.copy(isExpanded = !box.isExpanded)
                        } else box
                    }
                )
            } else pallet
        }
    }



    fun setLoadingDock(context: Context, deliveryID: Int){
        viewModelScope.launch {
            try{
                _loadingStatus.value = true
                val token = UserManager.getToken(context).toString()
                val response = deliveryRepository.setLoadingDock(token, deliveryID)
                val statusResponse = deliveryRepository.getDeliveryStatus(token, deliveryID)
                _deliveryStatus.value = statusResponse.status
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
            }catch (e: Exception){
                e.message ?: "Unknown error occurred"
            }finally {
                _loadingStatus.value = false
            }
        }
    }

    fun startDelivering(context: Context, deliveryID: Int){
        viewModelScope.launch {
            try{
                _loadingStatus.value = true
                val token = UserManager.getToken(context).toString()
                val response = deliveryRepository.startDelivering(token,deliveryID)
                val statusResponse = deliveryRepository.getDeliveryStatus(token, deliveryID)
                _deliveryStatus.value = statusResponse.status
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
            }catch (e: Exception){
                e.message ?: "Unknown error occurred"
            }finally {
                _loadingStatus.value = false
            }
        }
    }

    fun setDockingStatus(context: Context, deliveryId: Int) {
        viewModelScope.launch {
            try {
                _loadingStatus.value = true
                println("Setting docking status with ID: $deliveryId")
                val token = UserManager.getToken(context).toString()
                val response = deliveryRepository.setToDocking(token, deliveryId)
                println("Response: $response")
                val statusResponse = deliveryRepository.getDeliveryStatus(token, deliveryId)
                _deliveryStatus.value = statusResponse.status
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Status update failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }finally {
                _loadingStatus.value = false
            }
        }
    }

    fun confirmArrival(context: Context, deliveryId: Int) {
        viewModelScope.launch {
            try {
                _loadingStatus.value = true
                println("confirming arrival: $deliveryId")
                val token = UserManager.getToken(context).toString()
                val response = deliveryRepository.confirmDeliveryArrival(token, deliveryId)
                println("Response: $response")
                val statusResponse = deliveryRepository.getDeliveryStatus(token, deliveryId)
                _deliveryStatus.value = statusResponse.status
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Status update failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }finally {
                _loadingStatus.value = false
            }
        }
    }

    fun getDriverVehicle(context: Context) {
        viewModelScope.launch {
            try {
                println("Getting driver vehicle")
                _loading.value = true
                _error.value = null
                val token = UserManager.getToken(context).toString()
                val driverId = UserManager.getUser(context)?.id ?: run {
                    _error.value = "No user ID found"
                    return@launch
                }
                val response = deliveryRepository.getDriverVehicle(token, driverId)
                _driverVehicle.value = response
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun confirmDeliveryByDriver(context: Context, confirmationCode: String) {
        viewModelScope.launch {
            try{
                _loading.value = true
                val token = UserManager.getToken(context).toString()
                val response = deliveryRepository.confirmDeliveryByCode(token, confirmationCode)
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
            }catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun freeLot(context: Context, lotID: Int) {
        viewModelScope.launch {
            try{
                val token = UserManager.getToken(context).toString()
                deliveryRepository.freeLot(token, lotID)
                Toast.makeText(context, "Lot freed", Toast.LENGTH_SHORT).show()
            }catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            }
        }
    }


    //Make the btn have a Circular progress indicator to show that its actually doing something
    fun getButtonState(deliveryStatus: String?): DeliveryButtonState {
        return when(deliveryStatus) {
            "Pending" -> DeliveryButtonState(
                text = "Start Docking",
                enabled = true,
                color = LightBlue,
                textColor = Color.White,
                status = "Pending"
            )
            "Docking" -> DeliveryButtonState(
                text = "Start Loading",
                enabled = true,
                color = LightBlue,
                textColor = Color.White,
                status = "Docking"
            )
            "Loading" -> DeliveryButtonState(
                text = "Finish Loading & Start Route",
                enabled = true,
                color = Create,
                textColor = Color.White,
                status = "Loading"
            )
            "Delivering" -> DeliveryButtonState(
                text = "View Route",
                enabled = true,
                color = Create,
                textColor = Color.White,
                status = "Delivering"
            )
            else -> DeliveryButtonState(
                text = "Unavailable",
                enabled = false,
                color = Color.Gray,
                textColor = Color.White,
                status = "Unknown"
            )
        }
    }
}

sealed class RouteUiState {
    object Loading : RouteUiState()
    data class Success(
        val delivery: DeliveryData,
        val truckParkingLot: ParkingLotResponse?,
        val trailerParkingLot: ParkingLotResponse?
    ) : RouteUiState()
    data class Error(val message: String) : RouteUiState()
}

data class DeliveryButtonState(
    val text: String,
    val enabled: Boolean,
    val color: Color,
    val textColor: Color,
    val status: String, // Add status field for decision making
    val isLoading: Boolean = false // Add this

)